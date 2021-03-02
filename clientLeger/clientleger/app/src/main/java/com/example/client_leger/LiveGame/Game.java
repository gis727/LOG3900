package com.example.client_leger.LiveGame;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public String secretWord = "secret";
    public Difficulty difficulty;
    public DrawingMode drawingMode;
    public DrawingDirection drawingDirection;
    public SelectedDrawer selectedDrawer = SelectedDrawer.AVirtualPlayer;
    public GameImage image;

    public Game() { difficulty = Difficulty.Easy; drawingMode = DrawingMode.Classic; drawingDirection = DrawingDirection.LeftToRight; }
    public Game(String secretWord, Difficulty difficulty, DrawingMode drawingMode, DrawingDirection drawingDirection, SelectedDrawer selectedDrawer, GameImage image)
    {
        this.secretWord = secretWord;
        this.difficulty = difficulty;
        this.drawingMode = drawingMode;
        this.drawingDirection = drawingDirection;
        this.selectedDrawer = selectedDrawer;
        this.image = image;
    }

    public enum Difficulty
    {
        none,
        Easy,
        Medium,
        Hard,
        Creation,
    }
    public enum DrawingMode
    {
        Classic,
        Random,
        Panoramic,
        Centered,
        Instantaneous
    }
    public enum DrawingDirection
    {
        LeftToRight,
        RightToLeft,
        TopToBottom,
        BottomToTop,
        InOut,
        OutIn,
    }
    public enum SelectedDrawer {
        CurrentClient,
        AnotherClient,
        AVirtualPlayer
    }
}

