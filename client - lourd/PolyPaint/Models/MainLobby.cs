using FMUD.Models.Coms;
using PolyPaint.Services;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows;

namespace FMUD.Models
{
    class MainLobby : INotifyPropertyChanged
    {
        public string CurrentPartyId = null;
        ObservableCollection<Party> _allParties = new ObservableCollection<Party>();

        public ObservableCollection<Party> AllParties
        {
            get
            {
                return _allParties;
            }
            set
            {
                _allParties = value;
                PropertyModified("AllParties");
            }
        }

        public ObservableCollection<Player> AllPlayers
        {
            get
            {
                return CurrentParty.players;
            }
        }
        public Party CurrentParty
        {
            get
            {
                if (CurrentPartyId != null)
                {
                    foreach (Party party in _allParties)
                    {
                        if (party.id.Equals(CurrentPartyId))
                        {
                            return party;
                        }
                    }
                }
                return new Party();
            }
        }
        public void SelectParty(string partyId)
        {
            // Update the current party id
            CurrentPartyId = partyId;
            PropertyModified("AllPlayers");
        }

        public MainLobby()
        {
            setUp();
        }

        public void setUp()
        {
            SetListeners();
            ServerService.GetParties();
        }

        private void SetListeners()
        {
            ServerService.OffParties();
            ServerService.OnGetParties((parties) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    AllParties.Clear();
                    foreach (Party party in parties)
                    {
                        AllParties.Add(party);
                    }
                    PropertyModified("AllParties");
                });
            });
            ServerService.OnNewPartyCreation((party) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    AllParties.Add(party);
                    PropertyModified("AllParties");
                });
            });
            ServerService.OnPartyRemoved((partyId) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    foreach (Party party in AllParties)
                    {
                        if (party.id == partyId) { AllParties.Remove(party); break; }
                    }
                    PropertyModified("AllParties");
                });
            });
            ServerService.OnPlayerJoined((player) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    foreach (Party party in AllParties)
                    {
                        if (party.id == player.partyId) { party.players.Add(player); party.playersCount++; }
                    }
                    PropertyModified("AllParties");
                });
            });
            ServerService.OnPlayerLeft((player) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    foreach (Party party in AllParties)
                    {
                        if (party.id == player.partyId)
                        {
                            foreach (Player _player in party.players)
                            {
                                if (_player.id == player.id) { party.players.Remove(_player); party.playersCount--; break; }
                            }
                        }
                    }
                    PropertyModified("AllParties");
                });
            });
            ServerService.OnPartyStarted((partyId) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    foreach (Party party in AllParties)
                    {
                        if (party.id == partyId) { party.started = true; break; }
                    }
                    PropertyModified("AllParties");
                });
            });
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
