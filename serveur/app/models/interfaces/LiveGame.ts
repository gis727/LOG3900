// tslint:disable:max-classes-per-file
import * as Events from "events";
import * as socketIo from "socket.io";
import { getClientInRoom } from "../../Tools/SocketHelper";
import { shuffle } from "../../Tools/Array";
import { DrawingMode, getGameTime, GameData } from "../../models/interfaces/Game";
import { BotStats, KnownPlayerStat, VPManager } from "../../models/interfaces/VirtualPlayer";
import { IAccountData } from "../../models/interfaces/user";
import { ChatService } from "../../services/chat.service";
import { DatabaseService } from "../../services/database.service";
import { SocketRoutes } from "../socketRoutes";
import { GameIds } from "./GameIds";
import { GameStats, IGameStats, PlayerStats } from "./GameStats";
import { Party, PartyMode, Player } from "./Party";


export class LiveGame {
    // chrono params
    private readonly UPDATE_INTERVAL: number = 1000; // seconds
    private readonly CONNECTION_LATENCY: number = 1;

    public party: Party;
    public stats: GameStats;
    public gameIds: GameIds;
    public botStats: BotStats[];
    public events: Events.EventEmitter;
    public timer: ReturnType<typeof setInterval>;
    public currentGame: GameData;
    private db: DatabaseService;
    private vp: VPManager;
    public ioServer: socketIo.Server;

    public constructor(party: Party, db: DatabaseService, vp: VPManager) {
        this.db = db;
        this.party = party;
        this.stats = new GameStats(party.mode, party.players);
        this.gameIds = new GameIds();
        this.events = new Events.EventEmitter();
        this.vp = vp;
    }

    public async loadGames(): Promise<void> {
        await this.gameIds.load(this.db);
    }

    public hasPlayer(playerId: string): boolean {
        return (this.party.players.find((player: Player) => player.id === playerId) !== undefined);
    }

    public start(freshStart: boolean, client?: socketIo.Socket): void {
        if (freshStart && client !== undefined) { this.ioServer = client.server; }
        if (this.party.mode === PartyMode.freeForAll) {
            if (freshStart) { this.vp.reactOnPartyStart(this); } else { this.vp.reactOnNewGame(this); }
            this.endGame();
            this.stats.resetForFFA(this);
            // tslint:disable-next-line:no-magic-numbers
            this.timer = setInterval(this.onTimeUpdate, this.UPDATE_INTERVAL, this);
        } else {
            if (freshStart) {
                this.vp.reactOnPartyStart(this);
                this.endGame();
                // tslint:disable-next-line:no-magic-numbers
                this.timer = setInterval(this.onTimeUpdate, this.UPDATE_INTERVAL, this);
            } else {
                this.stats.reset();
            }
        }
    }

    public proposeAnswer(answer: string, client: socketIo.Socket): boolean {
        if (this.gameIds.getDrawerId() === client["userId"]) { return false; }
        const isCorrectAnswer: boolean = (answer.toLowerCase() === this.currentGame.secretWord.toLowerCase());
        // tslint:disable-next-line:prefer-switch
        if (this.party.mode === PartyMode.solo || this.party.mode === PartyMode.coop) {
            if (this.stats.trialsLeft > 0) { // player had trials left
                this.stats.trialsLeft--;
                this.vp.reactOnAnswer(client, this, isCorrectAnswer, answer);
                if (isCorrectAnswer) {
                    this.stats.updateScore(this);
                    this.events.emit(LiveGameEvent.start_new_game, this);
                } else if (this.stats.trialsLeft === 0) {
                    this.vp.reactOnGameEnd(this);
                    this.events.emit(LiveGameEvent.start_new_game, this);
                }
                return isCorrectAnswer;
            }
        } else if (this.party.mode === PartyMode.freeForAll) {
            if (this.stats.updateTrial(client)) { // player had trials left
                this.vp.reactOnAnswer(client, this, isCorrectAnswer, answer);
                if (isCorrectAnswer) {
                    this.stats.updateFFAScore(client, this.gameIds);
                    if (this.stats.noTrialsLeftForAllPlayers()) {
                        // EXTRACT MAYBE
                        this.stats.gamesLeft--;
                        if (this.stats.gamesLeft <= 0) { // was the last game
                            this.endParty();
                        } else {
                            this.events.emit(LiveGameEvent.start_new_game, this);
                        }
                    }
                } else if (this.stats.noTrialsLeftForAllPlayers()) {
                    this.vp.reactOnGameEnd(this);
                    this.events.emit(LiveGameEvent.start_new_game, this);
                }
                return isCorrectAnswer;
            }
        }

        return false;
    }

    public kickPlayer(client: socketIo.Socket, culpritId: string): void {
        if (this.stats.addKickCount(client, culpritId, this.party)) {
            const culpritClient: socketIo.Socket | undefined = getClientInRoom(this.ioServer, culpritId, this.party.id);
            if (culpritClient !== undefined) {
                culpritClient.emit(SocketRoutes.kick);
                this.vp.reactOnPlayerKicked(this, culpritId );
                this.events.emit(
                    LiveGameEvent.kick,
                    getClientInRoom(this.ioServer, culpritId, this.party.id) as socketIo.Socket,
                    this.party.id);
            }
            const drawerId: string = this.gameIds.getDrawerId();
            this.gameIds.clearFromDrawers(culpritId);
            if (drawerId === culpritId) {
                this.vp.reactOnGameEnd(this);
                this.events.emit(LiveGameEvent.start_new_game, this);
            }
        }
    }

    private endGame(): void {
        clearInterval(this.timer);
    }

    private endParty(ghostEnd?: boolean): void {
        this.endGame();
        if (ghostEnd) {
            this.events.emit(LiveGameEvent.end_party, this);
        } else {
            this.stats.selectWinner();
            this.gameIds.updateProfile(this.party, this.stats, this.db).then(() => {
                this.events.emit(LiveGameEvent.end_party, this);
            }).catch(() => {
                this.events.emit(LiveGameEvent.end_party, this);
            });
        }
    }

    private onTimeUpdate($this: LiveGame): void {
        if (!Party.hasHumanPlayersLeft($this.party)) { $this.endParty(true); return; }
        $this.stats.tick();
        $this.events.emit(LiveGameEvent.update_stats, $this);

        // tslint:disable-next-line:prefer-switch
        if ($this.party.mode === PartyMode.solo || $this.party.mode === PartyMode.coop) {
            if ($this.stats.timeLeft <= 0) { // time is up (check if party finished)
                $this.endParty();
            } else {
                // check if game is finished
                const timeSinceGameStart: number = $this.stats.lastGameStartTime - $this.stats.timeLeft;
                const gameFinished: boolean = (timeSinceGameStart - $this.CONNECTION_LATENCY) > getGameTime($this.currentGame.difficulty);
                if (gameFinished) {
                    $this.vp.reactOnGameEnd($this);
                    $this.events.emit(LiveGameEvent.start_new_game, $this);
                }
            }
        } else if ($this.party.mode === PartyMode.freeForAll) {

            if ($this.stats.timeLeft <= 0) {
                // EXTRACT MAYBE
                $this.stats.gamesLeft--;
                if ($this.stats.gamesLeft <= 0) { // was the last game
                    $this.vp.reactOnGameEnd($this);
                    $this.endParty();
                } else {
                    $this.vp.reactOnGameEnd($this);
                    $this.endGame();
                    $this.events.emit(LiveGameEvent.start_new_game, $this);
                }
            }
        }
    }

    public getIStats(client: socketIo.Socket, includeWinerStat?: boolean): IGameStats {
        if (this.party.mode === PartyMode.freeForAll) {
            const stat: PlayerStats | undefined = this.stats.getPlayerStats(client);
            return {
                score: stat?.score,
                timeLeft: this.stats.timeLeft,
                trialsLeft: stat?.trialsLeft,
                isWinner: includeWinerStat ? this.stats.clientIsWinner(client) : false,
            } as IGameStats;
        } else {
            return {
                score: this.stats.score,
                timeLeft: this.stats.timeLeft,
                trialsLeft: this.stats.trialsLeft,
                isWinner: includeWinerStat ? this.stats.clientIsWinner(client) : false,
            } as IGameStats;
        }
    }

    public chooseDrawer(client: socketIo.Socket): socketIo.Socket | undefined {
        return (this.party.mode === PartyMode.freeForAll) ? this.gameIds.chooseDrawer(client, this.party) : undefined;
    }

    public getAClient(): socketIo.Socket {
        let humanPlayer: Player = this.party.players[0];
        this.party.players.forEach((player: Player) => { if(!player.isVirtual) humanPlayer = player; });
        return getClientInRoom(this.ioServer, humanPlayer.id, this.party.id) as socketIo.Socket;
    }
}

export class LiveGamesManager {
    public games: LiveGame[] = [];
    private db: DatabaseService;
    private vp: VPManager;

    public constructor(db: DatabaseService, chatService: ChatService) {
        this.db = db;
        this.vp = new VPManager(chatService);
    }

    public async addGame(party: Party): Promise<LiveGame> {
        const game: LiveGame = new LiveGame(party, this.db, this.vp);
        this.games.push(game);
        await game.loadGames();
        return game;
    }

    public getLiveGame(client: socketIo.Socket): LiveGame | undefined {
        return this.games.find((game: LiveGame) => game.hasPlayer(client["userId"]));
    }

    public async getGame(game: LiveGame, client?: socketIo.Socket):
    Promise<{ liveGame: LiveGame|undefined, partyId: string, gameData: {}, choosenClient: socketIo.Socket | undefined }> {
        if (game !== undefined) {
            // 1: load all players profiles
            if (client) { await this.loadProfiles(game); }
            // 2: get the first game to be played
            const gameData: {} = await this.db.getGame(await game.gameIds.getNextGameId(this.db));
            // 3: copy the game
            game.currentGame = JSON.parse(JSON.stringify(gameData)) as GameData;
            game.currentGame.image.paths = []; // no need to store so much data
            // 4.0: hide secret stuff
            if (game.party.mode !== PartyMode.freeForAll) {
                gameData["secretWord"] = "";
            }
            delete gameData["clues"];
            // 4.1: Randomize paths for randome games
            if (+game.currentGame.drawingMode === DrawingMode.Random) {
                gameData["image"]["paths"] = shuffle(gameData["image"]["paths"]);
                gameData["drawingMode"] = DrawingMode.Classic;
            }
            // 5: choose the next drawer
            const choosenClient: socketIo.Socket | undefined = game.chooseDrawer(client ? client : game.getAClient());
            // 6: return
            return {
                liveGame: game,
                partyId: game.party.id,
                gameData: (game !== undefined) ? gameData : {},
                choosenClient: choosenClient,
            };
        } else {
            return {
                liveGame: game, partyId: "", gameData: {}, choosenClient: undefined,
            };
        }
    }

    public closeParty(game: LiveGame): void {
        const gameIndex: number = this.games.indexOf(game);
        if (gameIndex !== -1) { this.games.splice(gameIndex, 1); }
    }

    public async sendAClue(client: socketIo.Socket): Promise<void> {
        const game: LiveGame|undefined = this.getLiveGame(client);
        if (game !== undefined && game.gameIds.getDrawerId() !== client["userId"]) {
            await this.vp.sendAClue(client, game);
        }
    }

    private async loadProfiles(game: LiveGame): Promise<void> {
        // 1: load profiles
        const profiles: IAccountData[] = [];
        for (const player of game.party.players) {
            if (!player.isVirtual) {
                profiles.push((await this.db.getAccountData(player.id, true)) as IAccountData);
            }
        }
        // 2 : extract bots stats from profiles
        game.botStats = [];
        game.party.players.forEach((player: Player) => {
            if (player.isVirtual) {
                const virtualPlayerpId: string = player.id;
                const botStat: BotStats = {
                    botId: virtualPlayerpId,
                    players: [],
                };
                profiles.forEach((profile: IAccountData) => {
                    const playerStat: KnownPlayerStat = {
                        playerId: profile.nickname,
                        longestPartyDuration: -1,
                        shortestPartyDuration: Infinity,
                        bestScore: -1,
                        wonGamesCount: 0,
                        scoreForPartyPlayedTogether: [],
                        // playedMoreThan5PartiesToday: false,
                    };
                    for (const previousGame of profile.stats.previousGames) {
                        // update the durations
                        if (previousGame.duration > playerStat.longestPartyDuration) {
                            playerStat.longestPartyDuration = previousGame.duration;
                        }
                        if (previousGame.duration < playerStat.shortestPartyDuration) {
                            playerStat.shortestPartyDuration = previousGame.duration;
                        }
                        // update the best score
                        if (previousGame.score > playerStat.bestScore) { playerStat.bestScore = previousGame.score; }
                        // update the number of games won
                        if (previousGame.won) { playerStat.wonGamesCount++; }
                        // update data if party played together
                        if (previousGame.players.includes(virtualPlayerpId)) { // we played this together
                            playerStat.scoreForPartyPlayedTogether.push(previousGame.score);
                        }
                    }
                    botStat.players.push(playerStat);
                });
                game.botStats.push(botStat);
            }
        });
    }
}

export enum LiveGameEvent {
    update_stats   = "update-stats",
    propose_answer = "propose-answer",
    end_game       = "end-game",
    start_new_game = "start-new-game",
    end_party      = "end-party",
    kick           = "kick",
}
