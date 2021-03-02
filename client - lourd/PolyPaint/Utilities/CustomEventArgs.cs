using System;

namespace PolyPaint.Utilities
{
    public class CPropertyChangedEventArgs : EventArgs
    {
        private readonly string property;
        public CPropertyChangedEventArgs()
        {
            property = "";
        }
        public CPropertyChangedEventArgs(string property)
        {
            this.property = property;
        }

        public string Property
        {
            get { return property; }
        }
    }

    public class PartyLeftEventArgs : EventArgs
    {
        public bool Kicked { get; }
        public PartyLeftEventArgs(bool kicked)
        {
            Kicked = kicked;
        }
    }
}
