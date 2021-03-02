package com.example.client_leger.LiveGame;

import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class GameImagePath
{
    public List<PointF> points;
    public String hexColor;
    public String stylusPoint;
    public double strokeWidth;

    // FFA
    public boolean isNewStroke;
    public double canvasWidth;
    public double canvasHeight;

    public GameImagePath(String hexColor, String stylusPoint, double strokeWidth)
    {
        points = new ArrayList<>();
        this.hexColor = hexColor;
        this.stylusPoint = stylusPoint;
        this.strokeWidth = strokeWidth;
        isNewStroke = true;
    }

    public GameImagePath(GameImagePath path)
    {
        points = new ArrayList<>(path.points);
        this.hexColor = path.hexColor;
        this.stylusPoint = path.stylusPoint;
        this.strokeWidth = path.strokeWidth;
        isNewStroke = true;
    }
    public void addPoint(PointF point)
    {
        points.add(point);
    }

    public Paint.Cap getPaintCap() {
        return stylusPoint != null && stylusPoint.equals("Ellipse")? Paint.Cap.ROUND: Paint.Cap.SQUARE;
    }

    public static String capToStylusPoint(final Paint.Cap cap) {
        return cap.equals(Paint.Cap.ROUND) ? "Ellipse": "Rectangle";
    }
}
