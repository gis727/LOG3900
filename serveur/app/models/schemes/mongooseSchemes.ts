import * as mongoose from "mongoose";

// USER
const userPreviousGame: mongoose.Schema = new mongoose.Schema({
    date: Number,
    players: [String],
    won: Boolean,
    type: { type: Number, default: 0 },
    duration: { type: Number, default: 0 },
    score: { type: Number, default: 0 },
});
const userActivity: mongoose.Schema = new mongoose.Schema({
    connectionDate: Number,
    disconnectionDate: Number,
});
const userStats: mongoose.Schema = new mongoose.Schema({
    activity: { type: [userActivity], default: [] },
    previousGames:  { type: [userPreviousGame], default: [] },
});
const userSchema: mongoose.Model<mongoose.Document, {}> = mongoose.model("profiles", new mongoose.Schema({
    id: Number,
    // Private
    name: { type: String },
    surname: { type: String },
    password: { type: String },
    stats: userStats,
    // Public
    nickname: { type: String },
    avatar: { type: String },
}));

export { userSchema };

// GAME
const gameSchema: mongoose.Model<mongoose.Document, {}> = mongoose.model("games", new mongoose.Schema({
    id: Number,
    clues: { type: [String], default: [] },
    secretWord: { type: String },
    difficulty: { type: Number },
    drawingMode: { type: String, default: "" },
    drawingDirection: { type: String, default: "" },
    image: { type: Object },
}));

export { gameSchema };
