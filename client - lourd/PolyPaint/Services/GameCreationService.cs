using PolyPaint.Models.Coms;
using Svg;
using Svg.Pathing;
using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Markup;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Xml.Linq;

namespace PolyPaint.Services
{
    class GameCreationService
    {
        public event EventHandler DrawingFinished;
        public void RedrawFromCanvas(InkCanvas canvas)
        {
            (new DrawingService()).Draw(canvas, null, null, new Game());
        }

        public void Redraw(InkCanvas canvas, Shape curtain, Shape outInCurtain, Game game)
        {
            DrawingService drawer = new DrawingService();
            drawer.Draw(canvas, curtain, outInCurtain, game);
            drawer.DrawingFinished += (object s, EventArgs e) => { DrawingFinished?.Invoke(this, EventArgs.Empty); };
        }

        public GameImage GetImageFromCanvas(InkCanvas canvas)
        {
            GameImage gameImage = new GameImage();
            try
            {
                foreach (Stroke stroke in canvas.Strokes)
                {
                    // BUILD THE GEOMETRY PATH
                    PathGeometry geometry = ExtractPathGeometry(stroke.StylusPoints);
                    geometry.Freeze();

                    // EXTRACT THE SVG PATH FROM THE GEOMETRY PATH
                    string s = XamlWriter.Save(geometry);
                    if (s != null && s.Length > 0)
                    {
                        XElement element = XElement.Parse(s);
                        string data = element.Attribute("Figures")?.Value;
                        if (data != null && data.Length > 0)
                        {
                            // Get the path colors and stuff
                            GameImagePath gamePath = new GameImagePath(
                                FMUD.Converters.ColorConverter.FromRGBToHex(stroke.DrawingAttributes.Color),
                                stroke.DrawingAttributes.StylusTip.ToString(),
                                stroke.DrawingAttributes.Width
                                );
                            gamePath.SetCanvasDimensions(canvas.ActualWidth, canvas.ActualHeight);
                            // Get the path data
                            SvgPathSegmentList segments = SvgPathBuilder.Parse(data);
                            foreach (SvgPathSegment segment in segments)
                            {
                                gamePath.points.Add(new Point(segment.End.X, segment.End.Y));
                            }
                            gameImage.paths.Add(gamePath);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("COULDN'T GET IMAGES FROM THE CANVAS: " + e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
            return gameImage;
        }

        public GameImagePath GetPath(Stroke stroke, InkCanvas canvas)
        {
            GameImagePath gamePath = null;
            try
            {
                // BUILD THE GEOMETRY PATH
                PathGeometry geometry = ExtractPathGeometry(stroke.StylusPoints);
                geometry.Freeze();

                // EXTRACT THE SVG PATH FROM THE GEOMETRY PATH
                string s = XamlWriter.Save(geometry);
                if (s != null && s.Length > 0)
                {
                    XElement element = XElement.Parse(s);
                    string data = element.Attribute("Figures")?.Value;
                    if (data != null && data.Length > 0)
                    {
                        // Get the path colors and stuff
                        gamePath = new GameImagePath(
                            FMUD.Converters.ColorConverter.FromRGBToHex(stroke.DrawingAttributes.Color),
                            stroke.DrawingAttributes.StylusTip.ToString(),
                            stroke.DrawingAttributes.Width
                            );
                        gamePath.SetCanvasDimensions(canvas.ActualWidth, canvas.ActualHeight);
                        // Get the path data
                        SvgPathSegmentList segments = SvgPathBuilder.Parse(data);
                        foreach (SvgPathSegment segment in segments)
                        {
                            gamePath.points.Add(new Point(segment.End.X, segment.End.Y));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("COULDN'T CONVERT STROKE TO IMAGE PATH: " + e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
            return gamePath;
        }

        public GameImage GetImageFromSvg(string svgString)
        {
            GameImage gameImage = new GameImage();
            try
            {
                SvgDocument svg = SvgDocument.FromSvg<SvgDocument>(svgString);
                foreach (SvgElement element in svg.Children.Descendants())
                {
                    if (!element.HasChildren()) // Path
                    {
                        // Get transforms
                        Point translation = new Point(0, 0);
                        Point scale = new Point(1, 1);
                        foreach (Svg.Transforms.SvgTransform transform in element.Parent.Transforms)
                        {
                            if (transform.WriteToString().StartsWith("translate"))
                            {
                                // Get translation
                                translation.X = transform.Matrix.OffsetX;
                                translation.Y = transform.Matrix.OffsetY;
                            }
                            else if (transform.WriteToString().StartsWith("scale"))
                            {
                                // Get scale
                                scale.X = transform.Matrix.Elements[0];
                                scale.Y = transform.Matrix.Elements[3];
                            }
                        }

                        // Build game data with the path properties
                        SvgPath path = element as SvgPath;

                        // Get all the points
                        Geometry geo = Geometry.Parse(path.PathData.ToString());
                        PathGeometry pathGeo = geo.GetFlattenedPathGeometry();
                        foreach (PathFigure figure in pathGeo.Figures)
                        {
                            GameImagePath gamePath = new GameImagePath(
                                FMUD.Converters.ColorConverter.FromRGBToHex(path.Fill.ToString()),
                                "",
                                path.StrokeWidth
                            );
                            foreach (PathSegment segment in figure.Segments)
                            {
                                foreach (Point point in ((PolyLineSegment)segment).Points)
                                {
                                    gamePath.AddPoint(new Point(
                                        point.X * scale.X + translation.X,
                                        point.Y * scale.Y + translation.Y)
                                    );
                                }
                            }
                            gameImage.paths.Add(gamePath);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("COULDN'T GET DRAWING STROKES: " + e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
            return gameImage;
        }

        public PathGeometry ExtractPathGeometry(StylusPointCollection points)
        {
            List<LineSegment> segments = new List<LineSegment>();
            foreach (StylusPoint point in points)
            {
                segments.Add(new LineSegment(new Point(point.X, point.Y), true));
            }
            segments.Remove(segments[0]); // First element is the start point
            if (points.Count == 1)
            {
                segments.Add(new LineSegment(new Point(points[0].X, points[0].Y), true)); // At least 1 segment is required
            }

            PathFigure figure = new PathFigure(new Point(points[0].X, points[0].Y), segments, false);
            return new PathGeometry(new List<PathFigure> { figure });
        }
    }
}
