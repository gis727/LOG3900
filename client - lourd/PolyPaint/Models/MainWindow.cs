using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace PolyPaint.Models
{
    class MainWindow : INotifyPropertyChanged
    {
        public MainWindow()
        {

        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
