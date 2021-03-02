// REQUESTS
export interface UserLoginData {
    nickname: string;
    password: string;
}

export interface AccountData {
    name: string;
    surname: string;
    password: string;
    nickname: string;
    avatar: string;
}

export interface PreviousGame {
    date: Date;
    players: string[];
    won: boolean;
    type: number;
    duration: number;
    score: number;
}
interface Stats {
    previousGames: PreviousGame[];
}
export interface IAccountData {
    nickname: string;
    stats: Stats;
}

export interface MessageData {
    roomId: string;
    message: string;
}

// RESPONSES
export const getResponseState: Function = (responseNumber: ResponseNumber, extra?: string): string => {
    if (extra) { return JSON.stringify({state: responseNumber, roomId: extra}); }
    return JSON.stringify({state: responseNumber} as Response);
};
export interface Response {
    state: number;
}
export enum ResponseNumber {
    server_error = -2,

    // General
    bad_format = -1,
    success = 0,

    // id usage
    id_already_used = 1,

    // login
    already_connected = 2,
    wrong_credential = 3,

    // chat rooms
    room_doesnt_exist = 4,
}
