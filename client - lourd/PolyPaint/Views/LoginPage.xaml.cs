using PolyPaint.Models.Coms;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for LoginPage.xaml
    /// </summary>
    public partial class LoginPage : UserControl
    {
        private readonly List<string> ErrorTexts;
        public event EventHandler LoggedIn;
        public event EventHandler SignUp;
        public LoginPage()
        {
            InitializeComponent();
            ErrorTexts = new List<string>{
                "This password doesn't match the username",
                "A user is already connected with this username"
            };
        }
        public void SetConnectionListener()
        {
            ServerService.Off(SocketRoutes.Login);
            ServerService.OnLoginMessage((response) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    switch ((STATE)response.state)
                    {
                        case STATE.Success:
                            LoggedIn(this, EventArgs.Empty);
                            ErrorText.Text = "";
                            break;
                        case STATE.BadPassword:
                            ErrorText.Text = ErrorTexts[0];
                            ErrorText.Visibility = Visibility.Visible;
                            Password.Focus();
                            break;
                        case STATE.AlreadyConnected:
                            ErrorText.Text = ErrorTexts[1];
                            ErrorText.Visibility = Visibility.Visible;
                            Nickname.Focus();
                            break;
                        default:
                            ErrorText.Visibility = Visibility.Hidden;
                            break;
                    }
                });
            });
        }
        private void Login_Button_Click(object sender, RoutedEventArgs e)
        {
            ServerService.Login(Nickname.Text, Password.Password);
            Nickname.Text = "";
            Password.Password = "";
        }

        private void Sign_Up_Click(object sender, RoutedEventArgs e)
        {
            SignUp(this, EventArgs.Empty);
        }

        private void Credentials_Changed(object sender, RoutedEventArgs e)
        {
            LoginButton.IsEnabled = Password.Password.Trim().Length > 0 && Nickname.Text.Trim().Length > 0;
        }
        private void Password_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return && LoginButton.IsEnabled)
            {
                ServerService.Login(Nickname.Text, Password.Password);
                Nickname.Text = "";
                Password.Password = "";
            }
        }
        public void setUp()
        {
            Visibility = Visibility.Visible;
            SetConnectionListener();
            Nickname.Focus();
            Password.Password = "";
        }
    }
}
