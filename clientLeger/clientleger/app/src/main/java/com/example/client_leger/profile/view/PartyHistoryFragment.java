package com.example.client_leger.profile.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.client_leger.R;
import com.example.client_leger.databinding.FragmentMatchHistoryBinding;
import com.example.client_leger.profile.model.PlayedParty;
import com.example.client_leger.profile.viewmodel.UserViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PartyHistoryFragment extends Fragment {
    private FragmentMatchHistoryBinding binding;
    private PlayedPartyAdapter playedPartyAdapter;
    private LinearLayoutManager layoutManager;
    private UserViewModel userViewModel;
    private String lastFilterOption;


    public PartyHistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        binding = FragmentMatchHistoryBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        playedPartyAdapter = new PlayedPartyAdapter();
        binding.matchHistory.setAdapter(playedPartyAdapter);
        binding.matchHistory.setHasFixedSize(true);

        userViewModel = new ViewModelProvider(requireParentFragment().requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), userResource ->
        {
            playedPartyAdapter.setPlayedParties((userResource.getData().userStats.matchHistory));
        });

        lastFilterOption = "All";
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_filter_menu, menu);
        MenuItem item = menu.findItem(R.id.filter);
        item.setActionView(R.layout.profile_filter_option);
        AutoCompleteTextView autoCompleteTextView = item.getActionView().findViewById(R.id.filter_field);

        String[] filterOptions = getResources().getStringArray(R.array.profile_filter_options);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.filter_item,
                filterOptions);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(filterOptions[0], false);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (filterOptions[i].equals(lastFilterOption))
                    return;

                lastFilterOption = filterOptions[i];

                if (i == 0)
                    playedPartyAdapter.setPlayedParties(( userViewModel.getUser().getData().userStats.matchHistory));
                else
                {
                    ArrayList<PlayedParty> filteredParties = new ArrayList<>();
                    for (PlayedParty playedParty : userViewModel.getUser().getData().userStats.matchHistory)
                    {
                        if (playedParty.matchType == i)
                            filteredParties.add(playedParty);
                    }

                    playedPartyAdapter.setPlayedParties(filteredParties);
                }

                playedPartyAdapter.notifyDataSetChanged();
                binding.matchHistory.scrollToPosition(0);
            }
        });

        autoCompleteTextView.setSelection(0);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
