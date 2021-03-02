using PolyPaint.Models.Coms;
using PolyPaint.Services;
using PolyPaint.Utilities;
using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows;

namespace PolyPaint.Models
{
    class Chat : INotifyPropertyChanged
    {
        public string CurrentChannelId
        {
            get
            {
                return SharedChatModel.CurrentChannelId;
            }
            set
            {
                SharedChatModel.CurrentChannelId = value;
                PropertyModified();
            }
        }

        public ObservableCollection<ChatMessage> ChatMessages
        {
            get
            {
                Channel currentChannel = SharedChatModel.Channels[0];
                foreach (Channel channel in SharedChatModel.Channels)
                {
                    if (channel.id.Equals(SharedChatModel.CurrentChannelId))
                    {
                        currentChannel = channel;
                    }
                }
                return currentChannel.messages;
            }
        }

        public ObservableCollection<Channel> AllChannels
        {
            get
            {
                return SharedChatModel.Channels;
            }
            set
            {
                SharedChatModel.Channels = value;
                PropertyModified("AllChannels");
            }
        }

        public string UserCurrentMessage
        {
            get
            {
                return SharedChatModel.UserCurrentMessage;
            }
            set
            {
                SharedChatModel.UserCurrentMessage = value;
                PropertyModified();
            }
        }
        public string ChannelInput
        {
            get
            {
                return SharedChatModel.ChannelInput;
            }
            set
            {
                SharedChatModel.ChannelInput = value;
                PropertyModified();
            }
        }

        public Chat()
        {
            SharedChatModel.SetUp();
            SharedChatModel.PropertyChanged -= OnSharedModelChanged;
            SharedChatModel.PropertyChanged += OnSharedModelChanged;
        }

        public void SendMessage(object o)
        {
            string message = (string)o;
            message = message != null ? message : UserCurrentMessage;
            MessageInterpreter interpreter = new MessageInterpreter();

            if (interpreter.IsHistoryRequest(message))
            {
                ShowOlderMessages(o);
            }
            else if (SharedChatModel.IsInPartyChat())
            {
                if (interpreter.IsAGuess(message))
                {
                    ServerService.Answer(interpreter.ExtractWordFromCommand(message));
                }
                else if (interpreter.IsAClueRequest(message))
                {
                    ServerService.GetAClue();
                }
                #region DROPPED FEATURE
                //else if (interpreter.IsAKickRequest(message))
                //{
                //    ServerService.Kick(interpreter.ExtractWordFromCommand(message));
                //}
                #endregion
                else
                {
                    ServerService.SendMessage(message, SharedChatModel.CurrentChannelId);
                }
            }
            else
            {
                ServerService.SendMessage(message, SharedChatModel.CurrentChannelId);
            }

            if (SharedChatModel.IsInPartyChat())
            {
                UserCurrentMessage = MessageInterpreter.GameGuessPrefix + " ";
            }
            else
            {
                UserCurrentMessage = "";
            }
        }

        public void ShowOlderMessages(object o)
        {
            ServerService.GetChannelHistory(SharedChatModel.CurrentChannelId);
        }

        public void JoinChannel(object o)
        {
            ServerService.JoinChannel(ChannelInput);
            ChannelInput = "";
            // iterate to highlight through listbox
        }

        public void SwitchChannel(string channelId)
        {
            foreach (Channel channel in SharedChatModel.Channels)
            {
                if (channel.id.Equals(channelId))
                {
                    if (channel.joined)
                    {
                        SharedChatModel.SwitchChannel(channelId);
                        break;
                    }
                    else
                    {
                        ServerService.JoinChannel(channelId);
                    }
                }
            }
        }

        public void LeaveChannel(string channelId)
        {
            foreach (Channel channel in SharedChatModel.Channels)
            {
                if (channel.id.Equals(channelId))
                {
                    if (channel.joined)
                    {
                        ServerService.LeaveChannel(channelId);
                    }
                    break;
                }
            }
        }

        public void CreateChannel(string channelId)
        {
            ServerService.CreateChannel(channelId);
        }

        public void DeleteChannel(string channelId)
        {
            ServerService.DeleteChannel(channelId);
        }

        public void GetChannelHistory(object o)
        {
            ServerService.GetChannelHistory((string)o);
        }

        // INotifyPropertyChanged Inheritance contract
        public event PropertyChangedEventHandler PropertyChanged;
        protected void PropertyModified([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        private void OnSharedModelChanged(object sender, EventArgs a)
        {
            PropertyModified(((CPropertyChangedEventArgs)a).Property);
        }
    }

    static class SharedChatModel
    {
        private static bool listenerSet = false;

        public static void SetUp()
        {
            // Set listeners
            if (listenerSet)
            {
                return;
            }

            listenerSet = true;
            ServerService.OffChannels();
            ServerService.OnChatMessage((newMessage) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    foreach (Channel channel in Channels)
                    {
                        if (channel.id.Equals(newMessage.roomId))
                        {
                            channel.messages.Add(newMessage);
                        }
                    }
                    PropertyChanged(null, new CPropertyChangedEventArgs("ChatMessages"));
                });
            });

            ServerService.OnGetChannelHistory((messages) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    foreach (Channel channel in Channels)
                    {
                        if (channel.id.Equals(CurrentChannelId))
                        {
                            channel.messages.Clear();
                            foreach (ChatMessage message in messages)
                            {
                                channel.messages.Add(message);
                            }
                        }
                    }
                    PropertyChanged(null, new CPropertyChangedEventArgs("ChatMessages"));
                });
            });

            ServerService.OnGetAllChannels((channels) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    Channels.Clear();
                    foreach (ChannelsMessage channel in channels)
                    {
                        Channel newChannel = (channel.id.Equals("main")) ? new Channel(channel.id, true) : new Channel(channel.id, channel.joined, channel.owner);
                        Channels.Add(newChannel);
                    }
                });
            });

            ServerService.OnCreateChannel((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    switch ((STATE)state.state)
                    {
                        case STATE.Success:
                            Channels.Add(new Channel(state.roomId, true, true));
                            break;
                        default:
                            break;
                    }
                });
            });

            ServerService.OnNewChannel((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    switch ((STATE)state.state)
                    {
                        case STATE.Success:
                            Channels.Add(new Channel(state.roomId));
                            break;
                        default:
                            break;
                    }
                });
            });

            ServerService.OnDeleteChannel((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    switch ((STATE)state.state)
                    {
                        case STATE.Success:
                            Channel target = null;
                            foreach (Channel channel in Channels)
                            {
                                if (channel.id.Equals(state.roomId))
                                {
                                    target = channel;
                                }
                            }
                            if (target != null)
                            {
                                Channels.Remove(target);
                                if (target.id.Equals(CurrentChannelId)) // we are leaving the active channel
                                {
                                    CurrentChannelId = "main"; // head to main, or maybe the previous channel ?
                                    PropertyChanged(null, new CPropertyChangedEventArgs("ChatMessages"));
                                    trigger("CurrentChannelId");
                                }
                            }
                            break;
                        default:
                            break;
                    }
                });
            });

            ServerService.OnLeaveChannel((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    switch ((STATE)state.state)
                    {
                        case STATE.Success:
                            foreach (Channel channel in Channels)
                            {
                                if (channel.id.Equals(state.roomId))
                                {
                                    channel.joined = false;
                                    break;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                });
            });

            ServerService.OnJoinChannel((state) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    switch ((STATE)state.state)
                    {
                        case STATE.Success:
                            Channel targetChannel = null;
                            foreach (Channel channel in Channels)
                            {
                                if (channel.id.Equals(state.roomId))
                                {
                                    targetChannel = channel;
                                    break;
                                }
                            }
                            if (targetChannel == null) // We weren't informed that the channel was there
                            {
                                targetChannel = new Channel(state.roomId);
                                Channels.Add(targetChannel);
                            }
                            targetChannel.joined = true;
                            CurrentChannelId = targetChannel.id;
                            PropertyChanged(null, new CPropertyChangedEventArgs("ChatMessages"));
                            trigger("CurrentChannelId");
                            break;
                        default:
                            break;
                    }
                });
            });

            // Get all channels
            ServerService.GetAllChannels();
        }

        public static void reset()
        {
            UserCurrentMessage = "";
            ChannelInput = "";
            CurrentChannelId = "main";
            Channels = new ObservableCollection<Channel> { new Channel("") };
        }

        public static void dispose()
        {
            listenerSet = false;
            PropertyChanged = null;
        }

        public static void SwitchChannel(string channelId)
        {
            CurrentChannelId = channelId;
            trigger("ChatMessages");
            trigger("CurrentChannelId");
        }
        public static void SetMessage(string message)
        {
            UserCurrentMessage = message;
            trigger("UserCurrentMessage");
        }

        // Let all views know that something is up !
        public static void trigger(string propertyName)
        {
            PropertyChanged(null, new CPropertyChangedEventArgs(propertyName));
        }

        public static bool IsInPartyChat()
        {
            return GameMode && CurrentChannelId.Equals(CurrenPartyId);
        }

        public static string UserCurrentMessage = "";
        public static string ChannelInput = "";
        public static string CurrentChannelId = "main";
        public static ObservableCollection<Channel> Channels = new ObservableCollection<Channel> { new Channel("") };
        public static bool GameMode = false;
        public static string CurrenPartyId = "";
        public static event EventHandler PropertyChanged;
    }
}
