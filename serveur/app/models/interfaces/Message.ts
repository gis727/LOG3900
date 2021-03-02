import * as socketIo from "socket.io";

export interface IMessage {
    timestamp: number;
    author: string; // userId
    message: string;
    roomId: string;
    avatar: string;
}

export class Message {
    public static getMessage(client: socketIo.Socket, roomId: string, message: string): IMessage {
        return {
            timestamp: Date.now(),
            author: client["userId"],
            message: message,
            roomId: roomId,
            avatar: client["avatar"],
        } as IMessage;
    }
    public static getSystemMessage(author: string, avatar: string, roomId: string, message: string): IMessage {
        return {
            timestamp: Date.now(),
            author: author,
            message: message,
            roomId: roomId,
            avatar: avatar,
        } as IMessage;
    }
}
