using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace PolyPaint.Models.Coms
{
    public class Game : INotifyPropertyChanged
    {
        private string[] _clues;
        private string _secretWord;
        private Difficulty _difficulty;
        private DrawingMode _drawingMode;
        private DrawingDirection _drawingDirection;
        private GameImage _image;
        private SelectedDrawer _selectedDrawer;

        public string[] clues { get { return _clues; } set { _clues = value; PropertyModified(); } }
        public string secretWord { get { return _secretWord; } set { _secretWord = value; PropertyModified(); } }
        public Difficulty difficulty { get { return _difficulty; } set { _difficulty = value; PropertyModified(); } }
        public DrawingMode drawingMode { get { return _drawingMode; } set { _drawingMode = value; PropertyModified(); } }
        public DrawingDirection drawingDirection { get { return _drawingDirection; } set { _drawingDirection = value; PropertyModified(); } }
        public GameImage image { get { return _image; } set { _image = value; PropertyModified(); } }
        public SelectedDrawer selectedDrawer { get { return _selectedDrawer; } set { _selectedDrawer = value; PropertyModified(); } }

        public Game() { difficulty = Difficulty.Easy; drawingMode = DrawingMode.Classic; drawingDirection = DrawingDirection.LeftToRight; }
        public Game(string[] clues, string secretWord, Difficulty difficulty, DrawingMode drawingMode, DrawingDirection drawingDirection, GameImage image)
        {
            this.clues = clues;
            this.secretWord = secretWord;
            this.difficulty = difficulty;
            this.drawingMode = drawingMode;
            this.drawingDirection = drawingDirection;
            this.image = image;
        }

        // INotifyPropertyChanged Inheritance contract
        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }


        public enum Difficulty
        {
            none = 0,
            Easy,
            Medium,
            Hard,
            Creation,
        }

        public enum DrawingMode
        {
            Classic = 0,
            Random,
            Panoramic,
            Centered,
            Instantaneous
        }

        public enum DrawingDirection
        {
            LeftToRight = 0,
            RightToLeft,
            TopToBottom,
            BottomToTop,
            InOut,
            OutIn,
        }
        public enum SelectedDrawer
        {
            CurrentClient,
            AnotherClient,
            AVirtualPlayer,
        }
    }

    [Flags]
    enum GameMode
    {
        none = 0,
        ffa,
        solo,
        coop,
    }
}
