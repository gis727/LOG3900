package com.example.client_leger.MainLobby;

import android.content.Context;

import com.example.client_leger.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;

import java.util.ArrayList;
import java.util.List;

// All parties
public class PartyModel {
    public static List<Party> PARTIES = new ArrayList<>();
    public static void setUp(final Context context, final MainLobbyFragment.FragmentUpdater fragmentUpdater) {

        // LISTENERS
        SocketSingleton.get(context).OnGetParties(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                PARTIES = (List<Party>)args[0];
                fragmentUpdater.update();
            }
        });
        SocketSingleton.get(context).OnNewPartyCreated(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                PARTIES.add((Party)args[0]);
                fragmentUpdater.update();
            }
        });
        SocketSingleton.get(context).OnPartyRemoved(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String partyId = (String)args[0];
                for(int i=0; i < PARTIES.size(); i++) {
                    if (PARTIES.get(i).id.equals(partyId)) {
                        PARTIES.remove(i);
                        break;
                    }
                }
                fragmentUpdater.update();
            }
        });

        SocketSingleton.get(context).OnPartyStarted(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String partyId = (String) args[0];
                for(Party party: PARTIES) {
                    if (party.id.equals(partyId)) {
                        party.started = true;
                        break;
                    }
                }
                fragmentUpdater.update();
            }
        });

        SocketSingleton.get(context).OnPlayerJoined(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Player joiningPlayer = (Player) args[0];
                for(Party party: PARTIES) {
                    if (party.id.equals(joiningPlayer.partyId)) {
                        party.players.add(joiningPlayer);
                        party.playersCount++;
                        break;
                    }
                }
                fragmentUpdater.update();
            }
        });
        SocketSingleton.get(context).OnPlayerLeft(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Player leavingPlayer = (Player) args[0];
                for(int i=0; i < PARTIES.size(); i++) {
                    Party party = PARTIES.get(i);
                    if (party.id.equals(leavingPlayer.partyId)) {
                        for(Player player: party.players) {
                            if (player.id.equals(leavingPlayer.id)) {
                                party.players.remove(player);
                                if (party.playersCount > 0) party.playersCount--;
                                break;
                            }
                        }
                    }
                }
                fragmentUpdater.update();
            }
        });
        SocketSingleton.get(context).GetParties();
    }

    public static Party find(String id) {
        for (Party party: PARTIES) {
            if (party.id.equals(id)) return party;
        }
        return null;
    }

    public static class Party {
        public String id;
        public String name;
        public Difficulty difficulty;
        public GameMode mode;
        public int playersCount;
        public int playerCapacity;
        public List<Player> players;
        public Platform platform;
        public boolean started;

        public Party(String id, String name, Difficulty difficulty, GameMode mode, int playersCount, int playerCapacity, Platform platform, boolean started) {
            this.id = id;
            this.name = name;
            this.difficulty = difficulty;
            this.mode = mode;
            this.playersCount = playersCount;
            this.playerCapacity = playerCapacity;
            this.platform = platform;
            this.started = started;

            this.players = new ArrayList<>(this.playerCapacity);
        }
        public Party(String id) {
            this.id = id;
            this.name = "PARTY_NAME";
            this.mode = GameMode.solo;
            this.playersCount = 2;
            this.playerCapacity = 5;
            platform = Platform._android;
        }

        public String getModeAsString() {
            if (mode.equals(GameMode.solo)) return "SOLO";
            else if (mode.equals(GameMode.coop)) return "COOP";
            else return "FFA";
        }
    }

    public static class Player {
        public String id;
        public String avatar;
        public boolean isVirtual;
        public String partyId;

        public Player() {}
        public Player(String id, String avatar, boolean isVirtual) {
            this.id = id;
            this.avatar = avatar;
            this.isVirtual = isVirtual;
        }
    }

    // For creation
    class PartyControlData
    {
        public String name;
        public Difficulty difficulty;
        public GameMode mode;
        public Platform platform;
        public PartyControlData(Difficulty difficulty, GameMode mode, Platform platform, String name)
        {
            this.difficulty = difficulty;
            this.mode = mode;
            this.platform = platform;
            this.name = name;
        }
    }
    public enum Platform
    {
        all,
        pc,
        _android
    }
    public enum Difficulty
    {
        none,
        Easy,
        Medium,
        Hard,
        Creation,
    }
    public enum GameMode
    {
        none,
        ffa,
        solo,
        coop,
    }
}
