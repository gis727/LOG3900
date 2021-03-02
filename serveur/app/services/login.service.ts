import { inject, injectable } from "inversify";
import "reflect-metadata";
import * as socketIo from "socket.io";
import { objHasAttrWithAttr } from "../Tools/Array";
import { Deserializer } from "../Tools/deserializer";
import { getResponseState, ResponseNumber, UserLoginData } from "../models/interfaces/user";
import { SocketRoutes } from "../models/socketRoutes";
import { PartyService } from "../services/party.service";
import Types from "../types";
import { ChatService } from "./chat.service";
import { DatabaseService } from "./database.service";

/*
 * Handles users log in/out
 */
@injectable()
export class LoginService {

    public constructor(@inject(Types.DatabaseService) public db: DatabaseService,
                       @inject(Types.ChatService) public chatService: ChatService,
                       @inject(Types.PartyService) public partyService: PartyService) { }

    public async login(client: socketIo.Socket, clientData: UserLoginData|string, trusted?: boolean): Promise<void> {
        const data: UserLoginData | undefined = (typeof clientData === "string") ?
        Deserializer.deserializeLoginData(clientData as string) : clientData as UserLoginData;

        if (data === undefined) {
            client.emit(SocketRoutes.login, getResponseState(ResponseNumber.bad_format));
        } else {

            // 1: Check if user is already connected
            const alreadyConnected: boolean = objHasAttrWithAttr(client.server.sockets.sockets, "userId", data.nickname);
            if (alreadyConnected) { client.emit(SocketRoutes.login, getResponseState(ResponseNumber.already_connected)); return; }
            // 2: Check password
            if (!trusted && !await this.db.credentialsAreValid(data.nickname, data.password)) {
                client.emit(SocketRoutes.login, getResponseState(ResponseNumber.wrong_credential));
                return;
            }
            // 3: Assign nickname and avatar link
            client["userId"] = data.nickname;
            const profile: object = (await this.db.getAccountData(data.nickname));
            client["uuid"] = profile["_id"].toString();
            client["avatar"] = profile["avatar"];
            // 4: Add to main chat room and restore join for other rooms
            this.chatService.joinRoom(client, true);
            this.chatService.setUpRoomsForClient(client);
            // 5: Log connection
            await this.db.logActivity(data.nickname);
            // 6: Respond
            client.emit(SocketRoutes.login, getResponseState(ResponseNumber.success));
            // 7: Advise everyone
            client.broadcast.emit(SocketRoutes.new_user_connected, client["userId"]);
        }
    }

    public async logout(client: socketIo.Socket, data: string): Promise<void> {

        if (client["userId"] !== undefined) {
            // 1: Log disconnection
            await this.db.logActivity(client["userId"], true);
            // 2: clear rooms where the client is the owner (if no other client is in)
            this.chatService.clearRoomsForClient(client);
            // 3: remove from all parties
            await this.partyService.clearPartiesForClient(client);
            // 4: Advise everyone
            client.broadcast.emit(SocketRoutes.user_disconnected, client["userId"]);
        }
        // 4: Disconnect
        client.disconnect(true);
    }
}
