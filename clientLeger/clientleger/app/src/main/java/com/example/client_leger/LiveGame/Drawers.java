package com.example.client_leger.LiveGame;

import com.example.client_leger.CanvasView;
import com.example.client_leger.LiveGame.DrawingAnimators.CenteredAnimator;
import com.example.client_leger.LiveGame.DrawingAnimators.ClassicAnimator;
import com.example.client_leger.LiveGame.DrawingAnimators.DrawingStrategy;
import com.example.client_leger.LiveGame.DrawingAnimators.PanoramicAnimator;

public class Drawers {
    // Animators
    private static final ClassicAnimator classicAnimator = new ClassicAnimator();
    private static final PanoramicAnimator panoramicAnimator = new PanoramicAnimator();
    private static final CenteredAnimator centeredAnimator = new CenteredAnimator();

    public void draw(final CanvasView canvasView, final Game game) {
        StopAllAnimationsAtOnce();
        DrawingStrategy strategy = new DrawingStrategy(game);

        if (strategy.game.drawingMode.equals(Game.DrawingMode.Panoramic)) drawInPanoramicMode(canvasView, strategy);
        else if (strategy.game.drawingMode.equals(Game.DrawingMode.Centered)) drawInCenteredMode(canvasView, strategy);
        else drawInOrder(canvasView, strategy);
    }

    private void drawInOrder(final CanvasView canvasView, final DrawingStrategy strategy) {
        classicAnimator.run(strategy, canvasView);
    }

    private void drawInPanoramicMode(final CanvasView canvasView, final DrawingStrategy strategy) {
        panoramicAnimator.run(strategy, canvasView);
    }

    private void drawInCenteredMode(final CanvasView canvasView, final DrawingStrategy strategy) {
        centeredAnimator.run(strategy, canvasView);
    }

    public static void StopAllAnimationsAtOnce() {
        classicAnimator.resetAllAnimations(false);
        panoramicAnimator.resetAllAnimations(false);
        centeredAnimator.resetAllAnimations(false);
    }
}
