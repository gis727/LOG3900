using PolyPaint.Models.Coms;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using static PolyPaint.Models.Coms.Game;

namespace FMUD.Models.Coms
{
    // A party
    class Party : INotifyPropertyChanged
    {
        public readonly short MAX_PLAYERS = 4;
        public readonly short MAX_VIRTUAL_PLAYERS = 2;
        public readonly short MIN_FFA_HUMAN_PLAYERS = 2;

        private string _id { get; set; }
        private string _name { get; set; }
        private Difficulty _difficulty { get; set; }
        private GameMode _mode;
        private int _playersCount { get; set; }
        private int _playerCapacity { get; set; }
        private ObservableCollection<Player> _players { get; set; }
        private Platform _platform { get; set; }
        private bool _started { get; set; }

        public string id { get { return _id; } set { _id = value; PropertyModified(); } }
        public string name { get { return _name; } set { _name = value; PropertyModified(); } }
        public Difficulty difficulty { get { return _difficulty; } set { _difficulty = value; PropertyModified(); } }
        public GameMode mode { get { return _mode; } set { _mode = value; PropertyModified(); } }
        public int playersCount { get { return _playersCount; } set { _playersCount = value; PropertyModified(); } }
        public int playerCapacity { get { return _playerCapacity; } set { _playerCapacity = value; PropertyModified(); } }
        public ObservableCollection<Player> players { get { return _players; } set { _players = value; PropertyModified(); } }
        public Platform platform { get { return _platform; } set { _platform = value; PropertyModified(); } }
        public bool started { get { return _started; } set { _started = value; PropertyModified(); } }

        public Party()
        {
            name = "some unique game id";
            difficulty = Difficulty.Medium;
            mode = GameMode.coop;
            playersCount = 1;
            playerCapacity = 4;
            started = false;
        }

        public Party(string name, Difficulty difficulty, GameMode mode, int playersCount, int playerCapacity, bool started)
        {
            this.name = name;
            this.difficulty = difficulty;
            this.mode = mode;
            this.playersCount = playersCount;
            this.playerCapacity = playerCapacity;
            this.started = started;
        }

        public bool CanStart()
        {
            if (mode.Equals(GameMode.solo))
            {
                return true;
            }
            else
            {
                int humanCount = 0;
                if (players != null)
                {
                    humanCount = players.Count(player => { return !player.isVirtual; });
                }
                return (players != null) ? ((players.Count == MAX_PLAYERS) || (humanCount >= MIN_FFA_HUMAN_PLAYERS)) : false;
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }

    // A player
    class Player : INotifyPropertyChanged
    {
        private string _id { get; set; }
        private string _avatar { get; set; }
        private bool _isVirtual { get; set; }

        public string partyId { get; set; } // For when a player joins or leaves a party
        public string id { get { return _id; } set { _id = value; PropertyModified(); } }
        public string avatar { get { return _avatar; } set { _avatar = value; PropertyModified(); } }
        public bool isVirtual { get { return _isVirtual; } set { _isVirtual = value; PropertyModified(); } }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }

    // For creation
    class PartyControlData
    {
        public string name { get; set; }
        public Difficulty difficulty { get; set; }
        public GameMode mode { get; set; }
        public Platform platform { get; set; }

        public PartyControlData(Difficulty difficulty, GameMode mode, Platform platform, string name = "")
        {
            this.difficulty = difficulty;
            this.mode = mode;
            this.platform = platform;
            this.name = name;
        }
    }

    enum Platform
    {
        all = 0,
        pc,
        android
    }
}
