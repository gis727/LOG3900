using PolyPaint.Models;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace PolyPaint.ViewModels
{
    class ChannelsListVM : INotifyPropertyChanged
    {
        private readonly Chat Chat = new Chat();
        public ObservableCollection<Channel> AllChannels
        {
            get
            {
                return Chat.AllChannels;
            }
            set
            {
                Chat.AllChannels = value;
                PropertyModified();
            }
        }

        public string ChannelInput
        {
            get { return Chat.ChannelInput; }
            set { Chat.ChannelInput = value; PropertyModified(); }
        }

        public string CurrentChannelId
        {
            get { return Chat.CurrentChannelId; }
            set { Chat.CurrentChannelId = value; PropertyModified(); }
        }
        public void LeaveChannel(string channelId)
        {
            Chat.LeaveChannel(channelId);
            PropertyModified();
        }

        public void SwitchChannel(string channelId)
        {
            Chat.SwitchChannel(channelId);
        }

        public void JoinChannel(string channelId)
        {
            bool channelExists = false;
            foreach (Channel channel in AllChannels)
            {
                if (channel.id == channelId)
                {
                    channelExists = true;
                }
            }
            if (channelExists)
            {
                Chat.SwitchChannel(channelId);
            }
            else
            {
                Chat.CreateChannel(channelId);
            }
        }

        public void DeleteChannel(string channelId)
        {
            Chat.DeleteChannel(channelId);
        }

        public ChannelsListVM()
        {
            Chat.PropertyChanged += new PropertyChangedEventHandler(ChatPropertyModified);
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        private void ChatPropertyModified(object sender, PropertyChangedEventArgs e)
        {
            PropertyModified(e.PropertyName);
        }
    }
}
