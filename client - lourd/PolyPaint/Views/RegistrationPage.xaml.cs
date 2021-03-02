using Microsoft.Win32;
using PolyPaint.Models.Coms;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media.Imaging;


namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for Registration.xaml
    /// </summary>
    public partial class RegistrationPage : UserControl
    {
        public event EventHandler Registered;
        public event EventHandler Canceled;
        private bool avatarSelected = false;

        public RegistrationPage()
        {
            InitializeComponent();
        }
        public void SetConnectionListener()
        {
            ServerService.Off(SocketRoutes.Login);
            ServerService.Off(SocketRoutes.CreateProfile);
            ServerService.OnLoginMessage((response) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    if ((STATE)response.state == STATE.Success)
                    {
                        Registered(this, EventArgs.Empty);
                    }
                });
            });
            ServerService.OnProfileCreation((response) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    if ((STATE)response.state == STATE.UsedNickname)
                    {
                        SubmitButton.IsEnabled = true;
                        ErrorText.Text = "Username is already in use";
                    }
                });
            });
        }

        private void Cancel_Button_Click(object sender, RoutedEventArgs e)
        {
            Canceled(this, EventArgs.Empty);
        }

        private void Submit_Button_Click(object sender, RoutedEventArgs e)
        {
            if (avatarSelected == false)
            {
                AvatarImage.Source = new BitmapImage(new Uri("../Resources/avatar.jpg", UriKind.Relative));
            }
            ServerService.CreateProfile(FirstName.Text, LastName.Text, Password.Password, NickName.Text,
(new ImageConverter()).ToBase64String((BitmapImage)AvatarImage.Source, AvatarImage.Source.ToString()));
            SubmitButton.IsEnabled = false;
        }

        private void Credentials_Changed(object sender, RoutedEventArgs e)
        {
            SubmitButton.IsEnabled = CredentialsAreValid();
        }

        private bool CredentialsAreValid()
        {
            return Password.Password.Trim().Length > 0 && FirstName.Text.Trim().Length > 0 &&
                                     LastName.Text.Trim().Length > 0 && NickName.Text.Trim().Length > 0;
        }

        public void setUp()
        {
            Visibility = Visibility.Visible;
            SetConnectionListener();
        }

        public void reset()
        {
            FirstName.Text = "";
            LastName.Text = "";
            NickName.Text = "";
            Password.Password = "";
            ErrorText.Text = "";
            AvatarIcon.Visibility = Visibility.Visible;
            AvatarImage.Visibility = Visibility.Collapsed;
            SubmitButton.IsEnabled = false;
            avatarSelected = false;
        }

        private void Avatar_Button_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog
            {
                Title = "Choose an avatar",
                Filter = "Image files only (*.png;*.jpeg;*.jpg;*.bmp)|*.png;*.jpeg;*.jpg;*.bmp"
            };
            if (dialog.ShowDialog() == true)
            {
                AvatarImage.Source = new BitmapImage(new Uri(dialog.FileName));
                AvatarImage.Visibility = Visibility.Visible;
                AvatarIcon.Visibility = Visibility.Collapsed;
                avatarSelected = true;
                SubmitButton.IsEnabled = CredentialsAreValid();
            }
        }
    }
}
