package com.example.client_leger.chat.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.client_leger.R;
import com.example.client_leger.chat.model.Channel;
import com.example.client_leger.chat.viewmodel.ChannelsViewModel;
import com.example.client_leger.databinding.FragmentChatBinding;
import com.example.client_leger.utils.LiveResource;
import com.example.client_leger.utils.Resource;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ChatFragment extends Fragment {
    public boolean IN_GAME_MODE = false;
    private final MessageInterpreter messageInterpreter = new MessageInterpreter();
    private static final String ARG_PARAM1 = "channelName";
    public String channelName;
    private LiveResource<Channel> channel;

    private FragmentChatBinding binding;
    private ChannelsViewModel channelsViewModel;
    private MessageAdapter messageAdapter;

    private MaterialToolbar toolbar;

    public static ChatFragment newInstance(String channelName) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, channelName);
        fragment.setArguments(args);
        return fragment;
    }

    public ChatFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        channelName = getArguments().getString(ARG_PARAM1);

        binding = FragmentChatBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    private final Observer<Resource<Channel>> channelObserver =
            new Observer<Resource<Channel>>() {
                @Override
                public void onChanged(Resource<Channel> channelResource) {
                    switch (channelResource.getStatus()) {
                        case SUCCESS: {
                            if (!binding.messages.canScrollVertically(1)) {
                                messageAdapter.notifyItemInserted(messageAdapter.getItemCount());
                                binding.messages.scrollToPosition(messageAdapter.getItemCount() - 1);
                            } else
                                messageAdapter.notifyItemInserted(messageAdapter.getItemCount());

                            channel.getData().addNewMessages(-1);

                            return;
                        }
                        case ROOM_HISTORY_FETCHED: {
                            messageAdapter.setMessages(channelsViewModel.getChannels().getData().get(channelName).getData().getMessages());

                            return;
                        }
                        default:

                            return;
                    }

                }
            };

    private void setUpToolbar() {
        toolbar.getMenu().clear();
        toolbar.setTitle(channelName);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_black_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SocialFragment) requireParentFragment()).displayChannels();
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = ((SocialFragment) requireParentFragment()).getBinding().socialBar;

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        binding.messages.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter();
        binding.messages.setAdapter(messageAdapter);

        channelsViewModel = new ViewModelProvider(requireParentFragment()).get(ChannelsViewModel.class);

        channel = channelsViewModel.getChannels().getData().get(channelName);

        channel.getData().setNewMessages(0);

        messageAdapter.setMessages(channel.getData().getMessages());
        channel.postStatus(Resource.DataStatus.NONE);
        channel.observe(getViewLifecycleOwner(), channelObserver);

        binding.messageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                String message = binding.messageField.getText().toString().trim();
                binding.sendButton.setEnabled(!message.isEmpty());
                if (!message.isEmpty()) binding.messageField.setTextColor(messageInterpreter.isACommand(message, IN_GAME_MODE)? Color.RED: Color.BLACK);
            }

        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageField.getText().toString().trim();

                if (message.isEmpty()) return;

                if (messageInterpreter.isAGuess(message, IN_GAME_MODE))
                {
                    channelsViewModel.answer(messageInterpreter.extractWordFromCommand(message));
                }
                else if (messageInterpreter.isAClueRequest(message, IN_GAME_MODE))
                {
                    channelsViewModel.getClue();
                }
                else if (messageInterpreter.isAnHistoryRequest(message))
                {
                    channelsViewModel.getChannelHistory(channelName);
                }
                else
                {
                    channelsViewModel.sendMessage(channelName, message);
                }
                binding.messageField.getText().clear();
                if (IN_GAME_MODE) setUpMessageForGuess();
            }
        });
        if (IN_GAME_MODE) setUpMessageForGuess(); // set up when the game begins

        setUpToolbar();
    }

    private void setUpMessageForGuess() {
        binding.messageField.setText(String.format("%s ", MessageInterpreter.GuessPrefix));
        final Editable textField = binding.messageField.getText();
        if (textField != null) binding.messageField.setSelection(textField.length());
    }

    private static class MessageInterpreter {
        static final String GuessPrefix = ".g";
        static final String CluePrefix = ".c";
        static final String HistoryPrefix = ".h";

        final boolean isACommand(final String message, final boolean inGameMode) {
            return (inGameMode && (isAClueRequest(message, true) || isAGuess(message, true))) || isAnHistoryRequest(message);
        }
        final String extractWordFromCommand(final String message) {
            return message.substring(2).trim();
        }
        final boolean isAGuess(final String message, final boolean inGameMode) {
            return inGameMode && message.startsWith(GuessPrefix);
        }
        final boolean isAClueRequest(final String message, final boolean inGameMode) {
            return inGameMode && message.equals(CluePrefix);
        }
        final boolean isAnHistoryRequest(final String message) {
            return message.equals(HistoryPrefix);
        }
    }
}
