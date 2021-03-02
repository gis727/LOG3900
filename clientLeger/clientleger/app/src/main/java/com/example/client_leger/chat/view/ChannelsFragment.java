package com.example.client_leger.chat.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.client_leger.R;
import com.example.client_leger.chat.model.Channel;
import com.example.client_leger.chat.viewmodel.ChannelsViewModel;
import com.example.client_leger.databinding.FragmentChannelsBinding;
import com.example.client_leger.utils.LiveResource;
import com.example.client_leger.utils.Resource;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.LinkedHashMap;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ChannelsFragment extends Fragment {
    private FragmentChannelsBinding binding;
    private MaterialToolbar toolbar;
    private ChannelAdapter channelAdapter;
    private ChannelsViewModel channelsViewModel;

    public ChannelsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChannelsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = ((SocialFragment) requireParentFragment()).getBinding().socialBar;

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        binding.channels.setLayoutManager(layoutManager);
        binding.channels.setHasFixedSize(true);

        channelAdapter = new ChannelAdapter();
        channelAdapter.setChannelListener(channelListener);
        binding.channels.setAdapter(channelAdapter);

        channelsViewModel = new ViewModelProvider(requireParentFragment()).get(ChannelsViewModel.class);
        channelAdapter.setChannels(channelsViewModel.getChannels().getData());
        channelsViewModel.getChannels().observe(getViewLifecycleOwner(), channelsObserver);

        binding.setLifecycleOwner(getViewLifecycleOwner());

        setUpToolbar();
    }


    private void setUpToolbar() {
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);

        toolbar.setTitle(R.string.channels_fragment_title);

        toolbar.inflateMenu(R.menu.channels_menu);

        MenuItem addChannel = toolbar.getMenu().findItem(R.id.add_channel);
        addChannel.setActionView(R.layout.add_channel_input);
        ((TextInputLayout) (addChannel.getActionView().findViewById(R.id.add_channel_layout))).setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText channelName = addChannel.getActionView().findViewById(R.id.channel_name_field);
                if (!channelName.getText().toString().trim().isEmpty())
                    channelsViewModel.createChannel(channelName.getText().toString().trim());
            }
        });
    }

    private final Observer<Resource<LinkedHashMap<String, LiveResource<Channel>>>> channelsObserver =
            new Observer<Resource<LinkedHashMap<String, LiveResource<Channel>>>>() {
                @Override
                public void onChanged(Resource<LinkedHashMap<String, LiveResource<Channel>>> channels) {

                    int totalNewMessages = 0;
                    for (LiveResource<Channel> channel : channels.getData().values())
                    {
                        totalNewMessages += channel.getData().getNewMessages();
                    }

                    ((SocialFragment) requireParentFragment()).getBinding().setTotalNewMessages(new Integer(totalNewMessages));

                    switch (channels.getStatus()) {
                        case SUCCESS: {
                            channelAdapter.notifyDataSetChanged();
                            return;
                        }
                        case ID_ALREADY_USED: {
                            Snackbar.make(binding.channelsSnackbarLayout, "This channel name is already used.", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        case ROOM_CREATED: {
                            ((TextInputEditText) toolbar.getMenu().findItem(R.id.add_channel).getActionView().findViewById(R.id.channel_name_field)).getText().clear();
                            channelAdapter.notifyDataSetChanged();
                            return;
                        }
                        default:
                            channelAdapter.notifyDataSetChanged();
                            return;
                    }
                }
            };

    private final ChannelAdapter.channelListener channelListener = new ChannelAdapter.channelListener() {
        @Override
        public void onJoinChannel(Channel channel) {
            if (channel.isJoined())
                channelsViewModel.leaveChannel(channel.getName());
            else
                channelsViewModel.enterChannel(channel.getName());
        }

        @Override
        public void onDeleteChannel(Channel channel) {
            channelsViewModel.deleteChannel(channel.getName());
        }

        @Override
        public void onChannelClicked(Channel channel) {
            ((SocialFragment) requireParentFragment()).displayChat(channel);
        }


    };
}