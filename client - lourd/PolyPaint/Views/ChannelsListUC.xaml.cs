using PolyPaint.ViewModels;
using System.Linq;
using System.Windows;
using System.Windows.Controls;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for ChannelsListUC.xaml
    /// </summary>
    public partial class ChannelsListUC : UserControl
    {
        public ChannelsListUC()
        {
            InitializeComponent();
            DataContext = new ChannelsListVM();
        }
        private void CurrentChannelInput_TextChanged(object sender, TextChangedEventArgs e)
        {
            JoinButton.IsEnabled = CurrentChannelInput.Text.Trim().Length > 0;
        }
        private void Create_Add_Channel_Click(object sender, RoutedEventArgs e)
        {
            string channelId = (((Button)sender).Parent as DockPanel).Children.OfType<TextBox>().First().Text;
            ((ChannelsListVM)DataContext).JoinChannel(channelId);
        }
        private void Leave_Channel_Click(object sender, RoutedEventArgs e)
        {
            string channelId = (((Button)sender).Parent as DockPanel).Children.OfType<TextBlock>().First().Text;
            ((ChannelsListVM)DataContext).LeaveChannel(channelId);

        }

        private void View_Channel_Click(object sender, RoutedEventArgs e)
        {
            string channelId = ((DockPanel)sender).Children.OfType<TextBlock>().First().Text;

            ((ChannelsListVM)DataContext).SwitchChannel(channelId);
        }

        private void Delete_Channel_Click(object sender, RoutedEventArgs e)
        {
            string channelId = (((Button)sender).Parent as DockPanel).Children.OfType<TextBlock>().First().Text;
            ((ChannelsListVM)DataContext).DeleteChannel(channelId);
        }
    }
}
