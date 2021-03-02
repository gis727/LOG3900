import * as socketIo from "socket.io";
import { getGameBonusTime, getGameTime } from "./Game";
import { GameIds } from "./GameIds";
import { LiveGame } from "./LiveGame";
import { Party, PartyMode, Player } from "./Party";

const INIT_SCORE: number = 0;
// initial times in seconds
const SOLO_INIT_TIME: number = 30;
const COOP_INIT_TIME: number = 30;
const FFA_INIT_EXTRA_TIME: number = 20;
// game ids management
// const FFA_GAMES_COUNT: number = 3;
const FFA_INIT_TRIALS: number = 3;
const INIT_TRIALS: number = 5;
const FFA_NUMBER_OF_ROUNDS: number = 2;

export class GameStats implements IGameStats {

    public score: number;
    public totalDuration: number = 0;
    public timeLeft: number;
    public trialsLeft: number;
    public lastGameStartTime: number;
    public gamesLeft: number; // for FFA
    public playersStats: PlayerStats[] = [];

    public constructor(partyMode: PartyMode, players: Player[]) {
        this.trialsLeft = INIT_TRIALS;
        if (partyMode === PartyMode.solo) {
            this.score    = INIT_SCORE;
            this.timeLeft = SOLO_INIT_TIME;
        } else if (partyMode === PartyMode.coop) {
            this.score    = INIT_SCORE;
            this.timeLeft = COOP_INIT_TIME;
        } else {
            this.score    = INIT_SCORE;
            this.timeLeft = FFA_INIT_EXTRA_TIME;
        }
        this.lastGameStartTime = this.timeLeft;

        players.forEach((player: Player) => {
            this.playersStats.push(new PlayerStats(player.id, player.isVirtual));
        });
        this.gamesLeft = players.length * FFA_NUMBER_OF_ROUNDS;
    }

    public reset(): void {
        this.trialsLeft = INIT_TRIALS;
        this.lastGameStartTime = this.timeLeft;
    }

    public resetForFFA(game: LiveGame): void {
        if (game.gameIds.drawerIsVirtual(game.party)) {
            this.timeLeft = getGameTime(game.currentGame.difficulty);
        } else {
            this.timeLeft = getGameTime(game.currentGame.difficulty) + FFA_INIT_EXTRA_TIME;
        }
        this.playersStats.forEach((playerStat: PlayerStats) => {
            playerStat.reset(game.gameIds.getDrawerId());
        });
    }

    public getPlayerStats(client: socketIo.Socket): PlayerStats | undefined {
        return this.playersStats.find((playerStat: PlayerStats) => playerStat.playerId === client["userId"]);
    }

    public updateTrial(client: socketIo.Socket): boolean {
        for (const playerStat of this.playersStats) {
            if (playerStat.playerId === client["userId"]) {
                const hadTrialsLeft: boolean = (playerStat.trialsLeft > 0);
                if (hadTrialsLeft) { playerStat.trialsLeft--; }
                return hadTrialsLeft;
            }
        }
        return false;
    }

    public updateFFAScore(client: socketIo.Socket, gameIds: GameIds): void {
        // 1: get the number of players who scored for the current game
        const nbrOfscoredForCurrentGame: number = this.nbrOfScoredForCurrentGame();
        // 2: update scores
        const drawerId: string = gameIds.getDrawerId();
        this.playersStats.forEach((playerStat: PlayerStats) => {
            if (playerStat.playerId === client["userId"]) { // the player who guessed right
                playerStat.scoredForCurrentGame = true;
                playerStat.score += Math.ceil((this.timeLeft * 0.5) + (((this.playersStats.length - 2) - nbrOfscoredForCurrentGame) * 10));
                playerStat.trialsLeft = 0;
            } else if (playerStat.playerId === drawerId) { // the player who is drawing
                playerStat.score += 10;
            }
        });
    }

    // Updates score for COOP and SOLO
    public updateScore(game: LiveGame): void {
        this.timeLeft += getGameBonusTime(game.currentGame.difficulty);
        this.score += 10;
    }

    public noTrialsLeftForAllPlayers(): boolean {
        let result: boolean = true;
        this.playersStats.forEach((stat: PlayerStats) => {
            if (!stat.isVirtual) { result = result && stat.trialsLeft === 0; }
        });
        return result;
    }

    public nbrOfScoredForCurrentGame(): number {
        let nbrOfscoredForCurrentGame: number = 0;
        this.playersStats.forEach((playerStat: PlayerStats) => {
            if (playerStat.scoredForCurrentGame) { nbrOfscoredForCurrentGame++; }
        });
        return nbrOfscoredForCurrentGame;
    }

    public tick(): void {
        this.timeLeft--;
        this.totalDuration++;
    }

    public selectWinner(): void {
        let bestScore: number = 0;
        this.playersStats.forEach((playerStat: PlayerStats) => {
            if (playerStat.score >= bestScore) { bestScore = playerStat.score; }
            playerStat.isWinner = false;
        });
        this.playersStats.forEach((playerStat: PlayerStats) => {
            if (playerStat.score === bestScore && bestScore > 0) { playerStat.isWinner = true; }
        });
    }

    public clientIsWinner(client: socketIo.Socket): boolean {
        let isWinner: boolean = false;
        this.playersStats.forEach((playerStat: PlayerStats) => {
            if (playerStat.playerId === client["userId"]) { isWinner = playerStat.isWinner; }
        });
        return isWinner;
    }

    private canCkick(kickerId: string, culpritId: string): boolean {
        if (kickerId === culpritId) { return false; } // a player can't kick him/her-self
        for (const stat of this.playersStats) {
            if (((stat.playerId === culpritId) && stat.kickedBy.includes(kickerId)) || stat.isVirtual) { return false; }
        }
        return true;
    }

    public addKickCount(client: socketIo.Socket, culpritId: string, party: Party): boolean {
        if (this.canCkick(client["userId"], culpritId)) {
            for (let index: number = 0; index < this.playersStats.length; ++index) {
                const stat: PlayerStats = this.playersStats[index];
                if (stat.playerId === culpritId && stat.kick(client["userId"])) { // the culprit is really to be kicked out
                    // remove player from the stats
                    this.playersStats.splice(index, 1);
                    // remove player from the party
                    for (let pIndex: number = 0; pIndex < party.players.length; ++pIndex) {
                        if (party.players[pIndex].id === culpritId) { party.players.splice(pIndex, 1); break; }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}

export class PlayerStats {
    public playerId: string;
    public score: number;
    public trialsLeft: number;
    public isWinner: boolean = false;
    public scoredForCurrentGame: boolean;
    public kickedBy: string[];
    public isVirtual: boolean = false;
    private readonly MAX_KICK_COUNT: number = 3;

    public constructor(playerId: string, isVirtual: boolean) {
        this.trialsLeft = FFA_INIT_TRIALS;
        this.score = INIT_SCORE;
        this.playerId = playerId;
        this.scoredForCurrentGame = false;
        this.kickedBy = [];
        this.isVirtual = isVirtual;
    }

    public reset(drawerId: string): void {
        this.trialsLeft = (this.playerId === drawerId) ? 0 : FFA_INIT_TRIALS;
        this.scoredForCurrentGame = false;
    }

    public kick(kicker: string): boolean {
        this.kickedBy.push(kicker);
        return this.kickedBy.length >= this.MAX_KICK_COUNT;
    }
}

export interface IGameStats {
    score: number;
    timeLeft: number;
    trialsLeft: number;
    isWinner?: boolean;
}
