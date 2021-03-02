package com.example.client_leger.chat.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.client_leger.R;
import com.example.client_leger.chat.model.Channel;
import com.example.client_leger.chat.viewmodel.ChannelsViewModel;
import com.example.client_leger.databinding.FragmentSocialBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class SocialFragment extends Fragment {
    ChannelsViewModel channelsViewModel;
    private FragmentSocialBinding binding;
    public ChatFragment chatFragment;

    public SocialFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSocialBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        channelsViewModel = new ViewModelProvider(this).get(ChannelsViewModel.class);
        binding.setIsShowingChannels(true);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.social_container, new ChannelsFragment())
                    .commit();
        }
    }

    public FragmentSocialBinding getBinding() {
        return binding;
    }

    public void displayChat(Channel channel) {
        displayChatByName(channel.getName(), false);
    }

    public void displayChatByName(final String channelName, final boolean inGameMode) {
        binding.setIsShowingChannels(false);

        chatFragment = ChatFragment.newInstance(channelName);
        chatFragment.IN_GAME_MODE = inGameMode;

        getChildFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.social_container, chatFragment)
                .commit();
    }

    public void displayChannels() {
        binding.setIsShowingChannels(true);

        getChildFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.social_container, new ChannelsFragment())
                .commit();
    }

    public void setGameMode(final boolean enterGameMode, final String partyId) {
        if (enterGameMode) {
            displayChatByName(partyId, true);
        } else {
            displayChannels();
        }
    }
}
