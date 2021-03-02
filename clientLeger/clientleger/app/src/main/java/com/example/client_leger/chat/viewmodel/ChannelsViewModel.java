package com.example.client_leger.chat.viewmodel;

import android.app.Application;

import com.example.client_leger.chat.model.Channel;
import com.example.client_leger.chat.repository.ChannelsRepository;
import com.example.client_leger.utils.LiveResource;

import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ChannelsViewModel extends AndroidViewModel {
    private ChannelsRepository channelsRepository;
    private LiveResource<LinkedHashMap<String, LiveResource<Channel>>> channels;

    public ChannelsViewModel(@NonNull Application application) {

        super(application);
        channelsRepository = ChannelsRepository.getInstance(application);
        channels = channelsRepository.getChannels();
    }

    public LiveResource<LinkedHashMap<String, LiveResource<Channel>>> getChannels() {
        return channels;
    }

    public void sendMessage(String channelName, String message) { channelsRepository.sendMessage(channelName, message); }

    public void createChannel(String name) { channelsRepository.createChannel(name); }

    public void deleteChannel(String name) {
        channelsRepository.deleteChannel(name);
    }

    public void enterChannel(String name) { channelsRepository.enterChannel(name); }

    public void leaveChannel(String name) {
        channelsRepository.leaveChannel(name);
    }

    public void getChannelHistory(String name) { channelsRepository.getChannelHistory(name);}

    public void answer(String answer) { channelsRepository.answer(answer); }

    public void getClue() {
        channelsRepository.getClue();
    }
}