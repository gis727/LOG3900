package com.example.client_leger.LiveGame.DrawingAnimators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.example.client_leger.CanvasView;
import com.example.client_leger.LiveGame.Game;

public class PanoramicAnimator extends DrawingAnimator {

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
        ValueAnimator animation = ValueAnimator.ofFloat(0, 100f);
        animation.setInterpolator(new LinearInterpolator());
        animation.addUpdateListener(valueAnimator -> {
            final Bitmap newBitmap = Bitmap.createBitmap(animationData.originalBitmap);
            canvas.clear();
            canvas.drawCanvas.drawBitmap(newBitmap,0,0, animationData.curtainPaint);
            float curtainPercentageToHide = (((float) valueAnimator.getAnimatedValue()) / 100f), curtainPercentageToShow = 1f - curtainPercentageToHide;

            if (strategy.game.drawingDirection.equals(Game.DrawingDirection.BottomToTop)) {
                canvas.drawCanvas.drawRect(
                        animationData.curtain.left,
                        animationData.curtain.top,
                        animationData.curtain.right,
                        animationData.curtain.bottom * curtainPercentageToShow,
                        animationData.curtainPaint);
            } else if (strategy.game.drawingDirection.equals(Game.DrawingDirection.TopToBottom)) {
                canvas.drawCanvas.drawRect(
                        animationData.curtain.left,
                        animationData.curtain.top + animationData.curtain.height() * curtainPercentageToHide,
                        animationData.curtain.right,
                        animationData.curtain.bottom,
                        animationData.curtainPaint);
            } else if (strategy.game.drawingDirection.equals(Game.DrawingDirection.LeftToRight)) {
                canvas.drawCanvas.drawRect(
                        animationData.curtain.left + animationData.curtain.width() * curtainPercentageToHide,
                        animationData.curtain.top,
                        animationData.curtain.right,
                        animationData.curtain.bottom,
                        animationData.curtainPaint);
            } else {
                canvas.drawCanvas.drawRect(
                        animationData.curtain.left,
                        animationData.curtain.top,
                        animationData.curtain.right * curtainPercentageToShow,
                        animationData.curtain.bottom,
                        animationData.curtainPaint);
            }
            canvas.invalidate();
        });
        animation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (!animationData.hasAnimationsLeft()) {
                    PROFILER_LAPSE = System.currentTimeMillis() - PROFILER_LAPSE;
                    Log.e("NOT AN ERROR :) ", "Image drawn in " + PROFILER_LAPSE + "ms instead of " + strategy.getTimeToDraw() + "ms");
                }
            }
        });
        animation.setDuration(strategy.getTimeToDraw());
        animationData.addAnimation(animation);
    }

    private static class AnimationData extends CustomAnimationData {
        final RectF curtain = new RectF();
        final Paint curtainPaint = new Paint();
        Bitmap originalBitmap;

        @Override
        protected void reset(final boolean checkIfReadyToRun) {
            super.reset(checkIfReadyToRun);
            if(checkIfReadyToRun) buildOriginalBitmap();
            positionCurtainOnImage();
            curtainPaint.setColor(Color.WHITE);
        }

        private void buildOriginalBitmap() {
            drawImageInstantaneously(canvasView, strategy.game.image);
            originalBitmap = Bitmap.createBitmap(canvasView.canvasBitmap);
        }

        private void positionCurtainOnImage() {
            curtain.set(strategy.game.image.getImageBounds(canvasView.drawCanvas, strategy.game.image.paths));
        }
    }
}
