package com.example.client_leger.chat.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.chat.model.Message;
import com.example.client_leger.databinding.MessageBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages = new ArrayList<>();

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private MessageBinding binding;

        public MessageViewHolder(MessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.setMessage(message);
            Glide.with(itemView).load(message.getAvatar()).apply(RequestOptions.circleCropTransform()).into(binding.messageAvatar);
            binding.executePendingBindings();
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageBinding binding = MessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);

        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }


}