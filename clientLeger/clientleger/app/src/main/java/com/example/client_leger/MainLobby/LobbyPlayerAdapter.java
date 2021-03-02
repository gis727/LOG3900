package com.example.client_leger.MainLobby;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.R;
import com.example.client_leger.databinding.LobbyPlayerBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LobbyPlayerAdapter extends RecyclerView.Adapter<LobbyPlayerAdapter.LobbyPlayerViewHolder>
{
    private List<PartyModel.Player> lobbyPlayers = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LobbyPlayerListener lobbyPlayerListener;


    public LobbyPlayerAdapter() {}

    public LobbyPlayerAdapter(List<PartyModel.Player> lobbyPlayers) {
        this.lobbyPlayers = lobbyPlayers;
    }

    @NonNull
    @Override
    public LobbyPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LobbyPlayerBinding binding = LobbyPlayerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);

        return new LobbyPlayerAdapter.LobbyPlayerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyPlayerViewHolder holder, int position)
    {
        PartyModel.Player lobbyPlayer = lobbyPlayers.get(position);
        holder.bind(lobbyPlayer);
    }

    @Override
    public int getItemCount() {
        return (lobbyPlayers != null) ? lobbyPlayers.size(): 0;
    }

    public void setLobbyPlayers(List<PartyModel.Player> lobbyPlayers) {
        this.lobbyPlayers = lobbyPlayers;
    }

    public void setLobbyPlayerListener(LobbyPlayerListener listener) {
        lobbyPlayerListener = listener;
    }

    public void update() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public class LobbyPlayerViewHolder extends RecyclerView.ViewHolder
    {
        private LobbyPlayerBinding binding;

        public LobbyPlayerViewHolder(LobbyPlayerBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.removeVirtualPlayerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (lobbyPlayerListener != null && position != RecyclerView.NO_POSITION)
                        lobbyPlayerListener.onRemoveVirtualPlayer(lobbyPlayers.get(position));
                }
            });
        }

        public void bind(PartyModel.Player lobbyPlayer)
        {
            if (lobbyPlayer.id.isEmpty()) {
                binding.lobbyPlayerNickname.setText(R.string.party_player_placeholder);
                binding.lobbyPlayerAvatar.setImageResource(R.drawable.ic_account_circle_white_24dp);
                binding.lobbyPlayerIcon.setImageDrawable(null);
                binding.removeVirtualPlayerButton.setVisibility(View.INVISIBLE);
            }
            else if (lobbyPlayer.isVirtual) {
                binding.lobbyPlayerNickname.setText(lobbyPlayer.id);
                Glide.with(this.itemView).load(lobbyPlayer.avatar).apply(RequestOptions.circleCropTransform())
                        .into(binding.lobbyPlayerAvatar);
                binding.lobbyPlayerIcon.setImageResource(R.drawable.ic_android_24dp);
                binding.removeVirtualPlayerButton.setVisibility(View.VISIBLE);
            }
            else {
                binding.lobbyPlayerNickname.setText(lobbyPlayer.id);
                Glide.with(this.itemView).load(lobbyPlayer.avatar).apply(RequestOptions.circleCropTransform())
                        .into(binding.lobbyPlayerAvatar);
                binding.lobbyPlayerIcon.setImageResource(R.drawable.ic_face_24dp);
                binding.removeVirtualPlayerButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public interface LobbyPlayerListener {
        void onRemoveVirtualPlayer(PartyModel.Player player);
    }
}
