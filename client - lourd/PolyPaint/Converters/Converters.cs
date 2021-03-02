using MaterialDesignThemes.Wpf;
using PolyPaint.Models.Coms;
using System;
using System.Globalization;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace FMUD.Converters
{
    public class CurrentChannelBoldConverter : IMultiValueConverter
    {
        public object Convert(object[] values, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            string currentChannelID = (string)values[1];
            string received = (string)values[0];

            if (currentChannelID == received)
            {
                return FontWeights.UltraBlack;
            }

            return FontWeights.Bold;
        }

        public object[] ConvertBack(object value, Type[] targetTypes, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException("Going back to what you had isn't supported.");
        }
    }


    public class FilterPreviousPartiesConverter : IMultiValueConverter
    {
        public object Convert(object[] values, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            string mode = values[0].ToString();
            string filter = values[1].ToString();
            // pls just ignore this terrible piece of code
            switch (filter)
            {
                case "none":
                    return Visibility.Visible;
                case "coop":
                    if (mode == GameMode.coop.ToString())
                    {
                        return Visibility.Visible;
                    }
                    break;
                case "ffa":
                    if (mode == GameMode.ffa.ToString())
                    {
                        return Visibility.Visible;
                    }
                    break;
                default:
                    break;
            }
            return Visibility.Collapsed;
        }

        public object[] ConvertBack(object value, Type[] targetTypes, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException("Going back to what you had isn't supported.");
        }
    }

    public class JoinEnabledConverter : IMultiValueConverter
    {
        public object Convert(object[] values, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            int firstValue = (int)values[0];
            int secondValue = (int)values[1];
            bool partyStarted = (bool)values[2];
            bool isEnabled = (firstValue < secondValue) && !partyStarted;
            return isEnabled;
        }

        public object[] ConvertBack(object value, Type[] targetTypes, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException("Going back to what you had isn't supported.");
        }
    }
    public class JoinEnabledGreyscaleConverter : IMultiValueConverter
    {
        public object Convert(object[] values, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            int firstValue = (int)values[0];
            int secondValue = (int)values[1];
            bool partyStarted = (bool)values[2];
            bool isEnabled = (firstValue < secondValue) && !partyStarted;
            BitmapImage color = new BitmapImage(new Uri("../Resources/pattern1.jpg", UriKind.Relative));
            BitmapImage grey = new BitmapImage(new Uri("../Resources/full.jpg", UriKind.Relative));
            return isEnabled ? color : grey;
        }

        public object[] ConvertBack(object value, Type[] targetTypes, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException("Going back to what you had isn't supported.");
        }
    }

    class DateTimeConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            string val = value.ToString();
            val = val.Replace("-", "");
            val = val.Replace(".", "");
            val = val.Remove(17, 3);
            return DateTime.ParseExact(val, "yyyyMMddTHH:mm:ssZ",
                                System.Globalization.CultureInfo.InvariantCulture);
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    class MatchModeToColorConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            GameMode matchMode = (GameMode)value;
            switch (matchMode)
            {
                case GameMode.coop:
                    return "#ded300";
                case GameMode.ffa:
                    return "#ff54d1";
                case GameMode.solo:
                    return "#2ef2d2";
                default:
                    return "#000000";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
    class GameDifficultyToInfoConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            Game.Difficulty difficulty = (Game.Difficulty)value;

            switch (difficulty)
            {
                case Game.Difficulty.Easy:
                    return "Players will have 15 seconds and 5 (3 in ffa) trials to guess";
                case Game.Difficulty.Medium:
                    return "Players will have 10 seconds and 5 (3 in ffa) trials to guess";
                case Game.Difficulty.Hard:
                    return "Players will have 5 seconds and 5 (3 in ffa) trials to guess";
                default:
                    return "";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
    class MatchModeToToolTipConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            GameMode matchMode = (GameMode)value;
            switch (matchMode)
            {
                case GameMode.coop:
                    return "Play with others against an AI in this mode!";
                case GameMode.ffa:
                    return "Compete against others in this mode!";
                case GameMode.solo:
                    return "Play solo against an AI in this mode!";
                default:
                    return "#000000";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
    class IconConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if ((bool)value)
            {
                return Visibility.Visible;
            }
            else
            {
                return Visibility.Collapsed;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    class BoolWonCrownIconConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if ((bool)value)
            {
                return PackIconKind.Crown;
            }
            else
            {
                return PackIconKind.Skull;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    class BoolWonIconColorConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if ((bool)value)
            {
                return "Gold";
            }
            else
            {
                return "Black";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    class BoolWonCrownToolTipConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if ((bool)value)
            {
                return "Won";
            }
            else
            {
                return "Lost";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    class BoolToVirtualConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if ((bool)value)
            {
                return PackIconKind.Robot;
            }
            else
            {
                return PackIconKind.Face;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    class ProfileGameWonVisibiltyIconConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if ((bool)value)
            {
                return Visibility.Visible;
            }
            else
            {
                return Visibility.Collapsed;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    class URLtoImageConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            try
            {
                return new BitmapImage(new Uri((string)value));
            }
            catch
            {
                return new BitmapImage(new Uri("https://darksouls3.wiki.fextralife.com/file/Dark-Souls-3/smough's_helm.png"));
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    /// <summary>
    /// Permet de générer une couleur en fonction de la chaine passée en paramètre.
    /// Par exemple, pour chaque bouton d'un groupe d'options on compare son nom avec l'élément actif (sélectionné) du groupe.
    /// S'il y a correspondance, la bordure du bouton aura une teinte bleue, sinon elle sera transparente.
    /// Cela permet de mettre l'option sélectionnée dans un groupe d'options en évidence.
    /// </summary>
    /// 

    class ConvertisseurBordure : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            return (value.ToString() == parameter.ToString()) ? "Transparent" : "Transparent";
        }
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture) => System.Windows.DependencyProperty.UnsetValue;
    }

    /// Permet de générer une couleur en fonction de la chaine passée en paramètre.
    /// Par exemple, pour chaque bouton d'un groupe d'option on compare son nom avec l'élément actif (sélectionné) du groupe.
    /// S'il y a correspondance, la couleur de fond du bouton aura une teinte bleue, sinon elle sera transparente.
    /// Cela permet de mettre l'option sélectionnée dans un groupe d'options en évidence.
    class ConvertisseurCouleurFond : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            return (value.ToString() == parameter.ToString()) ? "Transparent" : "Transparent";
        }
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture) => System.Windows.DependencyProperty.UnsetValue;
    }

    /// <summary>
    /// Permet au InkCanvas de définir son mode d'édition en fonction de l'outil sélectionné.
    /// </summary>
    class ConvertisseurModeEdition : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            switch (value)
            {
                case "lasso":
                    return InkCanvasEditingMode.Select;
                case "efface_segment":
                    return InkCanvasEditingMode.EraseByPoint;
                case "efface_trait":
                    return InkCanvasEditingMode.EraseByStroke;
                default:
                    return InkCanvasEditingMode.Ink;
            }
        }
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture) => System.Windows.DependencyProperty.UnsetValue;
    }


    /// <summary>
    /// Converts timestamps to a readable format.
    /// </summary>
    class ConverterTimestamp : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            return (new DateTime(1970, 1, 1, 0, 0, 0, 0, System.DateTimeKind.Utc)).AddMilliseconds((long)value).ToLocalTime();
        }
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture) => System.Windows.DependencyProperty.UnsetValue;
    }


    /// <summary>
    /// Converts timestamps to a readable format.
    /// </summary>
    class TimeLeftConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            return (new TimeSpan(0, 0, (int)value)).ToString(@"mm\:ss");
        }
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture) => System.Windows.DependencyProperty.UnsetValue;
    }

    /// <summary>
    /// Converts timestamps to a readable format.
    /// </summary>
    class TimeLeftColorConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            if ((int)value < 4 && (int)value != 0)
            {
                return Brushes.Red;
            }
            else
            {
                return Brushes.White;
            }
        }
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture) => System.Windows.DependencyProperty.UnsetValue;
    }


    /// <summary>
    /// Converts colors
    /// </summary>
    class ColorConverter
    {
        public static string FromRGBToHex(Color color)
        {
            return "#" + string.Format("{0:X2}{1:X2}{2:X2}", color.R, color.G, color.B);
        }
        public static string FromRGBToHex(string hexColor)
        {
            if (!hexColor.StartsWith("#"))
            {
                return hexColor == "White" ? "#FFFFFF" : "#000000";
            }
            else
            {
                return hexColor;
            }
        }
    }
}
