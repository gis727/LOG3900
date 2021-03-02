using FMUD.VueModeles;
using MaterialDesignThemes.Wpf;
using PolyPaint.Models.Coms;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;

namespace FMUD.Views.LiveGame
{
    /// <summary>
    /// Interaction logic for GameDrawer.xaml
    /// </summary>
    public partial class GameDrawer : UserControl
    {
        private readonly DrawingService drawer;
        private readonly GameCreationService creator; // for strokes conversion
        private bool waitingforStrokes = false;
        private StrokeGatherer strokeGatherer;
        private bool BlinkOn = false;
        private int nbBlinks = 0;

        public GameDrawer()
        {
            InitializeComponent();
            DataContext = new VueModele();
            drawer = new DrawingService();
            creator = new GameCreationService();
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            if (nbBlinks < 16 && drawerText.Text == "Your turn! Draw: ")
            {
                if (BlinkOn)
                {
                    drawerText.Foreground = Brushes.CornflowerBlue;
                }
                else
                {
                    drawerText.Foreground = Brushes.White;
                }
                BlinkOn = !BlinkOn;
                nbBlinks++;
            }
        }

        #region Setup/Reset
        public void start(Snackbar snackbar, Game game = null, DrawingUpdate drawingUpdate = null)
        {
            enableAvatarSaving();
            strokeGatherer = new StrokeGatherer();
            waitingforStrokes = false;
            if (drawingUpdate != null) // draw stroke
            {
                drawer.DrawNewStroke(surfaceDessin, drawingUpdate);
            }
            else if (game != null) // Thats a new game
            {

                surfaceDessin.Strokes.Clear();
                surfaceDessin.Children.Clear();
                if (game.selectedDrawer.Equals(Game.SelectedDrawer.CurrentClient)) // Its our turn to draw
                {
                    // blinking ignore shitty code
                    if (drawerText.Text != "Your turn! Draw: ")
                    {
                        nbBlinks = 0;
                        DispatcherTimer timer = new DispatcherTimer();
                        timer.Tick += timer_Tick;
                        timer.Interval = TimeSpan.FromMilliseconds(100);
                        timer.Start();
                    }
                    // blinking

                    drawer.Reset();
                    drawerText.Text = "Your turn! Draw: ";
                    drawerText.Foreground = Brushes.White;
                    secretWordText.Foreground = Brushes.Pink;
                    secretWordText.Text = game.secretWord;
                    secretWordText.FontSize = 40;
                    secretWordText.FontWeight = FontWeights.UltraBold;
                    surfaceDessin.IsEnabled = true;
                    waitingforStrokes = true;

                }
                else if (game.selectedDrawer.Equals(Game.SelectedDrawer.AnotherClient)) // Another player is drawing. We wait for strokes
                {
                    strokeGatherer = null;
                    drawer.Reset();
                    drawerText.Text = "Someone else is drawing...";
                    drawerText.Foreground = Brushes.LightGray;
                    secretWordText.Foreground = Brushes.LightGray;
                    secretWordText.FontWeight = FontWeights.Normal;
                    secretWordText.FontSize = 20;
                    secretWordText.Text = "Guess with \".g\" in chat e.g: \".g Apple\"\nGet clues with \".c\"";
                    surfaceDessin.IsEnabled = false;
                    // Just wait...
                }
                else if (game.selectedDrawer.Equals(Game.SelectedDrawer.AVirtualPlayer)) // Virtual player (the usual stuff...)
                {
                    drawerText.Text = "A virtual player is drawing...";
                    drawerText.Foreground = Brushes.LightGray;
                    secretWordText.Foreground = Brushes.LightGray;
                    secretWordText.FontWeight = FontWeights.Normal;
                    secretWordText.FontSize = 20;
                    secretWordText.Text = "Guess with \".g\" in chat e.g: \".g Apple\"\nGet clues with \".c\"";
                    drawer.Draw(surfaceDessin, panoramicCurtain, outInCurtain, game, false);
                    surfaceDessin.IsEnabled = false;
                }
            }
        }
        public void prepareNewGame()
        {
            surfaceDessin.IsEnabled = false;
        }
        public void setUp()
        {
            OutilCrayonSelected(null, EventArgs.Empty);
            PointeRondeSelected(null, EventArgs.Empty);
            if (DataContext as VueModele != null)
            {
                (DataContext as VueModele).CouleurSelectionnee = "Black";
            }
        }
        #endregion

        #region Coming from PolyPaint
        private int selectedPointe = 0;
        private int selectedOutils = 0;
        private void SupprimerSelection(object sender, RoutedEventArgs e) => surfaceDessin.CutSelection();
        private void PointeCarreSelected(object sender, System.EventArgs e)
        {
            if (DataContext as VueModele != null)
            {
                (DataContext as VueModele).editeur.ChoisirPointe("carree");
            }
            selectedPointe = 1;
        }
        private void PointeRondeSelected(object sender, System.EventArgs e)
        {
            if (DataContext as VueModele != null)
            {
                (DataContext as VueModele).editeur.ChoisirPointe("ronde");
            }
            selectedPointe = 0;
        }
        private void OutilCrayonSelected(object sender, System.EventArgs e)
        {
            if (DataContext as VueModele != null)
            {
                (DataContext as VueModele).editeur.ChoisirOutil("crayon");
            }
            selectedOutils = 0;
        }
        private void OutilEffaceTraitSelected(object sender, System.EventArgs e)
        {
            if (DataContext as VueModele != null)
            {
                (DataContext as VueModele).editeur.ChoisirOutil("efface_trait");
            }
            selectedOutils = 2;
        }
        private void OutilEffaceSegmentSelected(object sender, System.EventArgs e)
        {
            if (DataContext as VueModele != null)
            {
                (DataContext as VueModele).editeur.ChoisirOutil("efface_segment");
            }
            selectedOutils = 1;
        }
        private void SelectionOutilsChanged(object sender, System.EventArgs e)
        {
            if (OutilsList.SelectedIndex == -1)
            {
                OutilsList.SelectedIndex = selectedOutils;
            }
        }
        private void SelectionPointeChanged(object sender, System.EventArgs e)
        {
            if (PointeList.SelectedIndex == -1)
            {
                PointeList.SelectedIndex = selectedPointe;
            }
        }
        #endregion

        #region Strokes handling during FFA
        private void surfaceDessin_PreviewMouseMove(object sender, MouseEventArgs e)
        {
            if (waitingforStrokes && e.LeftButton == MouseButtonState.Pressed)
            {
                strokeGatherer.OnMouseMove(surfaceDessin, creator, DataContext as VueModele);
            }
        }
        private void surfaceDessin_PreviewMouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            if (waitingforStrokes)
            {
                strokeGatherer.OnMousUp(surfaceDessin, creator, DataContext as VueModele);
            }
        }
        private void surfaceDessin_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (waitingforStrokes)
            {
                strokeGatherer.OnMouseDown(surfaceDessin, creator, DataContext as VueModele);
            }
        }
        private void surfaceDessin_StrokeErased(object sender, RoutedEventArgs e)
        {
            strokeGatherer.SendStroke(surfaceDessin, creator, DataContext as VueModele, true);
        }
        private void surfaceDessin_StrokeCollected(object sender, InkCanvasStrokeCollectedEventArgs e)
        {
            if (!waitingforStrokes && !surfaceDessin.IsEnabled)
            {
                surfaceDessin.Strokes.Remove(e.Stroke); e.Handled = true;
            }
        }
        #endregion

        #region Avatar saving
        private void saveAsAvatarButton_Click(object sender, RoutedEventArgs e)
        {
            ServerService.OnAvatarModified((response) =>
            {
                if ((STATE)response.state == STATE.Success)
                {
                    Application.Current.Dispatcher.InvokeAsync(() =>
                    {
                        MatSnackBar.MessageQueue = new SnackbarMessageQueue(TimeSpan.FromSeconds(2));
                        MatSnackBar.MessageQueue.Enqueue("Avatar saved !");
                        enableAvatarSaving();
                    });
                }
            });
            disableAvatarSaving();
            ServerService.ModifyAvatar((new ImageConverter()).GetBase64FromCanvas(surfaceDessin));
        }
        private void enableAvatarSaving()
        {
            saveAsAvatarButton.IsEnabled = true;
            MatProgress.Visibility = Visibility.Collapsed;
        }
        private void disableAvatarSaving()
        {
            saveAsAvatarButton.IsEnabled = false;
            MatProgress.Visibility = Visibility.Visible;
        }
        #endregion
    }
}

class StrokeGatherer
{
    private const short STROKE_SIZE = 5; // Increasing this impacts negatively the v-frame rate
    private readonly List<Point> points = new List<Point>(STROKE_SIZE);
    public bool isNewStroke = true;

    public void OnMousUp(InkCanvas drawer, GameCreationService creator, VueModele model)
    {
        SendStroke(drawer, creator, model);
        points.Clear();
    }

    public void OnMouseDown(InkCanvas drawer, GameCreationService creator, VueModele model)
    {
        points.Clear();
        isNewStroke = true;
        SendStroke(drawer, creator, model);
    }

    public void OnMouseMove(InkCanvas drawer, GameCreationService creator, VueModele model)
    {
        if (!model.EraserSelected())
        {
            points.Add(Mouse.GetPosition(drawer));
            if (points.Count % STROKE_SIZE == 0)
            {
                SendStroke(drawer, creator, model);
            }
        }
    }

    public void SendStroke(InkCanvas drawer, GameCreationService creator, VueModele model, bool ptsToErase = false)
    {
        if (ptsToErase)
        {
            ServerService.SendDrawingUpdate(new DrawingUpdate(null, creator.GetImageFromCanvas(drawer)));
        }
        else if (points.Count > 0)
        {
            Stroke stroke = new Stroke(new StylusPointCollection(points))
            {
                DrawingAttributes = drawer.DefaultDrawingAttributes
            };
            GameImagePath path = creator.GetPath(stroke, drawer);

            if (path != null)
            {
                path.isNewStroke = isNewStroke;
                DrawingUpdate drawingUpdate = new DrawingUpdate(null, creator.GetImageFromCanvas(drawer));
                drawingUpdate.image.paths.Add(path);
                ServerService.SendDrawingUpdate(drawingUpdate);
            }
            isNewStroke = false;
        }
    }
}
