package com.example.client_leger.LiveGame.DrawingAnimators;

import com.example.client_leger.LiveGame.Game;
import com.example.client_leger.LiveGame.GameImagePath;

public class DrawingStrategy {
    public Game game;
    int pointsToDraw;

     public DrawingStrategy(Game game){
        this.game = game;
        pointsToDraw = 0;
        for (GameImagePath path: game.image.paths) {
            pointsToDraw += path.points.size();
        }
    }

    long getTimeToDraw() {
        if (game.difficulty.equals(Game.Difficulty.Medium)) return 10000;
        else if (game.difficulty.equals(Game.Difficulty.Hard)) return 5000;
        else return 15000;
    }
}