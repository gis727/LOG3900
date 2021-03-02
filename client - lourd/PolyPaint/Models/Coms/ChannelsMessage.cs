namespace PolyPaint.Models.Coms
{
    class ChannelsMessage
    {
        public string id { get; set; }
        public bool owner { get; set; }
        public bool joined { get; set; }

        public ChannelsMessage(string id, bool owner, bool joined)
        {
            this.id = id;
            this.owner = owner;
            this.joined = joined;
        }
    }
}
