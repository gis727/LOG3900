using FMUD.Utilities;
using PolyPaint.Models.Coms;
using PolyPaint.Services;
using System;
using System.Windows.Controls;

namespace PolyPaint.Views.GameCreation
{
    /// <summary>
    /// Interaction logic for QuickDrawUC.xaml
    /// </summary>
    public partial class QuickDrawUC : UserControl
    {
        private QuickDrawProvider drawingsProvider = null;
        public Game game = null;
        public event EventHandler SaveImage;
        public QuickDrawUC()
        {
            InitializeComponent();
        }

        public void setUp()
        {
            // Initialize the drawings provider
            if (drawingsProvider == null)
            {
                drawingsProvider = new QuickDrawProvider();
            }
            // Clear any previously saved game
            game = null;
            // Load a drawing
            LoadNextDrawingButton_Click(null, null);
        }

        private void LoadNextDrawingButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            game = drawingsProvider.GetNextGame();
            if (game != null)
            {
                GameCreationService creator = new GameCreationService();
                creator.Redraw(surfaceDessin, null, null, game);
                SecretWord.Text = game.secretWord;
            }
        }

        private void AcceptDrawingButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            SaveImage?.Invoke(this, EventArgs.Empty);
        }
    }
}
