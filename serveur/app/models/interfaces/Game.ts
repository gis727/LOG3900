
export interface GameData {
    id: number;
    clues: string[];
    secretWord: string;
    difficulty: Difficulty;
    drawingMode: DrawingMode;
    drawingDirection: DrawingDirection;
    selectedDrawer: SelectedDrawer;
    image: {paths: Path[]};
}

export enum Difficulty {
    none= 0,
    Easy,
    Medium,
    Hard,
}

export enum DrawingMode {
    Classic = 0,
    Random,
    Panoramic,
    Centered,
    Instantaneous,
}

enum DrawingDirection {
    LeftToRight = 0,
    RightToLeft,
    TopToBottom,
    BottomToTop,
    OutIn,
    InOut,
}

export enum SelectedDrawer {
    CurrentClient,
    AnotherClient,
    AVirtualPlayer,
}

interface Path {
    points: Point[];
    hexColor: string;
    stylusPoint: string; // Ellipse | Rectangle
    strokeWidth: number;
}

interface Point {
    x: number;
    y: number;
}

export const getGameTime: Function = (difficulty: Difficulty) => {
    // tslint:disable:one-line
    if (difficulty === Difficulty.Easy) { return 15; }
    else if (difficulty === Difficulty.Medium) { return 10; }
    else { return 5; }
};

export const getGameBonusTime: Function = (difficulty: Difficulty) => {
    return getGameTime(getGameTime(Difficulty.Easy + getGameTime(Difficulty.Hard)) - difficulty);
};
