namespace PolyPaint.Models
{
    class ChatMessage
    {
        public string author { get; set; }
        public string message { get; set; }
        public long timestamp { get; set; }
        public string roomId { get; set; }
        public string avatar { get; set; }

        public ChatMessage(string author, string message, long timestamp, string roomId, string avatar)
        {
            this.author = author;
            this.message = message;
            this.timestamp = timestamp;
            this.roomId = roomId;
            this.avatar = avatar;
        }
    }
    class ChatMessageOut
    {
        public string roomId { get; set; }
        public string message { get; set; }

        public ChatMessageOut(string roomId = "main", string message = "")
        {
            this.roomId = roomId;
            this.message = message;
        }
    }
}
