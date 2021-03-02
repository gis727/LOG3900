package com.example.client_leger.profile.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.client_leger.databinding.PlayedPartyBinding;
import com.example.client_leger.profile.model.PlayedParty;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PlayedPartyAdapter extends RecyclerView.Adapter<PlayedPartyAdapter.PlayedPartyViewHolder> {
    final private String[] MATCH_TYPES = new String[] {"None", "ffa", "Sprint solo", "Sprint Coop"};

    private List<PlayedParty> playedParties = new ArrayList<>();

    public class PlayedPartyViewHolder extends RecyclerView.ViewHolder
    {
        public PlayedPartyBinding binding;

        public PlayedPartyViewHolder(PlayedPartyBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PlayedParty playedParty)
        {
            binding.setPlayedParty(playedParty);

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < playedParty.players.size() - 1; ++i)
                sb.append(playedParty.players.get(i) + "\n");
            sb.append(playedParty.players.get(playedParty.players.size() - 1));

            binding.playedPartyPlayers.setText(sb.toString());
        }
    }

    public void setPlayedParties(List<PlayedParty> playedParties)
    {
        this.playedParties = playedParties;
    }

    @Override
    public PlayedPartyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        PlayedPartyBinding binding = PlayedPartyBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);

        return new PlayedPartyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(PlayedPartyViewHolder holder, int position)
    {
        holder.bind(playedParties.get(position));
    }

    @Override
    public int getItemCount()
    {
        return playedParties != null ? playedParties.size() : 0;
    }
}
