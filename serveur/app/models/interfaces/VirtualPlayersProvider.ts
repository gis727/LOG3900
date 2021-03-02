import { Player } from "./Party";

export enum BotsIds {
    GoodBot = "Nice bot",
    BadBot = "Bad bot",
    NeutralBot = "Neutral Bot",
}

export class VPProvider {
    // Bots
    public static BOTS: Player[] = [
    {
        id: BotsIds.GoodBot,
        avatar: "https://blog.lateral.io/wp-content/uploads/2015/06/bot.png",
        isVirtual: true,
        ready: true,
        partyId: "",
    },
    {
        id: BotsIds.BadBot,
        avatar: "https://pbs.twimg.com/profile_images/1141699653706166273/FAwMrlFP_400x400.png",
        isVirtual: true,
        ready: true,
        partyId: "",
    },
    {
        id: BotsIds.NeutralBot,
        avatar: "https://thumbs.dreamstime.com/z/ai-robot-head-chat-bot-icon-isolated-white-background-ai-robot-head-chat-bot-icon-109860127.jpg",
        isVirtual: true,
        ready: true,
        partyId: "",
    }];

    // Bots messages
    public getClueMessage(botId: string, clue: string | undefined, userId?: string): string {
        const prefix: string = "@" + userId + " ";
        switch (botId) {
            case BotsIds.BadBot:
                if (clue === undefined) {
                    return (userId ? prefix : "You ") + "still can't find it ? Shame on you !";
                } else {
                    return "I would find it without knowing this: " + clue;
                }
                break;

            case BotsIds.GoodBot:
                if (clue === undefined) {
                    return "No more clues :(. But i know you can find it !";
                } else {
                    return (userId ? prefix : "") + "Here is a clue just for you: " + clue;
                }
                break;

            default:
                if (clue === undefined) {
                    return "No more clues left.";
                } else {
                    return (userId ? prefix : "") + "Here is a clue: " + clue;
                }
                break;
        }
    }

    public getNewPartyMessage(botId: string): string {
        switch (botId) {
            case BotsIds.BadBot:
                return "Here we go with another boring party.";
                break;

            case BotsIds.GoodBot:
                return "It's playtime !";
                break;

            default:
                return "Let's get started.";
                break;
        }
    }

    public getDirectedNewPartyMessage(botId: string, userId: string, lastScore?: number): string {
        const prefix: string = "@" + userId + " ";
        switch (botId) {
            case BotsIds.BadBot:
                return prefix + (lastScore !== undefined ? "I don't think you can do better than " + lastScore + "! Thats what you did last time i saw you." : "You're a newbie !");
                break;

            default:
                return prefix + (lastScore !== undefined ? "You scored " + lastScore + " at our last party together. Hope you do better this time :)." : "Nice to meet you !");
                break;
        }
    }

    public getPartyEndMessage(botId: string): string {
        switch (botId) {
            case BotsIds.BadBot:
                return "Get a life !";
                break;

            default:
                return "I had a nice time with y'all ! Let's do this again !";
                break;
        }
    }

    public getAnswerMessage(botId: string, wasCorrectAnswer: boolean, userId: string, answer: string, secret: string, hideAnswer: boolean, hideId?: boolean): string {
        const prefix: string = ((!hideId) ? "@" + userId + " " : "");
        switch (botId) {
            case BotsIds.BadBot:
                if (wasCorrectAnswer) { return prefix + (hideAnswer ? "Ok" : "It was \"" + secret + "\". I'll accept your answer."); } else {
                    return prefix + "Why are you even trying ?";
                }
                break;

            case BotsIds.GoodBot:
                if (wasCorrectAnswer) { return prefix + "Wonderful !" + (hideAnswer ? "" : " The answer was : \"" + secret + "\" indeed !"); } else {
                    return prefix + (hideAnswer ? "You're close ! Keep trying." : "Its not \"" + answer + "\". But you're close ! Keep trying.");
                }
                break;

            default:
                if (wasCorrectAnswer) { return prefix + "Well done !" + (hideAnswer ? "" : " The answer was : \"" + secret + "\" indeed !"); } else {
                    return prefix + (hideAnswer ? "Nope." : "Its not \"" + answer + "\".");
                }
                break;
        }
    }

    public getNewGameMessage(botId: string): string {
        switch (botId) {
            case BotsIds.BadBot:
                return "Here comes another round. Can we stop ?";
                break;

            case BotsIds.GoodBot:
                return "A new image == a new chance to shine !";
                break;

            default:
                return "A new round starts now !";
                break;
        }
    }

    public getGameEndMessage(answer: string): string {
        return "It was '" + answer + "'.";
    }

    public getKickMessage(botId: string, culpritId: string): string {
        switch (botId) {
            case BotsIds.BadBot:
                return culpritId + " got kicked !!! lol";
                break;

            case BotsIds.GoodBot:
                return culpritId + " is not in the party anymore :(";
                break;

            default:
                return culpritId + " has been removed from the party.";
                break;
        }
    }
}

export interface KnownPlayerStat {
    playerId: string;
    longestPartyDuration: number;
    shortestPartyDuration: number;
    bestScore: number;
    wonGamesCount: number;
    scoreForPartyPlayedTogether: number[];
    // lastPartyDate?: Date;
    // playedMoreThan5PartiesToday: boolean;
}
export interface BotStats {
    botId: string;
    players: KnownPlayerStat[];
}
