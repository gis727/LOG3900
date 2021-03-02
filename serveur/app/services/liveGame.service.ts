import { inject, injectable } from "inversify";
import "reflect-metadata";
import * as socketIo from "socket.io";
import { getAllClientsInRoom } from "../Tools/SocketHelper";
import { GameData, SelectedDrawer } from "../models/interfaces/Game";
import { LiveGame, LiveGamesManager, LiveGameEvent/*, Player*/ } from "../models/interfaces/LiveGame";
import { Party } from "../models/interfaces/Party";
// import { getResponseState, ResponseNumber } from "../models/interfaces/user";
import { SocketRoutes } from "../models/socketRoutes";
import Types from "../types";
import { ChatService } from "./chat.service";
import { DatabaseService } from "./database.service";
import { PartyService } from "./party.service";
/*
 * Handles all live games
 */
@injectable()
export class LiveGameService {

    public constructor(@inject(Types.ChatService) public chatService: ChatService,
                       @inject(Types.DatabaseService) public db: DatabaseService) {
        LiveGameService.games = (LiveGameService.games === undefined) ?
                                new LiveGamesManager(db, chatService) : LiveGameService.games;
    }

    private static games: LiveGamesManager;

    public async startParty(party: Party, client: socketIo.Socket): Promise<void> {
        // 1: initialize the game structure
        const newGame: LiveGame = await LiveGameService.games.addGame(party);
        // 2: register to the game's listener
        newGame.events.on(LiveGameEvent.update_stats, (game: LiveGame) => { this.onUpdateStats(game); });
        newGame.events.on(LiveGameEvent.end_game, (game: LiveGame) => { this.onEndGame(game); }); // TODO:nec?
        newGame.events.on(LiveGameEvent.end_party, (game: LiveGame) => { this.onEndParty(game, this); });
        newGame.events.on(LiveGameEvent.start_new_game, (game: LiveGame) => { this.onStartNewGame(game, this).catch(); });
        newGame.events.on(LiveGameEvent.kick, (culpritClient: socketIo.Socket, partyId: string) => { this.onKick(culpritClient, partyId); });
        // 3: send the first game
        await this.startGame(client, newGame);
    }

    private async startGame(client: socketIo.Socket, game: LiveGame): Promise<void> {
        // 1: get the next game data
        const { liveGame, partyId, gameData, choosenClient } = await LiveGameService.games.getGame(game, client);

        // 2: send response to the clients
        this.emitStartGame(client, choosenClient, gameData as GameData, partyId);

        // 3: start the chrono
        liveGame?.start(true, client);
    }

    public proposeAnswer(client: socketIo.Socket, answer: string): void {
        const result: boolean|undefined = LiveGameService.games.getLiveGame(client)?.proposeAnswer(answer, client);
        if (result !== undefined) { client.emit(SocketRoutes.answer, JSON.stringify(result)); }
    }

    public kick(client: socketIo.Socket, culpritId: string): void {
        LiveGameService.games.getLiveGame(client)?.kickPlayer(client, culpritId);
    }

    public getAClue(client: socketIo.Socket): void {
        LiveGameService.games.sendAClue(client).then().catch();
    }

    public sendStroke(client: socketIo.Socket, strokeData: string): void {
        const game: LiveGame | undefined = LiveGameService.games.getLiveGame(client);
        if (game !== undefined) {
            client.broadcast.to(game.party.id).emit(SocketRoutes.stroke, strokeData);
        }
    }

    private onUpdateStats(game: LiveGame): void {
        const room: socketIo.Room = game.ioServer.sockets.adapter.rooms[game.party.id];
        if (room !== undefined) {
            // tslint:disable-next-line:forin
            for (const clientSocketId in room.sockets) {
                const _client: socketIo.Socket = game.ioServer.sockets.connected[clientSocketId];
                _client.emit(SocketRoutes.update_stats, JSON.stringify(game.getIStats(_client)));
            }
        }
    }

    private async onStartNewGame(game: LiveGame, $this: LiveGameService): Promise<void> {
        const { liveGame, partyId, gameData, choosenClient } = await LiveGameService.games.getGame(game);
        this.emitStartGame(game.getAClient(), choosenClient, gameData as GameData, partyId);
        liveGame?.start(false);
    }

    private onEndGame(game: LiveGame): void {
        game.getAClient().server.in(game.party.id).emit(SocketRoutes.end_game); // TODO: necessary ?
    }

    private onKick(culpritClient: socketIo.Socket, partyId: string): void {
        this.chatService.leaveRoom(culpritClient, partyId);
        this.chatService.deleteGameRoomForSingleClient(culpritClient, partyId);
    }

    private onEndParty(game: LiveGame, $this: LiveGameService): void {
        // TODO: TO BE EXTRACTED IN THE RIGHT HANDLER (if needed)
        const clients: socketIo.Socket[] = getAllClientsInRoom(game.ioServer, game.party.id);
        clients.forEach((client: socketIo.Socket) => {
            client.emit(SocketRoutes.end_party, JSON.stringify(game.getIStats(client, true)));
        });

        LiveGameService.games.closeParty(game);
        PartyService.removeParty(game.ioServer, game.party.id);
        this.chatService.deleteGameRoom(game.ioServer, game.party.id);
    }

    private emitStartGame(client: socketIo.Socket, choosenClient: socketIo.Socket|undefined, gameData: GameData, partyId: string): void {
        if (choosenClient === undefined) { // a virtual player is drawing
            gameData.selectedDrawer = SelectedDrawer.AVirtualPlayer;
            client.server.in(partyId).emit(SocketRoutes.start_game, JSON.stringify(gameData));
        } else {
            delete gameData["image"];
            // make the other clients wait
            gameData.selectedDrawer = SelectedDrawer.AnotherClient;
            choosenClient.to(partyId).emit(SocketRoutes.start_game, JSON.stringify(gameData));

            // allow drawing on current client
            gameData.selectedDrawer = SelectedDrawer.CurrentClient;
            choosenClient.emit(SocketRoutes.start_game, JSON.stringify(gameData));
        }
    }
}
