import * as socketIo from "socket.io";
import { findIndexOfElemWithAttr, GenericObject } from "../../Tools/Array";
import { OwnerShipError } from "../../Tools/Exceptions";
import { IMessage } from "./Message";

export enum RoomNames { main = "main" }
const MAX_MESSAGES_SIZE: number = 2020;
const SYSTEM_ROOM_OWNER: string = "SYSTEM";

export interface IRoom {
    id: string;
    owner: boolean;
    joined: boolean;
}

export class Room {

    public constructor(id: string, owner?: string, closed?: boolean) {
        this.id = id;
        this.messages = [];
        this.owner = owner ? owner : "";
        this.closed = closed ? closed : false;
        this.joinedClients = {};
    }

    public id: string;
    public messages: IMessage[]; // chat history
    public owner: string;
    public closed: boolean; // game channels are closed (not visible to other players)
    private joinedClients: GenericObject;

    public static getMainRoom(): Room {
        return new Room(RoomNames.main, "DEFAULT_MAIN_OWNER");
    }

    public size(): number {
        return 0;
    }

    public join(client: socketIo.Socket): void {
        client.join(this.id);
        this.joinedClients[client["uuid"]] = client["uuid"];
    }

    public leave(client: socketIo.Socket): void {
        client.leave(this.id);
        delete this.joinedClients[client["uuid"]];
    }

    public saveMessage(message: IMessage): void {
        if (this.id === RoomNames.main && this.messages.length >= MAX_MESSAGES_SIZE) {
            this.messages.shift();
        }
        this.messages.push(message);
    }

    public isOwner(userUuid: string): boolean {
        return this.owner === userUuid;
    }

    public isOwnerAndLastInRoom(client: socketIo.Socket): boolean {
        return this.isOwner(client["uuid"]) && client.server.sockets.adapter.rooms[this.id] === undefined;
    }

    public joined(userUuid: string): boolean {
        return this.joinedClients[userUuid] !== undefined;
    }
}

export class RoomManager {
    private rooms: Room[];

    public constructor() {
        this.rooms = [Room.getMainRoom()];
    }

    public getRoom(roomId: string): Room|undefined {
        return this.rooms.find((room: Room) => room.id === roomId);
    }

    public getRoomIndex(roomId: string): number {
        return findIndexOfElemWithAttr(this.rooms, "id", roomId);
    }

    public createRoom(roomId: string, userUuid?: string, closed?: boolean): boolean {
        if (this.getRoom(roomId) !== undefined) { return false; }
        this.rooms.push(new Room(roomId, closed ? SYSTEM_ROOM_OWNER : userUuid, closed));
        return true;
    }

    public deleteRoom(roomId: string, client?: socketIo.Socket, force?: boolean): boolean {
        const roomIndex: number = this.getRoomIndex(roomId);
        if (roomIndex === -1 || roomId === RoomNames.main) { return false; }
        if (!force && client !== undefined) {
            if (!this.rooms[roomIndex].isOwner(client["uuid"])) { throw new OwnerShipError(); }
            // tslint:disable-next-line:no-any
            client.server.of("/").in(roomId).clients((error: any, socketIds: any) => {
                socketIds.forEach((socketId: string) => client.server.sockets.sockets[socketId].leave(roomId));
            });
        }
        this.rooms.splice(roomIndex, 1);
        return true;
    }

    public getAllOpenRooms(client: socketIo.Socket): IRoom[] {
        const openRooms: IRoom[] = [];
        this.rooms.forEach((room: Room) => {
            if (!room.closed) {
                openRooms.push({
                    id: room.id,
                    owner: (room.isOwner(client["uuid"])),
                    joined: room.joined(client["uuid"]),
                });
            } else if (room.joined(client["uuid"])) {
                openRooms.push({
                    id: room.id,
                    owner: false,
                    joined: true,
                });
            }
        });
        return openRooms;
    }

    public rejoinAllJoinedRooms(client: socketIo.Socket): void {
        this.rooms.forEach((room: Room) => {
            if (!room.closed && room.joined(client["uuid"])) {
                room.join(client);
            }
        });
    }
}
