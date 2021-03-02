using FMUD.Models;
using FMUD.Models.Coms;
using PolyPaint.Models;
using PolyPaint.Models.Coms;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace FMUD.ViewModels
{
    class MainLobbyVM : INotifyPropertyChanged
    {
        private readonly MainLobby MainLobby = new MainLobby();

        public ObservableCollection<Party> allParties
        {
            get { return MainLobby.AllParties; }
            set { MainLobby.AllParties = value; PropertyModified(); }

        }

        public ObservableCollection<Player> allPlayers
        {
            get { return MainLobby.AllPlayers; }
        }

        public bool CanAddVirtualPlayer
        {
            get
            {
                Party currentParty = MainLobby.CurrentParty;
                if (currentParty.mode.Equals(GameMode.ffa))
                {
                    short vpCount = 0;
                    foreach (Player player in currentParty.players)
                    {
                        if (player.isVirtual)
                        {
                            vpCount++;
                        }
                    }
                    return vpCount < currentParty.MAX_VIRTUAL_PLAYERS && currentParty.players.Count < currentParty.MAX_PLAYERS;
                }
                else
                {
                    return false;
                }
            }
        }

        public bool CanStartParty
        {
            get
            {
                return MainLobby.CurrentParty.CanStart();
            }
        }

        public void SelectParty(string partyId = "")
        {
            MainLobby.SelectParty(partyId);
            SharedChatModel.CurrenPartyId = partyId;
        }

        public int CurrentPlayerCount
        {
            get { return MainLobby.CurrentParty.playersCount; }
        }

        public int CurrentPlayerCapacity
        {
            get { return MainLobby.CurrentParty.playerCapacity; }
        }

        public MainLobbyVM()
        {
            MainLobby.PropertyChanged += new PropertyChangedEventHandler(GamePropertyModified);

        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        private void GamePropertyModified(object sender, PropertyChangedEventArgs e)
        {
            PropertyModified(e.PropertyName);
            if (MainLobby.CurrentPartyId != null)
            {
                PropertyModified("CanAddVirtualPlayer");
                PropertyModified("CanStartParty");
                PropertyModified("CurrentPlayerCapacity");
                PropertyModified("CurrentPlayerCount");
            }
        }
    }
}
