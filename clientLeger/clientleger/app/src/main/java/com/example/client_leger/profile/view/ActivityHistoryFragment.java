package com.example.client_leger.profile.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.client_leger.databinding.FragmentActivityHistoryBinding;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.viewmodel.UserViewModel;
import com.example.client_leger.utils.Resource;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ActivityHistoryFragment extends Fragment {
    private FragmentActivityHistoryBinding binding;
    private SessionAdapter activityHistoryAdapter;
    private LinearLayoutManager layoutManager;
    private UserViewModel userViewModel;


    public ActivityHistoryFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivityHistoryBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.activityHistory.setLayoutManager(layoutManager);
        binding.activityHistory.setHasFixedSize(true);

        activityHistoryAdapter = new SessionAdapter();
        binding.activityHistory.setAdapter(activityHistoryAdapter);

        userViewModel = new ViewModelProvider(requireParentFragment().requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<Resource<User>>() {
            @Override
            public void onChanged(Resource<User> user) {
                activityHistoryAdapter.setUserSessions(user.getData().userStats.activityHistory);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
