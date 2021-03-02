using Newtonsoft.Json;
using PolyPaint.Models.Coms;
using PolyPaint.Utilities;
using System;
using System.Collections.Generic;
using System.IO;
using System.Windows;
using System.Windows.Resources;

namespace FMUD.Utilities
{
    class QuickDrawProvider
    {
        private int currentDrawingIndex;
        private readonly List<QuickDraw> drawings;

        public QuickDrawProvider()
        {
            currentDrawingIndex = 0;
            drawings = LoadDrawingsFromFile();
            drawings.Shuffle();
        }

        public Game GetNextGame()
        {
            Game game = null;
            if (drawings != null)
            {
                if (currentDrawingIndex >= drawings.Count)
                {
                    drawings.Shuffle();
                    currentDrawingIndex = 0;
                }

                game = drawings[currentDrawingIndex].ToGame();
                currentDrawingIndex++;
            }
            return game;
        }

        private List<QuickDraw> LoadDrawingsFromFile()
        {
            try
            {
                StreamResourceInfo stream = Application.GetResourceStream(new Uri("Resources/" + Settings.QUICK_DRAW_FILE_NAME, UriKind.Relative));
                using (StreamReader reader = new StreamReader(stream.Stream))
                {
                    return JsonConvert.DeserializeObject<List<QuickDraw>>(reader.ReadToEnd());
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("Unable to get the quick draws: " + e.Message);
                return null;
            }
        }
    }

    // The format is provided by "quickdraw-dataset": https://github.com/googlecreativelab/quickdraw-dataset
    class QuickDraw
    {
        public string word = null;
        public double[][][] drawing = null;

        public Game ToGame()
        {
            GameImage gameImage = new GameImage();
            foreach (double[][] stroke in drawing)
            {
                GameImagePath path = new GameImagePath("#000000", "", 1);
                for (short i = 0; i < stroke[0].Length; i++)
                {
                    double x = stroke[0][i], y = stroke[1][i];
                    path.AddPoint(new Point(x, y));
                }
                gameImage.paths.Add(path);
            }
            return new Game(null, word, 0, Game.DrawingMode.Instantaneous, 0, gameImage);
        }
    }
}
