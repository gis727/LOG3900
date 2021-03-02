using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;

namespace PolyPaint.Models.Coms
{
    public class GameImage
    {
        public List<GameImagePath> paths;

        public GameImage()
        {
            paths = new List<GameImagePath>();
        }
    }

    public class GameImagePath
    {
        public List<Point> points;
        public string hexColor;
        public string stylusPoint;
        public double strokeWidth;

        // FFA
        public bool isNewStroke;
        public double canvasWidth;
        public double canvasHeight;

        public GameImagePath(string hexColor, string stylusPoint, double strokeWidth)
        {
            points = new List<Point>();
            this.hexColor = hexColor;
            this.stylusPoint = stylusPoint;
            this.strokeWidth = strokeWidth;
        }
        public GameImagePath()
        {
            points = new List<Point>();
        }

        public void AddPoint(Point point)
        {
            points.Add(point);
        }
        public void SetCanvasDimensions(double canvasWidth, double canvasHeight)
        {
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;
        }
    }

    public class DrawingUpdate
    {
        public GameImagePath path;
        public GameImage image;
        public DrawingUpdate(GameImagePath stroke = null, GameImage image = null)
        {
            path = (stroke != null) ? stroke : new GameImagePath();
            this.image = (image != null) ? image : new GameImage();
        }
        public bool RedrawRequired()
        {
            return path.points.Count == 0;
        }
        public void FitPointsIntoCanvas(InkCanvas canvas)
        {
            if (path != null)
            {
                UpdatePathPoints(path, canvas);
            }
            else if (image != null && image.paths.Count > 0)
            {
                if (image.paths[0].canvasWidth != canvas.ActualWidth || image.paths[0].canvasHeight != canvas.ActualHeight)
                {
                    image.paths.ForEach(path => UpdatePathPoints(path, canvas));
                }
            }
        }

        private void UpdatePathPoints(GameImagePath path, InkCanvas canvas)
        {
            if (path.canvasWidth != canvas.ActualWidth || path.canvasHeight != canvas.ActualHeight)
            {
                for (int index = path.points.Count - 1; index >= 0; index--)
                {
                    Point point = path.points[index];
                    path.points[index] = new Point((point.X / path.canvasWidth) * canvas.ActualWidth, (point.Y / path.canvasHeight) * canvas.ActualHeight);
                }
            }
        }
    }
}
