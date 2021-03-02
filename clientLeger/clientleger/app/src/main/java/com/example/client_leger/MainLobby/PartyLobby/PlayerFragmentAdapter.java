package com.example.client_leger.MainLobby.PartyLobby;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.client_leger.MainLobby.PartyModel;
import com.example.client_leger.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayerFragmentAdapter extends RecyclerView.Adapter<PlayerFragmentAdapter.ViewHolder> {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final OnListFragmentInteractionListener mListener;
    private final String mPartyId;

    public PlayerFragmentAdapter(String partyId, OnListFragmentInteractionListener listener) {
        mPartyId = partyId;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PartyModel.Party party = PartyModel.find(mPartyId);
        if (party==null) return;
        PartyModel.Player player = party.players.get(position);

        holder.mItem = player;
        holder.mIdView.setText(player.id);
        if (player.isVirtual) holder.mIsABotView.setVisibility(View.VISIBLE);
        else holder.mIsABotView.setVisibility(View.INVISIBLE);
        Picasso.get().load(player.avatar).into(holder.mAvatarView);

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
        PartyModel.Party party = PartyModel.find(mPartyId);
        return (party != null) ? party.players.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAvatarView;
        public final TextView mIdView;
        public final TextView mIsABotView;
        public final TextView mContentView;
        public PartyModel.Player mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatarView = view.findViewById(R.id.player_avatar);
            mIdView = view.findViewById(R.id.player_id);
            mIsABotView = view.findViewById(R.id.player_is_virtual);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PartyModel.Player party);
    }

    public void update() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
