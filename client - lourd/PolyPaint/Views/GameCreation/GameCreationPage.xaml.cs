using FMUD;
using PolyPaint.Models.Coms;
using PolyPaint.Services;
using System;
using System.Drawing;
using System.Windows;
using System.Windows.Controls;
using static PolyPaint.Models.Coms.Game;

namespace PolyPaint.Views.GameCreation
{
    /// <summary>
    /// Interaction logic for GameCreationPage.xaml
    /// </summary>
    public partial class GameCreationPage : UserControl
    {
        public event EventHandler GameCreationFinished;
        private readonly Game game = new Game(); // The game that is being created
        private LoadedImage image; // The image that will be used with Potrace

        public GameCreationPage()
        {
            InitializeComponent();
            DataContext = game;
        }

        private void Tutorial_Click(object sender, RoutedEventArgs e)
        {
            TutorialDialog.IsOpen = true;
        }

        private void resetWhenTutorialClosed(object sender, RoutedEventArgs e)
        {
            TutorialUC.resetWhenTutorialClosed(sender, e);
        }

        private void CancelButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            if (Transitioner.SelectedIndex == 1) { Transitioner.SelectedIndex = 0; Drawer.reset(); }
            else if (Transitioner.SelectedIndex == 2) { Transitioner.SelectedIndex = 0; }
            else if (Transitioner.SelectedIndex == 3) { Transitioner.SelectedIndex = 0; }
            else
            {
                GameCreationFinished(this, EventArgs.Empty);
            }
        }

        public void setUp()
        {
            SecretWord.Focus();
            game.image = null;
            Snackbar.IsActive = false;
            CreationProgress.Visibility = Visibility.Hidden;
            SubmitButton.IsEnabled = false;
        }

        private bool CredentialsAreValid()
        {
            return Clues.Text.Trim().Length > 0 && SecretWord.Text.Trim().Length > 0 && drawingMode.SelectedItem != null && game.image != null; ;
        }

        private void Update_SecretWord(object sender, RoutedEventArgs e)
        {
            // so the search updates not on each character typed, but when it's done being typed
            if (SecretWord.Text != "")
            {
                #region DROPPED FEATURE
                //InternetImagesViewer.setUp(SecretWord.Text);
                #endregion
            }
        }

        private void Credentials_Changed(object sender, RoutedEventArgs e)
        {
            // Update buttons
            if (SubmitButton != null)
            {
                SubmitButton.IsEnabled = CredentialsAreValid();
            }
            #region DROPPED FEATURE
            //InternetImageSearchButton.IsEnabled = SecretWord.Text.Trim() != "";
            #endregion
            // Update game
            game.clues = Clues.Text.Split(new char[] { '\n', '\r' }, StringSplitOptions.RemoveEmptyEntries);
            game.difficulty = (Difficulty)Difficulty.Value;
            if (DifficultyDescription != null)
            {
                switch (game.difficulty)
                {
                    case Game.Difficulty.Easy:
                        DifficultyDescription.Text = "Players will have 15 seconds and 5 (3 in ffa) trials to guess";
                        break;
                    case Game.Difficulty.Medium:
                        DifficultyDescription.Text = "Players will have 10 seconds and 5 (3 in ffa) trials to guess";
                        break;
                    case Game.Difficulty.Hard:
                        DifficultyDescription.Text = "Players will have 5 seconds and 5 (3 in ffa) trials to guess";
                        break;
                    default:
                        DifficultyDescription.Text = "";
                        break;
                }
            }

            if (drawingMode != null)
            {
                game.drawingMode = (DrawingMode)Enum.Parse(typeof(DrawingMode), (drawingMode.SelectedItem != null) ? ((ComboBoxItem)drawingMode.SelectedItem).Tag.ToString() : "0");
            }

            if (Drawer != null && Drawer.game != null)
            {
                Drawer.game.drawingMode = game.drawingMode;
            }
            if (game.drawingMode == DrawingMode.Panoramic || game.drawingMode == DrawingMode.Centered)
            {
                if (game.drawingMode == DrawingMode.Centered)
                {
                    panoramicDrawingMode1.Visibility = Visibility.Collapsed;
                    panoramicDrawingMode2.Visibility = Visibility.Collapsed;
                    panoramicDrawingMode3.Visibility = Visibility.Collapsed;
                    panoramicDrawingMode4.Visibility = Visibility.Collapsed;
                    centerDrawingMode1.Visibility = Visibility.Visible;
                    centerDrawingMode2.Visibility = Visibility.Visible;
                    drawingDirection.SelectedIndex = (drawingDirection.SelectedIndex < 4) ? 4 : drawingDirection.SelectedIndex;
                }
                else
                {
                    panoramicDrawingMode1.Visibility = Visibility.Visible;
                    panoramicDrawingMode2.Visibility = Visibility.Visible;
                    panoramicDrawingMode3.Visibility = Visibility.Visible;
                    panoramicDrawingMode4.Visibility = Visibility.Visible;
                    centerDrawingMode1.Visibility = Visibility.Collapsed;
                    centerDrawingMode2.Visibility = Visibility.Collapsed;
                    drawingDirection.SelectedIndex = (drawingDirection.SelectedIndex >= 4) ? 0 : drawingDirection.SelectedIndex;
                }
                drawingDirection.Visibility = Visibility.Visible;
                game.drawingDirection = (DrawingDirection)Enum.Parse(typeof(DrawingDirection), (drawingDirection.SelectedItem != null) ? ((ComboBoxItem)drawingDirection.SelectedItem).Tag.ToString() : "0");

                if (Drawer != null && Drawer.game != null)
                {
                    Drawer.game.drawingDirection = game.drawingDirection;
                }
            }

            else if (drawingDirection != null)
            {
                drawingDirection.Visibility = Visibility.Collapsed;
            }
        }

        private void Submit_Button_Click(object sender, RoutedEventArgs e)
        {
            ServerService.OnGameCreation((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    CreationProgress.Visibility = Visibility.Hidden;
                    SubmitButton.IsEnabled = true;
                    if (((STATE)state.state) == STATE.Success)
                    {
                        Snackbar.Message.Content = "Game created succesfully. Thank you for contributing!";
                        Snackbar.IsActive = true;
                        SecretWord.Text = "";
                        Clues.Text = "";
                        drawingMode.SelectedIndex = 0;
                        drawingDirection.SelectedIndex = 0;
                        Difficulty.Value = 0;
                    }
                    else
                    {
                        Snackbar.Message.Content = "Game creation failed :(. Try again later?";
                        Snackbar.IsActive = true;
                    }
                });
            });
            SubmitButton.IsEnabled = false;
            CreationProgress.Visibility = Visibility.Visible;
            ServerService.CreateGame(game);
        }

        private void CloseSnackbar(object sender, RoutedEventArgs e)
        {
            Snackbar.IsActive = false;
            Transitioner.SelectedIndex = 0;
            Drawer.reset();
            GameCreationFinished(this, EventArgs.Empty);
        }
        private void Image_Draw_Button_Click(object sender, RoutedEventArgs e)
        {
            RunImageViewer();
            Transitioner.SelectedIndex = 1;
        }

        private void ImageEditor_SaveImage(object sender, EventArgs e)
        {
            if (sender is FenetreDessin)
            {
                game.image = (sender as FenetreDessin).game.image;
            }
            else if (sender is QuickDrawUC)
            {
                game.image = QuickDrawViewer.game.image;
                game.secretWord = QuickDrawViewer.game.secretWord; // Save the secret word as well
                Image_Draw_Button_Click(null, null); // Transit back to the creation page !
            }
            else if (sender is InternetImagesUC)
            {
                Transitioner.SelectedIndex = 1;
                image = new LoadedImage((sender as InternetImagesUC).image, "internetImage.bmp");
                #region DROPPED FEATURE
                //PotraceOptionsDialog.IsOpen = true;
                #endregion
            }
            Credentials_Changed(null, null);
        }
        private void RunImageViewer()
        {
            Drawer.setUp(
                canRedraw: true,
                game: new Game(null, null, Game.Difficulty.Creation, game.drawingMode, game.drawingDirection, game.image),
                drawImmediately: (game.image != null)
            );
        }

        #region DROPPED FEATURES
        //private void Image_Load_Click(object sender, RoutedEventArgs e)
        //{
        //    OpenFileDialog dialog = new OpenFileDialog();
        //    dialog.Title = "Choose an image";
        //    dialog.Filter = "Image files only (*.png;*.jpeg;*.jpg;*.bmp)|*.png;*.jpeg;*.jpg;*.bmp|All files (*.*)|*.*";
        //    if (dialog.ShowDialog() == true)
        //    {
        //        image = new LoadedImage(Bitmap.FromFile(dialog.FileName) as Bitmap, dialog.FileName);

        //        // Get options
        //        PotraceOptionsDialog.IsOpen = true;
        //    }
        //}
        //private void Potrace_Load_Click(object sender, RoutedEventArgs e)
        //{
        //    PotraceLoadButton.IsEnabled = false;
        //    PotraceOptionsDialog.IsOpen = false;

        //    // Save the image
        //    string svgString = (new Utilities.ImageConverter()).GetSvgStringFromPotrace(
        //        image.bitmap,
        //        image.fileName,
        //        new Utilities.PotraceOptions(
        //            OptimzeCurve.IsChecked == true,
        //            WhiteShapeOpaque.IsChecked == true,
        //            InvertColors.IsChecked == true,
        //            Rotate.IsChecked == true,
        //            (Rotate.IsChecked == true) ? int.Parse(Rotation.Text) : 90
        //        )
        //    );
        //    game.image = (new GameCreationService()).GetImageFromSvg(svgString);
        //    Credentials_Changed(null, null);

        //    // Run image viewer and transit
        //    RunImageViewer();
        //    Transitioner.SelectedIndex = 1;
        //}
        //private void PotraceOptionsDialog_DialogOpened(object sender, MaterialDesignThemes.Wpf.DialogOpenedEventArgs eventArgs)
        //{
        //    PotraceLoadButton.IsEnabled = true;
        //}
        private void QuickDrawButton_Click(object sender, RoutedEventArgs e)
        {
            QuickDrawViewer.setUp();
            Transitioner.SelectedIndex = 2;
        }
        //private void InternetImageSearchButton_Click(object sender, RoutedEventArgs e)
        //{
        //    Transitioner.SelectedIndex = 3;
        //    InternetImagesViewer.setUp(SecretWord.Text);
        //}
        #endregion
    }

    struct LoadedImage
    {
        public Bitmap bitmap;
        public string fileName;

        public LoadedImage(Bitmap bitmap, string fileName)
        {
            this.bitmap = bitmap;
            this.fileName = fileName;
        }
    }
}
