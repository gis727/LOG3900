import * as socketIo from "socket.io";

export const getClientInRoom: (server: socketIo.Server, targetClientId: string, roomId: string) =>
socketIo.Socket | undefined = (server: socketIo.Server, targetClientId: string, roomId: string): socketIo.Socket | undefined => {
    const room: socketIo.Room = server.sockets.adapter.rooms[roomId];
    if (room !== undefined) {
        // tslint:disable-next-line:forin
        for (const clientSocketId in room.sockets) {
            const _client: socketIo.Socket = server.sockets.connected[clientSocketId];
            if (_client["userId"] === targetClientId) {
                return _client;
            }
        }
    }
    return undefined;
};

export const getAllClientsInRoom: (server: socketIo.Server, roomId: string) =>
socketIo.Socket[] = (server: socketIo.Server, roomId: string): socketIo.Socket[] => {
    const clients: socketIo.Socket[] = [];
    const room: socketIo.Room = server.sockets.adapter.rooms[roomId];
    if (room !== undefined) {
            // tslint:disable-next-line:forin
            for (const clientSocketId in room.sockets) {
                clients.push(server.sockets.connected[clientSocketId]);
            }
        }
    return clients;
};
