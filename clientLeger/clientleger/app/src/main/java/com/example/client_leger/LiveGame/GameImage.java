package com.example.client_leger.LiveGame;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class GameImage
{
    public List<GameImagePath> paths = new ArrayList<>();
    public GameImage() { }

    public void centerImage(Canvas canvas) {
        if (canvas != null && paths != null) {
            RectF boundsRect = getImageBounds(canvas, paths);
            double minX = boundsRect.left, minY = boundsRect.top, maxX = boundsRect.right, maxY = boundsRect.bottom;
            int canvasWidth = canvas.getWidth(), canvasHeight = canvas.getHeight();
            // get the translations needed to center the image
            double translationX = (minX >= 0 && maxX <= canvasWidth)? (canvasWidth - maxX - minX) * 0.5: 0,
                    translationY = (minY >= 0 && maxY <= canvasHeight)? (canvasHeight - maxY - minY) * 0.5: 0;
            // keep translations inside the canvas
            translationX = ((minX + translationX) < 0 || ((minX + translationX) > canvasWidth)) ? 0 : translationX;
            translationY = ((minY + translationY) < 0 || ((minY + translationY) > canvasHeight)) ? 0 : translationY;
            // apply the translations to all points
            for (final GameImagePath path: paths) {
                for (final PointF point : path.points) {
                    point.offset((float) translationX, (float) translationY);
                }
            }
        }
    }

    public RectF getImageBounds(Canvas canvas, List<GameImagePath> paths) {
        int canvasWidth = canvas.getWidth(), canvasHeight = canvas.getHeight();
        double maxX = 0, maxY = 0, minX = canvasWidth, minY = canvasHeight;
        for (final GameImagePath path: paths) {
            for (final PointF point: path.points) {
                double strokeWidth = path.strokeWidth;
                if (point.x + strokeWidth > maxX) maxX = point.x + strokeWidth;
                if (point.y + strokeWidth > maxY) maxY = point.y + strokeWidth;
                if (point.x - strokeWidth < minX) minX = point.x - strokeWidth;
                if (point.y - strokeWidth < minY) minY = point.y - strokeWidth;
            }
        }
        // keep bounds in canvas limits
        minX = minX < 0? 0: minX;
        minY = minY < 0? 0: minY;
        maxX = maxX > canvasWidth? canvas.getWidth(): maxX;
        maxY = maxY > canvasHeight? canvas.getHeight(): maxY;
        return new RectF((float) minX, (float) minY, (float) maxX, (float) maxY);
    }
}
