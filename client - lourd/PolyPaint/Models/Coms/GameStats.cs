using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace FMUD.Models.Coms
{
    class GameStats : INotifyPropertyChanged
    {
        public int _score;
        public int _timeLeft;
        public int _trialsLeft;
        public bool _isWinner;

        public int score { get { return _score; } set { _score = value; PropertyModified(); } }
        public int timeLeft { get { return _timeLeft; } set { _timeLeft = value; PropertyModified(); } }
        public int trialsLeft { get { return _trialsLeft; } set { _trialsLeft = value; PropertyModified(); } }
        public bool isWinner { get { return _isWinner; } set { _isWinner = value; PropertyModified(); } }

        public GameStats()
        {
            score = 0;
            timeLeft = 0;
            trialsLeft = 0;
            isWinner = false;
        }

        // INotifyPropertyChanged Inheritance contract
        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
