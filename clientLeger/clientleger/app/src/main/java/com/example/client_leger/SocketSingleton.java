package com.example.client_leger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;

import com.example.client_leger.LiveGame.DrawingUpdate;
import com.example.client_leger.LiveGame.GameImagePath;
import com.example.client_leger.MainLobby.PartyModel;
import com.example.client_leger.Utilities.Extractor;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SocketSingleton {

    private static SocketSingleton instance;
    private static final String SERVER_ADDRESS = "//pi3-2020.herokuapp.com/";
    private Socket socket;
    private Context context;

    // INNER SOCKET METHODS
    public static SocketSingleton get(Context context){
        if(instance == null){
            instance = getSync(context);
        }
        instance.context = context;
        return instance;
    }

    public static synchronized SocketSingleton getSync(Context context){
        if (instance == null) {
            instance = new SocketSingleton(context);
        }
        instance.context = context;
        return instance;
    }

    public Socket getSocket(){
        return this.socket;
    }

    private SocketSingleton(Context context){
        this.context = context;
        this.socket = getChatServerSocket();
    }

    private Socket getChatServerSocket(){
        try {
            Socket socket;
            {
                socket = IO.socket(SERVER_ADDRESS);
            }
            return socket;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void dispose() {
        instance.getSocket().off();
        instance.getSocket().disconnect();
        instance.getSocket().close();
        instance = null;
    }

    /* ----- SOCKET INTERFACE ----- */

    private Extractor extractor = new Extractor();

    // EMITTERS
    public void Login(String username, String password) {
        if(!instance.getSocket().connected()) { // connect if not connected
            instance.getSocket().connect();
        }
        try {
            JSONObject loginDetails = new JSONObject();
            loginDetails.put("nickname", new String(username.getBytes("UTF-8")));
            loginDetails.put("password", new String(password.getBytes("UTF-8") ));
            instance.getSocket().emit("login", loginDetails);
        } catch (Exception e) {}
    }

    public void ViewAccount(String nickname) {
        try {
            instance.getSocket().emit("view-account", nickname);
        } catch (Exception e) {}
    }

    public void ModifyAccount(String nickname, String name, String surname, String password, String avatar) {
        try {
            Log.d("socket", "ModifyAccount: ");
            JSONObject accountDataJSON = new JSONObject();
            accountDataJSON.put("nickname", new String(nickname.getBytes("UTF-8")));
            accountDataJSON.put("name", new String(name.getBytes("UTF-8")));
            accountDataJSON.put("surname", new String(surname.getBytes("UTF-8")));
            accountDataJSON.put("password", new String(password.getBytes("UTF-8")));
            accountDataJSON.put("avatar", avatar);
            instance.getSocket().emit("modify-account", accountDataJSON.toString());
        } catch (Exception e) {}
    }

    public void SendMessage(String channelName, String message) {
        try {
            JSONObject messageJSON = new JSONObject();
            messageJSON.put("roomId", channelName);
            messageJSON.put("message", new String(message.getBytes("UTF-8")));
            instance.getSocket().emit("send-message", messageJSON.toString());
        } catch (Exception e) {}
    }


    public void CreateChannel(String name) {
        try {
            instance.getSocket().emit("create-chat-room", name);
        } catch (Exception e) {}
    }

    public void DeleteChannel(String name) {
        try {
            instance.getSocket().emit("delete-chat-room", name);
        } catch (Exception e) {}
    }

    public void EnterChannel(String name) {
        try {
            instance.getSocket().emit("enter-chat-room", name);
        } catch (Exception e) {}
    }

    public void LeaveChannel(String name) {
        try {
            instance.getSocket().emit("leave-chat-room", name);
        } catch (Exception e) {}
    }

    public void GetAllChannels() {
        try {
            instance.getSocket().emit("get-all-rooms");
        } catch (Exception e) {}
    }

    public void GetChannelHistory(String name) {
        try {
            instance.getSocket().emit("chat-room-history", name);
        } catch (Exception e) {}
    }

    public void CreateAccount(String name, String surname, String password, String nickname, String avatar) {
        if(!instance.getSocket().connected()) { // connect if not connected
            instance.getSocket().connect();
        }
        try {
            JSONObject accountDetails = new JSONObject();
            accountDetails.put("name", new String(name.getBytes("UTF-8")));
            accountDetails.put("surname", new String(surname.getBytes("UTF-8")));
            accountDetails.put("password", new String(password.getBytes("UTF-8") ));
            accountDetails.put("nickname", new String(nickname.getBytes("UTF-8")));
            accountDetails.put("avatar", new String(avatar.getBytes("UTF-8")));
            instance.getSocket().emit("create-account", accountDetails.toString());
        } catch (Exception e) {}
    }

    public void Answer(String answer) {
        try {
            instance.getSocket().emit("answer", answer);
        } catch (Exception e) {}
    }

    public void GetClue() {
        try {
            instance.getSocket().emit("get-clue");
        } catch (Exception e) {}
    }

    public void Kick(String name) {
        try {
            instance.getSocket().emit("kick", name);
        } catch (Exception e) {}
    }

    public void AddVirtualPlayer(String partyId) {
        try {
            instance.getSocket().emit("add-virtual-player", partyId);
        } catch (Exception e) {}
    }

    public void RemoveVirtualPlayer(String playerId, String partyId) {
        try {
            JSONObject virtualPlayer = new JSONObject();
            virtualPlayer.put("id", playerId);
            virtualPlayer.put("partyId", partyId);
            instance.getSocket().emit("remove-virtual-player", virtualPlayer.toString());
        } catch (Exception e) {}
    }

    public void LaunchParty() {
        try {
            instance.getSocket().emit("launch-party");
        } catch (Exception e) {}
    }

    public void StartParty() {
        try {
            instance.getSocket().emit("start-game");
        } catch (Exception e) {}
    }

    public void GetParties() {
        try {
            instance.getSocket().emit("get-all-parties");
        } catch (Exception e) {}
    }

    public void CreateParty(String partyName, PartyModel.GameMode mode) {
        try {
            JSONObject party = new JSONObject();
            party.put("name", new String(partyName.getBytes("UTF-8")));
            party.put("mode", mode.ordinal());
            party.put("platform", PartyModel.Platform.all.ordinal());
            party.put("difficulty", 1); // Only for debug. Difficulty will be removed from the server. So, the value doesn't really matter.
            instance.getSocket().emit("create-party", party.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void JoinParty(String partyId) {
        try {
            instance.getSocket().emit("join-party", partyId);
        } catch (Exception e) {}
    }

    public void LeaveParty(String partyId) {
        try {
            instance.getSocket().emit("leave-party", partyId);
        } catch (Exception e) {}
    }

    public void SendStroke(DrawingUpdate update, Canvas canvas) {
        try {
            JSONObject updateToSend = new JSONObject();
            if (update.path != null){
                updateToSend.put("path", buildJsonPathObject(update.path, canvas));
            }
            JSONArray imageToSend = new JSONArray();
            if (update.image != null){
                for(GameImagePath path: update.image.paths){
                    imageToSend.put(buildJsonPathObject(path, canvas));
                }
            }
            JSONObject gameImage = new JSONObject();
            gameImage.put("paths",imageToSend);
            updateToSend.put("image", gameImage);
            instance.getSocket().emit("stroke", updateToSend.toString());
        } catch (Exception e) {}
    }
    private JSONObject buildJsonPathObject(GameImagePath path, Canvas canvas) throws JSONException {
        JSONObject pathToSend = new JSONObject();
        if (path != null) {
            JSONArray points = new JSONArray();
            for (PointF point: path.points) {
                points.put(point.x + "," + point.y);
            }
            pathToSend.put("hexColor", path.hexColor);
            pathToSend.put("stylusPoint", path.stylusPoint);
            pathToSend.put("strokeWidth", path.strokeWidth);
            pathToSend.put("points", points);
            pathToSend.put("isNewStroke", path.isNewStroke);
            pathToSend.put("canvasWidth", (double)canvas.getWidth());
            pathToSend.put("canvasHeight", (double)canvas.getHeight());
        }
        return pathToSend;
    }

    // RECEIVERS
    public void OnDisconnect(final Emitter.Listener listener) {
        instance.getSocket().on(Socket.EVENT_DISCONNECT, listener::call);
    }

    public void OnLogin(final Emitter.Listener listener) {
        instance.getSocket().on("login", listener::call);
    }

    public void OnCreateAccount(final Emitter.Listener listener) {
        instance.getSocket().on("create-account", listener::call);
    }

    public void OnViewAccount(final Emitter.Listener listener) {
        instance.getSocket().on("view-account", listener::call);
    }

    public void OnModifyAccount(final Emitter.Listener listener) {
        instance.getSocket().on("modify-account", listener::call);
    }

    public void OnSendMessage(final Emitter.Listener listener) {
        instance.getSocket().on("send-message", listener::call);
    }

    public void OnCreateChannel(final Emitter.Listener listener) {
        instance.getSocket().on("create-chat-room", listener::call);
    }

    public void OnNewChannel(final Emitter.Listener listener) {
        instance.getSocket().on("new-chat-room", listener::call);
    }

    public void OnDeleteChannel(final Emitter.Listener listener) {
        instance.getSocket().on("delete-chat-room", listener::call);
    }

    public void OnEnterChannel(final Emitter.Listener listener) {
        instance.getSocket().on("enter-chat-room", listener::call);
    }

    public void OnLeaveChannel(final Emitter.Listener listener) {
        instance.getSocket().on("leave-chat-room", listener::call);
    }

    public void OnGetChannelHistory(final Emitter.Listener listener) {
        instance.getSocket().on("chat-room-history", listener::call);
    }

    public void OnGetAllChannels(final Emitter.Listener listener) {
        instance.getSocket().on("get-all-rooms", listener::call);
    }

    public void OnAnswer(final Emitter.Listener listener) {
        instance.getSocket().on("answer", listener::call);
    }

    public void OnLaunchParty(final Emitter.Listener listener) {
        instance.getSocket().on("launch-party", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call(args);
            }
        });
    }

    public void OnGetParties(final Emitter.Listener listener) {
        instance.getSocket().off("get-all-parties");
        instance.getSocket().on("get-all-parties", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final List<PartyModel.Party> PARTIES = new ArrayList<>();
                try {
                    JSONArray data = new JSONArray((String) args[0]);
                    for (int i = 0; i < data.length(); i++) {
                        Log.i("PARTIES", " " + data.getJSONObject(i));
                        PARTIES.add(extractor.extractParty(data.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                        e.printStackTrace();
                }
                listener.call(PARTIES);
            }
        });
    }

    public void OnPartyStarted(final Emitter.Listener listener) {
        instance.getSocket().on("party-started", listener::call);
    }

    public void OnCreateParty(final Emitter.Listener listener) {
        instance.getSocket().off("create-party");
        instance.getSocket().on("create-party", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject state = new JSONObject((String)args[0]);
                    if (state.getInt("state") == 0) listener.call(state.getString("roomId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void OnNewPartyCreated(final Emitter.Listener listener) {
        instance.getSocket().off("new-party");
        instance.getSocket().on("new-party", new Emitter.Listener() {
            PartyModel.Party party = null;
            @Override
            public void call(Object... args) {
                try {
                    party = extractor.extractParty(new JSONObject((String) args[0]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.call(party);
            }
        });
    }

    public void OnJoinParty(final Emitter.Listener listener) {
        instance.getSocket().off("join-party");
        instance.getSocket().on("join-party", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String partyId = null;
                try {
                    JSONObject data = new JSONObject((String) args[0]);
                    int state = data.getInt("state");
                    if (state == 0) partyId = data.getString("roomId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.call(partyId);
            }
        });
    }

    public void OnLeaveParty(final Emitter.Listener listener) {
        instance.getSocket().off("leave-part");
        instance.getSocket().on("leave-party", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String partyId = null;
                try {
                    JSONObject data = new JSONObject((String) args[0]);
                    int state = data.getInt("state");
                    if (state == 0) partyId = data.getString("roomId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.call(partyId);
            }
        });
    }

    public void OnPartyRemoved(final Emitter.Listener listener) {
        instance.getSocket().off("party-removed");
        instance.getSocket().on("party-removed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call((String) args[0]);
            }
        });
    }

    public void OnPlayerJoined(final Emitter.Listener listener) {
        instance.getSocket().off("player-joined");
        instance.getSocket().on("player-joined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                PartyModel.Player player = null;
                try {
                    JSONObject playerObj = new JSONObject((String)args[0]);
                    player = extractor.extractPlayer(playerObj, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.call(player);
            }
        });
    }

    public void OnPlayerLeft(final Emitter.Listener listener) {
        instance.getSocket().off("player-left");
        instance.getSocket().on("player-left", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                PartyModel.Player player = new PartyModel.Player();
                try {
                    JSONObject playerObj = new JSONObject((String)args[0]);
                    player.id = playerObj.getString("id");
                    player.partyId = playerObj.getString("partyId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.call(player);
            }
        });
    }

    public void OnStartGame(final Emitter.Listener listener, boolean clearPreviousListeners) {
        if (clearPreviousListeners) instance.getSocket().off("start-game");
        instance.getSocket().on("start-game", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    listener.call(extractor.extractGame(new JSONObject((String)args[0])));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void OnStatsUpdate(final Emitter.Listener listener) {
        instance.getSocket().off("update-stats");
        instance.getSocket().on("update-stats", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    listener.call(extractor.extractGameStats(new JSONObject((String)args[0])));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void OnPartyFinished(final Emitter.Listener listener) {
        instance.getSocket().off("end-party");
        instance.getSocket().on("end-party", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call();
            }
        });
    }

    public void OnStroke(final Emitter.Listener listener) {
        instance.getSocket().off("stroke");
        instance.getSocket().on("stroke", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    DrawingUpdate update = extractor.extractDrawingUpdate(new JSONObject((String)args[0]));
                    //update.fitPointsIntoCanvas();
                    if (!update.path.points.isEmpty() || (update.image != null)) listener.call(update);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}