using FMUD.Models.Coms;
using Newtonsoft.Json;
using PolyPaint.Models;
using PolyPaint.Models.Coms;
using PolyPaint.Utilities;
using Quobject.SocketIoClientDotNet.Client;
using System;
using System.Collections.Generic;
using static PolyPaint.Models.Coms.Game;

namespace PolyPaint.Services
{
    static class ServerService
    {
        // SENDERS
        public static void SendMessage(string message, string roomId = "main")
        {
            SSocket.Instance.io.Emit(SocketRoutes.SendMessage,
                JsonConvert.SerializeObject(new ChatMessageOut(roomId, message)));
        }
        public static void CreateChannel(string roomId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.CreateChannel, roomId);
        }
        public static void DeleteChannel(string roomId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.DeleteChannel, roomId);
        }
        public static void LeaveChannel(string roomId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.LeaveChannel, roomId);
        }
        public static void JoinChannel(string roomId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.JoinChannel, roomId);
        }
        public static void GetChannelHistory(string roomId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.ChannelHistory, roomId);
        }
        public static void GetAllChannels()
        {
            SSocket.Instance.io.Emit(SocketRoutes.GetAllChannels);
        }
        public static void Login(string nickname, string password)
        {
            SSocket.Instance.io.Emit(SocketRoutes.Login, JsonConvert.SerializeObject(new LoginMessage(nickname, password)));
        }
        public static void Logout(Action<object> action)
        {
            SSocket.Instance.io.Off();
            SSocket.Instance.io.Emit(SocketRoutes.Logout);
            SSocket.Instance.SetUpReset();
            action(null);
        }
        public static void CreateProfile(string name, string surname, string password, string nickname, string avatar)
        {
            SSocket.Instance.io.Emit(SocketRoutes.CreateProfile, JsonConvert.SerializeObject(new ProfileCreationMessage(name, surname, password, nickname, avatar)));
        }
        public static void ModifyAccount(string name, string surname, string password, string nickname, string avatar)
        {
            SSocket.Instance.io.Emit(SocketRoutes.ModifyAccount, JsonConvert.SerializeObject(new ProfileCreationMessage(name, surname, password, nickname, avatar)));
        }
        public static void ModifyAvatar(string avatar)
        {
            SSocket.Instance.io.Emit(SocketRoutes.ModifyAvatar, avatar);
        }
        public static void GetAccountInfo()
        {
            SSocket.Instance.io.Emit(SocketRoutes.ViewAccount);
        }
        public static void CreateGame(Game game)
        {
            SSocket.Instance.io.Emit(SocketRoutes.CreateGame, JsonConvert.SerializeObject(game));
        }

        public static void GetParties(GameMode mode = GameMode.none, Platform platform = Platform.all)
        {
            SSocket.Instance.io.Emit(SocketRoutes.GetParties, JsonConvert.SerializeObject(new PartyControlData(Difficulty.Easy, mode, platform)));
        }
        public static void CreateParty(GameMode mode, Platform platform, string name)
        {
            SSocket.Instance.io.Emit(SocketRoutes.CreateParty, JsonConvert.SerializeObject(new PartyControlData(Difficulty.Easy, mode, platform, name)));
        }
        public static void JoinParty(string partyId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.JoinParty, partyId);
        }
        public static void LeaveParty(string partyId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.LeaveParty, partyId);
        }

        public static void StartParty()
        {
            SSocket.Instance.io.Emit(SocketRoutes.StartGame);
        }
        public static void Answer(string answer)
        {
            SSocket.Instance.io.Emit(SocketRoutes.Answer, answer);
        }
        public static void GetAClue()
        {
            SSocket.Instance.io.Emit(SocketRoutes.GetClue);
        }
        public static void SendDrawingUpdate(DrawingUpdate drawingUpdate)
        {
            SSocket.Instance.io.Emit(SocketRoutes.Stroke, JsonConvert.SerializeObject(drawingUpdate));
        }
        public static void AddVirtualPlayer(string partyId)
        {
            SSocket.Instance.io.Emit(SocketRoutes.AddVirtualPlayer, partyId);
        }
        public static void RemoveVirtualPlayer(string partyId, string playerId)
        {
            Player player = new Player
            {
                id = playerId,
                partyId = partyId
            };
            SSocket.Instance.io.Emit(SocketRoutes.RemoveVirtualPlayer, JsonConvert.SerializeObject(player));
        }

        #region DROPPED FEATURES
        //public static void Kick(string playerId)
        //{
        //    SSocket.Instance.io.Emit(SocketRoutes.Kick, playerId);
        //}
        //public static void OnKicked(Action action)
        //{
        //    SSocket.Instance.io.Off(SocketRoutes.Kick);
        //    SSocket.Instance.io.On(SocketRoutes.Kick, () =>
        //    {
        //        action();
        //    });
        //}
        #endregion

        // RECEIVERS
        public static void OnChatMessage(Action<ChatMessage> action)
        {
            SSocket.Instance.io.On(SocketRoutes.SendMessage, (data) =>
            {
                try
                {
                    ChatMessage newMessage = JsonConvert.DeserializeObject<ChatMessage>((string)data);
                    action(newMessage);
                }
                catch { /*...*/ }
            });
        }
        public static void OnLoginMessage(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.Login, (data) =>
            {
                try
                {
                    State loginResult = JsonConvert.DeserializeObject<State>((string)data);
                    action(loginResult);
                }
                catch { /*...*/ }
            });
        }
        public static void OnCreateChannel(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.CreateChannel, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnNewChannel(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.NewChannel, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnDeleteChannel(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.DeleteChannel, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
                
            });
        }
        public static void OnJoinChannel(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.JoinChannel, (data) =>
            {
                try
                {
                    State joinChannelResult = JsonConvert.DeserializeObject<State>((string)data);
                    action(joinChannelResult);
                }
                catch { /*...*/ }
            });
        }
        public static void OnLeaveChannel(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.LeaveChannel, (data) =>
            {
                try
                {
                    State leaveChannelResult = JsonConvert.DeserializeObject<State>((string)data);
                    action(leaveChannelResult);
                }
                catch { /*...*/ }
            });
        }
        public static void OnGetChannelHistory(Action<ChatMessage[]> action)
        {
            SSocket.Instance.io.On(SocketRoutes.ChannelHistory, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<List<ChatMessage>>((string)data).ToArray());
                }
                catch { /*...*/ }
            });
        }
        public static void OnGetAllChannels(Action<ChannelsMessage[]> action)
        {
            SSocket.Instance.io.On(SocketRoutes.GetAllChannels, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<List<ChannelsMessage>>((string)data).ToArray());
                }
                catch { /*...*/ }
            });
        }
        public static void OnProfileCreation(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.CreateProfile, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnGetAccountInfo(Action<ProfileGetMessage> action)
        {
            SSocket.Instance.io.On(SocketRoutes.ViewAccount, (data) =>
            {
                try
                {
                    ProfileGetMessage profile = JsonConvert.DeserializeObject<ProfileGetMessage>((string)data);
                    action(profile);
                }
                catch { /*...*/ }
            });
        }
        public static void OnAvatarModified(Action<State> action)
        {
            SSocket.Instance.io.Off(SocketRoutes.ModifyAvatar);
            SSocket.Instance.io.On(SocketRoutes.ModifyAvatar, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnGameCreation(Action<State> action)
        {
            SSocket.Instance.io.Off(SocketRoutes.CreateGame);
            SSocket.Instance.io.On(SocketRoutes.CreateGame, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnModifyAccount(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.ModifyAccount, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }

        public static void OnGetParties(Action<Party[]> action)
        {
            SSocket.Instance.io.On(SocketRoutes.GetParties, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<List<Party>>((string)data).ToArray());
                }
                catch { /*...*/ }
            });
        }
        public static void OnPartyCreation(Action<State> action)
        {
            SSocket.Instance.io.Off(SocketRoutes.CreateParty);
            SSocket.Instance.io.On(SocketRoutes.CreateParty, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnNewPartyCreation(Action<Party> action)
        {
            SSocket.Instance.io.On(SocketRoutes.NewPartyCreated, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<Party>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnJoinParty(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.JoinParty, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnLeaveParty(Action<State> action)
        {
            SSocket.Instance.io.On(SocketRoutes.LeaveParty, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<State>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnPartyRemoved(Action<string> action)
        {
            SSocket.Instance.io.On(SocketRoutes.PartyRemoved, data => action((string)data));
        }
        public static void OnPlayerJoined(Action<Player> action)
        {
            SSocket.Instance.io.On(SocketRoutes.PlayerJoined, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<Player>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnPlayerLeft(Action<Player> action)
        {
            SSocket.Instance.io.On(SocketRoutes.PlayerLeft, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<Player>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnPartyStarted(Action<string> action)
        {
            SSocket.Instance.io.On(SocketRoutes.PartyStarted, data => action((string)data));
        }

        // Live game
        public static void OnUpdateStats(Action<GameStats> action)
        {
            SSocket.Instance.io.Off(SocketRoutes.UpdateStats);
            SSocket.Instance.io.On(SocketRoutes.UpdateStats, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<GameStats>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnEndParty(Action<GameStats> action)
        {
            SSocket.Instance.io.On(SocketRoutes.EndParty, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<GameStats>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnAnswer(Action<bool> action)
        {
            SSocket.Instance.io.On(SocketRoutes.Answer, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<bool>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnStartGame(Action<Game> action, bool clearPreviousListener = false)
        {
            if (clearPreviousListener)
            {
                SSocket.Instance.io.Off(SocketRoutes.StartGame);
            }

            SSocket.Instance.io.On(SocketRoutes.StartGame, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<Game>((string)data));
                }
                catch { /*...*/ }
            });
        }
        public static void OnDrawingUpdate(Action<DrawingUpdate> action)
        {
            SSocket.Instance.io.Off(SocketRoutes.Stroke);
            SSocket.Instance.io.On(SocketRoutes.Stroke, (data) =>
            {
                try
                {
                    action(JsonConvert.DeserializeObject<DrawingUpdate>((string)data));
                }
                catch { /*...*/ }
            });
        }

        public static void OnConnectionError(Action<object> action)
        {
            SSocket.Instance.io.On(Socket.EVENT_RECONNECT, () => action(null));
        }

        public static void Off(string route)
        {
            SSocket.Instance.io.Off(route);
        }
        public static void OffChannels()
        {
            SSocket.Instance.io.Off(SocketRoutes.ChannelHistory);
            SSocket.Instance.io.Off(SocketRoutes.SendMessage);
            SSocket.Instance.io.Off(SocketRoutes.GetAllChannels);
            SSocket.Instance.io.Off(SocketRoutes.CreateChannel);
            SSocket.Instance.io.Off(SocketRoutes.DeleteChannel);
            SSocket.Instance.io.Off(SocketRoutes.LeaveChannel);
            SSocket.Instance.io.Off(SocketRoutes.JoinChannel);
            SSocket.Instance.io.Off(SocketRoutes.SendMessage);
        }
        public static void OffParties()
        {
            SSocket.Instance.io.Off(SocketRoutes.GetParties);
            SSocket.Instance.io.Off(SocketRoutes.JoinParty);
            SSocket.Instance.io.Off(SocketRoutes.LeaveParty);
            SSocket.Instance.io.Off(SocketRoutes.NewPartyCreated);
            SSocket.Instance.io.Off(SocketRoutes.PartyRemoved);
        }
    }
}
