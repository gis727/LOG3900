// tslint:disable:max-file-line-count
import { Server  } from "http";
import { inject, injectable } from "inversify";
import * as socketIo from "socket.io";
import { SocketRoutes } from "../models/socketRoutes";
import { AccountService } from "../services/account.service";
import { ChatService } from "../services/chat.service";
import { GameCreationService } from "../services/gameCreation.service";
import { LiveGameService } from "../services/liveGame.service";
import { LoginService } from "../services/login.service";
import { PartyService } from "../services/party.service";
import Types from "../types";

@injectable()
export class SocketController {
    public io: socketIo.Server;

    public constructor(@inject(Types.ChatService) public chatService: ChatService,
                       @inject(Types.AccountService) public accountService: AccountService,
                       @inject(Types.LoginService) public loginService: LoginService,
                       @inject(Types.GameCreationService) public gameCreationService: GameCreationService,
                       @inject(Types.PartyService) public partyService: PartyService,
                       @inject(Types.LiveGameService) public liveGameService: LiveGameService) { }

    public init(server: Server): void {
        this.io = socketIo(server);
        this.initHandlers();
    }

    private initHandlers(): void {
        this.io.on("connection", (socket: socketIo.Socket) => {
            this.listen(socket);
        });
    }

    private async onSendMessage(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.chatService.sendMessage(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onRoomHistory(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.chatService.getRoomHistory(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onCreateRoom(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.chatService.createRoom(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onDeleteRoom(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.chatService.deleteRoom(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onJoinRoom(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.chatService.joinRoom(socket, false, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onLeaveRoom(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.chatService.leaveRoom(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onGetAllRooms(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.chatService.getAllRooms(socket);
        } catch (err) {
            console.error(err);
        }
    }

    private async onLogin(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.loginService.login(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onLogout(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.loginService.logout(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onCreateAccount(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.accountService.createNewAccount(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onViewAccount(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.accountService.getAccountData(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onChangeAvatar(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.accountService.changeAvatar(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onModifiyAccount(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.accountService.editAccount(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onCreateGame(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.gameCreationService.create(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onRemoveGame(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.gameCreationService.remove(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onCreateParty(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.partyService.createParty(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onJoinParty(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.partyService.joinParty(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onLeaveParty(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.partyService.leaveParty(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onGetAllParties(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.partyService.getAllParties(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onStartParty(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.partyService.startParty(socket);
        } catch (err) {
            console.error(err);
        }
    }

    private async onAnswer(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.liveGameService.proposeAnswer(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onStroke(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.liveGameService.sendStroke(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onAddVp(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.partyService.addVirtualPlayer(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onRemoveVp(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.partyService.removeVirtualPlayer(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onGetClue(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.liveGameService.getAClue(socket);
        } catch (err) {
            console.error(err);
        }
    }

    private async onKick(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            this.liveGameService.kick(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    private async onDisconnect(socket: socketIo.Socket, data: string): Promise<void> {
        try {
            await this.loginService.logout(socket, data);
        } catch (err) {
            console.error(err);
        }
    }

    // tslint:disable:max-func-body-length
    private listen(socket: socketIo.Socket): void {
        socket.on(SocketRoutes.send_message, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onSendMessage(socket, data); }
        });
        socket.on(SocketRoutes.chat_room_history, async (data: string): Promise<void> => {
            await this.onRoomHistory(socket, data);
        });
        socket.on(SocketRoutes.create_chat_room, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onCreateRoom(socket, data); }
        });
        socket.on(SocketRoutes.delete_chat_room, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onDeleteRoom(socket, data); }
        });
        socket.on(SocketRoutes.enter_chat_room, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onJoinRoom(socket, data); }
        });
        socket.on(SocketRoutes.leave_chat_room, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onLeaveRoom(socket, data); }
        });
        socket.on(SocketRoutes.get_all_rooms, async (data: string): Promise<void> => {
            await this.onGetAllRooms(socket, data);
        });
        socket.on(SocketRoutes.account_creation, async (data: string): Promise<void> => {
            await this.onCreateAccount(socket, data);
        });
        socket.on(SocketRoutes.view_account, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onViewAccount(socket, data); }
        });
        socket.on(SocketRoutes.modify_avatar, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onChangeAvatar(socket, data); }
        });
        socket.on(SocketRoutes.modify_account, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onModifiyAccount(socket, data); }
        });
        socket.on(SocketRoutes.login, async (data: string): Promise<void> => {
            await this.onLogin(socket, data);
        });
        socket.on(SocketRoutes.logout, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onLogout(socket, data); }
        });
        socket.on(SocketRoutes.create_game, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onCreateGame(socket, data); }
        });
        socket.on(SocketRoutes.delete_game, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onRemoveGame(socket, data); }
        });
        socket.on(SocketRoutes.create_party, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onCreateParty(socket, data); }
        });
        socket.on(SocketRoutes.join_party, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onJoinParty(socket, data); }
        });
        socket.on(SocketRoutes.leave_party, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onLeaveParty(socket, data); }
        });
        socket.on(SocketRoutes.get_all_parties, async (data: string): Promise<void> => {
            await this.onGetAllParties(socket, data);
        });
        socket.on(SocketRoutes.start_game, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onStartParty(socket, data); }
        });
        socket.on(SocketRoutes.answer, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onAnswer(socket, data); }
        });
        socket.on(SocketRoutes.stroke, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onStroke(socket, data); }
        });
        socket.on(SocketRoutes.add_vp, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onAddVp(socket, data); }
        });
        socket.on(SocketRoutes.remove_vp, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onRemoveVp(socket, data); }
        });
        socket.on(SocketRoutes.get_clue, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onGetClue(socket, data); }
        });
        socket.on(SocketRoutes.kick, async (data: string): Promise<void> => {
            if (socket["uuid"] !== undefined) { await this.onKick(socket, data); }
        });
        socket.on("disconnect", async (data: string): Promise<void> => {
            await this.onDisconnect(socket, data);
        });
    }
}
