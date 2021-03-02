package com.example.client_leger.profile.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.R;
import com.example.client_leger.databinding.FragmentIdentityBinding;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.viewmodel.UserViewModel;
import com.example.client_leger.utils.Resource;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class IdentityFragment extends Fragment {
    private FragmentIdentityBinding binding;
    private UserViewModel userViewModel;


    public IdentityFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentIdentityBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireParentFragment().requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), avatarObserver);
        binding.setViewModel(userViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_identity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.edit_profile_identity: {
                showProfileEditor();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showProfileEditor() {
        Fragment identityEditorFragment = new IdentityEditorFragment();
        ((ProfileFragment) getParentFragment()).setProfileTab(identityEditorFragment);
    }

    final Observer<Resource<User>> avatarObserver = new Observer<Resource<User>>() {
        @Override
        public void onChanged(Resource<User> user) {
            Glide.with(getActivity()).load(user.getData().avatar).apply(RequestOptions.circleCropTransform()).into(binding.profileImage);
        }
    };
}
