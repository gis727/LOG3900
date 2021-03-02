package com.example.client_leger.LiveGame.DrawingAnimators;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.example.client_leger.CanvasView;
import com.example.client_leger.LiveGame.GameImage;
import com.example.client_leger.LiveGame.GameImagePath;
import com.example.client_leger.Utilities.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains everything used by the Drawing animators for all modes (panoramic, centered, classic)
 */
public abstract class DrawingAnimator {

    /**
     * TO OVERRIDE
     */
    public void run(final DrawingStrategy strategy, final CanvasView canvas) {
        canvas.post(() -> {
            strategy.game.image.centerImage(canvas.drawCanvas);
            resetAllAnimations(true);
            buildAnimations(strategy, canvas);
            playAnimations(canvas);
        });
    }

    /**
     * TO OVERRIDE
     */
    public void resetAllAnimations(final boolean checkIfReadyToRun) { }

    /**
     * TO OVERRIDE
     */
    protected void playAnimations(final CanvasView canvas) { }

    /**
     * TO OVERRIDE
     */
    protected void buildAnimations(final DrawingStrategy strategy, final CanvasView canvas) { }

    /**
     * CLASS TO OVERRIDE
     * Holds all data required for the animations
     * For Panoramic or CENTERED, we store the curtains here.
     */
    protected static abstract class CustomAnimationData {
        DrawingStrategy strategy;
        CanvasView canvasView;

        int animationIndex = 0;
        final List<Animator> animators = new ArrayList<>(); // Contains all animations
        AnimatorSet animatorsSet = new AnimatorSet(); // To run all animations in sequence

        protected void reset(final boolean checkIfReadyToRun) {
            if (animatorsSet.isStarted()) {
                animatorsSet.cancel();
            }
            animatorsSet = new AnimatorSet();
            animationIndex = 0;
            if (checkIfReadyToRun && strategy == null || canvasView == null) throw new IllegalStateException("AnimationData is not ready for the run because some data are missing! Duh.");
        }

        void setUp(DrawingStrategy strategy, CanvasView canvasView) {
            this.strategy = strategy;
            this.canvasView = canvasView;
        }

        void resetAnimators() {
            animationIndex = 0;
            animators.clear();
        }

        protected void start() {
            animationIndex = 0;
            animatorsSet.playSequentially(animators);
            animatorsSet.start();
        }

        boolean hasAnimationToRun() {
            return animators.size() > 0;
        }

        void addAnimation(Animator animation) {
            animators.add(animation);
            animationIndex++;
        }

        boolean hasAnimationsLeft() {
            return animationIndex < animators.size()-1;
        }

        void drawImageInstantaneously(final CanvasView canvasView, final GameImage image) {
            canvasView.clear();
            for(GameImagePath path: image.paths) { // DRAW EACH PATH
                if (!path.points.isEmpty()) {
                    drawSinglePath(canvasView.drawCanvas, path);
                    canvasView.invalidate();
                }
            }
        }

        void drawSinglePath(final Canvas canvas, final GameImagePath path) {
            if (canvas == null) return;
            // Get the paint
            Paint paint = new Paint();
            paint.setColor(Converter.StringToColor(path.hexColor));
            paint.setAntiAlias(true);
            paint.setStrokeWidth((float) path.strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            if (path.points.size() == 1) { // Only one point to draw
                canvas.drawPoint(path.points.get(0).x, path.points.get(0).y, paint);
                return;
            }
            Path pathToDraw = new Path();
            // Move to the first point
            pathToDraw.moveTo(path.points.get(0).x, path.points.get(0).y);
            short index = 0; // to ignore the first point in the loop
            for(PointF point: path.points) { // DRAW EACH POINT
                if (index !=0 ) pathToDraw.lineTo(point.x, point.y);
                index++;
            }
            // Draw the path
            canvas.drawPath(pathToDraw, paint);
        }
    }
}
