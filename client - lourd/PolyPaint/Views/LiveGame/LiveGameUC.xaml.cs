using MaterialDesignThemes.Wpf;
using PolyPaint.Models;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media.Animation;

namespace FMUD.Views.LiveGame
{
    /// <summary>
    /// Interaction logic for LiveGameUC.xaml
    /// </summary>
    public partial class LiveGameUC : UserControl
    {
        public event EventHandler<PartyLeftEventArgs> PartyOver;
        public LiveGameUC()
        {
            InitializeComponent();
            ServerService.OnStartGame((game) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    DrawerUC.start(Snackbar, game, null);
                    Stats.setUp();
                    SharedChatModel.GameMode = true;
                    SharedChatModel.SetMessage(MessageInterpreter.GameGuessPrefix + " ");

                });
            });

            ServerService.OnEndParty((finalStats) =>
            {
                Application.Current.Dispatcher.InvokeAsync(() =>
                {
                    Snackbar.MessageQueue = new SnackbarMessageQueue(TimeSpan.FromDays(1));
                    Snackbar.MessageQueue.Enqueue("Party is over!", "Leave", () => { PartyOver?.Invoke(this, new PartyLeftEventArgs(false)); });
                    SharedChatModel.GameMode = false;
                    SharedChatModel.SetMessage("");

                    score.Text = finalStats.score.ToString();
                    winText.Visibility = Visibility.Visible;
                    Stats.Visibility = Visibility.Hidden;
                    DrawerUC.Visibility = Visibility.Hidden;
                    con.Visibility = finalStats.isWinner ? Visibility.Visible : Visibility.Collapsed;
                    fetti.Visibility = finalStats.isWinner ? Visibility.Visible : Visibility.Collapsed;
                    scoreText.Text = finalStats.isWinner ? "You won!!! With" : "Game over! You finished with";
                    ServerService.GetAccountInfo(); // Update profile with new data
                });
            });

            ServerService.OnDrawingUpdate((drawingUpdate) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    DrawerUC.start(Snackbar, null, drawingUpdate);
                });
            });
            #region DROPPED FEATURE
            //ServerService.OnKicked(() =>
            //{
            //    Application.Current.Dispatcher.Invoke(() =>
            //    {
            //        PartyOver?.Invoke(this, new PartyLeftEventArgs(true));
            //    });
            //});
            #endregion

            // game JUICE
            ServerService.OnAnswer((action) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {

                    if (action)
                    {
                        DoubleAnimation animation = new DoubleAnimation(55, new Duration(new TimeSpan(0, 0, 1)))
                        {
                            EasingFunction = new BounceEase()
                        };

                        animation.Completed += (s, e) =>
                        {
                            DoubleAnimation endanimation = new DoubleAnimation(45, new Duration(new TimeSpan(0, 0, 1)));
                            Stats.Score.BeginAnimation(TextBlock.FontSizeProperty, endanimation);
                        };
                        Stats.Score.BeginAnimation(TextBlock.FontSizeProperty, animation);

                    }
                    else
                    {

                        DoubleAnimation animation = new DoubleAnimation(0, new Duration(new TimeSpan(0, 0, 0, 0, 500)))
                        {
                            EasingFunction = new ElasticEase()
                        };

                        animation.Completed += (s, e) =>
                        {
                            DoubleAnimation endanimation = new DoubleAnimation(1, new Duration(new TimeSpan(0, 0, 0, 0, 500)));
                            Stats.TriesLeft.BeginAnimation(TextBlock.OpacityProperty, endanimation);
                        };
                        Stats.TriesLeft.BeginAnimation(TextBlock.OpacityProperty, animation);

                    }
                });
            });
        }

        private void Tutorial_Click(object sender, RoutedEventArgs e)
        {
            TutorialDialog.IsOpen = true;
        }

        private void resetWhenTutorialClosed(object sender, RoutedEventArgs e)
        {
            TutorialUC.resetWhenTutorialClosed(sender, e);
        }
        public void setUp()
        {
            Snackbar.IsActive = false;
            winText.Visibility = Visibility.Collapsed;
            con.Visibility = Visibility.Collapsed;
            fetti.Visibility = Visibility.Collapsed;
            Stats.Visibility = Visibility.Visible;
            DrawerUC.Visibility = Visibility.Visible;
            DrawerUC.setUp();
        }

        private void Stats_StatsUpdated(object sender, EventArgs e)
        {
            if (Stats.CurrentTime.TotalSeconds <= 1)
            {
                DrawerUC.prepareNewGame();
            }
        }
    }
}
