package com.example.client_leger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.example.client_leger.LiveGame.Drawers;
import com.example.client_leger.LiveGame.DrawingUpdate;
import com.example.client_leger.LiveGame.Game;
import com.example.client_leger.LiveGame.GameImage;
import com.example.client_leger.LiveGame.GameImagePath;
import com.example.client_leger.Utilities.Converter;

public class GameService {
    private static final Drawers drawers = new Drawers();
    private static PointF lastPoint;

    /**
     * Decides if the game should be drawn and calls the right drawing method
     * @param canvasView The canvas view on which to draw
     * @param game The game to draw (if another player is drawing, the paths will be contained in this object)
     */
    public static void drawGame(final CanvasView canvasView, final Game game) {
        if (canvasView.drawCanvas == null || game == null) return; // We need those things to work.

        Drawers.StopAllAnimationsAtOnce();
        canvasView.setTouchable(false); // by default the canvas is disabled
        canvasView.clear();
        canvasView.resetGatherer();

        if (game.selectedDrawer.equals(Game.SelectedDrawer.AnotherClient)) { // Its another player's turn
            // we just wait for the strokes...
        } else if (game.selectedDrawer.equals(Game.SelectedDrawer.CurrentClient)) { // Its our turn to draw
            canvasView.setTouchable(true);
        } else { // A virtual player is drawing (so we draw with animations)
            drawers.draw(canvasView, game);
        }
    }

    /**
     * Draws a single path on the canvas (used for FFA)
     * @param canvas The canvas on which to draw
     * @param path The path to be drawn
     */
    private static void drawSinglePath(final Canvas canvas, final GameImagePath path, final boolean newStroke) {
        if (canvas == null) return;

        // Get the paint
        Paint paint = new Paint();
        paint.setColor(Converter.StringToColor(path.hexColor)); // need to convert hex string to int
        paint.setAntiAlias(true);
        paint.setStrokeWidth((float) path.strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(path.getPaintCap());

        if (path.points.size() == 1) { // Only one point to draw
            canvas.drawPoint(path.points.get(0).x, path.points.get(0).y, paint);
            return;
        }

        Path pathToDraw = new Path();
        // move to the first point
        if (!newStroke) pathToDraw.moveTo(lastPoint.x, lastPoint.y);
        else pathToDraw.moveTo(path.points.get(0).x, path.points.get(0).y);

        short index = 0; // to ignore the first point in the loop
        lastPoint = new PointF();
        for(PointF point: path.points) { // DRAW EACH POINT
            if (!newStroke) pathToDraw.lineTo(point.x, point.y);
            else if (index !=0 ) pathToDraw.lineTo(point.x, point.y);
            lastPoint.x = point.x;
            lastPoint.y = point.y;
            index++;
        }

        // Draw the path
        canvas.drawPath(pathToDraw, paint);
    }

    public static void drawImageInstantaneously(final CanvasView canvasView, final GameImage image) {
        canvasView.clear();

        for(GameImagePath path: image.paths) { // DRAW EACH PATH
            if (!path.points.isEmpty()) {
                drawSinglePath(canvasView.drawCanvas, path, true);
                canvasView.invalidate();
            }
        }
    }

    public void drawStroke(final CanvasView canvasView, final DrawingUpdate update) {
        canvasView.post(() -> {
            if (update.path.points.size() > 0) drawSinglePath(canvasView.drawCanvas, update.path, update.path.isNewStroke);
            else if (update.image.paths != null) drawImageInstantaneously(canvasView, update.image);
        });
    }
}
