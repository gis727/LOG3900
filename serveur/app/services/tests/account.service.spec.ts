// tslint:disable:no-any no-unused-expression
import { assert, expect } from "chai";
import * as sinon from "sinon";
import { container } from "../../inversify.config";
import { RoomNames } from "../../models/interfaces/Room";
import { getResponseState, AccountData, ResponseNumber } from "../../models/interfaces/user";
import { SocketRoutes } from "../../models/socketRoutes";
import Types from "../../types";
import { AccountService } from "../account.service";
import { DatabaseService } from "../database.service";
import { accounts } from "./mocks.spec";

xdescribe("AccountService", () => {
    const accountService: AccountService = container.get<AccountService>(Types.AccountService);
    const db: DatabaseService = container.get<DatabaseService>(Types.DatabaseService);
    let client: any;

    after( async () => {
        await clear();
    });
    const clear: Function = async (): Promise<void> => {
        // REMOVE ALL TEST ACCOUNTS AT ONCE
        for (const account of accounts) {
            await db.removeAccount(account.nickname);
        }
    };
    // D
    describe("createNewAccount", () => {
        beforeEach(() => {
            client = {};
            client.server = { sockets: {sockets : ""} };
            client.emit = sinon.fake();
            client.broadcast = { emit: sinon.fake() };
            client.join = sinon.fake();
        });

        // ORDERED !

        it("should create a new account", async () => {
            try {
                await accountService.createNewAccount(client, JSON.stringify(accounts[0]));

                // credentials saved
                expect( client["userId"] ).to.equal( accounts[0].nickname );
                expect( client["userId"] ).not.to.be.undefined;
                expect( (client["avatar"] as string).startsWith("http") ).to.be.true;
                // response sent
                sinon.assert.calledWith(client.emit, SocketRoutes.login, getResponseState(ResponseNumber.success));
                // other users advised
                sinon.assert.calledWith(client.broadcast.emit, SocketRoutes.new_user_connected, client["userId"]);
                // main room joined
                sinon.assert.calledWith(client.join, RoomNames.main);

            } catch (err) { console.error(err); await clear(); assert.fail(); }
        });

        it("should not create an account with an existing nickname", async () => {
            try {
                await accountService.createNewAccount(client, JSON.stringify(accounts[0]));

                // credentials not saved
                expect( client["userId"] ).to.be.undefined;
                expect( client["avatar"] ).to.be.undefined;
                expect( client["userId"] ).to.be.undefined;
                // response sent
                sinon.assert.calledWith(client.emit, SocketRoutes.account_creation, getResponseState(ResponseNumber.id_already_used));
                sinon.assert.notCalled(client.broadcast.emit);
                sinon.assert.notCalled(client.join);

            } catch (err) { console.error(err); await clear(); assert.fail(); }
        });
    });

    // D
    describe("getAccountData", () => {
        beforeEach(() => {
            client = {};
            client.server = { sockets: {sockets : ""} };
            client.emit = sinon.stub();
            client.broadcast = { emit: sinon.fake() };
            client.join = sinon.fake();
        });

        // ORDERED !

        it("should return all account data for the requiring user if no Id provided", async () => {
            try {
                client["userId"] = accounts[0].nickname; // client is connected

                await accountService.getAccountData(client, (undefined as any));

                // response sent
                sinon.assert.calledWith(client.emit, SocketRoutes.view_account);
                const account: any = JSON.parse((client.emit as sinon.SinonStub).getCall(0).args[1]);
                expect( account["name"] ).to.equal( accounts[0].name );
                expect( account["surname"] ).to.equal( accounts[0].surname );
                expect( account["nickname"] ).to.equal( accounts[0].nickname );
                expect( (account["avatar"] as string).startsWith("http") ).to.be.true;
                expect( (account["stats"]["activity"] as object[]).length ).to.equal(0);
            } catch (err) { console.error(err); await clear(); assert.fail(); }
        });

        it("should return all public account data if user Id is from another user", async () => {
            try {
                await accountService.createNewAccount(client, JSON.stringify(accounts[1])); // another user
                // reset client
                client = {userId: accounts[0].nickname, emit: sinon.stub()};

                await accountService.getAccountData(client, accounts[1].nickname);

                // response sent
                sinon.assert.calledWith(client.emit, SocketRoutes.view_account);
                const account: any = JSON.parse(client.emit.getCall(0).args[1]);
                expect( account["nickname"] ).to.equal( accounts[1].nickname );
                expect( (account["avatar"] as string).startsWith("http") ).to.be.true;
                expect( account["name"] ).to.be.undefined;
                expect( account["surname"] ).to.be.undefined;
                expect( (account["stats"] ) ).to.be.undefined;
            } catch (err) { console.error(err); await clear(); assert.fail(); }
        });
    });

    describe("editAccount", () => {
        before( async () => {
            await clear();
        });
        beforeEach(() => {
            client = {join: sinon.fake(), broadcast: { emit: sinon.fake() }, emit: sinon.stub(), server: { sockets: {sockets : ""} }};
        });

        // ORDERED

        it("should update all account data", async () => {
            try {
                const oldAccount: AccountData = accounts[0];
                await accountService.createNewAccount(client, JSON.stringify(oldAccount));
                const avatar: string = client["avatar"];
                client = {userId: oldAccount.nickname, avatar: avatar, emit: sinon.fake() };
                const newAccount: AccountData = accounts[1];
                await accountService.editAccount(client, JSON.stringify(newAccount));

                // response sent
                sinon.assert.calledWith(client.emit, SocketRoutes.modify_account, getResponseState(ResponseNumber.success));
                // account updated
                const account: any = await db.getAccountData(newAccount.nickname);
                expect( account["name"] ).to.equal( newAccount.name );
                expect( account["surname"] ).to.equal( newAccount.surname );
                expect( account["nickname"] ).to.equal( newAccount.nickname );
                expect( (account["avatar"] as string).startsWith("http") ).to.be.true;
                // socket updated
                expect( client["userId"] ).to.equal( newAccount.nickname );
                expect( client["avatar"] ).to.equal( account.avatar );
            } catch (err) { console.error(err); await clear(); assert.fail(); }
        });

        it("should not update if the username is taken", async () => {
            try {
                await accountService.createNewAccount(client, JSON.stringify(accounts[0])); // another account
                const oldAccount: any = await db.getAccountData(accounts[1].nickname);
                client = {userId: oldAccount.nickname, avatar: oldAccount.avatar, emit: sinon.fake() };

                await accountService.editAccount(client, JSON.stringify(accounts[0]));

                // response sent
                sinon.assert.calledOnce(client.emit);
                sinon.assert.calledWith(client.emit, SocketRoutes.modify_account, getResponseState(ResponseNumber.id_already_used));
                // account not updated
                const account: any = await db.getAccountData(oldAccount.nickname);
                expect( account["name"] ).to.equal( oldAccount.name );
                expect( account["surname"] ).to.equal( oldAccount.surname );
                expect( account["nickname"] ).to.equal( oldAccount.nickname );
                expect( (account["avatar"] as string).startsWith("http") ).to.be.true;
            } catch (err) { console.error(err); await clear(); assert.fail(); }
        });
    });
});
