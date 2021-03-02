package com.example.client_leger.MainLobby.PartyLobby;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.client_leger.MainLobby.LobbyPlayerAdapter;
import com.example.client_leger.MainLobby.PartyModel;
import com.example.client_leger.R;
import com.example.client_leger.SocketSingleton;
import com.example.client_leger.tutorial.TutorialDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class PartyLobbyFragment extends Fragment {
    private static final String PARTY_ID = "";
    private PartyModel.Party mParty;
    private RecyclerView mPlayersContainer;
    private LobbyPlayerAdapter mPlayersAdapter;
    private TextView mLobbyCapacity;
    private Button mAddVirtualPlayerButton;
    private Button mReadyButton;
    private Button mLobbyTutorialButton;
    private boolean mPlayerIsReady;

    public String mPartyId;

    public PartyLobbyFragment() {
        // Required empty public constructor
    }

    public static PartyLobbyFragment newInstance(String partyId) {
        PartyLobbyFragment fragment = new PartyLobbyFragment();
        Bundle args = new Bundle();
        args.putString(PARTY_ID, partyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPartyId = getArguments().getString(PARTY_ID);
            mParty = PartyModel.find(mPartyId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_lobby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar();
        mPlayersContainer = view.findViewById(R.id.players_container);
        mPlayersAdapter = new LobbyPlayerAdapter();
        mPlayersAdapter.setLobbyPlayerListener(playerListener);
        mLobbyCapacity = view.findViewById(R.id.lobby_capacity);
        mPlayersContainer.setAdapter(mPlayersAdapter);
        mReadyButton = view.findViewById(R.id.ready_button);
        mAddVirtualPlayerButton = view.findViewById(R.id.add_virtual_player_button);
        mLobbyTutorialButton = view.findViewById(R.id.lobby_tutorial_button);

        update();

        mReadyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketSingleton.get(getContext()).StartParty();
                mReadyButton.setEnabled(false);
                mPlayerIsReady = true;
            }
        });

        final Button backButton = view.findViewById(R.id.lobby_back_button);
        backButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                leaveParty();
            }
        });

        mAddVirtualPlayerButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SocketSingleton.get(getContext()).AddVirtualPlayer(mPartyId);
            }
        });

        mLobbyTutorialButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getParentFragmentManager().findFragmentByTag("tutorial") == null)
                {
                    DialogFragment newTutorialFragment = new TutorialDialogFragment();
                    newTutorialFragment.show(getParentFragmentManager(), "tutorial");
                }
            }
        });
    }

    public void leaveParty()
    {
        SocketSingleton.get(getContext()).LeaveParty(mPartyId);
    }

    private void setUpToolbar() {
        getActivity().setTitle(mParty.name);
    }

    public void update() {
        if (mPlayersAdapter != null) {
//            List<PartyModel.Player> partyPlayers = new ArrayList<>(mParty.playerCapacity);
//            partyPlayers.addAll(mParty.players);
//
//            int emptyPartySlots = mParty.playerCapacity - mParty.playersCount;
//            for (int i = 0; i < emptyPartySlots; i++) {
//                partyPlayers.add(new PartyModel.Player("", "", false));
//            }

            mPlayersAdapter.setLobbyPlayers(mParty.players);
            mPlayersAdapter.update();
        }

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mLobbyCapacity.setText(getString(R.string.party_capacity, mParty.playersCount, mParty.playerCapacity));

                int nVirtualPlayers = 0;
                for (int i = 0; i < mParty.players.size(); i++)
                    nVirtualPlayers += mParty.players.get(i).isVirtual ? 1 : 0;

                mAddVirtualPlayerButton.setVisibility(
                        mParty.mode != PartyModel.GameMode.ffa ||
                                nVirtualPlayers > 1 ||
                                mParty.playersCount == mParty.playerCapacity ? View.GONE : View.VISIBLE);

//                mReadyButton.setEnabled(!mPlayerIsReady && (mParty.playersCount - nVirtualPlayers >= 2));
            }
        });
    }

    private final LobbyPlayerAdapter.LobbyPlayerListener playerListener = new LobbyPlayerAdapter.LobbyPlayerListener()
    {
        @Override
        public void onRemoveVirtualPlayer(PartyModel.Player player)
        {
            SocketSingleton.get(getContext()).RemoveVirtualPlayer(player.id, player.partyId);
        }
    };
}
