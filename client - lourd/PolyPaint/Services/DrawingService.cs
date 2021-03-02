using FMUD.Services;
using PolyPaint.Models.Coms;
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

namespace PolyPaint.Services
{
    class DrawingService
    {
        // Old algorithm's threads
        private static Thread prevAnimationThread;
        private static Thread curAnimationThread;

        private Difficulty difficulty;
        private static readonly List<Storyboard> stories = new List<Storyboard> { };
        public event EventHandler DrawingFinished;
        private bool enableCanvasWhenDone;

        // Draws a game according to its specifications
        public void Draw(InkCanvas canvas, Shape curtain, Shape outInCurtain, Game game, bool enableCanvasWhenDone = true)
        {
            this.enableCanvasWhenDone = enableCanvasWhenDone;
            reset();
            if (game != null && game.image == null && canvas.Strokes.Count == 0) { AnnounceFinished(canvas); return; }
            StrokeCollection strokes = (game.image != null) ? GetDrawingStrokes(game.image, canvas) : new StrokeCollection(canvas.Strokes);
            if (strokes.Count == 0) { AnnounceFinished(canvas); return; } // Nothing to draw

            canvas.Strokes.Clear();
            canvas.IsEnabled = false; // User cannot draw during the auto-drawing

            // Draw
            difficulty = game.difficulty;
            if (game.drawingMode == DrawingMode.Classic)
            {
                DrawInInitialOrder(canvas, strokes);
            }
            else if (game.drawingMode == DrawingMode.Random)
            {
                DrawInRandomOrder(canvas, strokes);
            }
            else if (game.drawingMode == DrawingMode.Panoramic)
            {
                DrawInPanoramicOrder(canvas, curtain, strokes, game.drawingDirection);
            }
            else if (game.drawingMode == DrawingMode.Centered)
            {
                DrawInCenteredOrder(canvas, strokes, game.drawingDirection, outInCurtain);
            }
            else
            {
                DrawInstantaneously(canvas, strokes);
            }
        }

        #region With-Curtain drawers
        public void DrawInInitialOrder(InkCanvas canvas, StrokeCollection strokes)
        {
            prevAnimationThread = curAnimationThread; // save previous thread
            ThreadStart starter = () => Drawers.DrawInOrder(prevAnimationThread, enableCanvasWhenDone, DrawingFinished, canvas, strokes, DrawingStrategy.Build(strokes, difficulty, DrawingMode.Classic));
            Thread thread = (new Thread(starter) { IsBackground = true });
            curAnimationThread = thread; // save current thread
            thread.Start();
        }
        public void DrawInRandomOrder(InkCanvas canvas, StrokeCollection strokes)
        {
            prevAnimationThread = curAnimationThread; // save previous thread
            ThreadStart starter = () => Drawers.DrawInOrder(prevAnimationThread, enableCanvasWhenDone, DrawingFinished, canvas, strokes, DrawingStrategy.Build(strokes, difficulty, DrawingMode.Random));
            Thread thread = (new Thread(starter) { IsBackground = true });
            curAnimationThread = thread; // save current thread
            thread.Start();
        }
        public void DrawInPanoramicOrder(InkCanvas canvas, Shape curtain, StrokeCollection strokes, DrawingDirection direction)
        {
            // Find the edges of the entire drawing (for the curtain)
            double minX = canvas.ActualWidth, minY = canvas.ActualHeight;
            GetImageBounds(out minX, out minY, out double maxX, out double maxY, strokes, canvas);

            // Draw everything
            DrawInstantaneously(canvas, strokes);

            // Set up the curtain
            curtain.Visibility = Visibility.Visible;
            if (direction == DrawingDirection.LeftToRight)
            {
                curtain.HorizontalAlignment = HorizontalAlignment.Right;
                curtain.VerticalAlignment = VerticalAlignment.Stretch;
            }
            else if (direction == DrawingDirection.RightToLeft)
            {
                curtain.HorizontalAlignment = HorizontalAlignment.Left;
                curtain.VerticalAlignment = VerticalAlignment.Stretch;
            }
            else if (direction == DrawingDirection.TopToBottom)
            {
                curtain.VerticalAlignment = VerticalAlignment.Bottom;
                curtain.HorizontalAlignment = HorizontalAlignment.Stretch;
            }
            else if (direction == DrawingDirection.BottomToTop)
            {
                curtain.VerticalAlignment = VerticalAlignment.Top;
                curtain.HorizontalAlignment = HorizontalAlignment.Stretch;
            }

            curtain.Margin = new Thickness(minX, minY, canvas.ActualWidth - maxX, canvas.ActualHeight - maxY);

            // Animate
            bool horizontalAnimation = (direction == DrawingDirection.LeftToRight) || (direction == DrawingDirection.RightToLeft);
            double initialWidth = maxX - minX, initialHeight = maxY - minY;

            DoubleAnimation animation = new DoubleAnimation
            {
                From = horizontalAnimation ? initialWidth : initialHeight,
                To = 0.0,
                Duration = new Duration(TimeSpan.FromMilliseconds(DrawingStrategy.GetTimeToDraw(difficulty)))
            };
            Storyboard.SetTarget(animation, curtain);
            Storyboard.SetTargetProperty(animation, new PropertyPath(horizontalAnimation ? FrameworkElement.WidthProperty : FrameworkElement.HeightProperty));
            Storyboard storyboard = new Storyboard();
            stories.Add(storyboard);
            storyboard.Children.Add(animation);

            // Reset the curtain
            storyboard.Completed += (object sender, EventArgs e) =>
            {
                try
                {
                    curtain.Visibility = Visibility.Collapsed;
                    AnnounceFinished(canvas);

                    /* Trick to force the original dimenssion (the dp doesn't seem to be accessible from here and we shouldn't try to access it from here anyway) */
                    DoubleAnimation anim = new DoubleAnimation();
                    animation.From = horizontalAnimation ? initialWidth : initialHeight;
                    animation.To = horizontalAnimation ? initialWidth : initialHeight;
                    animation.Duration = new Duration(TimeSpan.FromMilliseconds(0)); Storyboard.SetTarget(animation, curtain);
                    Storyboard.SetTargetProperty(animation, new PropertyPath(horizontalAnimation ? FrameworkElement.WidthProperty : FrameworkElement.HeightProperty));
                    Storyboard story = new Storyboard();
                    story.Children.Add(animation);
                    story.Begin();
                    /* End of the trick */
                }
                catch (Exception ex)
                {
                    Console.Error.WriteLine("Animation Error: " + ex.Message);
                    Console.Error.WriteLine(ex.StackTrace);
                }
            };
            storyboard.Begin();
        }
        public void DrawInCenteredOrder(InkCanvas canvas, StrokeCollection strokes, DrawingDirection direction, Shape outInCurtain)
        {
            // Find the edges of the entire drawing (for the curtain)
            double minX = canvas.ActualWidth, minY = canvas.ActualHeight;
            GetImageBounds(out minX, out minY, out double maxX, out double maxY, strokes, canvas);

            // Draw everything
            DrawInstantaneously(canvas, strokes);

            // Set up the curtain
            outInCurtain.Visibility = Visibility.Visible;
            outInCurtain.HorizontalAlignment = HorizontalAlignment.Center;
            outInCurtain.VerticalAlignment = VerticalAlignment.Center;

            // Center the curtain on the image
            double initialWidth = maxX - minX, initialHeight = maxY - minY;
            double centerX = canvas.ActualWidth / 2, centerY = canvas.ActualHeight / 2;
            outInCurtain.RenderTransform = new TranslateTransform(-centerX + minX + initialWidth / 2, -centerY + minY + initialHeight / 2);

            if (direction == DrawingDirection.OutIn)
            {
                Storyboard _storyboard = resetCenteredCurtains(initialWidth * 1.414, initialHeight * 1.414, outInCurtain);
                _storyboard.Completed += (object _sender, EventArgs _e) =>
                {
                    try
                    {
                        // Animate
                        DoubleAnimation horizontalAnimation = new DoubleAnimation
                        {
                            From = initialWidth * 1.414,
                            To = 0.0,
                            Duration = new Duration(TimeSpan.FromMilliseconds(DrawingStrategy.GetTimeToDraw(difficulty)))
                        };
                        Storyboard.SetTarget(horizontalAnimation, outInCurtain);
                        Storyboard.SetTargetProperty(horizontalAnimation, new PropertyPath(FrameworkElement.WidthProperty));
                        Storyboard storyboard = new Storyboard();
                        storyboard.Children.Add(horizontalAnimation);

                        DoubleAnimation verticalAnimation = new DoubleAnimation
                        {
                            From = initialHeight * 1.414,
                            To = 0.0,
                            Duration = new Duration(TimeSpan.FromMilliseconds(DrawingStrategy.GetTimeToDraw(difficulty)))
                        };
                        Storyboard.SetTarget(verticalAnimation, outInCurtain);
                        Storyboard.SetTargetProperty(verticalAnimation, new PropertyPath(FrameworkElement.HeightProperty));
                        storyboard.Children.Add(verticalAnimation);

                        storyboard.Completed += (object sender, EventArgs e) =>
                        {
                            try
                            {
                                outInCurtain.Visibility = Visibility.Collapsed;
                                AnnounceFinished(canvas);
                            }
                            catch (Exception exception)
                            {
                                Console.Error.WriteLine("Animation Error: " + exception.Message);
                                Console.Error.WriteLine(exception.StackTrace);
                            }
                        };
                        storyboard.Begin();
                    }
                    catch(Exception exception)
                    {
                        Console.Error.WriteLine("Animation Error: " + exception.Message);
                        Console.Error.WriteLine(exception.StackTrace);
                    }
                };
                _storyboard.Begin();
            }
            else
            {
                // Set the outInCurtain
                Storyboard _storyboard = resetCenteredCurtains(initialWidth * 1.414, initialHeight * 1.414, outInCurtain);
                stories.Add(_storyboard);
                _storyboard.Completed += (object _sender, EventArgs _e) =>
                {
                    try
                    {
                        outInCurtain.Visibility = Visibility.Visible;
                        // Animate
                        DoubleAnimation horizontalAnimation = new DoubleAnimation
                        {
                            From = 0.0,
                            To = 50.0,
                            Duration = new Duration(TimeSpan.FromMilliseconds(DrawingStrategy.GetTimeToDraw(difficulty)))
                        };
                        Storyboard.SetTarget(horizontalAnimation, outInCurtain);
                        Storyboard.SetTargetProperty(horizontalAnimation, new PropertyPath("OpacityMask.Drawing.Geometry.Children[1].RadiusX"));
                        Storyboard storyboard = new Storyboard();
                        stories.Add(storyboard);
                        storyboard.Children.Add(horizontalAnimation);

                        DoubleAnimation verticalAnimation = new DoubleAnimation
                        {
                            From = 0.0,
                            To = 50.0,
                            Duration = new Duration(TimeSpan.FromMilliseconds(DrawingStrategy.GetTimeToDraw(difficulty)))
                        };
                        Storyboard.SetTarget(verticalAnimation, outInCurtain);
                        Storyboard.SetTargetProperty(verticalAnimation, new PropertyPath("OpacityMask.Drawing.Geometry.Children[1].RadiusY"));
                        storyboard.Children.Add(verticalAnimation);

                        storyboard.Completed += (object sender, EventArgs e) =>
                        {
                            try
                            {
                                outInCurtain.Visibility = Visibility.Hidden;
                                AnnounceFinished(canvas);
                            }
                            catch (Exception exception)
                            {
                                Console.Error.WriteLine("Animation Error: " + exception.Message);
                                Console.Error.WriteLine(exception.StackTrace);
                            }
                        };
                        storyboard.Begin();
                    }
                    catch (Exception exception)
                    {
                        Console.Error.WriteLine("Animation Error: " + exception.Message);
                        Console.Error.WriteLine(exception.StackTrace);
                    }
                };
                _storyboard.Begin();
            }
        }
        private Storyboard resetCenteredCurtains(double initialWidth, double initialHeight, Shape outInCurtain)
        {
            // OUT-IN curtain
            DoubleAnimation hAnimation = new DoubleAnimation
            {
                From = initialWidth,
                To = initialWidth,
                Duration = new Duration(TimeSpan.FromMilliseconds(1))
            };
            Storyboard.SetTarget(hAnimation, outInCurtain);
            Storyboard.SetTargetProperty(hAnimation, new PropertyPath(FrameworkElement.WidthProperty));
            Storyboard storyboard = new Storyboard();
            storyboard.Children.Add(hAnimation);
            DoubleAnimation vAnimation = new DoubleAnimation
            {
                From = initialHeight,
                To = initialHeight,
                Duration = new Duration(TimeSpan.FromMilliseconds(1))
            };
            Storyboard.SetTarget(vAnimation, outInCurtain);
            Storyboard.SetTargetProperty(vAnimation, new PropertyPath(FrameworkElement.HeightProperty));
            storyboard.Children.Add(vAnimation);
            storyboard.Begin();

            // IN-OUT curtain
            DoubleAnimation horizontalAnimation = new DoubleAnimation
            {
                From = 0.0,
                To = 0.0,
                Duration = new Duration(TimeSpan.FromMilliseconds(1))
            };
            Storyboard.SetTarget(horizontalAnimation, outInCurtain);
            Storyboard.SetTargetProperty(horizontalAnimation, new PropertyPath("OpacityMask.Drawing.Geometry.Children[1].RadiusX"));
            storyboard.Children.Add(horizontalAnimation);
            DoubleAnimation verticalAnimation = new DoubleAnimation
            {
                From = 0.0,
                To = 0.0,
                Duration = new Duration(TimeSpan.FromMilliseconds(1))
            };
            Storyboard.SetTarget(verticalAnimation, outInCurtain);
            Storyboard.SetTargetProperty(verticalAnimation, new PropertyPath("OpacityMask.Drawing.Geometry.Children[1].RadiusY"));
            storyboard.Children.Add(verticalAnimation);
            return storyboard;
        }
        public void DrawInstantaneously(InkCanvas canvas, StrokeCollection strokes)
        {
            // Clear the canvas
            canvas.Strokes.Clear();

            // Draw everything at once
            foreach (Stroke stroke in strokes)
            {
                canvas.Strokes.Add(new Stroke(stroke.StylusPoints.Clone(), stroke.DrawingAttributes.Clone()));
            }
        }
        public void DrawNewStroke(InkCanvas canvas, DrawingUpdate drawingUpdate)
        {
            try
            {
                if (drawingUpdate.RedrawRequired())
                {
                    canvas.Strokes.Clear();
                    drawingUpdate.image.paths.ForEach(path => canvas.Strokes.Add(GetDrawingStroke(path)));
                }
                else if (drawingUpdate.path.isNewStroke)
                {
                    canvas.Strokes.Add(GetDrawingStroke(drawingUpdate.path));
                }
                else
                {
                    int lastStrokeIndex = canvas.Strokes.Count - 1;
                    if (lastStrokeIndex >= 0)
                    {
                        drawingUpdate.path.points.ForEach(point => canvas.Strokes[lastStrokeIndex].StylusPoints.Add(new StylusPoint(point.X, point.Y)));
                    }
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine("Unable to draw stroke: " + ex.Message);
                Console.Error.WriteLine(ex.StackTrace);
            }
        }
        #endregion

        #region Strokes extractor
        private StrokeCollection GetDrawingStrokes(GameImage image, InkCanvas canvas)
        {
            StrokeCollection strokes = new StrokeCollection();
            try
            {
                // Build the stroke collection
                foreach (GameImagePath path in image.paths)
                {
                    StylusPointCollection strokePoints = new StylusPointCollection();
                    foreach (Point point in path.points) // Get all points
                    {
                        strokePoints.Add(new StylusPoint(point.X, point.Y));
                    }
                    Stroke stroke = new Stroke(strokePoints);
                    stroke.DrawingAttributes.Width = path.strokeWidth;
                    stroke.DrawingAttributes.Height = path.strokeWidth;
                    stroke.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(path.hexColor);
                    stroke.DrawingAttributes.StylusTip = (path.stylusPoint == "Ellipse") ? StylusTip.Ellipse : StylusTip.Rectangle;
                    strokes.Add(stroke); // Add the stroke
                }
                // Center the image
                CenterImage(strokes, canvas);
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("COULD'NT GET DRAWING STROKES: " + e.Message);
                Console.Error.WriteLine(e.StackTrace);
                return new StrokeCollection();
            }
            return strokes;
        }
        public Stroke GetDrawingStroke(GameImagePath path)
        {
            StylusPointCollection strokePoints = new StylusPointCollection();
            foreach (Point point in path.points) // Get all points
            {
                strokePoints.Add(new StylusPoint(point.X, point.Y));
            }
            Stroke stroke = new Stroke(strokePoints);
            stroke.DrawingAttributes.Width = path.strokeWidth;
            stroke.DrawingAttributes.Height = path.strokeWidth;
            stroke.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(path.hexColor);
            stroke.DrawingAttributes.StylusTip = (path.stylusPoint == "Ellipse") ? StylusTip.Ellipse : StylusTip.Rectangle;
            return stroke;
        }
        #endregion

        private void AnnounceFinished(InkCanvas canvas)
        {
            Application.Current.Dispatcher.Invoke(() =>
            {
                if (enableCanvasWhenDone)
                {
                    canvas.IsEnabled = true;
                }
            });
            DrawingFinished?.Invoke(this, EventArgs.Empty);
        }

        public static void reset()
        {
            Drawers.reset();
            foreach (Storyboard story in stories)
            {
                if (story != null) { story.Stop(); }
            }
        }
        public void Reset()
        {
            reset();
        }
        private void GetImageBounds(out double minX, out double minY, out double maxX, out double maxY, StrokeCollection strokes, InkCanvas canvas)
        {
            maxX = 0;
            maxY = 0;
            minX = canvas.ActualWidth;
            minY = canvas.ActualHeight;

            foreach (Stroke stroke in strokes)
            {
                foreach (StylusPoint point in stroke.StylusPoints)
                {
                    double strokeWidth = stroke.DrawingAttributes.Width;
                    if (point.X + strokeWidth > maxX)
                    {
                        maxX = point.X + strokeWidth;
                    }

                    if (point.Y + strokeWidth > maxY)
                    {
                        maxY = point.Y + strokeWidth;
                    }

                    if (point.X - strokeWidth < minX)
                    {
                        minX = point.X - strokeWidth;
                    }

                    if (point.Y - strokeWidth < minY)
                    {
                        minY = point.Y - strokeWidth;
                    }
                }
            }

            // Keep bounds in canvas limits
            minX = minX < 0 ? 0 : minX;
            minY = minY < 0 ? 0 : minY;
            maxX = maxX > canvas.ActualWidth ? canvas.ActualWidth : maxX;
            maxY = maxY > canvas.ActualHeight ? canvas.ActualHeight : maxY;
        }
        private void CenterImage(StrokeCollection strokes, InkCanvas canvas)
        {
            try
            {
                // Get the translations
                GetImageBounds(out double minX, out double minY, out double maxX, out double maxY, strokes, canvas);
                double translationX = (minX >= 0 && maxX <= canvas.ActualWidth) ? (canvas.ActualWidth - maxX - minX) * 0.5 : 0;
                double translationY = (minY >= 0 && maxY <= canvas.ActualHeight) ? (canvas.ActualHeight - maxY - minY) * 0.5 : 0;
                translationX = ((minX + translationX) < 0) || ((minX + translationX) > canvas.ActualWidth) ? 0 : translationX;
                translationY = ((minY + translationY) < 0) || ((minY + translationY) > canvas.ActualHeight) ? 0 : translationY;
                // Center the image
                foreach (Stroke stroke in strokes)
                {
                    for (int ptIndex = 0; ptIndex < stroke.StylusPoints.Count; ptIndex++)
                    {
                        stroke.StylusPoints[ptIndex] = new StylusPoint(stroke.StylusPoints[ptIndex].X + translationX, stroke.StylusPoints[ptIndex].Y + translationY);
                    }
                }
            }
            catch(Exception exception)
            {
                Console.Error.WriteLine("Unable to center images: " + exception.Message);
                Console.Error.WriteLine(exception.StackTrace);
            }
        }
    }
}
