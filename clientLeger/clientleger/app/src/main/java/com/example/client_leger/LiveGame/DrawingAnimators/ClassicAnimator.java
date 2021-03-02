package com.example.client_leger.LiveGame.DrawingAnimators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.example.client_leger.CanvasView;
import com.example.client_leger.LiveGame.GameImagePath;
import com.example.client_leger.Utilities.Converter;


public class ClassicAnimator extends DrawingAnimator {

    private AnimationData animationData = new AnimationData();
    private long PROFILER_LAPSE; // To log the time taken to run the animation (in order to verify that it ran in the expected time) TODO: Remove before the final build

    @Override
    public void run(DrawingStrategy strategy, CanvasView canvas) {
        animationData.setUp(strategy, canvas);
        super.run(strategy, canvas);
    }

    @Override
    public void resetAllAnimations(final boolean checkIfReadyToRun) {
        super.resetAllAnimations(checkIfReadyToRun);
        if (checkIfReadyToRun) animationData.reset(true);
        else if (animationData.canvasView != null) {
            animationData.canvasView.post(() -> {
                animationData.reset(false);
            });
        }
    }

    @Override
    protected void playAnimations(final CanvasView canvas) {
        super.playAnimations(canvas);
        if (animationData.hasAnimationToRun()) {
            canvas.clear();
            PROFILER_LAPSE = System.currentTimeMillis();
            animationData.start();
        }
    }

    @Override
    protected void buildAnimations(final DrawingStrategy strategy, final CanvasView canvas) {
        super.buildAnimations(strategy, canvas);
        animationData.resetAnimators();
        for(final GameImagePath path: strategy.game.image.paths) {
            ValueAnimator animation = ValueAnimator.ofInt(0, 100);
            animation.setInterpolator(new LinearInterpolator());
            animation.addUpdateListener(valueAnimator -> {
                animationData.setCurrentPathToDraw();
                drawSinglePath(canvas.drawCanvas, (Integer)valueAnimator.getAnimatedValue());
                canvas.invalidate();
            });
            animation.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    animationData.pathToDraw = null;
                    if (animationData.hasAnimationsLeft()) {
                        animationData.animationIndex++;
                    } else {
                        PROFILER_LAPSE = System.currentTimeMillis() - PROFILER_LAPSE;
                        Log.e("NOT AN ERROR :) ", "Image drawn in " + PROFILER_LAPSE + "ms instead of " + strategy.getTimeToDraw() + "ms");
                    }
                }
            });
            final double duration = strategy.getTimeToDraw() * ((double)path.points.size()/(double)strategy.pointsToDraw);
            animation.setDuration((long)duration);
            animation.setRepeatCount(0);
            animationData.addAnimation(animation);
        }
    }

    /**
     * Draws a portion of the path currently held by the animatorData
     * @param canvas The canvas on which to draw
     * @param pathOffset The percentage of the path we must draw (starting from it's beginning)
     */
    private void drawSinglePath(final Canvas canvas, float pathOffset) {
        if (canvas == null) return;
        if (pathOffset >= 0) {
            final float length = animationData.measure.getLength();
            final Path partialPath = new Path();
            final float start = pathOffset==0? 0: animationData.previousMeasureStart, end = (length * pathOffset) / 100f;
            animationData.previousMeasureStart = end;
            animationData.measure.getSegment(start, end, partialPath, true);
            partialPath.rLineTo(0.0f, 0.0f);
            canvas.drawPath(partialPath, animationData.paintToDraw);
        }
    }

    private static class AnimationData extends CustomAnimationData {
        // Data that we save here so we don't have to re-create them at each animation cycle
        private PathMeasure measure = null;
        private Path pathToDraw = null;
        private Paint paintToDraw = null;
        private float previousMeasureStart = 0;

        @Override
        protected void reset(final boolean checkIfReadyToRun) {
            super.reset(checkIfReadyToRun);
            measure = null;
            pathToDraw = null;
            paintToDraw = null;
            previousMeasureStart = 0;
        }

        /**
         * Saves all data regarding the path we are about to draw
         */
        private void setCurrentPathToDraw() {
            if (pathToDraw == null && animationIndex < strategy.game.image.paths.size()) {
                final GameImagePath path = strategy.game.image.paths.get(animationIndex);
                pathToDraw = new Path();
                pathToDraw.moveTo(path.points.get(0).x, path.points.get(0).y); // move to the first point
                short index = 0; // to ignore the first point in the loop

                for(final PointF point: path.points) { // DRAW EACH POINT
                    if (index !=0 ) pathToDraw.lineTo(point.x, point.y);
                    index++;
                }
                paintToDraw = new Paint();
                paintToDraw.setColor(Converter.StringToColor(path.hexColor));
                paintToDraw.setAntiAlias(true);
                paintToDraw.setStrokeWidth((float) path.strokeWidth);
                paintToDraw.setStyle(Paint.Style.STROKE);
                paintToDraw.setStrokeJoin(Paint.Join.ROUND);
                paintToDraw.setStrokeCap(Paint.Cap.ROUND);
                measure = new PathMeasure(pathToDraw, false);
            }
        }
    }
}
