import { inject, injectable } from "inversify";
import "reflect-metadata";
import * as socketIo from "socket.io";
import { Deserializer } from "../Tools/deserializer";
import { BotsIds } from "../models/interfaces/VirtualPlayersProvider";
import { getResponseState, AccountData, ResponseNumber } from "../models/interfaces/user";
import { SocketRoutes } from "../models/socketRoutes";
import Types from "../types";
import { DatabaseService } from "./database.service";
import { LoginService } from "./login.service";

@injectable()
export class AccountService {

    public constructor(@inject(Types.DatabaseService) public db: DatabaseService,
                       @inject(Types.LoginService) public loginService: LoginService) { }

    private async nicknameIsTaken(nickname: string): Promise<boolean> {
        return (await this.db.nicknameIsTaken(nickname)) && nickname !== BotsIds.BadBot &&
        nickname !== BotsIds.GoodBot && nickname !== BotsIds.NeutralBot;
    }

    public async createNewAccount(client: socketIo.Socket, clientData: string): Promise<void> {
        const data: AccountData | undefined = Deserializer.deserializeNewAccountData(clientData);
        if (data === undefined) {
            client.emit(SocketRoutes.account_creation, getResponseState(ResponseNumber.bad_format));
        } else {
            // 1: Check if username already exists
            if (await this.nicknameIsTaken(data.nickname)) {
                client.emit(SocketRoutes.account_creation, getResponseState(ResponseNumber.id_already_used));
                return;
            }
            // 2: add account in DB -> OK
            if (!await this.db.addNewAccount(data)) {
                client.emit(SocketRoutes.modify_account, getResponseState(ResponseNumber.server_error));
                return;
            }
            // 3: log client
            await this.loginService.login(client, {nickname: data.nickname, password: data.password}, true);
        }
    }

    public async getAccountData(client: socketIo.Socket, userId: string): Promise<void> {
        userId = userId ? userId : client["userId"];
        let data: object = await this.db.getAccountData(userId);
        if (Object.keys(data).length === 0)  data = await this.db.getAccountData(userId); // second attempt
        if (Object.keys(data).length === 0)  data = await this.db.getAccountData(userId); // third attempt (we don't use a loop for determinism)
        if (Object.keys(data).length !== 0) {
            client.emit(SocketRoutes.view_account, (client["userId"] === userId) ?
            JSON.stringify(data) : JSON.stringify({nickname: data["nickname"], avatar: data["avatar"]}));
        }
    }

    public async editAccount(client: socketIo.Socket, clientData: string): Promise<void> {
        const data: AccountData | undefined = Deserializer.deserializeNewAccountData(clientData);
        if (data === undefined) {
            client.emit(SocketRoutes.modify_account, getResponseState(ResponseNumber.bad_format));
        } else {
            if (data.nickname !== client["userId"] && await this.db.nicknameIsTaken(data.nickname)) {
                client.emit(SocketRoutes.modify_account, getResponseState(ResponseNumber.id_already_used));
                return;
            }
            if (await this.db.modifyAccount(data, client)) {
                client.emit(SocketRoutes.modify_account, getResponseState(ResponseNumber.success));
            } else {
                client.emit(SocketRoutes.modify_account, getResponseState(ResponseNumber.server_error));
            }
        }
    }

    public async changeAvatar(client: socketIo.Socket, clientData: string): Promise<void> {
        const data: string | undefined = Deserializer.deserializeAvatarData(clientData);
        if (data === undefined) {
            client.emit(SocketRoutes.modify_avatar, getResponseState(ResponseNumber.bad_format));
        } else {
            if (await this.db.modifyAvatar(client["userId"], client["avatar"], data)) {
                client.emit(SocketRoutes.modify_avatar, getResponseState(ResponseNumber.success));
            } else {
                client.emit(SocketRoutes.modify_avatar, getResponseState(ResponseNumber.server_error));
            }
        }
    }
}
