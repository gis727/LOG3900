using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace PolyPaint.Models
{
    class Channel : INotifyPropertyChanged
    {
        private string _id;
        public string id
        {
            get { return _id; }
            set { _id = value; PropertyModified(); }
        }

        private bool _joined;
        public bool joined
        {
            get { return _joined; }
            set { _joined = value; PropertyModified(); }
        }


        private bool _owner;
        public bool owner
        {
            get { return _owner; }
            set { _owner = value; PropertyModified(); }
        }

        public ObservableCollection<ChatMessage> messages { get; set; }
        public Channel(string id)
        {
            this.id = id;
            joined = false;
            owner = false;
            messages = new ObservableCollection<ChatMessage>();
        }
        public Channel(string id, bool joined)
        {
            this.id = id;
            this.joined = joined;
            owner = false;
            messages = new ObservableCollection<ChatMessage>();
        }
        public Channel(bool owner, string id)
        {
            this.id = id;
            joined = false;
            this.owner = owner;
            messages = new ObservableCollection<ChatMessage>();
        }
        public Channel(string id, bool joined, bool owner)
        {
            this.id = id;
            this.joined = joined;
            this.owner = owner;
            messages = new ObservableCollection<ChatMessage>();
        }

        // INotifyPropertyChanged Inheritance contract
        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
