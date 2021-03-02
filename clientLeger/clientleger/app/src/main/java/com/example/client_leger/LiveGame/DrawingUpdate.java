package com.example.client_leger.LiveGame;

import android.graphics.PointF;

import com.example.client_leger.CanvasView;
import com.example.client_leger.Utilities.Converter;

import java.util.List;

public class DrawingUpdate {
    public GameImagePath path;
    public GameImage image;
    public static int canvasWidth;
    public static int canvasHeight;

    public DrawingUpdate(GameImagePath path, GameImage image) {
        this.path = path;
        this.image = image==null? new GameImage(): image;
    }

    public DrawingUpdate() {
        this.path = new GameImagePath("#000000", "Ellipse", 25);
        this.image = new GameImage();
    }

    public void setUpPath(CanvasView canvasView) {
        if(path != null) {
            path.hexColor = Converter.ColorToString(canvasView.currentColor);
            path.strokeWidth = canvasView.currentWidth;
            path.stylusPoint = GameImagePath.capToStylusPoint(canvasView.drawPaint.getStrokeCap());
            image.paths.clear();
        }
    }

    public void setupImage(List<GameImagePath> imagePaths) {
        path.points.clear();
        image.paths = imagePaths;
    }

    public void fitPointsIntoCanvas() {
        if(path.points.size() > 0) {
            updatePathPoints(path);
        } else if (image != null && image.paths.size() > 0) {
            for (GameImagePath path: image.paths) {
                updatePathPoints(path);
            }
        }
    }

    private void updatePathPoints(GameImagePath path) {
        if( canvasWidth != path.canvasWidth || canvasHeight != path.canvasHeight) {
            for (PointF point: path.points) {
                point.x = (point.x / (float) path.canvasWidth) + canvasWidth;
                point.y = (point.y / (float) path.canvasHeight) + canvasHeight;
            }
        }
    }
}
