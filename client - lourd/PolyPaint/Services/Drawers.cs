using PolyPaint.Services;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using static PolyPaint.Models.Coms.Game;

namespace FMUD.Services
{
    class Drawers
    {
        private static readonly List<Storyboard> stories = new List<Storyboard> { };
        public static bool StopDrawings = false;

        public static void reset()
        {
            StopDrawings = true;
            foreach (Storyboard story in stories)
            {
                if (story != null) { story.Stop(); }
            }
        }

        public static void DrawInOrder(Thread previousAnimationThread, bool enableCanvasWhenDone, EventHandler DrawingFinished, InkCanvas canvas, StrokeCollection strokes, DrawingStrategy strategy)
        {
            if (previousAnimationThread != null && previousAnimationThread.IsAlive)
            {
                StopDrawings = true;
                previousAnimationThread.Join(); // Wait for the previous animation's thread to finish
            }
            StopDrawings = false;
            if (strategy.DrawRandomly)
            {
                if (strategy.UseAdvancedAlgo)
                {
                    DrawInRandomOrderWithPaths(enableCanvasWhenDone, DrawingFinished, canvas, strokes, strategy);
                }
                else
                {
                    DrawInRandomOrder(enableCanvasWhenDone, DrawingFinished, canvas, strokes, strategy);
                }
            }
            else
            {
                if (strategy.UseAdvancedAlgo)
                {
                    DrawInOrderWithPaths(enableCanvasWhenDone, DrawingFinished, canvas, strokes, strategy);
                }
                else
                {
                    DrawInInitialOrder(enableCanvasWhenDone, DrawingFinished, canvas, strokes, strategy);
                }
            }
        }

        // Advanced algos
        private static void DrawInOrderWithPaths(bool enableCanvasWhenDone, EventHandler DrawingFinished, InkCanvas canvas, StrokeCollection strokes, DrawingStrategy strategy)
        {
            try
            {
                int TIME_SPAN = 0;
                Storyboard storyboard = null;

                // Clear the canvas and init the storyboard
                Application.Current.Dispatcher.Invoke(() =>
                {
                    canvas.Children.Clear();
                    storyboard = new Storyboard();
                    stories.Add(storyboard);
                });

                // Build the story
                foreach (Stroke stroke in strokes)
                {
                    PathGeometry geometry = (new GameCreationService()).ExtractPathGeometry(stroke.StylusPoints); // Its needed everywhere !
                    geometry.Freeze();

                    double DURATION = strategy.TimeToDraw * (stroke.StylusPoints.Count / (double)strategy.PointsToDraw);
                    Application.Current.Dispatcher.Invoke(() =>
                    {
                        // Create and set up the path
                        Path p = new Path
                        {
                            Data = geometry,
                            StrokeThickness = stroke.DrawingAttributes.Width,
                            StrokeStartLineCap = PenLineCap.Round,
                            StrokeEndLineCap = PenLineCap.Round,
                            StrokeMiterLimit = 1,
                            StrokeDashCap = PenLineCap.Round,
                            StrokeLineJoin = PenLineJoin.Round,
                            Stroke = new SolidColorBrush(stroke.DrawingAttributes.Color),
                            StrokeDashOffset = GetGeoLength(geometry, stroke),
                            StrokeDashArray = new DoubleCollection() { GetGeoLength(geometry, stroke) },
                            Opacity = 0
                        };

                        // Create and set up the main path animation
                        DoubleAnimation animation = new DoubleAnimation
                        {
                            From = GetGeoLength(geometry, stroke),
                            To = GetFinalGeoLength(geometry, stroke),
                            BeginTime = new TimeSpan(0, 0, 0, 0, TIME_SPAN),
                            Duration = new Duration(TimeSpan.FromMilliseconds(DURATION))
                        };
                        Storyboard.SetTarget(animation, p);
                        Storyboard.SetTargetProperty(animation, new PropertyPath(Shape.StrokeDashOffsetProperty));

                        // Create and set up the visibility animation
                        DoubleAnimation visibilityAnim = new DoubleAnimation
                        {
                            From = 0,
                            To = 1,
                            BeginTime = new TimeSpan(0, 0, 0, 0, TIME_SPAN),
                            Duration = new Duration(TimeSpan.FromMilliseconds(0))
                        };
                        Storyboard.SetTarget(visibilityAnim, p);
                        Storyboard.SetTargetProperty(visibilityAnim, new PropertyPath(UIElement.OpacityProperty));

                        // Add all animations in the storyboard
                        storyboard.Children.Add(animation);
                        storyboard.Children.Add(visibilityAnim);

                        // Add the path in the canvas and update the timespan
                        canvas.Children.Add(p);
                        TIME_SPAN += (int)DURATION;
                    });
                }

                // Handle the paths cycles
                Application.Current.Dispatcher.Invoke(() =>
                {
                    storyboard.Completed += (object sender, EventArgs e) =>
                    {
                        Application.Current.Dispatcher.Invoke(() =>
                        {
                            canvas.Strokes.Clear(); // Clear and fill the canvas with all strokes
                            foreach (Stroke stroke in strokes)
                            {
                                canvas.Strokes.Add(new Stroke(stroke.StylusPoints.Clone(), stroke.DrawingAttributes.Clone()));
                            }
                            canvas.Children.Clear(); // Remove all paths
                            if (enableCanvasWhenDone)
                            {
                                canvas.IsEnabled = true;
                            }

                            DrawingFinished?.Invoke(null, EventArgs.Empty);
                        });
                    };
                    storyboard.Begin();
                });
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
        }
        private static void DrawInRandomOrderWithPaths(bool enableCanvasWhenDone, EventHandler DrawingFinished, InkCanvas canvas, StrokeCollection strokes, DrawingStrategy strategy)
        {
            // Shuffle
            StrokeCollection shuffledStrokes = new StrokeCollection();
            Random rand = new Random();
            for (int randIndex = rand.Next(strokes.Count); randIndex < strokes.Count; randIndex = rand.Next(strokes.Count))
            {
                shuffledStrokes.Add(strokes[randIndex].Clone());
                strokes.RemoveAt(randIndex);
            }

            // Draw with the shuffled strokes
            DrawInOrderWithPaths(enableCanvasWhenDone, DrawingFinished, canvas, shuffledStrokes, strategy);
        }

        // Classic algos
        private static void DrawInInitialOrder(bool enableCanvasWhenDone, EventHandler DrawingFinished, InkCanvas canvas, StrokeCollection strokes, DrawingStrategy strategy)
        {
            try
            {
                int i = 0, j = -1;
                while (i < strokes.Count)
                {
                    if (StopDrawings)
                    {
                        return;
                    }

                    if (++j >= strokes[i].StylusPoints.Count) { i++; j = 0; }
                    if (i >= strokes.Count)
                    {
                        return;
                    }

                    Application.Current.Dispatcher.Invoke(() =>
                    {
                        if (i >= canvas.Strokes.Count) // thats a new stroke !
                        {
                            canvas.Strokes.Add(new Stroke(new StylusPointCollection { strokes[i].StylusPoints[0] }));
                            canvas.Strokes[i].DrawingAttributes = strokes[i].DrawingAttributes;
                        }
                        else
                        {
                            canvas.Strokes[i].StylusPoints.Add(strokes[i].StylusPoints[j]);
                        }
                    });
                    Thread.Sleep(strategy.DrawingInterval);
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
            finally
            {
                if (!StopDrawings)
                {
                    Application.Current.Dispatcher.Invoke(() =>
                    {
                        if (enableCanvasWhenDone)
                        {
                            canvas.IsEnabled = true;
                        }
                        DrawingFinished?.Invoke(null, EventArgs.Empty);
                    });
                }
            }
        }
        private static void DrawInRandomOrder(bool enableCanvasWhenDone, EventHandler DrawingFinished, InkCanvas canvas, StrokeCollection strokes, DrawingStrategy strategy)
        {
            try
            {
                Random rand = new Random();
                int i = 0, j = -1, randIndex = rand.Next(strokes.Count);
                while (strokes.Count > 0)
                {
                    if (StopDrawings)
                    {
                        return;
                    }

                    if (++j >= strokes[randIndex].StylusPoints.Count) { i++; strokes.Remove(strokes[randIndex]); randIndex = rand.Next(strokes.Count); j = 0; }
                    if (strokes.Count == 0)
                    {
                        return;
                    }
                    // Update points
                    Application.Current.Dispatcher.Invoke(() =>
                    {
                        if (j == 0) // thats a new stroke !
                        {
                            canvas.Strokes.Add(new Stroke(new StylusPointCollection { strokes[randIndex].StylusPoints[0] }));
                            canvas.Strokes[i].DrawingAttributes = strokes[randIndex].DrawingAttributes;
                        }
                        else
                        {
                            canvas.Strokes[i].StylusPoints.Add(strokes[randIndex].StylusPoints[j]);
                        }
                    });
                    Thread.Sleep(strategy.DrawingInterval);
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
            finally
            {
                if (!StopDrawings)
                {
                    Application.Current.Dispatcher.Invoke(() =>
                    {
                        if (enableCanvasWhenDone)
                        {
                            canvas.IsEnabled = true;
                        }
                        DrawingFinished?.Invoke(null, EventArgs.Empty);
                    });
                }
            }
        }

        private static double GetFinalGeoLength(PathGeometry geometry, Stroke stroke)
        {
            return GetGeoLength(geometry, stroke) - (GetGeoLength(geometry, stroke) / (stroke.DrawingAttributes.Width));
        }
        private static double GetGeoLength(PathGeometry geometry, Stroke stroke)
        {
            try
            {
                if (stroke.StylusPoints.Count <= 1)
                {
                    return stroke.StylusPoints.Count;
                }

                geometry.GetPointAtFractionLength(0.0001, out Point p, out Point tp);
                return (geometry.Figures[0].StartPoint - p).Length * 10000;
            }
            catch
            {
                return stroke.StylusPoints.Count;
            }
        }
    }

    public class DrawingStrategy
    {
        readonly public int TimeToDraw;
        readonly public bool UseAdvancedAlgo; // True when no stroke contains a rectangular stylus point
        readonly public bool DrawRandomly;
        readonly public int PointsToDraw;
        readonly public int DrawingInterval; // For the old algorithm only

        public DrawingStrategy(int timeToDraw, bool useAdvancedAlgo, bool drawRandomly, int pointsToDraw, int drawingInterval)
        {
            TimeToDraw = timeToDraw;
            UseAdvancedAlgo = useAdvancedAlgo;
            DrawRandomly = drawRandomly;
            PointsToDraw = pointsToDraw;
            DrawingInterval = drawingInterval;
        }
        public static DrawingStrategy Build(StrokeCollection strokes, Difficulty difficulty, DrawingMode mode)
        {
            int pointsToDraw = GetPointsToDraw(strokes, out bool hasNonCircularPoints), timeToDraw = GetTimeToDraw(difficulty);
            int drawingInterval = hasNonCircularPoints ? GetDrawingInterval(pointsToDraw, timeToDraw) : 0;
            return new DrawingStrategy(timeToDraw, !hasNonCircularPoints, mode == DrawingMode.Random, pointsToDraw, drawingInterval);
        }
        private static int GetPointsToDraw(StrokeCollection strokes, out bool hasNonCircularPoints)
        {
            int pointsToDraw = 0;
            hasNonCircularPoints = false;
            foreach (Stroke stroke in strokes)
            {
                pointsToDraw += stroke.StylusPoints.Count;
                if ((stroke.DrawingAttributes.Width > 1) && (stroke.DrawingAttributes.StylusTip != StylusTip.Ellipse))
                {
                    hasNonCircularPoints = true;
                }
            }
            return pointsToDraw;
        }
        private static int GetDrawingInterval(int pointsToDraw, int timeToDraw)
        {
            return timeToDraw / pointsToDraw;
        }
        public static int GetTimeToDraw(Difficulty difficulty)
        {
            int timeToDraw = 15000;
            if (difficulty == Difficulty.Medium)
            {
                timeToDraw = 10000;
            }
            else if (difficulty == Difficulty.Hard)
            {
                timeToDraw = 5000;
            }
            else if (difficulty == Difficulty.Creation)
            {
                timeToDraw = 1500;
            }

            return timeToDraw;
        }
    }
}
