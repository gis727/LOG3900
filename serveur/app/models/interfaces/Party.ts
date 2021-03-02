import * as socketIo from "socket.io";

export class Party {

    public constructor( creator: socketIo.Socket,
                        difficulty: Difficulty,
                        mode: PartyMode,
                        roomId: string,
                        platform: Platform,
                        name: string) {
        this.id = roomId;
        this.name = (name === undefined) || (name === "") ? roomId : name;
        this.players = [{id: creator["userId"], avatar: creator["avatar"], isVirtual: false, ready: false} as Player];
        this.difficulty = difficulty;
        this.mode = mode;
        this.platform = platform;
        this.started = false;
    }

    private readonly MAX_SOLO_PLAYERS: number = 1;
    private readonly MAX_COOP_PLAYERS: number = 4;
    private readonly MAX_FFA_PLAYERS: number = 4;
    private readonly MIN_MULTI_PLAYERS_COUNT: number = 2;

    public id: string;
    public name: string;
    public players: Player[];
    public mode: PartyMode;
    public difficulty: Difficulty;
    public platform: Platform;
    public started: boolean;

    public static hasHumanPlayersLeft(party: Party): boolean {
        if (party !== undefined) {
            for (const player of party.players) {
                if (!player.isVirtual) { return true; }
            }
        }
        return false;
    }

    public join(client: socketIo.Socket): Player|undefined {
        const player: Player = {
            id: client["userId"],
            avatar: client["avatar"],
            isVirtual: false,
            partyId: this.id,
            ready: false,
        } as Player;
        const alreadyJoined: boolean = this.players.filter((plr: Player) => plr.id === client["userId"]).length !== 0;
        const canJoin: boolean = !alreadyJoined && !this.isFull();
        if (canJoin) { this.addPlayer(player); }
        return canJoin ? player : undefined;
    }

    public leave(client: socketIo.Socket): void {
        if (!this.isEmpty()) {
            let playerIndex: number = -1;
            this.players.forEach((player: Player, index: number) => {
                if (player.id === client["userId"]) { playerIndex = index; }
            });
            if (playerIndex >= 0) { this.players.splice(playerIndex, 1); }
        }
    }

    public addPlayer(player: Player): void {
        player.partyId = this.id;
        this.players.push(player);
    }

    public removePlayer(playerId: string): void {
        const index: number = this.players.findIndex((plr: Player) => plr.id === playerId );
        if (index > -1) { this.players.splice(index, 1); }
    }

    public isFull(): boolean {
        const capacity: number = this.getCapacity();
        return capacity !== -1 && this.players.length >= capacity;
    }

    public isReady(): boolean {
        let playersAreReady: boolean = true;
        this.players.forEach((player: Player) => playersAreReady = (playersAreReady && player.ready));
        if (this.mode === PartyMode.solo) {
            return playersAreReady && this.isFull();
        } else {
            return playersAreReady && this.players.length >= this.MIN_MULTI_PLAYERS_COUNT;
        }
    }

    public getCapacity(): number {
        if (this.mode === PartyMode.solo) {
            return this.MAX_SOLO_PLAYERS;
        } else if (this.mode === PartyMode.coop) {
            return this.MAX_COOP_PLAYERS;
        } else {
            return this.MAX_FFA_PLAYERS;
        }
    }

    // Party is empty when no HUMAN-PLAYER is left
    public isEmpty(): boolean {
        return (this.players.length === 0 || this.players.every((player: Player) => player.isVirtual));
    }
}

export class PartiesManager {
    private parties: Party[];

    public constructor() {
        this.parties = [];
    }

    public createParty(client: socketIo.Socket, data: PartyData, roomId: string): IParty {
        const party: Party = new Party(client, data.difficulty, data.mode, roomId, data.platform, data.name);
        this.parties.push(party);
        return this.getPartyData(party);
    }

    public getParty(partyId: string): Party|undefined {
        return this.parties.find((party: Party) => party.id === partyId);
    }

    public removeParty(party: Party): void {
        const index: number = this.parties.indexOf(party);
        if (index !== undefined) { this.parties.splice(index, 1); }
    }

    public getPartiesWithUser(userId: string): string[] {
        const parties: string[] = [];
        this.parties.forEach((party: Party) => {
            party.players.forEach((player: Player) => {
                if (player.id === userId) { parties.push(party.id); }
            });
        });
        return parties;
    }

    public getAllParties(_filter: PartyData|undefined): IParty[] {
        const parties: IParty[] = [];
        // set a default filter if none provided
        const filter: PartyData = (_filter === undefined) ? {
            difficulty: Difficulty.easy,
            mode: PartyMode.none,
            platform: Platform.all,
        } as PartyData : _filter;

        this.parties.forEach((party: Party) => {
            //const matchesDifficulty: boolean = (filter.difficulty === Difficulty.none) || (party.difficulty === filter.difficulty);
            const matchesMode: boolean = (filter.mode === PartyMode.none) || (party.mode === filter.mode);
            //const matchesPlatform: boolean = (filter.platform === Platform.all) || (party.platform === filter.platform);

            if (/*matchesDifficulty &&*/ matchesMode/* && matchesPlatform && !party.isFull()*/) {
                parties.push(this.getPartyData(party));
            }
        });
        return parties;
    }

    private getPartyData(party: Party): IParty {
        return {
            id: party.id,
            name: party.name,
            players: party.players,
            mode: party.mode,
            difficulty: party.difficulty,
            platform: party.platform,
            playersCount: party.players.length,
            playerCapacity: party.getCapacity(),
            started: party.started,
        } as IParty;
    }

    public setPlayerReady(client: socketIo.Socket): {readyState: boolean, partyIsReady: boolean, party: Party|undefined} {
        let newReadyState: boolean = false, userParty: Party|undefined, partyReady: boolean = true;
        this.getPartiesWithUser(client["userId"]).forEach((partyId: string) => {
            userParty = this.getParty(partyId);
            if (userParty !== undefined) {
                const player: Player|undefined = userParty.players.find((plr: Player) => plr.id === client["userId"]);
                if (player !== undefined) {
                    player.ready = !player.ready;
                    newReadyState = player.ready;
                }
                partyReady = userParty.isReady();
            } else {
                partyReady = false;
            }
        });
        return {readyState: newReadyState, partyIsReady: partyReady, party: userParty};
    }
}

enum Difficulty {
    none = 0,
    easy,
    medium,
    hard,
}

export enum PartyMode {
    none = 0,
    freeForAll,
    solo,
    coop,
}

enum Platform {
    all= 0,
    pc,
    android,
}

export interface Player {
    id: string;
    avatar: string;
    isVirtual: boolean;
    ready: boolean;
    partyId?: string;
}

export interface PartyData {
    difficulty: Difficulty;
    mode: PartyMode;
    platform: Platform;
    name: string;
}

export interface IParty {
    id: string;
    name: string;
    players: Player[];
    mode: PartyMode;
    difficulty: Difficulty;
    platform: Platform;
    playersCount: number;
    playerCapacity: number;
    started: boolean;
}
