// tslint:disable:max-line-length
import * as bcrypt from "bcrypt";
import * as cloudinary from "cloudinary";
import { injectable } from "inversify";
import * as mongoose from "mongoose";
import "reflect-metadata";
import * as socketIo from "socket.io";
import { dbParams } from "../models/dbParams";
import { GameData } from "../models/interfaces/Game";
import { AccountData } from "../models/interfaces/user";
import { gameSchema, userSchema } from "../models/schemes/mongooseSchemes";

cloudinary.v2.config({cloud_name: "htikdzzd5", api_key: "488854435147961", api_secret: "t0Nv-eEo2x1ZzgpMgOCssiIxsBA"});
const mongoParams: mongoose.ConnectionOptions = {
    useNewUrlParser : true,
    useUnifiedTopology: true,
    useCreateIndex: true,
} as mongoose.ConnectionOptions;
mongoose.set("useFindAndModify", false);

@injectable()
export class DatabaseService {

    // ACCOUNT

    public async addNewAccount(accountData: AccountData): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            const pass: string = await bcrypt.hash(accountData.password, 1);
            // tslint:disable-next-line:no-any
            const res: any = await cloudinary.v2.uploader.upload(accountData.avatar, { width: 100, height: 100, format: "png"});
            await userSchema.create({
                    name: accountData.name,
                    surname: accountData.surname,
                    password: pass,
                    nickname: accountData.nickname,
                    avatar: res["secure_url"],
                    stats: {
                        activity: [],
                        previousGames: [],
                    },
            });
            await mongoose.disconnect();
            return true;
        } catch (err) {
            await mongoose.disconnect();
            return false;
        }
    }

    public async removeAccount(_nickname: string): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            await userSchema.findOneAndDelete({nickname: _nickname});
            // tslint:disable-next-line:await-promise
            const data: {} = (await userSchema.find({nickname: _nickname}))[0];
            await mongoose.disconnect();
            if (data !== undefined ) { await cloudinary.v2.uploader.destroy(this.getPublicId(data["avatar"])); }
            return true;
        } catch (err) {
            await mongoose.disconnect();
            return false;
        }
    }

    public async modifyAccount(accountData: AccountData, client: socketIo.Socket): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            const newAvatar: string = await this.getNewAvatar(client["avatar"], accountData.avatar);
            const pass: string = await bcrypt.hash(accountData.password, 1);
            // tslint:disable-next-line:await-promise
            await userSchema.findOneAndUpdate({nickname: client["userId"]},
                                              {$set:
                    {
                        name: accountData.name,
                        surname: accountData.surname,
                        password: pass,
                        avatar: newAvatar,
                        nickname: accountData.nickname,
                    },
                },
            );
            await mongoose.disconnect();
            client["userId"] = accountData.nickname;
            client["avatar"] = newAvatar;
            return true;
        } catch (err) {
            await mongoose.disconnect();
            return false;
        }
    }

    public async modifyAvatar(_nickname: string, currentAvatar: string, newAvatarData: string): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            const newAvatar: string = await this.getNewAvatar(currentAvatar, newAvatarData);
            // tslint:disable-next-line:await-promise
            await userSchema.findOneAndUpdate({nickname: _nickname},
                                              {$set:
                    {avatar: newAvatar},
                },
            );
            await mongoose.disconnect();
            return true;
        } catch (err) {
            await mongoose.disconnect();
            return false;
        }
    }

    private getPublicId(avatar: string): string {
        const a: string[] = avatar.split("/");
        return (a[a.length - 1].split("."))[0];
    }

    private async getNewAvatar(currentAvatar: string, newAvatarData: string): Promise<string> {
        if (newAvatarData !== "" && (currentAvatar !== newAvatarData)) {
            // Delete old avatar
            await cloudinary.v2.uploader.add_tag("garbage", [this.getPublicId(currentAvatar)]);
            // Update with new avatar
            return (await cloudinary.v2.uploader.upload(newAvatarData, { width: 100, height: 100, format: "png"}))["secure_url"];
        } else { return currentAvatar; }
    }

    public async logActivity(_nickname: string, disconnecting?: boolean): Promise<void> {
        const MAX_ACTIVITY_LOG: number = 50;
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:no-any await-promise
            const account: any = (await userSchema.find({nickname: _nickname}))[0];
            if (disconnecting) {
                account.stats.activity[account.stats.activity.length - 1].disconnectionDate = Date.now();
                // tslint:disable-next-line:await-promise
                await userSchema.findOneAndUpdate({nickname: _nickname},
                                                  {$set:
                    { "stats.activity" : account.stats.activity },
                });
            } else {
            if (account.stats.activity.length >= MAX_ACTIVITY_LOG) {
                // tslint:disable-next-line:no-any await-promise
                await userSchema.findOneAndUpdate({nickname: _nickname},
                                                  {$pull:
                    { "stats.activity" : {"_id": account.stats.activity[0]["_id"] }},
                });
            }
            // tslint:disable-next-line:await-promise
            await userSchema.findOneAndUpdate({nickname: _nickname},
                                              {$push:
                    { "stats.activity" : {connectionDate: Date.now(), disconnectionDate: Date.now()}},
                });
            }
            await mongoose.disconnect();
        } catch (err) {
            await mongoose.disconnect();
        }
    }

    public async logGameStats(_nickname: string, _players: string[], _won: boolean, _type: number, _duration: number, _score: number): Promise<void> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            await userSchema.findOneAndUpdate({nickname: _nickname},
                                              {$push:
                { "stats.previousGames" : {date: Date.now(), players: _players, won: _won, type: _type, duration: _duration, score: _score}},
            });
            await mongoose.disconnect();
        } catch (err) {
            await mongoose.disconnect();
        }
    }

    public async getAccountData(_nickname: string, clean?: boolean): Promise<{}> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            const data: {} = (await userSchema.find({nickname: _nickname}))[0];
            data["password"] = undefined;
            data["stats"]["activity"].pop();
            if (clean) {
                data["avatar"] = undefined;
                data["stats"]["activity"] = undefined;
            }
            await mongoose.disconnect();
            return data;
        } catch (err) {
            await mongoose.disconnect();
            return {};
        }
    }

    public async credentialsAreValid(_nickname: string, password: string): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            const encryptedPass: string = (await userSchema.find({nickname: _nickname}))[0]["password"];
            await mongoose.disconnect();
            return await bcrypt.compare(password, encryptedPass);
        } catch (err) {
            await mongoose.disconnect();
            return false;
        }
    }

    public async nicknameIsTaken(_nickname: string): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            const nicknameIsTaken: boolean = (await userSchema.find({nickname: _nickname})).length > 0;
            await mongoose.disconnect();
            return nicknameIsTaken;
        } catch (err) {
            await mongoose.disconnect();
            return true; // Not a big deal if the name is not really taken
        }
    }

    // GAME

    public async addGame(gameData: GameData): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            await gameSchema.create({
                clues: gameData.clues,
                secretWord: gameData.secretWord,
                difficulty: gameData.difficulty,
                drawingMode: gameData.drawingMode,
                drawingDirection: gameData.drawingDirection,
                image: gameData.image,
            });
            await mongoose.disconnect();
            return true;
        } catch (err) {
            await mongoose.disconnect();
            return false;
        }
    }

    public async removeGame(gameId: string): Promise<boolean> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            await gameSchema.findOneAndDelete({_id: gameId});
            await mongoose.disconnect();
            return true;
        } catch (err) {
            await mongoose.disconnect();
            return false;
        }
    }

    public async getGames(amountOfGames: number, excludedIds?: string[]): Promise<string[]> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            const data: string[] = (await gameSchema.find({ _id: { $not: { $in: excludedIds ? excludedIds : [] }}}, {_id: 1}).limit(amountOfGames))
            .map((id: {}) => id["_id"]);
            await mongoose.disconnect();
            return data;
        } catch (err) {
            await mongoose.disconnect();
            return [];
        }
    }

    public async getGame(gameId: string): Promise<{}> {
        try {
            await mongoose.connect(dbParams.URL, mongoParams);
            // tslint:disable-next-line:await-promise
            const data: {} = (await gameSchema.find({ _id: { $eq: gameId}}).lean())[0];
            await mongoose.disconnect();
            return data;
        } catch (err) {
            await mongoose.disconnect();
            return {};
        }
    }
}
