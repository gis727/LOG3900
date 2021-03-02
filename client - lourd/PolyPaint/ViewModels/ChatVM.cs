using FMUD.Utilities;
using PolyPaint.Models;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace PolyPaint.ViewModels
{
    class ChatVM : INotifyPropertyChanged
    {
        private readonly Chat Chat = new Chat();
        public event System.EventHandler CurrentMsgChanged;
        public ObservableCollection<ChatMessage> ChatMessages
        {
            get
            {
                return Chat.ChatMessages;
            }
        }
        public string UserCurrentMessage
        {
            get { return Chat.UserCurrentMessage; }
            set { Chat.UserCurrentMessage = value; PropertyModified(); }
        }
        public RelayCommand<object> SendMessage { get; set; }
        public RelayCommand<object> ChangeChannels { get; set; }

        public ChatVM()
        {
            SendMessage = new RelayCommand<object>(Chat.SendMessage);
            Chat.PropertyChanged += new PropertyChangedEventHandler(ChatPropertyModified);
        }

        // INotifyPropertyChanged Inheritance contract
        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        private void ChatPropertyModified(object sender, PropertyChangedEventArgs e)
        {
            PropertyModified(e.PropertyName);
            if (e.PropertyName.Equals("UserCurrentMessage"))
            {
                CurrentMsgChanged?.Invoke(this, System.EventArgs.Empty);
            }
        }
    }
}
