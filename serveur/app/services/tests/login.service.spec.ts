// tslint:disable:no-any
import { expect } from "chai";
import * as sinon from "sinon";
import { container } from "../../inversify.config";
import { getResponseState, ResponseNumber, UserLoginData } from "../../models/interfaces/user";
import { SocketRoutes } from "../../models/socketRoutes";
import Types from "../../types";
import { ChatService } from "../chat.service";
import { DatabaseService } from "../database.service";
import { LoginService } from "../login.service";

describe("LoginService", () => {
    const loginService: LoginService = container.get<LoginService>(Types.LoginService);
    const loginData: UserLoginData  = {nickname: "nickname", password: "password"};
    let client: any = {},
    logActivity: sinon.SinonStub;

    beforeEach(() => {
        logActivity = sinon.stub(DatabaseService.prototype, "logActivity");
        client = {server: {sockets: {sockets: {key: {userId: ""}}}}};
        client.emit = sinon.fake();
        client.broadcast = {emit: sinon.fake()};
    });
    afterEach(() => {
        logActivity.restore();
    });

    describe("login", () => {

        /*
         * Tests to add:
         * - should not login the user if the password is incorrect
         * - should not check the password callee is trusted
        */

        let joinRoom: sinon.SinonStub,
        credentialsAreValid: sinon.SinonStub,
        getAccountData: sinon.SinonStub;
        const accountData: {} = {"_id": "id", "avatar": "http://avatar-link"};

        beforeEach(() => {
            joinRoom = sinon.stub(ChatService.prototype, "joinRoom");
            client.server.sockets.sockets.key.userId = ""; // user not already logged in
            credentialsAreValid = sinon.stub(DatabaseService.prototype, "credentialsAreValid").resolves(true);
            getAccountData = sinon.stub(DatabaseService.prototype, "getAccountData").resolves(accountData);
        });
        afterEach(() => {
            joinRoom.restore();
            credentialsAreValid.restore();
            logActivity.restore();
            getAccountData.restore();
        });

        it("should login the user if the user is not already logged in", async () => {
            await loginService.login(client, loginData).catch();

            sinon.assert.calledWith(credentialsAreValid, loginData.nickname, loginData.password);
            sinon.assert.calledWith(getAccountData, loginData.nickname);
            sinon.assert.calledWith(joinRoom, client, true);
            sinon.assert.calledOnce(logActivity);
            sinon.assert.calledWith(client.emit, SocketRoutes.login, getResponseState(ResponseNumber.success));
            sinon.assert.calledWith(client.broadcast.emit, SocketRoutes.new_user_connected, client["userId"]);
            expect( client["userId"] ).to.equal( loginData.nickname );
            expect( client["uuid"] ).to.equal( accountData["_id"] );
            expect( client["avatar"] ).to.equal( accountData["avatar"] );
        });

        it("should not login the user if the user is already logged in", async () => {
            client.server.sockets.sockets.key.userId = loginData.nickname; // user already logged in

            await loginService.login(client, loginData).catch();

            sinon.assert.notCalled(getAccountData);
            sinon.assert.notCalled(joinRoom);
            sinon.assert.notCalled(logActivity);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.login, getResponseState(ResponseNumber.already_connected));
            sinon.assert.notCalled(client.broadcast.emit);
        });

        it("should login the user if the user data are serialized", async () => {
            await loginService.login(client, JSON.stringify(loginData)).catch();

            sinon.assert.calledWith(getAccountData, loginData.nickname);
            sinon.assert.calledWith(joinRoom, client, true);
            sinon.assert.calledOnce(logActivity);
            sinon.assert.calledWith(client.emit, SocketRoutes.login, getResponseState(ResponseNumber.success));
            sinon.assert.calledWith(client.broadcast.emit, SocketRoutes.new_user_connected, client["userId"]);
            expect( client["userId"] ).to.equal( loginData.nickname );
            expect( client["uuid"] ).to.equal( accountData["_id"] );
            expect( client["avatar"] ).to.equal( accountData["avatar"] );
        });

        it("should return a negative response if user data is in a bad format", async () => {
            await loginService.login(client, "").catch();

            sinon.assert.notCalled(getAccountData);
            sinon.assert.notCalled(joinRoom);
            sinon.assert.notCalled(logActivity);
            sinon.assert.calledOnce(client.emit);
            sinon.assert.calledWith(client.emit, SocketRoutes.login, getResponseState(ResponseNumber.bad_format));
            sinon.assert.notCalled(client.broadcast.emit);
        });
    });

    describe("logout", () => {
        beforeEach(() => {
            client.disconnect = sinon.fake();
        });

        it("should disconnect the user and not log if the user is not connected", async () => {
            await loginService.logout(client, "").catch();

            sinon.assert.notCalled(logActivity);
            sinon.assert.calledWith(client.disconnect, true);
        });

        it("should log the user activity when disconnecting if the user is connected", async () => {
            client["userId"] = "userId";

            await loginService.logout(client, "").catch();

            sinon.assert.calledWith(logActivity, client["userId"], true);
            sinon.assert.calledWith(client.broadcast.emit, SocketRoutes.user_disconnected, client["userId"]);
            sinon.assert.calledWith(client.disconnect, true);
        });
    });
});
