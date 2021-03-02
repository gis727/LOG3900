import * as socketIo from "socket.io";
import { DatabaseService } from "../../services/database.service";
import { GameStats } from "./GameStats";
import { Party, PartyMode, Player } from "./Party";

const GAMES_TO_FETCH: number = 1000;

export class GameIds {
    // Drawers
    public passedDrawers: string[] = [];

    // Games
    private played: string[] = [];
    private toPlay: string[];

    public async load(db: DatabaseService): Promise<void> {
        this.toPlay = await db.getGames(GAMES_TO_FETCH, this.played);
        this.played = this.played.concat(this.toPlay);
    }

    public async getNextGameId(db: DatabaseService): Promise<string> {
        // fetch data from db
        if (this.toPlay.length === 0) {
           await this.load(db);
        }
        // second try (drop played games)
        if (this.toPlay.length === 0) {
            this.played = [];
            await this.load(db);
        }
        const randomIdIndex: number = Math.floor(Math.random() * this.toPlay.length);
        const nextGameId: string = this.toPlay[randomIdIndex];
        this.toPlay.splice(randomIdIndex, 1);
        return nextGameId;
    }

    public chooseDrawer(client: socketIo.Socket, party: Party): socketIo.Socket | undefined {
        if (client !== undefined) {
            // tslint:disable-next-line:forin
            const room: socketIo.Room = client.server.sockets.adapter.rooms[party.id];
            if (room !==  undefined) {
                for (const player of party.players) {
                    if (this.passedDrawers.length >= party.players.length) { // All players have drawn
                        this.passedDrawers = [];
                    }
                    if (!this.passedDrawers.includes(player.id)) {
                        this.passedDrawers.push(player.id);

                        if (player.isVirtual) {
                            return undefined;
                        } else {
                            // tslint:disable-next-line:forin
                            for (const clientSocketId in room.sockets) {
                                const _client: socketIo.Socket = client.server.sockets.connected[clientSocketId];
                                if (_client["userId"] === player.id) { return _client; }
                            }
                        }
                    }
                }
            }
        }

        return client;
    }

    public clearFromDrawers(kickedPlayerId: string): void {
        const index: number = this.passedDrawers.indexOf(kickedPlayerId);
        if (index >= 0) {
            this.passedDrawers.splice(index, 1);
        }
    }

    public drawerIsVirtual(party: Party): boolean {
        const drawerId: string = this.getDrawerId();
        let isVirtual: boolean = false;
        party.players.forEach((player: Player) => {
            if (player.id === drawerId && player.isVirtual) { isVirtual = true; }
        });
        return isVirtual;
    }

    public getDrawerId(): string {
        return this.passedDrawers.length > 0 ? this.passedDrawers[this.passedDrawers.length - 1] : "";
    }

    public async updateProfile(party: Party, stats: GameStats, db: DatabaseService): Promise<void> {
        // Get all players ids
        const players: string[] = [];
        party.players.forEach((player: Player) => {
            players.push(player.id);
        });
        // Update profiles for each player
        for (const playerStat of stats.playersStats) {
            if (party.mode === PartyMode.freeForAll) {
                await db.logGameStats(
                    playerStat.playerId,
                    players,
                    playerStat.isWinner,
                    party.mode,
                    stats.totalDuration,
                    playerStat.score);
            } else { // coop and solo
                await db.logGameStats(
                    playerStat.playerId,
                    players,
                    true, /*TODO: when are you a winner in solo and coop?*/
                    party.mode,
                    stats.totalDuration,
                    stats.score);
            }
        }
    }
}
