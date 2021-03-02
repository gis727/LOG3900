import { injectable } from "inversify";
import "reflect-metadata";
import * as socketIo from "socket.io";
import { Deserializer } from "../Tools/deserializer";
import { IMessage, Message } from "../models/interfaces/Message";
import { /*IRoom,*/ Room, RoomManager, RoomNames } from "../models/interfaces/Room";
import { getResponseState, MessageData, ResponseNumber } from "../models/interfaces/user";
import { SocketRoutes } from "../models/socketRoutes";
/*
 * Handles all chat communications
 */
@injectable()
export class ChatService {
    public static rooms: RoomManager = new RoomManager();

    // Removes a game room
    public deleteGameRoom(server: socketIo.Server, roomId: string): void {
        if (ChatService.rooms.deleteRoom(roomId, undefined, true)) {
            server.emit(SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));
        }
    }

    public deleteGameRoomForSingleClient(client: socketIo.Socket, roomId: string): void {
        const room: Room|undefined = ChatService.rooms.getRoom(roomId);
        if (room !== undefined) {
            room.leave(client);
        }
        client.emit(SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));
    }

    public sendMessage(client: socketIo.Socket, clientData: string): void {
        const data: MessageData|undefined = Deserializer.deserializeMessageData(clientData);
        if (data !== undefined && client["userId"] !== undefined) {
            const room: Room|undefined = ChatService.rooms.getRoom(data.roomId);
            if (room !== undefined) {
                const msg: IMessage = Message.getMessage(client, room.id, data.message);
                // 1: save the message
                room.saveMessage(msg);
                // 2: send to everyone in the room
                client.server.in(data.roomId).emit(SocketRoutes.send_message, JSON.stringify(msg));
            }
        }
    }

    public sendSystemMessage(server: socketIo.Server, roomId: string, senderId: string, avatar: string, message: string): void {
        const room: Room|undefined = ChatService.rooms.getRoom(roomId);
        if (room !== undefined) {
            const msg: IMessage = Message.getSystemMessage(senderId, avatar, room.id, message);
            // 1: save the message
            room.saveMessage(msg);
            // 2: send to everyone in the room
            server.in(roomId).emit(SocketRoutes.send_message, JSON.stringify(msg));
        }
    }

    public joinRoom(client: socketIo.Socket, silent: boolean, roomId?: string): void {
        const room: Room|undefined = ChatService.rooms.getRoom(roomId ? roomId : RoomNames.main);
        if (room !== undefined) {
            room.join(client);
            if (!silent) { client.emit(SocketRoutes.enter_chat_room, getResponseState(ResponseNumber.success, roomId)); }
        } else {
            client.emit(SocketRoutes.enter_chat_room, getResponseState(ResponseNumber.room_doesnt_exist, roomId));
        }
    }

    public leaveRoom(client: socketIo.Socket, roomId: string): void {
        const room: Room|undefined = ChatService.rooms.getRoom(roomId);
        if (room !== undefined) {
            room.leave(client);
            client.emit(SocketRoutes.leave_chat_room, getResponseState(ResponseNumber.success, roomId));
            /*if (room.isOwner(client["uuid"]) && client.server.sockets.adapter.rooms[room.id] === undefined) {
                if (ChatService.rooms.deleteRoom(client, roomId, true)) {
                    client.server.emit(SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));
                }
            }*/
        } else {
            client.emit(SocketRoutes.leave_chat_room, getResponseState(ResponseNumber.room_doesnt_exist, roomId));
        }
    }

    public createRoom(client: socketIo.Socket, roomId: string, forAGame?: boolean): void {
        if (ChatService.rooms.createRoom(roomId, client["uuid"], forAGame)) {
            if (forAGame) { client.emit(SocketRoutes.new_chat_room, getResponseState(ResponseNumber.success, roomId)); } else {
                client.emit(SocketRoutes.create_chat_room, getResponseState(ResponseNumber.success, roomId));
                client.broadcast.emit(SocketRoutes.new_chat_room, getResponseState(ResponseNumber.success, roomId));
            }
            this.joinRoom(client, false, roomId);
        } else {
            client.emit(SocketRoutes.create_chat_room, getResponseState(ResponseNumber.id_already_used));
        }
    }

    public deleteRoom(client: socketIo.Socket, roomId: string): void {
        try {
            if (ChatService.rooms.deleteRoom(roomId, client)) {
                client.server.emit(SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));
            } else {
                client.emit(SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.room_doesnt_exist, roomId));
            }
        } catch (err) { /* not the owner. no further actions*/console.error(err); }
    }

    public getRoomHistory(client: socketIo.Socket, roomId?: string): void {
        const room: Room|undefined = ChatService.rooms.getRoom(roomId ? roomId : RoomNames.main);
        if (room !== undefined) {
            client.emit(SocketRoutes.chat_room_history, JSON.stringify(room.messages));
        }
    }

    public getAllRooms(client: socketIo.Socket): void {
        client.emit(SocketRoutes.get_all_rooms, JSON.stringify(ChatService.rooms.getAllOpenRooms(client)));
    }

    // clear rooms where this client is the owner
    public clearRoomsForClient(client: socketIo.Socket): void {
        /*ChatService.rooms.getAllOpenRooms(client).forEach((room: IRoom) => {
            if (room.owner && client.server.sockets.adapter.rooms[room.id] === undefined) { // client is owner and the only one inside
                if (ChatService.rooms.deleteRoom(client, room.id, true)) {
                    client.server.emit(SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, room.id));
                }
            }
        });*/
    }

    // join client to all rooms he was in before the last logout
    public setUpRoomsForClient(client: socketIo.Socket): void {
        ChatService.rooms.rejoinAllJoinedRooms(client);
    }
}
