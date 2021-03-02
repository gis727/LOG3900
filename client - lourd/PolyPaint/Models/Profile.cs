using PolyPaint.Models.Coms;
using PolyPaint.Services;
using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows;

namespace PolyPaint.Models
{
    class Profile : INotifyPropertyChanged
    {
        private GameMode currentFilter = GameMode.none;
        private readonly ProfileGetMessage profile = new ProfileGetMessage();

        private ProfileCalculatedStats calculatedStats = new ProfileCalculatedStats();
        public string Name
        {
            get
            {
                return profile.name;
            }
            set
            {
                profile.name = value;
                PropertyModified();
            }
        }

        public string Surname
        {
            get
            {
                return profile.surname;
            }
            set
            {
                profile.surname = value;
                PropertyModified();
            }
        }

        public string Avatar
        {
            get
            {
                return profile.avatar;
            }
            set
            {
                profile.avatar = value;
                PropertyModified();
            }
        }

        public string Username
        {
            get
            {
                return profile.nickname;
            }
            set
            {
                profile.nickname = value;
                PropertyModified();
            }
        }
        public ProfileStatistics Stats
        {
            get
            {
                return profile.stats;
            }
            set
            {
                profile.stats = value;
                PropertyModified();
            }
        }

        public ProfileCalculatedStats CalculatedStats
        {
            get
            {
                return calculatedStats;
            }
            set
            {
                calculatedStats = value;
                PropertyModified();
            }
        }

        [Flags]
        enum GameModeUI
        {
            all = 0b111,
            ffa = 0b001,
            solo = 0b010,
            coop = 0b100,
        }
        public void FilterStatsBy(GameMode gameFilter = GameMode.none)
        {
            currentFilter = gameFilter;

            CalculatedStats = new ProfileCalculatedStats();
            foreach (ProfilePreviousGame game in profile.stats.previousGames)
            {
                GameModeUI res = getGameModeUI(game.type) & getGameModeUI(gameFilter);
                bool works = res == getGameModeUI(game.type);
                if (works)
                {
                    PropertyModified();
                    CalculatedStats.nbGamesPlayed++;
                    CalculatedStats.totalGameTime += game.duration;
                    if (game.won)
                    {
                        CalculatedStats.nbWins++;
                    }

                    if (game.score > CalculatedStats.highestScore)
                    {
                        CalculatedStats.highestScore = game.score;
                    }
                }
            }
            if (CalculatedStats.nbGamesPlayed > 0)
            {
                CalculatedStats.winRate = 100 * CalculatedStats.nbWins / CalculatedStats.nbGamesPlayed;
                CalculatedStats.avgGameTime = (CalculatedStats.totalGameTime / CalculatedStats.nbGamesPlayed) / 60;
                CalculatedStats.avgGameTime = Math.Round(CalculatedStats.avgGameTime, 1);
            }
            CalculatedStats.totalGameTime /= 60.0;
            CalculatedStats.totalGameTime = Math.Round(CalculatedStats.totalGameTime, 1);
        }


        // patch for removing the ui binary..
        private GameModeUI getGameModeUI(GameMode before)
        {
            switch (before)
            {
                case GameMode.none:
                    return GameModeUI.all;

                case GameMode.coop:
                    return GameModeUI.coop;

                case GameMode.ffa:
                    return GameModeUI.ffa;

                case GameMode.solo:
                    return GameModeUI.solo;

                default:
                    return GameModeUI.all;
            };
        }

        public Profile()
        {
            ServerService.GetAccountInfo();
            FilterStatsBy();

            ServerService.OnGetAccountInfo((profileInfo) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    if ((profileInfo.name.Length != 0))
                    {
                        Name = profileInfo.name;
                        Surname = profileInfo.surname;
                        Avatar = profileInfo.avatar;
                        Username = profileInfo.nickname;
                        if (profileInfo.previousGames != null) profileInfo.stats.previousGames.Reverse();
                        Stats = profileInfo.stats;
                        FilterStatsBy(currentFilter);
                    }
                });
            });

            ServerService.OnModifyAccount((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    switch ((STATE)state.state)
                    {
                        case STATE.Success:
                            ServerService.GetAccountInfo();
                            break;
                        default:
                            break;
                    }
                });
            });
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
    class ProfileCalculatedStats : INotifyPropertyChanged
    {
        private int _nbGamesPlayed = 0;
        private int _nbWins = 0;
        private double _totalGameTime = 0;
        private int _highestScore = 0;
        private double _avgGameTime = 0;
        private int _winRate = 0;
        public int nbGamesPlayed { get { return _nbGamesPlayed; } set { _nbGamesPlayed = value; PropertyModified(); } }
        public int nbWins { get { return _nbWins; } set { _nbWins = value; PropertyModified(); } }
        public double totalGameTime { get { return _totalGameTime; } set { _totalGameTime = value; PropertyModified(); } }
        public int highestScore { get { return _highestScore; } set { _highestScore = value; PropertyModified(); } }
        public double avgGameTime { get { return _avgGameTime; } set { _avgGameTime = value; PropertyModified(); } }
        public int winRate { get { return _winRate; } set { _winRate = value; PropertyModified(); } }

        public ProfileCalculatedStats()
        {

        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
