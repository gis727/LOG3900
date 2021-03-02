// tslint:disable:no-any no-unused-expression max-file-line-count
import { expect } from "chai";
import * as sinon from "sinon";
import { container } from "../../inversify.config";
import { IMessage } from "../../models/interfaces/Message";
import { Room, RoomManager, RoomNames } from "../../models/interfaces/Room";
import { getResponseState, MessageData, ResponseNumber } from "../../models/interfaces/user";
import { SocketRoutes } from "../../models/socketRoutes";
import Types from "../../types";
import { ChatService } from "../chat.service";
import { accounts, roomIds } from "./mocks.spec";

/*
 * TESTS LEFT: deleteRoom
 */

describe("ChatService", () => {
    const chatService: ChatService = container.get<ChatService>(Types.ChatService);
    let client: any = {};
    const messageData: MessageData  = {roomId: RoomNames.main, message: "msg"};

    afterEach(() => {
        expect( ChatService.rooms.getRoom(RoomNames.main) ).not.to.be.undefined; // main is safe
        ChatService.rooms = new RoomManager(); // rooms are clear
    });

    describe("sendMessage", () => {
        beforeEach(() => {
            client = {
                userId: accounts[0].nickname,
                uuid: "uuid",
                server: {in: sinon.stub().returns({emit: sinon.stub()})},
            };
        });

        it("should send a message to everyone in a room", () => {
            chatService.sendMessage(client, JSON.stringify(messageData));

            sinon.assert.calledOnce(client.server.in);
            sinon.assert.calledWith(client.server.in, messageData.roomId);
            sinon.assert.calledOnce(client.server.in().emit);
            sinon.assert.calledWith(client.server.in().emit, SocketRoutes.send_message);
            const msg: IMessage = JSON.parse(client.server.in().emit.getCall(0).args[1]);
            expect(msg.author).to.equal(client.userId);
            expect(msg.message).to.equal(messageData.message);
            expect(msg.avatar).to.equal(client.avatar);
        });

        it("should not send a message if the user is not in the room", () => {
            messageData.roomId = "fakeRoom";

            chatService.sendMessage(client, JSON.stringify(messageData));

            sinon.assert.notCalled(client.server.in);
        });

        it("should not send a message if message format is bad", () => {
            chatService.sendMessage(client, "");

            sinon.assert.notCalled(client.server.in);
        });

        it("should not send a message if the user isn't connected", () => {
            delete client.userId; // user is not connected

            chatService.sendMessage(client, "");

            sinon.assert.notCalled(client.server.in);
        });
    });

    describe("getRoomHistory", () => {
        beforeEach(() => {
            client.emit = sinon.fake();
            (ChatService.rooms.getRoom("main") as Room).messages = [];
        });

        it("should send all messages from main room if no room provided", () => {
            (ChatService.rooms.getRoom("main") as Room).messages = [
            { "timestamp": 150, "author": "user1", "message": "msg1", "roomId": "main", "avatar": ""},
            { "timestamp": 151, "author": "user2", "message": "msg3", "roomId": "main", "avatar": ""}];

            chatService.getRoomHistory(client);

            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.chat_room_history,
                                    JSON.stringify((ChatService.rooms.getRoom("main") as Room).messages));
        });

        it("should send an empty array if no history recorded in the room", () => {
            chatService.getRoomHistory(client, "main");

            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.chat_room_history, "[]");
            // const msg: IMessage = JSON.parse(client.server.in().emit.getCall(0).args[1]);
            // expect(msg.author).to.equal(client.userId);
            // expect(msg.message).to.equal(messageData.message);
        });
    });

    describe("createRoom", () => {
        beforeEach(() => {
            client = {
                userId: accounts[0].nickname,
                uuid: "uuid",
                emit: sinon.fake(),
                join: sinon.fake(),
                broadcast: {emit: sinon.fake()},
            };
        });

        it("should create a room, add the client in the room and broadcast the creation event to every other clients", () => {
            const newRoomId: string = roomIds[0];

            chatService.createRoom(client, newRoomId);

            // Joined the room
            sinon.assert.calledOnce(client.join);
            sinon.assert.calledWith(client.join, newRoomId);
            sinon.assert.calledTwice(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.enter_chat_room, getResponseState(ResponseNumber.success, newRoomId));
            // Response
            sinon.assert.calledWith(client.emit, SocketRoutes.create_chat_room, getResponseState(ResponseNumber.success, newRoomId));
            sinon.assert.calledWith(client.broadcast.emit, SocketRoutes.new_chat_room, getResponseState(ResponseNumber.success, newRoomId));
        });

        it("should not create a room, if the roomId already exists", () => {
            const oldRoomId: string = "Room", newRoomId: string = oldRoomId;
            chatService.createRoom(client, oldRoomId); // create the old room
            client = {userId: accounts[0].nickname, uuid: "uuid", emit: sinon.fake(), join: sinon.fake()}; // reset client

            chatService.createRoom(client, newRoomId);

            sinon.assert.notCalled(client.join);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.create_chat_room, getResponseState(ResponseNumber.id_already_used));
        });
    });

    describe("leaveRoom", () => {
        beforeEach(() => {
            client = {userId: accounts[0].nickname, join: sinon.fake(), emit: sinon.fake(), leave: sinon.fake(),
                      uuid: "uuid", broadcast: {emit: sinon.fake()},
            };
        });

        it("should remove the user from a room if the room exists", () => {

            chatService.leaveRoom(client, RoomNames.main);

            sinon.assert.calledOnce(client.leave);
            sinon.assert.calledWith(client.leave, RoomNames.main);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(
                client.emit,
                SocketRoutes.leave_chat_room,
                getResponseState(ResponseNumber.success, RoomNames.main),
            );
        });

        it("should not remove the user from a room if the room does not exist", () => {

            chatService.leaveRoom(client, roomIds[0]);

            sinon.assert.notCalled(client.leave); // no room left
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(
                client.emit,
                SocketRoutes.leave_chat_room,
                getResponseState(ResponseNumber.room_doesnt_exist, roomIds[0]),
            );
        });

        /*it("should delete the room if the user is the owner and no other client is in the room", () => {
            const roomId: string = roomIds[0];
            chatService.createRoom(client, roomId); // create the room
            client = {userId: accounts[0].nickname, uuid: "uuid", emit: sinon.fake(), leave: sinon.fake(),
                      broadcast: {emit: sinon.fake() }, server: {
                    emit: sinon.fake(), sockets: { adapter: { rooms: {}}},
                },
            }; // reset client
            client.server.sockets.adapter.rooms[roomId] = undefined; // no other client in the room

            chatService.leaveRoom(client, roomId);

            sinon.assert.calledOnce(client.leave);
            sinon.assert.calledWith(client.leave, roomId);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.leave_chat_room, getResponseState(ResponseNumber.success, roomId));
            // room deleted
            expect (ChatService.rooms.getRoom(roomId) ).to.be.undefined;
            sinon.assert.calledOnce(client.server.emit);
            sinon.assert.calledWith(client.server.emit, SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));
        });

        it("should not delete the room if the user is the owner and at least one client is in the room", () => {
            const roomId: string = roomIds[0];
            chatService.createRoom(client, roomId); // create the room
            client = {userId: accounts[0].nickname, emit: sinon.fake(), leave: sinon.fake(),
                      uuid: "uuid", broadcast: {emit: sinon.fake() }, server: {
                    emit: sinon.fake(), sockets: { adapter: { rooms: {}}},
            }}; // reset client
            client.server.sockets.adapter.rooms[roomId] = "something"; // at least one client left in the room

            chatService.leaveRoom(client, roomId);

            sinon.assert.calledOnce(client.leave);
            sinon.assert.calledWith(client.leave, roomId);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.leave_chat_room, getResponseState(ResponseNumber.success, roomId));
            // room not deleted
            expect (ChatService.rooms.getRoom(roomId) ).to.be.undefined;
            sinon.assert.calledOnce(client.server.emit);
            sinon.assert.calledWith(client.server.emit, SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));

        });*/
    });

    describe("joinRoom", () => {
        beforeEach(() => {
            client = {userId: "userId", emit: sinon.fake(), join: sinon.fake(), broadcast: {emit: sinon.fake() },
                      uuid: "uuid", server: {in: sinon.stub().returns({emit: sinon.stub()}) }};
        });

        it("should add the client in the desired room", () => {
            const roomId: string = roomIds[0];
            chatService.createRoom(client, roomId); // create the room
            client = {userId: accounts[0].nickname, uuid: "uuid", emit: sinon.fake(), join: sinon.fake()}; // reset client

            chatService.joinRoom(client, false, roomId);

            sinon.assert.calledWith(client.join, roomId);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.enter_chat_room, getResponseState(ResponseNumber.success, roomId));
        });

        it("should add the client in the room main room if no room Id provided", () => {
            chatService.joinRoom(client, false);

            sinon.assert.calledWith(client.join, RoomNames.main);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.enter_chat_room, getResponseState(ResponseNumber.success));
        });

        it("should not add the client in a room if the room does not exist", () => {
            const fakeRoomId: string = "fakeRoom";
            chatService.joinRoom(client, false, fakeRoomId);

            sinon.assert.notCalled(client.join);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(
                client.emit,
                SocketRoutes.enter_chat_room,
                getResponseState(ResponseNumber.room_doesnt_exist, fakeRoomId),
            );
        });
    });

    describe("deleteRoom", () => {
        const roomId: string = roomIds[0];
        beforeEach(() => {
            client = {
                userId: accounts[0].nickname, uuid: "uuid",
                emit: sinon.fake(), join: sinon.fake(), broadcast: {emit: sinon.fake() }};
            chatService.createRoom(client, roomId); // create the room
            client = {userId: accounts[0].nickname, uuid: "uuid", emit: sinon.fake(),
                      server: { emit: sinon.fake(), of: sinon.stub().returns({in: sinon.stub().returns({
                clients: (action: Function): void => { action(null, []); },
            })})}}; // reset client
        });

        it("should delete a room if the room exists", () => {
            chatService.deleteRoom(client, roomId);

            sinon.assert.calledOnce(client.server.emit);
            sinon.assert.calledWith(client.server.emit, SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));
        });

        it("should not delete a room if the room does not exist", () => {
            const fakeRoomId: string = roomIds[1];
            chatService.deleteRoom(client, fakeRoomId);

            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(
                client.emit,
                SocketRoutes.delete_chat_room,
                getResponseState(ResponseNumber.room_doesnt_exist, fakeRoomId),
            );
        });

        it("should remove any left client from the room", () => {
            const leftClients: string[] = ["client1", "client2"];
            client = {userId: accounts[0].nickname, uuid: "uuid", emit: sinon.fake(), server: {
                emit: sinon.fake(),
                sockets: { sockets: { "client1": { leave: sinon.fake() }, "client2": { leave: sinon.fake() } } },
                of: sinon.stub().returns({in: sinon.stub().returns({
                clients: (action: Function): void => { action(null, leftClients); },
            })})}}; // reset client

            chatService.deleteRoom(client, roomId);

            // clients removed
            sinon.assert.calledOnce(client.server.sockets.sockets[leftClients[0]].leave);
            sinon.assert.calledWith(client.server.sockets.sockets[leftClients[0]].leave, roomId);
            sinon.assert.calledOnce(client.server.sockets.sockets[leftClients[1]].leave);
            sinon.assert.calledWith(client.server.sockets.sockets[leftClients[1]].leave, roomId);
            // response
            sinon.assert.calledOnce(client.server.emit);
            sinon.assert.calledWith(client.server.emit, SocketRoutes.delete_chat_room, getResponseState(ResponseNumber.success, roomId));
        });
    });
});
