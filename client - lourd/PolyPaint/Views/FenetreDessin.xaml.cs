using FMUD.VueModeles;
using PolyPaint.Models.Coms;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Windows;
using System.Windows.Controls;

namespace FMUD
{
    public partial class FenetreDessin : UserControl
    {
        public Game game = null;
        public event EventHandler SaveImage;
        private int selectedPointe = 0;
        private int selectedOutils = 0;
        public FenetreDessin()
        {
            InitializeComponent();
            DataContext = new VueModele();
        }

        private void SupprimerSelection(object sender, RoutedEventArgs e) => surfaceDessin.CutSelection();

        public void SaveButton_Click(object sender, RoutedEventArgs e)
        {
            game.image = (new GameCreationService()).GetImageFromCanvas(surfaceDessin);
            SaveImage?.Invoke(this, EventArgs.Empty);
            SaveButton.IsEnabled = false;
        }

        private void saveAsAvatarButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                ServerService.OnAvatarModified((response) =>
                {
                    if ((STATE)response.state == STATE.Success)
                    {
                        ServerService.GetAccountInfo();
                    }

                    Application.Current.Dispatcher.InvokeAsync(() =>
                    {
                        MatProgress.Visibility = Visibility.Collapsed;
                    });
                });
                ServerService.ModifyAvatar((new ImageConverter()).GetBase64FromCanvas(surfaceDessin));
                saveAsAvatarButton.IsEnabled = false;
                MatProgress.Visibility = Visibility.Visible;
            }
            catch(Exception exception)
            {
                Console.Error.WriteLine("Unable to save the image as an avatar: " + exception.Message);
                Console.Error.WriteLine(exception.StackTrace);
            }
        }

        private void seeDrawing_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                seeDrawingButton.IsEnabled = false;
                saveAsAvatarButton.IsEnabled = false;
                bool saveBtnStateBeforeDrawing = SaveButton.IsEnabled;
                SaveButton.IsEnabled = false;
                GameCreationService creator = new GameCreationService();
                creator.Redraw(surfaceDessin, panoramicCurtain, outInCurtain, game);
                creator.DrawingFinished += (object s, EventArgs _e) =>
                {
                    Application.Current.Dispatcher.Invoke(() =>
                    {
                        seeDrawingButton.IsEnabled = true;
                        saveAsAvatarButton.IsEnabled = true;
                        SaveButton.IsEnabled = saveBtnStateBeforeDrawing;
                    });
                };
            }
            catch (Exception exception)
            {
                Console.Error.WriteLine("Unable to show the drawing: " + exception.Message);
                Console.Error.WriteLine(exception.StackTrace);
            }
        }

        public void setUp(bool canRedraw, Game game, bool drawImmediately = true)
        {
            reset();
            this.game = game;
            (DataContext as VueModele).CanRedraw = canRedraw;
            surfaceDessin.Strokes.Clear(); // Clear the canvas
            surfaceDessin.IsEnabled = true;
            seeDrawingButton.IsEnabled = (game.image != null || surfaceDessin.Strokes.Count != 0);
            saveAsAvatarButton.IsEnabled = seeDrawingButton.IsEnabled;
            SaveButton.IsEnabled = seeDrawingButton.IsEnabled;
            MatProgress.Visibility = Visibility.Collapsed;
            if (drawImmediately)
            {
                seeDrawing_Click(null, null);
            }
        }

        // The user draw something
        private void surfaceDessin_StrokeCollected(object sender, InkCanvasStrokeCollectedEventArgs e)
        {
            seeDrawingButton.IsEnabled = true;
            saveAsAvatarButton.IsEnabled = true;
            SaveButton.IsEnabled = true;
        }

        public void reset()
        {
            DrawingService.reset(); // Stop any ongoing drawing
        }

        #region From polypaint
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
    }
}
