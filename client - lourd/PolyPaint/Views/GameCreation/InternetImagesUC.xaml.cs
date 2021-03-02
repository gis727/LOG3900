using FMUD.Utilities;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media.Imaging;

namespace PolyPaint.Views.GameCreation
{
    /// <summary>
    /// Interaction logic for InternetImagesUC.xaml
    /// </summary>
    public partial class InternetImagesUC : UserControl
    {
        public event EventHandler SaveImage;
        private readonly ImagesFetcher imagesFetcher = new ImagesFetcher();
        private List<string> imagesLinks;
        public Bitmap image = null;

        public InternetImagesUC()
        {
            InitializeComponent();
        }

        public void setUp(string secretWord)
        {
            SecretWord.Text = secretWord;
            imagesFetcher.reset();
            UpdateImagesSources();
        }

        private void LoadNextImagesButton_Click(object sender, RoutedEventArgs e)
        {
            UpdateImagesSources();
        }

        private void UpdateImagesSources()
        {
            LoadNextImagesButton.IsEnabled = false;
            imagesLinks = imagesFetcher.GetNextImages(SecretWord.Text);
            if (imagesLinks != null && imagesLinks.Count >= 4)
            {
                Img1.Source = new BitmapImage(new Uri(@imagesLinks[0]));
                Img2.Source = new BitmapImage(new Uri(@imagesLinks[1]));
                Img3.Source = new BitmapImage(new Uri(@imagesLinks[2]));
                Img4.Source = new BitmapImage(new Uri(@imagesLinks[3]));
            }
            LoadNextImagesButton.IsEnabled = true;
        }

        private void Img_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            System.Windows.Controls.Image img = (System.Windows.Controls.Image)sender;
            int index = int.Parse(img.Tag.ToString());
            // Save the selected image
            image = (new Utilities.ImageConverter()).BitmapImageToBitmap((BitmapImage)(img.Source), imagesLinks[index]);
            // Get back to the creation page
            if (image != null)
            {
                SaveImage?.Invoke(this, EventArgs.Empty);
            }
        }
    }
}
