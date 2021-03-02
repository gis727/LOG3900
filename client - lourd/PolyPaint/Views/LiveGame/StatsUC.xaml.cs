using FMUD.Models.Coms;
using PolyPaint.Services;
using System;
using System.Windows;
using System.Windows.Controls;

namespace FMUD.Views.LiveGame
{
    /// <summary>
    /// Interaction logic for ChronoUC.xaml
    /// </summary>
    public partial class StatsUC : UserControl
    {
        public event EventHandler StatsUpdated;
        public TimeSpan CurrentTime;

        public StatsUC()
        {
            InitializeComponent();
            DataContext = new GameStats();
        }

        public void setUp()
        {
            CurrentTime = new TimeSpan(0, 0, 60);

            // Set listener
            ServerService.OnUpdateStats((newStats) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    // Update stats
                    GameStats currentStats = (GameStats)DataContext;
                    if (newStats.score > currentStats.score)
                    {
                        // make particle appear here
                    }
                    currentStats.score = newStats.score;
                    currentStats.timeLeft = newStats.timeLeft;
                    currentStats.trialsLeft = newStats.trialsLeft;
                    CurrentTime = new TimeSpan(0, 0, newStats.timeLeft);
                    StatsUpdated?.Invoke(this, EventArgs.Empty);
                });
            });

        }
    }
}
