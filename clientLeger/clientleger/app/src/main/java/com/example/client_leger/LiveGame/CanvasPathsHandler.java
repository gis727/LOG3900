package com.example.client_leger.LiveGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.client_leger.CanvasView;
import com.example.client_leger.GameService;
import com.example.client_leger.SocketSingleton;
import com.example.client_leger.Utilities.Converter;

import java.util.ArrayList;
import java.util.List;

public class CanvasPathsHandler {

    private DrawingUpdate updateToSend = new DrawingUpdate();
    private PathsGatherer pathsGatherer = new PathsGatherer();

    public final void reset(){
        updateToSend = new DrawingUpdate();
        pathsGatherer = new PathsGatherer();
    }

    public void OnFingerTouch(final boolean erasing, final PointF point, final CanvasView canvasView, final Paint drawPaint, final Context context, final int color){
        if (!erasing) {
            pathsGatherer.addPoint(true, point, Converter.ColorToString(color), GameImagePath.capToStylusPoint(canvasView.drawPaint.getStrokeCap()), drawPaint.getStrokeWidth(), canvasView.drawCanvas);
            updateToSend.setupImage(pathsGatherer.image.paths);
            SocketSingleton.get(context).SendStroke(updateToSend, canvasView.drawCanvas);
            updateToSend.path.isNewStroke = false;
        } else if (canvasView.strokeEraserSelected &&  (pathsGatherer.remove(point, canvasView, drawPaint))) {
            updateToSend.setupImage(pathsGatherer.image.paths);
            SocketSingleton.get(context).SendStroke(updateToSend, canvasView.drawCanvas);
        }
    }
    public void OnFingerMove(final boolean erasing, final PointF point, final CanvasView canvasView, final Paint drawPaint, final Context context, final int color){
        if (!erasing) {
            pathsGatherer.addPoint(updateToSend.path.isNewStroke, point, Converter.ColorToString(color), GameImagePath.capToStylusPoint(canvasView.drawPaint.getStrokeCap()), drawPaint.getStrokeWidth(), canvasView.drawCanvas);
            updateToSend.setupImage(pathsGatherer.image.paths);
            SocketSingleton.get(context).SendStroke(updateToSend, canvasView.drawCanvas);
            updateToSend.path.isNewStroke = false;
        } else if (pathsGatherer.remove(point, canvasView, drawPaint)) {
            updateToSend.setupImage(pathsGatherer.image.paths);
            SocketSingleton.get(context).SendStroke(updateToSend, canvasView.drawCanvas);
        }
    }

    // STROKES GATHERER
    class PathsGatherer{
        private final GameImage image = new GameImage();

        void addPoint(final boolean isNewPath, final PointF target, final String color, final String shape, final float strokeWidth, final Canvas canvas) {
            if (isNewPath) {
                GameImagePath newPath = new GameImagePath(color, shape, strokeWidth);
                newPath.canvasWidth = canvas.getWidth();
                newPath.canvasHeight = canvas.getHeight();
                newPath.points.add(target);
                newPath.isNewStroke = true;
                image.paths.add(newPath);
            }
            else image.paths.get(image.paths.size()-1).points.add(target);
        }

        boolean remove(final PointF target, final CanvasView canvasView, final Paint drawPaint) {
            if (canvasView.strokeEraserSelected && removePath(target, canvasView)) {
                GameService.drawImageInstantaneously(canvasView, image);
                return true;
            } else if(removeSinglePoint(target, canvasView, drawPaint)) {
                GameService.drawImageInstantaneously(canvasView, image);
                return true;
            }
            return false;
        }

        private boolean removeSinglePoint(final PointF target, final CanvasView canvasView, final Paint drawPaint) {
            boolean foundTheTarget = false;
            List<GameImagePath> paths = new ArrayList<>(image.paths);
            int mPathIndex = 0;
            for(GameImagePath path: paths) {

                int mPointIndex = 0;
                for(PointF point: new ArrayList<>(path.points)) {

                    /*if (path.stylusPoint.equals("Ellipse"))*/ {
                        final double centerDistance = Math.sqrt(Math.pow(target.y - point.y, 2) + Math.pow(target.x - point.x, 2));
                        if (centerDistance <= (canvasView.eraserRadius + path.strokeWidth)) {
                            foundTheTarget = true;
                            canvasView.drawCanvas.drawCircle(point.x, point.y, (float)path.strokeWidth, drawPaint);

                            List<PointF> mPoints = image.paths.get(mPathIndex).points;

                            if (mPointIndex > 0 && mPointIndex < mPoints.size()-1) {
                                // Split the current path
                                List<PointF> newPoints = new ArrayList<>(mPoints.subList(mPointIndex+1, mPoints.size()));
                                // Clear the remaining points
                                mPoints.subList(mPointIndex, path.points.size()).clear();
                                // Add new path
                                GameImagePath newPath = new GameImagePath(path);
                                newPath.points = newPoints;
                                image.paths.add(image.paths.indexOf(path), newPath);
                                // Move to the newly created path for the next iterations
                                mPointIndex=-1;
                                mPathIndex++;
                            } else if (mPoints.size() > 0) {
                                if (mPointIndex < mPoints.size()) mPoints.remove(mPointIndex);
                                mPointIndex--;
                            }
                        }
                    }
                    mPointIndex++;
                }
                if (image.paths.get(mPathIndex).points.isEmpty()) image.paths.remove(mPathIndex);
                else  mPathIndex++;
            }
            return foundTheTarget;
        }

        private boolean removePath(final PointF target, final CanvasView canvasView) {
            List<Integer> pathIndexes = new ArrayList<>();
            for (int index = 0; index < image.paths.size(); index++) {
                final GameImagePath path = image.paths.get(index);
                for (final PointF point: path.points) {
                    final double centerDistance = Math.sqrt(Math.pow(target.y - point.y, 2) + Math.pow(target.x - point.x, 2));
                    if (centerDistance <= (canvasView.eraserRadius + path.strokeWidth)) {
                        pathIndexes.add(index);
                        break;
                    }
                }
            }
            for (final int pathIndex: pathIndexes) {
                if(pathIndex < image.paths.size()) image.paths.remove(pathIndex);
            }
            return !pathIndexes.isEmpty();
        }
    }
}
