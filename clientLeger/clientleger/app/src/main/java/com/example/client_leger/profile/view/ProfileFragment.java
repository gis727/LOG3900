package com.example.client_leger.profile.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.MainActivity;
import com.example.client_leger.R;
import com.example.client_leger.databinding.FragmentProfileBinding;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.viewmodel.UserViewModel;
import com.example.client_leger.utils.Resource;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        setUpToolbar();
        setUpTabs();

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.viewUser("");
        userViewModel.getUser().observe(getViewLifecycleOwner(), avatarObserver);

        binding.profileAppBar.setViewModel(userViewModel);
        binding.profileAppBar.setMainActivity((MainActivity) getActivity());

        if (savedInstanceState == null)
        {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.profile_tab_view_container, new IdentityFragment())
                    .commit();
        }
    }

    public void setUpToolbar()
    {
        getActivity().setTitle("My Profile");
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.profileAppBar.appBar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24px);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((MaterialToolbar) getActivity().findViewById(R.id.app_bar)).setTitleTextAppearance(getContext(), R.style.TextAppearance_SocialToolBarTitle);
//        ((MaterialToolbar) getActivity().findViewById(R.id.app_bar)).setTitleTextColor(getResources().getColor(R.color.colorWhite));
    }

    public void setUpTabs()
    {
        binding.profileTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                switch (binding.profileTabs.getSelectedTabPosition())
                {
                    case 0:
                        setProfileTab(new IdentityFragment());
                        return;
                    case 1:
                        setProfileTab(new StatisticsFragment());
                        return;
                    case 2:
                        setProfileTab(new PartyHistoryFragment());
                        return;
                    case 3:
                        setProfileTab(new ActivityHistoryFragment());
                        return;
                    default:
                        return;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Log.d("TAG", "onOptionsItemSelected: toooozz");
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setProfileTab(Fragment profilePage)
    {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.profile_tab_view_container, profilePage);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    final Observer<Resource<User>> avatarObserver = new Observer<Resource<User>>()
    {
        @Override
        public void onChanged(Resource<User> user)
        {
            if (user.getData() != null)
                Glide.with(getActivity()).load(user.getData().avatar)
                        .apply(RequestOptions.circleCropTransform()).into(binding.profileAppBar.appBarMenuAvatar);
        }
    };
}
