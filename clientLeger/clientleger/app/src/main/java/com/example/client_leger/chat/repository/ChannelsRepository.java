package com.example.client_leger.chat.repository;

import android.app.Application;
import android.util.Log;

import com.example.client_leger.SocketSingleton;
import com.example.client_leger.chat.model.Channel;
import com.example.client_leger.chat.model.Message;
import com.example.client_leger.utils.LiveResource;
import com.example.client_leger.utils.MyJson;
import com.example.client_leger.utils.Resource;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

public class ChannelsRepository {
    private static ChannelsRepository instance;

    private SocketSingleton socket;
    private LiveResource<LinkedHashMap<String, LiveResource<Channel>>> channels =
            new LiveResource<>(Resource.DataStatus.NONE, new LinkedHashMap());

    private ChannelsRepository(Application application) {
        socket = SocketSingleton.get(application);


        socket.OnSendMessage(onSendMessage);
        socket.OnCreateChannel(onCreateChannel);
        socket.OnNewChannel(onNewChannel);
        socket.OnDeleteChannel(onDeleteChannel);
        socket.OnEnterChannel(onEnterChannel);
        socket.OnLeaveChannel(onLeaveChannel);
        socket.OnGetChannelHistory(onGetChannelHistory);
        socket.OnGetAllChannels(onGetAllChannels);

        getAllChannels();
    }

    public static ChannelsRepository getInstance(final Application application) {
        if (instance == null) {
            synchronized (ChannelsRepository.class) {
                if (instance == null) {
                    instance = new ChannelsRepository(application);
                }
            }
        }
        return instance;
    }

    public LiveResource<LinkedHashMap<String, LiveResource<Channel>>> getChannels() {
        return channels;
    }

    public void sendMessage(String channelName, String message) { socket.SendMessage(channelName, message); }

    public void createChannel(String name) {
        socket.CreateChannel(name);
    }

    public void deleteChannel(String name) {
        socket.DeleteChannel(name);
    }

    public void enterChannel(String name) {
        socket.EnterChannel(name);
    }

    public void leaveChannel(String name) {
        socket.LeaveChannel(name);
    }

    public void getChannelHistory(String name) { socket.GetChannelHistory(name);}

    public void getAllChannels() {
        socket.GetAllChannels();
    }

    public void answer(String answer) { socket.Answer(answer); }

    public void getClue() {
        socket.GetClue();
    }

    public void kick(String name) { socket.Kick(name); }

    Emitter.Listener onGetAllChannels = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONArray channelsJSON;

            try {
                channelsJSON = new JSONArray((String) args[0]);

                for (int i = 0; i < channelsJSON.length(); ++i) {
                    JSONObject channelJSON = channelsJSON.getJSONObject(i);

                    String channelName = channelJSON.getString("id");
                    Boolean isLeavable = !channelName.equals("main");
                    Boolean isJoined = channelJSON.getBoolean("joined");
                    Boolean isOwner = channelJSON.getBoolean("owner");

                    Channel channel = new Channel(channelName, isJoined, isOwner, isLeavable);

                    channels.getData().put(channelName, new LiveResource<>(Resource.DataStatus.NONE, channel));
                }

                channels.postStatus(Resource.DataStatus.NONE);
            } catch (Exception e) {
                Log.e("toz", e.getMessage());
            }
        }
    };

    Emitter.Listener onDeleteChannel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data;

            try {
                data = new JSONObject((String) args[0]);
                int state = data.getInt("state");

                if (Resource.DataStatus.valueOf(state) == Resource.DataStatus.ROOM_DOESNT_EXIST)
                    channels.postStatus(Resource.DataStatus.ROOM_DOESNT_EXIST);
                else {
                    String channelName = data.getString("roomId");
                    int channelsNewMessages = channels.getData().get(channelName).getData().getNewMessages();
                    channels.getData().get(channelName).getData().setNewMessages(0);

                    channels.getData().remove(channelName);
                    channels.postStatus(Resource.DataStatus.SUCCESS);
                }

            } catch (Exception e) {
                return;
            }
        }
    };

    Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                Message message = MyJson.parseMessage((String) args[0]);
                Log.d("asdfadfREPO", "call: " + (String) args[0]);

                if (channels.getData().get(message.getChannelName()) == null)
                    return;

                channels.getData().get(message.getChannelName()).getData().getMessages().add(message);
                channels.getData().get(message.getChannelName()).getData().addNewMessages(1);
                channels.getData().get(message.getChannelName()).postStatus(Resource.DataStatus.SUCCESS);
                channels.postStatus(Resource.DataStatus.SUCCESS);

            } catch (Exception e) {
                return;
            }
        }
    };

    Emitter.Listener onCreateChannel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data;

            try {
                data = new JSONObject((String) args[0]);
                int state = data.getInt("state");

                if (Resource.DataStatus.valueOf(state) == Resource.DataStatus.ID_ALREADY_USED)
                    channels.postStatus(Resource.DataStatus.ID_ALREADY_USED);
                else {
                    String channelName = data.getString("roomId");
                    Channel channel = new Channel(channelName, true, true, true);

                    channels.getData().put(channelName, new LiveResource<>(Resource.DataStatus.NONE, channel));
                    channels.postStatus(Resource.DataStatus.ROOM_CREATED);
                }

            } catch (Exception e) {
                return;
            }
        }
    };

    Emitter.Listener onNewChannel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data;

            try {
                data = new JSONObject((String) args[0]);
                String channelName = data.getString("roomId");
                Channel channel = new Channel(channelName, false, false, true);
                channels.getData().put(channelName, new LiveResource<>(Resource.DataStatus.NONE, channel));
                channels.postStatus(Resource.DataStatus.SUCCESS);

            } catch (Exception e) {
                return;
            }
        }
    };

    Emitter.Listener onEnterChannel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data;

            try {
                data = new JSONObject((String) args[0]);
                String channelName = data.getString("roomId");

                if (channels.getData().containsKey(channelName)) {
                    channels.getData().get(channelName).getData().setJoined(true);
                }
                else {
                    Channel channel = new Channel(channelName, true, false, true);
                    channels.getData().put(channelName, new LiveResource<>(Resource.DataStatus.NONE, channel));
                }

                channels.postStatus(Resource.DataStatus.SUCCESS);

            } catch (Exception e) {
                return;
            }
        }
    };

    Emitter.Listener onLeaveChannel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data;

            try {
                data = new JSONObject((String) args[0]);
                String channelName = data.getString("roomId");
                channels.getData().get(channelName).getData().setJoined(false);
                channels.postStatus(Resource.DataStatus.SUCCESS);

            } catch (Exception e) {
                return;
            }
        }
    };

    Emitter.Listener onGetChannelHistory = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            List<Message> channelHistory = MyJson.parseChannelHistory((String) args[0]);
            if (channelHistory != null && !channelHistory.isEmpty()) {
                String channelName = channelHistory.get(0).getChannelName();
                channels.getData().get(channelName).getData().setMessages(channelHistory);
                channels.getData().get(channelName).postStatus(Resource.DataStatus.ROOM_HISTORY_FETCHED);
            }

        }
    };

    public static void setInstanceToNull()
    {
        instance = null;
    }
}