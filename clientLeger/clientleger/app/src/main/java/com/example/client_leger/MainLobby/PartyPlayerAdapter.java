package com.example.client_leger.MainLobby;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.R;
import com.example.client_leger.databinding.PartyPlayerBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PartyPlayerAdapter extends RecyclerView.Adapter<PartyPlayerAdapter.PartyPlayerViewHolder> {
    List<PartyModel.Player> partyPlayers = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @NonNull
    @Override
    public PartyPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PartyPlayerBinding binding = PartyPlayerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);

        return new PartyPlayerAdapter.PartyPlayerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PartyPlayerViewHolder holder, int position) {
        PartyModel.Player partyPlayer = partyPlayers.get(position);
        holder.bind(partyPlayer);
    }

    @Override
    public int getItemCount() {
        return (partyPlayers != null) ? partyPlayers.size(): 0;
    }

    public void setPartyPlayers(List<PartyModel.Player> partyPlayers) {
        this.partyPlayers = partyPlayers;
    }


    public void update() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public PartyPlayerAdapter() {}

    public PartyPlayerAdapter(List<PartyModel.Player> partyPlayers)
    {
        this.partyPlayers = partyPlayers;
    }

    public class PartyPlayerViewHolder extends RecyclerView.ViewHolder
    {
        PartyPlayerBinding binding;

        public PartyPlayerViewHolder(PartyPlayerBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PartyModel.Player partyPlayer)
        {
            if (partyPlayer.id == null)
            {
                binding.partyPlayerNickname.setText("");
                binding.partyPlayerAvatar.setImageDrawable(null);
                binding.partyPlayerIcon.setImageDrawable(null);
            }
            else if (partyPlayer.id.isEmpty())
            {
                binding.partyPlayerNickname.setText(R.string.party_player_placeholder);
                binding.partyPlayerAvatar.setImageResource(R.drawable.ic_account_circle_24dp);
                binding.partyPlayerIcon.setImageDrawable(null);
            }
            else if (partyPlayer.isVirtual)
            {
                binding.partyPlayerNickname.setText(partyPlayer.id);
                Glide.with(this.itemView).load(partyPlayer.avatar).apply(RequestOptions.circleCropTransform())
                        .into(binding.partyPlayerAvatar);
                binding.partyPlayerIcon.setImageResource(R.drawable.ic_android_24dp);
            }
            else
            {
                binding.partyPlayerNickname.setText(partyPlayer.id);
                Glide.with(this.itemView).load(partyPlayer.avatar).apply(RequestOptions.circleCropTransform())
                        .into(binding.partyPlayerAvatar);
                binding.partyPlayerIcon.setImageResource(R.drawable.ic_face_24dp);
            }
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
