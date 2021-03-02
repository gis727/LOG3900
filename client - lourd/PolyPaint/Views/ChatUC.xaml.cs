using PolyPaint.Services;
using PolyPaint.Utilities;
using PolyPaint.ViewModels;
using System;
using System.Collections.Specialized;
using System.Media;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for ChatUC.xaml
    /// </summary>
    public partial class ChatUC : UserControl
    {
        public ChatUC()
        {
            InitializeComponent();
            DataContext = new ChatVM();
            ((ChatVM)DataContext).CurrentMsgChanged += SetCurrentMessageCarretAtEnd;
            ((INotifyCollectionChanged)ListMessages.Items).CollectionChanged += ListMessages_CollectionChanged;

            Loaded += Window_Loaded;
            ServerService.OnAnswer((action) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {

                    if (action)
                    {

                        using (SoundPlayer player = new SoundPlayer(System.IO.Path.GetFullPath("../../Resources/right.wav")))
                        {
                            player.Load();
                            player.Play();
                        }
                    }
                    else
                    {

                        using (SoundPlayer player = new SoundPlayer(System.IO.Path.GetFullPath("../../Resources/wrong.wav")))
                        {
                            player.Load();
                            player.Play();
                        }
                    }

                });
            });

        }

        private void SetCurrentMessageCarretAtEnd(object sender, EventArgs e)
        {
            CurrentMessage.CaretIndex = CurrentMessage.Text.Length;
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            //Height = Window.GetWindow(this).ActualHeight - 50;
        }

        private void ListMessages_CollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            if (e.Action == NotifyCollectionChangedAction.Add)
            {
                // scroll the new item into view   
                ListMessages.ScrollIntoView(e.NewItems[0]);
            }
        }

        private void Send_Button_Click(object sender, RoutedEventArgs e)
        {
            CurrentMessage.Focus();
        }
        private void Show_Channels_Button_Click(object sender, RoutedEventArgs e)
        {
            ShowChannelsButton.Visibility = Visibility.Collapsed;
            ChannelsListUC.Visibility = Visibility.Visible;
        }

        private void CurrentMessage_TextChanged(object sender, TextChangedEventArgs e)
        {
            SendButton.IsEnabled = CurrentMessage.Text.Trim().Length > 0;
            if ((new MessageInterpreter()).IsACommand(CurrentMessage.Text))
            {
                CurrentMessage.Foreground = Brushes.Red;
            }
            else
            {
                CurrentMessage.Foreground = Brushes.Black;
            }
        }

        private void CurrentMessage_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return && SendButton.IsEnabled)
            {
                ((ChatVM)DataContext).SendMessage.Execute(CurrentMessage.Text);
            }
        }
    }
}
