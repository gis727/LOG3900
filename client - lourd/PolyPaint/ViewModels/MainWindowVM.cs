using PolyPaint.Models;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace PolyPaint.ViewModels
{
    class MainWindowVM : INotifyPropertyChanged
    {
        private readonly MainWindow MainWindow = new MainWindow();

        public MainWindowVM()
        {
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
