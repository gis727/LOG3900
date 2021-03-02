using System.Windows;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for ChatWindow.xaml
    /// </summary>
    public partial class ChatWindow : Window
    {
        public ChatWindow()
        {
            InitializeComponent();
            ChatUC.CurrentMessage.Focus();
            ChatUC.PopoutButton.Visibility = Visibility.Collapsed;
        }
    }
}
