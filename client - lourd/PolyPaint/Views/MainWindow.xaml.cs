using MaterialDesignThemes.Wpf;
using PolyPaint.Models;
using PolyPaint.Services;
using PolyPaint.ViewModels;
using System;
using System.Windows;
using System.Windows.Controls;
namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public bool closing = true;
        public MainWindow()
        {
            InitializeComponent();
            WindowStartupLocation = System.Windows.WindowStartupLocation.CenterScreen;
            DataContext = new MainWindowVM();
            SharedChatModel.reset();
            LogoutButton.Click += (s, ee) =>
            {
                closing = false;
                Close();
            };

            ChatUC.PopoutButton.Click += (s, ee) =>
            {
                ChatUC.Visibility = Visibility.Collapsed;
                ChatWindow chatWindow = new ChatWindow
                {
                    Owner = this
                };
                chatWindow.Show();

                chatWindow.Closed += On_Windowed_Chat_Closed;
            };

            MainLobbyPage.Transitioner.SelectionChanged += ToggleProfileVisibilty;
            // Connection issue handler
            ServerService.OnConnectionError((o) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    GlobalSnackbar.MessageQueue = new SnackbarMessageQueue(TimeSpan.FromDays(1));
                    GlobalSnackbar.MessageQueue.Enqueue("You seem to have a connection issue. Please log back in.", "OK", () =>
                    {
                        closing = false;
                        Close();
                    });
                });
            });
        }

        private void ToggleProfileVisibilty(object sender, SelectionChangedEventArgs e)
        {
            ProfilePanel.Visibility = MainLobbyPage.Transitioner.SelectedIndex == 1 ? Visibility.Collapsed : Visibility.Visible;
        }

        private void PopOut_Button_Click(object sender, RoutedEventArgs e)
        {
            ChatUC.Visibility = Visibility.Collapsed;
            ChatWindow chatWindow = new ChatWindow
            {
                Owner = this
            };
            chatWindow.Show();
            chatWindow.Closed += On_Windowed_Chat_Closed;
        }

        private void On_Windowed_Chat_Closed(object sender, EventArgs e)
        {
            ChatUC.Visibility = Visibility.Visible;
        }

        private void ProfileUC_Loaded(object sender, RoutedEventArgs e)
        {

        }

        private void CreateGame_Click(object sender, EventArgs e)
        {
            Transitioner.SelectedIndex = 1;
            GameCreationPage.setUp();
        }

        private void GameCreation_Finished(object sender, EventArgs e)
        {
            Transitioner.SelectedIndex = 0;
        }

        private void MainLobbyPage_KickedOut(object sender, EventArgs e)
        {
            GlobalSnackbar.MessageQueue = new SnackbarMessageQueue(TimeSpan.FromSeconds(5));
            GlobalSnackbar.MessageQueue.Enqueue("You've been kicked out !", "OK", () => { /*...*/ });
        }
    }
}
