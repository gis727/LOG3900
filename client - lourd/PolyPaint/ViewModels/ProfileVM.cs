using PolyPaint.Models;
using PolyPaint.Models.Coms;
using PolyPaint.Utilities;
using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace PolyPaint.ViewModels
{
    class ProfileVM : INotifyPropertyChanged
    {
        private string _filter = GameMode.none.ToString();
        private readonly Profile Profile = new Profile();
        public string Name
        {
            get { return Profile.Name; }
            set { Profile.Name = value; PropertyModified(); }
        }

        public string CurrentFilter
        {
            get { return _filter; }
            set { _filter = value; PropertyModified(); }
        }

        public string Surname
        {
            get { return Profile.Surname; }
            set { Profile.Surname = value; PropertyModified(); }
        }

        public string Username
        {
            get { return Profile.Username; }
            set { Profile.Username = value; PropertyModified(); }
        }

        public ProfileStatistics Stats
        {
            get { return Profile.Stats; }
            set { Profile.Stats = value; PropertyModified(); }
        }

        public ProfileCalculatedStats CalculatedStats
        {
            get { return Profile.CalculatedStats; }
            set { Profile.CalculatedStats = value; PropertyModified(); }
        }

        public ImageSource Avatar
        {
            get
            {
                try
                {
                    return new BitmapImage(new Uri(Profile.Avatar));
                }
                catch
                {
                    return new BitmapImage(new Uri(Settings.DEFAULT_AVATAR_URI));
                }
            }
            set
            {
            }
        }

        public void Filter(GameMode gameTypeFilter)
        {
            CurrentFilter = gameTypeFilter.ToString();
            PropertyModified(CurrentFilter);
            Profile.FilterStatsBy(gameTypeFilter);
        }
        public ProfileVM()
        {
            Profile.PropertyChanged += new PropertyChangedEventHandler(ProfilePropertyModified);
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        private void ProfilePropertyModified(object sender, PropertyChangedEventArgs e)
        {
            PropertyModified(e.PropertyName);
        }
    }
}
