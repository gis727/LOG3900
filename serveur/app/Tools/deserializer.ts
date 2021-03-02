import { GameData } from "../models/interfaces/Game";
import { PartyData, Player } from "../models/interfaces/Party";
import { AccountData, MessageData, UserLoginData } from "../models/interfaces/user";

/*
 * Checks the validity of requests fields and returns the serialized objects
 */
export class Deserializer {

    public static deserializeNewAccountData(clientData: string): AccountData | undefined {
        try {
            const data: AccountData = JSON.parse(clientData);
            return (data !== undefined
                && Deserializer.dataIsOk(data.name)
                && Deserializer.dataIsOk(data.surname)
                && Deserializer.passwordIsOk(data.password)
                && Deserializer.dataIsOk(data.nickname)
                && Deserializer.dataIsOk(data.avatar)) ? data : undefined;
        } catch (err) {
            return undefined;
        }
    }

    public static deserializeLoginData(clientData: string): UserLoginData | undefined {
        try {
            const data: UserLoginData = JSON.parse(clientData);
            return (data !== undefined
                && Deserializer.passwordIsOk(data.password)
                && Deserializer.dataIsOk(data.nickname)) ? data : undefined;
        } catch (err) {
            return undefined;
        }
    }

    public static deserializeMessageData(clientData: string): MessageData | undefined {
        try {
            const data: MessageData = JSON.parse(clientData);
            return (data !== undefined
                && Deserializer.dataIsOk(data.roomId)
                && Deserializer.dataIsOk(data.message)) ? data : undefined;
        } catch (err) {
            return undefined;
        }
    }

    public static deserializeGameCreationData(clientData: string): GameData | undefined {
        try {
            const data: GameData = JSON.parse(clientData);
            return (data !== undefined
                && Deserializer.arrayDataIsOk(data.clues)
                && Deserializer.dataIsOk(data.secretWord)
                && data.difficulty >= 0
                && Deserializer.basicDataIsOk(data.drawingMode)
                && Deserializer.basicDataIsOk(data.drawingDirection)
                && Deserializer.basicDataIsOk(data.image)) ? data : undefined;
        } catch (err) {
            return undefined;
        }
    }

    public static deserializePartyData(clientData: string): PartyData | undefined {
        try {
            const data: PartyData = JSON.parse(clientData);
            return (data !== undefined
                && Deserializer.basicDataIsOk(data.difficulty)
                && Deserializer.basicDataIsOk(data.platform)
                && Deserializer.basicDataIsOk(data.mode)) ? data : undefined;
        } catch (err) {
            return undefined;
        }
    }

    public static deserializeAvatarData(clientData: string): string | undefined {
        try {
            return (clientData !== undefined
                && Deserializer.dataIsOk(clientData)) ? clientData : undefined;
        } catch (err) {
            return undefined;
        }
    }

    public static deserializePlayerData(clientData: string): Player | undefined {
        try {
            const data: Player = JSON.parse(clientData);
            return (data !== undefined
                && Deserializer.passwordIsOk(data.partyId)
                && Deserializer.dataIsOk(data.id)) ? data : undefined;
        } catch (err) {
            return undefined;
        }
    }

    private static passwordIsOk(password: string | undefined): boolean {
        return password !== undefined && password !== ""; // Are we gonna go harder on this
    }

    // tslint:disable-next-line:no-any
    private static basicDataIsOk(data: any): boolean {
        return data !== undefined;
    }

    private static dataIsOk(data: string): boolean {
        return data !== undefined && data !== "";
    }

    private static arrayDataIsOk(data: string[]): boolean {
        return data !== undefined && data.length > 0;
    }
}
