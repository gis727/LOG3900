using Quobject.SocketIoClientDotNet.Client;

namespace PolyPaint.Utilities
{
    public sealed class SSocket
    {
        public Socket io = IO.Socket(Settings.SERVER_SOCKET_IP);
        private static readonly SSocket instance = new SSocket();

        static SSocket()
        {
        }

        private SSocket()
        {
        }

        public static SSocket Instance
        {
            get
            {
                return instance;
            }
        }

        public void SetUpReset()
        {
            Instance.io = IO.Socket(Settings.SERVER_SOCKET_IP); // Reset socket
        }
    }

    public class SocketRoutes
    {
        public const string Login = "login";
        public const string Logout = "logout";
        public const string SendMessage = "send-message";
        public const string CreateChannel = "create-chat-room";
        public const string NewChannel = "new-chat-room";
        public const string JoinChannel = "enter-chat-room";
        public const string LeaveChannel = "leave-chat-room";
        public const string ChannelHistory = "chat-room-history";
        public const string DeleteChannel = "delete-chat-room";
        public const string CreateProfile = "create-account";
        public const string GetAllChannels = "get-all-rooms";

        public const string ViewAccount = "view-account";
        public const string ModifyAccount = "modify-account";
        public const string ModifyAvatar = "modify-avatar";

        public const string JoinChannelResponse = "join-chat-room";
        public const string CreateGame = "create-game";

        public const string GetParties = "get-all-parties";
        public const string CreateParty = "create-party";
        public const string JoinParty = "join-party";
        public const string LeaveParty = "leave-party";
        public const string PartyRemoved = "party-removed";
        public const string NewPartyCreated = "new-party";
        public const string PlayerJoined = "player-joined";
        public const string PlayerLeft = "player-left";
        public const string PartyStarted = "party-started";

        public const string StartGame = "start-game";
        public const string UpdateStats = "update-stats";
        public const string EndGame = "end-game"; // might not be necessary
        public const string EndParty = "end-party";
        public const string Answer = "answer";
        public const string Stroke = "stroke";
        public const string AddVirtualPlayer = "add-virtual-player";
        public const string RemoveVirtualPlayer = "remove-virtual-player";
        public const string GetClue = "get-clue";
        public const string Kick = "kick";
    }
}
