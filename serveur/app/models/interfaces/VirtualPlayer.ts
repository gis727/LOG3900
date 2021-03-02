import * as socketIo from "socket.io";
import { getRandomIndexInArray } from "../../Tools/Array";
import { ChatService } from "../../services/chat.service";
import { LiveGame } from "./LiveGame";
import { Party, PartyMode, Player } from "./Party";
import { BotsIds, BotStats, KnownPlayerStat, VPProvider } from "./VirtualPlayersProvider";
export { BotStats, KnownPlayerStat };

export class VPManager {

    private chatService: ChatService;
    private provider: VPProvider = new VPProvider();

    public constructor(chatService: ChatService) {
        this.chatService = chatService;
    }

    public static getNewPlayer(party: Party): Player {
        // get the current vps in the party
        const currentVPsInParty: string[] = this.getVPsInParty(party);

        // find an index for the new one if (excluding the previous one)
        const botsCount: number = 2;
        let randIndex: number = getRandomIndexInArray(VPProvider.BOTS, botsCount);
        if (currentVPsInParty.includes(VPProvider.BOTS[randIndex].id)) { randIndex = (botsCount - 1) - randIndex; }
        return VPProvider.BOTS[randIndex];
    }

    private static getVPsInParty(party: Party): string[] {
        const currentVPsInParty: string[] = [];
        party.players.forEach((player: Player) => {
            if (player.isVirtual) { currentVPsInParty.push(player.id); }
        });
        return currentVPsInParty;
    }

    private getReactionVp(liveGame: LiveGame): Player | undefined {
        const vpsInParty: string[] = VPManager.getVPsInParty(liveGame.party);
        if (vpsInParty.length > 0) {
            const randIndex: number = getRandomIndexInArray(vpsInParty);
            const botId: string = vpsInParty[randIndex];
            return VPProvider.BOTS.find((bot: Player) => bot.id === botId) as Player;
        } else { // No virtual player in the party
            return VPProvider.BOTS[VPProvider.BOTS.length - 1]; // return the neutral bot
        }
    }

    public async sendAClue(client: socketIo.Socket, liveGame: LiveGame): Promise<void> {
        const clue: string | undefined = liveGame.currentGame.clues.pop();
        // get a vp
        let vp: Player | undefined = this.getReactionVp(liveGame);
        if (vp === undefined) { vp = VPProvider.BOTS[2]; }

        if (liveGame.party.mode === PartyMode.freeForAll) {
            this.chatService.sendSystemMessage(
                client.server, liveGame.party.id, vp.id, vp.avatar, this.provider.getClueMessage(vp.id, clue, client["userId"]),
            );
        } else {
            if (clue !== undefined) {
                liveGame.stats.score -= 0.1 * liveGame.stats.score;
                liveGame.stats.score = liveGame.stats.score < 0? 0: liveGame.stats.score;
                liveGame.stats.score = Math.ceil(liveGame.stats.score);
            }
            this.chatService.sendSystemMessage(
                client.server, liveGame.party.id, vp.id, vp.avatar, this.provider.getClueMessage(vp.id, clue),
            );
        }
    }

    public reactOnPartyStart(liveGame: LiveGame): void {
        const vp: Player | undefined = this.getReactionVp(liveGame);
        if (vp !== undefined) {
            this.chatService.sendSystemMessage(liveGame.ioServer, liveGame.party.id, vp.id, vp.avatar, this.provider.getNewPartyMessage(vp.id));
            if (liveGame.party.mode === PartyMode.freeForAll && liveGame.botStats.length > 0 && vp.id !== BotsIds.NeutralBot) {
                liveGame.botStats[0].players.forEach((player: KnownPlayerStat) => {
                    this.chatService.sendSystemMessage(
                        liveGame.ioServer, liveGame.party.id, vp.id, vp.avatar,
                        this.provider.getDirectedNewPartyMessage(vp.id, player.playerId,
                                                                 player.scoreForPartyPlayedTogether.length === 0 ? undefined :
                            player.scoreForPartyPlayedTogether[player.scoreForPartyPlayedTogether.length - 1]));
                });
            }
        }
    }

    public reactOnPartyEnd(liveGame: LiveGame): void {
        const vp: Player | undefined = this.getReactionVp(liveGame);
        if (vp !== undefined && vp.id !== BotsIds.NeutralBot) {
            this.chatService.sendSystemMessage(liveGame.ioServer, liveGame.party.id, vp.id, vp.avatar, this.provider.getPartyEndMessage(vp.id));
        }
    }

    public reactOnAnswer(client: socketIo.Socket, liveGame: LiveGame, wasCorrectAnswer: boolean, answer: string): void {
        let vp: Player | undefined = this.getReactionVp(liveGame);
        if (vp === undefined) { vp = VPProvider.BOTS[2]; }

        if (liveGame.party.mode === PartyMode.freeForAll) {
            this.chatService.sendSystemMessage(
                client.server,
                liveGame.party.id,
                vp.id,
                vp.avatar,
                this.provider.getAnswerMessage(vp.id, wasCorrectAnswer, client["userId"], answer, liveGame.currentGame.secretWord, true),
            );
        } else {
            this.chatService.sendSystemMessage(
                client.server,
                liveGame.party.id,
                vp.id,
                vp.avatar,
                this.provider.getAnswerMessage(vp.id, wasCorrectAnswer, client["userId"], answer, liveGame.currentGame.secretWord, false),
            );
        }
    }

    public reactOnNewGame(liveGame: LiveGame): void {
        const vp: Player | undefined = this.getReactionVp(liveGame);
        if (vp !== undefined) {
            this.chatService.sendSystemMessage(liveGame.ioServer, liveGame.party.id, vp.id, vp.avatar, this.provider.getNewGameMessage(vp.id));
        }
    }

    public reactOnGameEnd(liveGame: LiveGame): void {
        const vp: Player | undefined = this.getReactionVp(liveGame);
        if (vp !== undefined) {
            this.chatService.sendSystemMessage(liveGame.ioServer, liveGame.party.id, vp.id, vp.avatar,
                                               this.provider.getGameEndMessage(liveGame.currentGame.secretWord));
        }
    }

    public reactOnPlayerKicked(liveGame: LiveGame, culpritId: string): void {
        const vp: Player | undefined = this.getReactionVp(liveGame);
        if (vp !== undefined) {
            this.chatService.sendSystemMessage(liveGame.ioServer, liveGame.party.id, vp.id, vp.avatar,
                                               this.provider.getKickMessage(vp.id, culpritId));
        }
    }
}
