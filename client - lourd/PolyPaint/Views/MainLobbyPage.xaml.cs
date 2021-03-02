using FMUD.Models.Coms;
using FMUD.ViewModels;
using PolyPaint.Models.Coms;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Windows;
using System.Windows.Controls;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for MainLobbyPage.xaml
    /// </summary>
    public partial class MainLobbyPage : UserControl
    {
        public event EventHandler CreateGame;
        public event EventHandler KickedOut;


        public MainLobbyPage()
        {
            InitializeComponent();
            DataContext = new MainLobbyVM();
            // Set the join listener
            ServerService.OnJoinParty((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    if ((STATE)state.state == STATE.Success)
                    {
                        Party_Joined(state.roomId);
                    }
                });
            });

        }

        // Provides the current DataContext to child UCs
        private MainLobbyVM CurrentDataContext()
        {
            return (MainLobbyVM)DataContext;
        }

        private void GameCreationButton_Click(object sender, RoutedEventArgs e)
        {
            CreateGame(this, EventArgs.Empty);
        }

        private void CreateParty_Click(object sender, RoutedEventArgs e)
        {
            PartyCreationDialog.IsOpen = true;
            PartyCreationSubmitBtn.IsEnabled = true;
        }

        private void Tutorial_Click(object sender, RoutedEventArgs e)
        {
            TutorialDialog.IsOpen = true;
        }

        private void FilterParty_Click(object sender, RoutedEventArgs e)
        {
            PartyFilteringDialog.IsOpen = true;
        }

        private void SubmitPartyCreation_Click(object sender, RoutedEventArgs e)
        {
            PartyCreationDialog.IsOpen = false;
            // Get the selected data
            GameMode mode = (GameMode)Enum.Parse(typeof(GameMode), ((ComboBoxItem)(GameMode.SelectedItem)).Tag.ToString());
            #region DROPPED FEATURE
            //Platform platform = (Platform)Enum.Parse(typeof(Platform), ((ComboBoxItem)(Platform.SelectedItem)).Tag.ToString());
            #endregion
            // Launch the creation
            ServerService.OnPartyCreation((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    PartyCreationSubmitBtn.IsEnabled = true;
                    if ((STATE)state.state == STATE.Success)
                    {
                        Party_Joined(state.roomId);
                    }
                });
            });
            #region DROPPED FEATURE
            //ServerService.CreateParty(mode, platform, PartyName.Text);
            #endregion
            ServerService.CreateParty(mode, Platform.all, PartyName.Text);
            PartyCreationSubmitBtn.IsEnabled = false;
        }

        private void SubmitPartyFilter_Click(object sender, RoutedEventArgs e)
        {
            PartyFilteringDialog.IsOpen = false;
            // Get the selected data
            GameMode mode = (GameMode)Enum.Parse(typeof(GameMode), ((ComboBoxItem)(GameModeFilter.SelectedItem)).Tag.ToString());
            #region DROPPED FEATURE
            //Platform platform = (Platform)Enum.Parse(typeof(Platform), ((ComboBoxItem)(PlatformFilter.SelectedItem)).Tag.ToString());
            // Get the Filtered data
            //ServerService.GetParties(difficulty, mode, platform);
            #endregion
            ServerService.GetParties(mode, Platform.all);
            if (mode != Models.Coms.GameMode.ffa && mode != Models.Coms.GameMode.coop && mode != Models.Coms.GameMode.solo)
            {
                #region DROPPED FEATURE
                //ServerService.GetParties(difficulty, Models.Coms.GameMode.none, platform);
                #endregion
                ServerService.GetParties(Models.Coms.GameMode.none, Platform.all);
            }
        }

        private void SnackbarMessage_ActionClick(object sender, RoutedEventArgs e)
        {
            Snackbar.IsActive = false;
        }
        private void Mode_Changed(object sender, EventArgs e)
        {
            if (ModeDescription != null)
            {
                switch ((GameMode)Enum.Parse(typeof(GameMode), ((ComboBoxItem)(GameMode.SelectedItem)).Tag.ToString()))
                {
                    case Models.Coms.GameMode.ffa:
                        ModeDescription.Text = "In this mode, you will compete against other players to earn as many points as possible.";
                        break;
                    case Models.Coms.GameMode.coop:
                        ModeDescription.Text = "In this mode, you will compete alongside other players against a virtual player to earn as many points as possible.";
                        break;
                    case Models.Coms.GameMode.solo:
                        ModeDescription.Text = "In this mode, you will compete alone against a virtual player to earn as many points as possible.";
                        break;
                }
            }
        }

        private void Mode_Changed_Filter(object sender, EventArgs e)
        {
            if (ModeDescriptionFilter != null)
            {
                switch ((GameMode)Enum.Parse(typeof(GameMode), ((ComboBoxItem)(GameModeFilter.SelectedItem)).Tag.ToString()))
                {
                    case Models.Coms.GameMode.ffa:
                        ModeDescriptionFilter.Text = "In this mode, players compete against each other to earn as many points as possible.";
                        break;
                    case Models.Coms.GameMode.coop:
                        ModeDescriptionFilter.Text = "In this mode, players compete together against a virtual player to earn as many points as possible.";
                        break;
                    case Models.Coms.GameMode.solo:
                        ModeDescriptionFilter.Text = "In this mode, a player competes alone against a virtual player to earn as many points as possible.";
                        break;
                    default:
                        ModeDescriptionFilter.Text = "Show all parties.";
                        break;
                }
            }
        }

        #region DROPPED FEATURES
        //private void Difficulty_Changed(object sender, EventArgs e)
        //{
        //    if (DifficultyDescription != null)
        //    {
        //        switch ((Difficulty)Enum.Parse(typeof(Difficulty), Difficulty.Value.ToString()))
        //        {
        //            case Game.Difficulty.Easy:
        //                DifficultyDescription.Text = "The virtual player(s) will draw simple drawings";
        //                break;
        //            case Game.Difficulty.Medium:
        //                DifficultyDescription.Text = "The virtual player(s) will draw average difficulty drawings";
        //                break;
        //            case Game.Difficulty.Hard:
        //                DifficultyDescription.Text = "The virtual player(s) will draw complex drawings";
        //                break;
        //        }
        //    }
        //}

        //private void Platform_Changed(object sender, EventArgs e)
        //{
        //    if (PlatformsDescription != null)
        //    {
        //        switch ((Platform)Enum.Parse(typeof(Platform), ((ComboBoxItem)(Platform.SelectedItem)).Tag.ToString()))
        //        {
        //            case FMUD.Models.Coms.Platform.all:
        //                PlatformsDescription.Text = "PCs and tablets are allowed in this party";
        //                break;
        //            case FMUD.Models.Coms.Platform.pc:
        //                PlatformsDescription.Text = "Only PCs are allowed in this party";
        //                break;
        //            default:
        //                break;
        //        }
        //    }
        //}

        //private void Platform_Changed_Filter(object sender, EventArgs e)
        //{
        //    if (PlatformsDescriptionFilter != null)
        //    {
        //        switch ((Platform)Enum.Parse(typeof(Platform), ((ComboBoxItem)(PlatformFilter.SelectedItem)).Tag.ToString()))
        //        {
        //            case FMUD.Models.Coms.Platform.all:
        //                PlatformsDescriptionFilter.Text = "PCs and tablets are allowed in this party";
        //                break;
        //            case FMUD.Models.Coms.Platform.pc:
        //                PlatformsDescriptionFilter.Text = "Only PCs are allowed in this party";
        //                break;
        //            case FMUD.Models.Coms.Platform.android:
        //                PlatformsDescriptionFilter.Text = "Only Android tablets are allowed in this party";
        //                break;
        //        }
        //    }
        //}
        #endregion

        private void PartyJoinButton_Click(object sender, RoutedEventArgs e)
        {
            string partyId = ((Button)sender).Tag.ToString();
            ServerService.JoinParty(partyId);
        }
        private void Party_Joined(string partyId)
        {
            PartyLobby.setUp(DataContext, partyId);
            if (Transitioner.SelectedIndex == 0)
            {
                Transitioner.SelectedIndex = 1;
            }
        }

        private void PartyLobby_Left(object sender, PartyLeftEventArgs e)
        {
            Transitioner.SelectedIndex = 0;
            if (e.Kicked)
            {
                KickedOut?.Invoke(this, EventArgs.Empty);
            }
        }
        private void resetWhenTutorialClosed(object sender, RoutedEventArgs e)
        {
            TutorialUC.resetWhenTutorialClosed(sender, e);
        }
    }
}
