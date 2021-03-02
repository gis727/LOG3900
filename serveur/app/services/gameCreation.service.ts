import { inject, injectable } from "inversify";
import "reflect-metadata";
import * as socketIo from "socket.io";
import { Deserializer } from "../Tools/deserializer";
import { GameData } from "../models/interfaces/Game";
import { getResponseState, ResponseNumber } from "../models/interfaces/user";
import { SocketRoutes } from "../models/socketRoutes";
import Types from "../types";
import { DatabaseService } from "./database.service";

@injectable()
export class GameCreationService {

    public constructor(@inject(Types.DatabaseService) public db: DatabaseService) { }

    public async create(client: socketIo.Socket, clientData: string): Promise<void> {
        const data: GameData | undefined = Deserializer.deserializeGameCreationData(clientData);
        if (data === undefined) {
            client.emit(SocketRoutes.create_game, getResponseState(ResponseNumber.bad_format));
        } else {
            if (! await this.db.addGame(data)) {
                client.emit(SocketRoutes.create_game, getResponseState(ResponseNumber.server_error));
                return;
            }
            client.emit(SocketRoutes.create_game, getResponseState(ResponseNumber.success));
        }
    }

    public async remove(client: socketIo.Socket, gameId: string): Promise<void> {
        if (gameId === "") {
            client.emit(SocketRoutes.delete_game, getResponseState(ResponseNumber.bad_format));
        } else {
            await this.db.removeGame(gameId);
            client.emit(SocketRoutes.delete_game, getResponseState(ResponseNumber.success));
        }
    }
}
