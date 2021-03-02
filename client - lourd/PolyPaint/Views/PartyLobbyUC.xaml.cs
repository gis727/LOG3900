using FMUD.ViewModels;
using PolyPaint.Models;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Windows;
using System.Windows.Controls;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for PartyLobbyUC.xaml
    /// </summary>
    public partial class PartyLobbyUC : UserControl
    {
        public event EventHandler<PartyLeftEventArgs> LeaveLobby;
        public string partyId;
        public PartyLobbyUC()
        {
            InitializeComponent();

            // Set listener
            ServerService.OnStartGame((game) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    // transit/keep user on live game page
                    Transitioner.SelectedIndex = 1;
                });
            });
        }

        private void Tutorial_Click(object sender, RoutedEventArgs e)
        {
            TutorialDialog.IsOpen = true;
        }

        private void resetWhenTutorialClosed(object sender, RoutedEventArgs e)
        {
            TutorialUC.resetWhenTutorialClosed(sender, e);
        }

        public void setUp(object dataContext, string partyId)
        {
            Transitioner.SelectedIndex = 0;
            this.partyId = partyId;
            ReadyButton.Visibility = Visibility.Visible;
            ReadyTextZone.Visibility = Visibility.Hidden;
            DataContext = dataContext; // We use the data context provided by the caller
            ((MainLobbyVM)DataContext).SelectParty(partyId); // Set the party as the current one
            SharedChatModel.SwitchChannel(partyId); // Switch to the party chat room
        }

        private void ReadyButton_Click(object sender, RoutedEventArgs e)
        {
            ReadyButton.Visibility = Visibility.Hidden;
            ReadyTextZone.Visibility = Visibility.Visible;
            LiveGameUC.setUp();
            ServerService.StartParty();
        }

        private void LeaveButton_Click(object sender, RoutedEventArgs e)
        {
            ((MainLobbyVM)DataContext).SelectParty(); // De-select party
            ServerService.LeaveParty(partyId);
            LeaveLobby?.Invoke(this, new PartyLeftEventArgs(false));
        }

        private void LiveGameUC_PartyOver(object sender, PartyLeftEventArgs e)
        {
            if (e.Kicked)
            {
                LeaveLobby?.Invoke(this, new PartyLeftEventArgs(true));
            }
            else
            {
                LeaveButton_Click(null, null);
            }
        }

        private void AddVirtualPlayerBtn_Click(object sender, RoutedEventArgs e)
        {
            ServerService.AddVirtualPlayer(partyId);
        }

        private void RemoveVirtualPlayerBtn_Click(object sender, RoutedEventArgs e)
        {
            string playerId = ((Button)sender).Tag.ToString();
            ServerService.RemoveVirtualPlayer(partyId, playerId);
        }
    }
}
