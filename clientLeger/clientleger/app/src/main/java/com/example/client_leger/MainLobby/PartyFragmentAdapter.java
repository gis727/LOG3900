package com.example.client_leger.MainLobby;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.client_leger.R;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PartyFragmentAdapter extends RecyclerView.Adapter<PartyFragmentAdapter.PartyViewHolder> {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final OnListFragmentInteractionListener mListener;
    private PartyModel.GameMode filterMode = PartyModel.GameMode.none;
    private List<PartyModel.Party> mParties;

    public PartyFragmentAdapter(OnListFragmentInteractionListener listener, List<PartyModel.Party> parties)
    {
        mListener = listener;
        mParties = parties;
    }

    public void setFilterMode(PartyModel.GameMode filterMode)
    {
        this.filterMode = filterMode;
        update();
    }

    @Override
    public PartyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.party, parent, false);

        return new PartyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PartyViewHolder holder, int position) {
        PartyModel.Party party = mParties.get(position);

        holder.mItem = party;

        holder.mTitleView.setText(party.name);
        holder.mNameView.setText(party.name);
        holder.mModeView.setText(party.mode.toString());
        holder.mCapacityView.setText(party.playersCount + " / " + party.playerCapacity);

        if (party.started || party.playersCount == party.playerCapacity)
        {
            holder.mPartyHeaderPattern.setImageResource(R.drawable.full);
            holder.mModeView.setAlpha((float) 0.5);
        }
        else
        {
            holder.mPartyHeaderPattern.setImageResource(R.drawable.pattern1);
            holder.mModeView.setAlpha(1);
        }

        switch (party.mode) {
            case solo:
                holder.mModeView.setBackground(ContextCompat.
                        getDrawable(holder.itemView.getContext(), R.drawable.party_game_mode_solo));
                break;

            case coop:
                holder.mModeView.setBackground(ContextCompat.
                        getDrawable(holder.itemView.getContext(), R.drawable.party_game_mode_coop));
                break;

            default:
                holder.mModeView.setBackground(ContextCompat.
                        getDrawable(holder.itemView.getContext(), R.drawable.party_game_mode_ffa));
                break;
        }

        int partyPlayersSize = party.playerCapacity < 4 ? 4 : party.playerCapacity;
        List<PartyModel.Player> partyPlayers = new ArrayList<>(partyPlayersSize);
        partyPlayers.addAll(party.players);

        int emptyPartySlots = party.playerCapacity - party.playersCount;
        for (int i = 0; i < emptyPartySlots; i++) {
            partyPlayers.add(new PartyModel.Player("", "", false));
        }

        int placeholderSlots = partyPlayersSize - party.playerCapacity;
        for (int i = 0; i < placeholderSlots; i++) {
            partyPlayers.add(new PartyModel.Player());
        }

        ((PartyPlayerAdapter) holder.mPartyPlayersListView.getAdapter()).setPartyPlayers(partyPlayers);
        holder.mPartyPlayersListView.getAdapter().notifyDataSetChanged();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mParties != null ? mParties.size() : 0;
    }

    public class PartyViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RecyclerView mPartyPlayersListView;
        public final TextView mTitleView;
        public final TextView mNameView;
        public final TextView mModeView;
        public final TextView mCapacityView;
        public final ImageView mPartyHeaderPattern;
        public PartyModel.Party mItem;

        public PartyViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.party_title);
            mNameView = view.findViewById(R.id.party_name);
            mCapacityView = view.findViewById(R.id.party_capacity);
            mModeView = view.findViewById(R.id.party_mode);
            mPartyHeaderPattern = view.findViewById(R.id.party_header_pattern);
            mPartyPlayersListView= view.findViewById(R.id.party_players_list);

            mPartyPlayersListView.setAdapter(new PartyPlayerAdapter());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PartyModel.Party item);
    }

    public void update() {

        if (filterMode == PartyModel.GameMode.none)
            mParties = PartyModel.PARTIES;
        else
        {
            Predicate<PartyModel.Party> byMode = party -> party.mode == filterMode;
            mParties = FluentIterable.from(PartyModel.PARTIES).filter(byMode).toList();
        }

        mHandler.post(new Runnable() {
            @Override
            public void run()
            {
                notifyDataSetChanged();
            }
        });
    }
}
