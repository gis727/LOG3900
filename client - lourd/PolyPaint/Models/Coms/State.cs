namespace PolyPaint.Models.Coms
{
    class State
    {
        public int state { get; set; }
        public string roomId { get; set; }
    }

    enum STATE
    {
        ServerError = -2,
        BadRequest,
        Success,
        UsedNickname,
        AlreadyConnected,
        BadPassword,
        RoomDoesntExist,
    }
}
