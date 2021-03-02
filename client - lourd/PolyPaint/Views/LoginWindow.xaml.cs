using PolyPaint.Models;
using PolyPaint.Services;
using System;
using System.Windows;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for LoginWindow.xaml
    /// </summary>
    public partial class LoginWindow : Window
    {
        public LoginWindow()
        {
            InitializeComponent();
            LoginPage.setUp();
        }

        private void Close_Button_Click(object sender, EventArgs e)
        {
            Close();
        }
        private void OnLogin(object sender, EventArgs e)
        {
            MainWindow mainWindow = new MainWindow
            {
                Owner = this
            };
            mainWindow.Show();
            mainWindow.Closed += On_Chat_Closed;
            Hide();
        }

        private void OnLoginFirstTime(object sender, EventArgs e)
        {
            MainWindow mainWindow = new MainWindow
            {
                Owner = this
            };
            mainWindow.Show();
            mainWindow.Closed += On_Chat_Closed;
            mainWindow.MainLobbyPage.TutorialDialog.IsOpen = true;
            mainWindow.MainLobbyPage.TutorialUC.LaunchCompleteTutorial(null, null);
            Hide();
        }

        // should be called on main window closed.
        private void On_Chat_Closed(object sender, EventArgs e)
        {
            if (((MainWindow)sender).closing)
            {
                Close();
            }
            else
            {
                SharedChatModel.dispose();
                Registration_Canceled(sender, e);
                Show();
                ServerService.Logout((o) =>
                {
                    System.Diagnostics.Process.Start(Application.ResourceAssembly.Location);
                    Application.Current.Shutdown();
                });
            }
        }

        private void Sign_Up_Click(object sender, EventArgs e)
        {
            LoginPage.Visibility = Visibility.Collapsed;
            RegistrationPage.setUp();
        }

        private void Registration_Canceled(object sender, EventArgs e)
        {
            LoginPage.setUp();
            RegistrationPage.Visibility = Visibility.Collapsed;
        }

        private void LoginPage_Loaded(object sender, RoutedEventArgs e)
        {

        }
    }
}
