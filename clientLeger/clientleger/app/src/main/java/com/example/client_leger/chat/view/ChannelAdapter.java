package com.example.client_leger.chat.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.client_leger.R;
import com.example.client_leger.chat.model.Channel;
import com.example.client_leger.utils.LiveResource;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Iterator;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
    LinkedHashMap<String, LiveResource<Channel>> channels = new LinkedHashMap();
    private channelListener channelListener;

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        Channel channel = channels.get(getKey(position)).getData();
        holder.channelName.setText(channel.getName());

        holder.newMessageIndicator.setVisibility(channel.getNewMessages() == 0 ? View.GONE : View.VISIBLE);
        holder.newMessageIndicator.setText(channel.getNewMessages() < 100 ?
                String.valueOf(channel.getNewMessages()) : "99+");

        if (!channel.isLeavable())
            return;

        if (channel.isOwner()) {
            holder.channelName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_crown, 0);
            holder.deleteButton.setVisibility(View.VISIBLE);
        }

        if (channel.isJoined()) {
            holder.joinButton.setIconResource(R.drawable.ic_remove_circle_24dp);
        } else
            holder.joinButton.setIconResource(R.drawable.ic_add_circle_24dp);


    }

    public void setChannels(LinkedHashMap<String, LiveResource<Channel>> channels) {
        this.channels = channels;
    }

    public void setChannelListener(channelListener channelListener) {
        this.channelListener = channelListener;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel, parent, false);
        ChannelAdapter.ChannelViewHolder vh = new ChannelAdapter.ChannelViewHolder((MaterialCardView) v);

        return vh;
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder {
        public TextView channelName;
        public TextView newMessageIndicator;
        public MaterialButton joinButton;
        public MaterialButton deleteButton;

        public ChannelViewHolder(MaterialCardView v) {
            super(v);
            channelName = v.findViewById(R.id.channel_name);
            newMessageIndicator = v.findViewById(R.id.new_message_indicator);
            joinButton = v.findViewById(R.id.join_button);
            deleteButton = v.findViewById(R.id.delete_button);

            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (channelListener != null && position != RecyclerView.NO_POSITION)
                        channelListener.onJoinChannel(channels.get(getKey(position)).getData());
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (channelListener != null && position != RecyclerView.NO_POSITION)
                        channelListener.onDeleteChannel(channels.get(getKey(position)).getData());
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (channelListener != null && position != RecyclerView.NO_POSITION)
                        channelListener.onChannelClicked(channels.get(getKey(position)).getData());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return channels != null ? channels.size() : 0;
    }

    public LinkedHashMap<String, LiveResource<Channel>> getChannels() {
        return channels;
    }

    private String getKey(int position) {
        Iterator<String> itr = channels.keySet().iterator();
        for (int i = 0; i < position; ++i) {
            itr.next();
        }
        return itr.next();
    }

    public interface channelListener {
        void onJoinChannel(Channel channel);

        void onDeleteChannel(Channel channel);

        void onChannelClicked(Channel channel);
    }
}
