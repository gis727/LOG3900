import { inject, injectable } from "inversify";
import "reflect-metadata";
import * as socketIo from "socket.io";
import { Deserializer } from "../Tools/deserializer";
import { IParty, PartiesManager, Party, PartyData, Player } from "../models/interfaces/Party";
import { VPManager } from "../models/interfaces/VirtualPlayer";
import { getResponseState, ResponseNumber } from "../models/interfaces/user";
import { SocketRoutes } from "../models/socketRoutes";
import Types from "../types";
import { ChatService } from "./chat.service";
import { LiveGameService } from "./liveGame.service";
/*
 * Handles all party activities
 */
@injectable()
export class PartyService {

    public constructor(@inject(Types.ChatService) public chatService: ChatService,
                       @inject(Types.LiveGameService) public liveGameService: LiveGameService) { }

    public static parties: PartiesManager = new PartiesManager();

    public static removeParty(server: socketIo.Server, partyId: string): void {
        const party: Party|undefined = PartyService.parties.getParty(partyId);
        if (party !== undefined) {
            PartyService.parties.removeParty(party);
            // advise everyone that the party has been removed
            server.emit(SocketRoutes.party_removed, party.id);
        }
    }

    public async addVirtualPlayer(client: socketIo.Socket, partyId: string): Promise<void> {
        const party: Party|undefined = PartyService.parties.getParty(partyId);
        if (party !== undefined && !party.isFull()) {
            const player: Player = VPManager.getNewPlayer(party);
            party.addPlayer(player);
            client.server.emit(SocketRoutes.player_joined, JSON.stringify(player));
            // if (party.isReady()) { await this.startParty(client, true, partyId); } // TODO: not needed according to the srs
        }
    }

    public removeVirtualPlayer(client: socketIo.Socket, clientData: string): void {
        const data: Player|undefined = Deserializer.deserializePlayerData(clientData);
        if (data !== undefined) {
            const party: Party|undefined = PartyService.parties.getParty(data.partyId as string);
            if (party !== undefined) {
                party.removePlayer(data.id);
                client.server.emit(SocketRoutes.player_left, JSON.stringify(data));
            }
        }
    }

    public joinParty(client: socketIo.Socket, partyId: string): void {
        const party: Party|undefined = PartyService.parties.getParty(partyId);
        if (party !== undefined) {
            // join the party
            const player: Player|undefined = party.join(client);
            // join the private room
            if (player !== undefined) {
                this.chatService.joinRoom(client, false, party.id);
                client.emit(SocketRoutes.join_party, getResponseState(ResponseNumber.success, partyId));
                // advise everyone
                client.server.emit(SocketRoutes.player_joined, JSON.stringify(player));
            }
        } else {
            client.emit(SocketRoutes.join_party, getResponseState(ResponseNumber.room_doesnt_exist, partyId));
        }
    }

    public async leaveParty(client: socketIo.Socket, partyId: string): Promise<void> {
        const party: Party|undefined = PartyService.parties.getParty(partyId);
        if (party !== undefined) {
            // leave the party
            party.leave(client);
            // delete the party if no one is left
            if (party.isEmpty()) {
                PartyService.parties.removeParty(party);
                // remove the chat room as well
                this.chatService.deleteGameRoom(client.server, party.id);
                // advise everyone that the party has been removed
                client.server.emit(SocketRoutes.party_removed, party.id);
            } else {
                // leave the private room
                this.chatService.deleteGameRoomForSingleClient(client, party.id);
                // advise everyone that a player left
                client.server.emit(SocketRoutes.player_left, JSON.stringify({"id": client["userId"], "partyId": party.id}));
                // try to launch the party if everyone is already ready
                await this.tryLaunchParty(client, party.id);
            }
            client.emit(SocketRoutes.leave_party, getResponseState(ResponseNumber.success, partyId));
        } else {
            client.emit(SocketRoutes.leave_party, getResponseState(ResponseNumber.room_doesnt_exist, partyId));
        }
    }

    public createParty(client: socketIo.Socket, clientData: string): void {
        const data: PartyData|undefined = Deserializer.deserializePartyData(clientData);
        if (data !== undefined) {
            // get a unique id for the game's room
            const roomId: string = client["uuid"] + "" + Date.now();
            // create the party
            const party: IParty = PartyService.parties.createParty(client, data, roomId);
            // create the private room
            this.chatService.createRoom(client, roomId, true);
            client.server.emit(SocketRoutes.new_party, JSON.stringify(party));
            client.emit(SocketRoutes.create_party, getResponseState(ResponseNumber.success, party.id));
        } else {
            client.emit(SocketRoutes.create_party, getResponseState(ResponseNumber.bad_format));
        }
    }

    public getAllParties(client: socketIo.Socket, clientData: string): void {
        const filter: PartyData|undefined = Deserializer.deserializePartyData(clientData);
        client.emit(SocketRoutes.get_all_parties, JSON.stringify(PartyService.parties.getAllParties(filter)));
    }

    public async startParty(client: socketIo.Socket, ignoreUpdate?: boolean, partyId?: string): Promise<void> {
        // TODO:another route (ready_to_play) should be used if enabling this)
        // client.emit(SocketRoutes.start_game, JSON.stringify(readyState));
        if (ignoreUpdate && partyId !== undefined) {
            const party: Party | undefined = PartyService.parties.getParty(partyId);
            if (party !== undefined) { await this.liveGameService.startParty(party, client); }
        } else {
            const {party} = PartyService.parties.setPlayerReady(client);
            if (party !== undefined) {
                await this.tryLaunchParty(client, party.id);
            }
        }
    }

    private async tryLaunchParty(client: socketIo.Socket, partyId: string): Promise<void> {
        const party: Party | undefined = PartyService.parties.getParty(partyId);
        if (party !== undefined && party.isReady()) {
            party.started = true;
            client.server.emit(SocketRoutes.party_started, party.id);
            await this.liveGameService.startParty(party, client);
        }
    }

    public async clearPartiesForClient(client: socketIo.Socket): Promise<void> {
        for (const partyId of PartyService.parties.getPartiesWithUser(client["userId"])) {
            await this.leaveParty(client, partyId);
        }
    }
}
