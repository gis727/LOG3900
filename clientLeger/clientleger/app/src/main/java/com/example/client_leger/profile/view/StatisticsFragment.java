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
import com.example.client_leger.databinding.FragmentStatisticsBinding;
import com.example.client_leger.profile.model.UserComputedStats;
import com.example.client_leger.profile.viewmodel.UserViewModel;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class StatisticsFragment extends Fragment
{
    private FragmentStatisticsBinding binding;
    private UserViewModel userViewModel;
    private DecimalFormat df;

    public StatisticsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireParentFragment().requireActivity()).get(UserViewModel.class);
        userViewModel.getUserComputedStats().observe(getViewLifecycleOwner(), statsObserver);

        binding.setLifecycleOwner(this);

        binding.setViewModel(userViewModel);
        binding.setDisplayedMatchType(0);
        df = new DecimalFormat("#.#");
        binding.setDf(df);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_filter_menu, menu);
        MenuItem item = menu.findItem(R.id.filter);
        item.setActionView(R.layout.profile_filter_option);
        AutoCompleteTextView autoCompleteTextView = item.getActionView().findViewById(R.id.filter_field);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(),
                        R.layout.filter_item,
                getResources().getStringArray(R.array.profile_filter_options));

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText("All", false);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                binding.setDisplayedMatchType(i);
            }
        });

        autoCompleteTextView.setSelection(0);
    }

    final Observer<UserComputedStats> statsObserver = new Observer<UserComputedStats>()
    {
        @Override
        public void onChanged(UserComputedStats userComputedStats) {

            return;
        }
    };
}
