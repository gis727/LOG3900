using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Windows;
using System.Windows.Ink;

namespace FMUD.Utilities
{
    class ArrayHelper
    {
        public List<Point> ExtractDifference(Stroke biggerStroke, Stroke smallerStroke, Stroke extraStroke = null)
        {
            List<System.Windows.Input.StylusPoint> remainingPoints = smallerStroke.StylusPoints.Clone().ToList();
            if (extraStroke != null)
            {
                remainingPoints.AddRange(extraStroke.StylusPoints.Clone().ToList());
            }

            List<System.Windows.Input.StylusPoint> stylusPoints = biggerStroke.StylusPoints.Clone().ToList().Except(remainingPoints).ToList();
            List<Point> points = new List<Point>();
            stylusPoints.ForEach(point => points.Add(point.ToPoint()));
            return points;
        }

        public List<Point> ToPoints(Stroke stroke)
        {
            List<Point> points = new List<Point>();
            stroke.StylusPoints.Clone().ToList().ForEach(point => points.Add(point.ToPoint()));
            return points;
        }
    }

    // List randomizer (https://stackoverflow.com/questions/273313/randomize-a-listt)
    public static class ThreadSafeRandom
    {
        [ThreadStatic] private static Random Local;

        public static Random ThisThreadsRandom
        {
            get { return Local ?? (Local = new Random(unchecked(Environment.TickCount * 31 + Thread.CurrentThread.ManagedThreadId))); }
        }
    }

    static class MExtensions
    {
        public static void Shuffle<T>(this IList<T> list)
        {
            int n = list.Count;
            while (n > 1)
            {
                n--;
                int k = ThreadSafeRandom.ThisThreadsRandom.Next(n + 1);
                T value = list[k];
                list[k] = list[n];
                list[n] = value;
            }
        }
    }
}
