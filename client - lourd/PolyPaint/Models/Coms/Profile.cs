using PolyPaint.Utilities;
using System.Collections.Generic;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace PolyPaint.Models.Coms
{
    class ProfileCreationMessage
    {
        public string name { get; set; }
        public string surname { get; set; }
        public string password { get; set; }
        public string nickname { get; set; }
        public string avatar { get; set; }


        public ProfileCreationMessage(string name, string surname, string password, string nickname, string avatar)
        {
            this.name = name;
            this.surname = surname;
            this.password = password;
            this.nickname = nickname;
            this.avatar = avatar;

        }
    }

    class ProfileGetMessage : INotifyPropertyChanged
    {
        private string _name;
        private string _surname;
        private string _nickname;
        private string _avatar;
        private ProfileStatistics _stats;
        private List<ProfilePreviousGame> _previousGames;

        public string name { get { return _name; } set { _name = value; PropertyModified(); } }
        public string surname { get { return _surname; } set { _surname = value; PropertyModified(); } }
        public string nickname { get { return _nickname; } set { _nickname = value; PropertyModified(); } }
        public string avatar { get { return _avatar; } set { _avatar = value; PropertyModified(); } }
        public ProfileStatistics stats { get { return _stats; } set { _stats = value; PropertyModified(); } }

        public List<ProfilePreviousGame> previousGames { get { return _previousGames; } set { _previousGames = value; PropertyModified(); } }
        public ProfileGetMessage()
        {
            name = "";
            surname = "";
            nickname = "";
            avatar = Settings.DEFAULT_AVATAR_URI;
            previousGames = new List<ProfilePreviousGame>();
            stats = new ProfileStatistics();
        }
        public ProfileGetMessage(string name, string surname, string nickname, string avatar, ProfileStatistics stats)
        {
            this.name = name;
            this.surname = surname;
            this.nickname = nickname;
            this.avatar = avatar;
            this.stats = stats;
            previousGames = stats.previousGames;

        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }

    class ProfileStatistics : INotifyPropertyChanged
    {

        private List<ProfileActivity> _activity;

        private List<ProfilePreviousGame> _previousGames;

        public List<ProfileActivity> activity
        {
            get
            {
                return _activity;
            }
            set
            {
                _activity = value; PropertyModified();
            }
        }

        public List<ProfilePreviousGame> previousGames
        {
            get
            {
                return _previousGames;
            }
            set
            {
                _previousGames = value; PropertyModified();
            }
        }

        public ProfileStatistics()
        {
            activity = new List<ProfileActivity>();
            previousGames = new List<ProfilePreviousGame>();
        }
        public ProfileStatistics(int playedGames, int victoryAmount, int averageGameTime, int timePlayed, List<ProfileActivity> activity, List<ProfilePreviousGame> previousGames)
        {
            this.activity = activity;
            this.previousGames = previousGames;
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }

    class ProfileActivity : INotifyPropertyChanged
    {
        private long _connectionDate;
        private long _disconnectionDate;

        public long connectionDate
        {
            get { return _connectionDate; }
            set { _connectionDate = value; PropertyModified(); }
        }

        public long disconnectionDate
        {
            get { return _disconnectionDate; }
            set { _disconnectionDate = value; PropertyModified(); }
        }

        public ProfileActivity()
        {
            disconnectionDate = 0;
            connectionDate = 0;
        }
        public ProfileActivity(long connectionDate, long disconnectionDate)
        {
            this.disconnectionDate = disconnectionDate;
            this.connectionDate = connectionDate;
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

    }

    class ProfilePreviousGame : INotifyPropertyChanged
    {
        private long _date;
        private List<string> _players;
        private bool _won;
        private GameMode _type;
        private int _duration;
        private int _score;

        public long date
        {
            get { return _date; }
            set { _date = value; PropertyModified(); }
        }
        public List<string> players
        {
            get { return _players; }
            set { _players = value; PropertyModified(); }
        }
        public bool won
        {
            get { return _won; }
            set { _won = value; PropertyModified(); }
        }
        public GameMode type
        {
            get { return _type; }
            set { _type = value; PropertyModified(); }
        }

        public int duration
        {
            get { return _duration; }
            set { _duration = value; PropertyModified(); }
        }

        public int score
        {
            get { return _score; }
            set { _score = value; PropertyModified(); }
        }

        public ProfilePreviousGame(long date, List<string> players, bool won, GameMode type, int duration, int score)
        {
            this.date = date;
            this.players = players;
            this.won = won;
            this.type = type;
            this.duration = duration;
            this.score = score;
        }
        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

    }
}
