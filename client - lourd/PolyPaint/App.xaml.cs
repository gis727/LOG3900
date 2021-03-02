using System.Windows;
using System.Windows.Threading;

namespace FMUD
{
    /// <summary>
    /// Logique d'interaction pour App.xaml
    /// </summary>
    public partial class App : Application
    {
        void App_DispatcherUnhandledException(object sender, DispatcherUnhandledExceptionEventArgs e)
        {
            System.Console.Error.WriteLine("THE APPLICATION WAS ABOUT TO CRASH BECAUSE: ");
            System.Console.Error.WriteLine(e.Exception.Message);
            e.Handled = true;
        }
    }
}
