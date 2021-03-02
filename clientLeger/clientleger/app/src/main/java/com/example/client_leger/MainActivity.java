package com.example.client_leger;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.client_leger.MainLobby.MainLobbyFragment;
import com.example.client_leger.MainLobby.PartyLobby.PartyLobbyFragment;
import com.example.client_leger.authentication.AuthenticationActivity;
import com.example.client_leger.chat.repository.ChannelsRepository;
import com.example.client_leger.chat.view.SocialFragment;
import com.example.client_leger.databinding.ActivityMainBinding;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.repository.UserRepository;
import com.example.client_leger.profile.view.ProfileFragment;
import com.example.client_leger.profile.viewmodel.UserViewModel;
import com.example.client_leger.tutorial.TutorialDialogFragment;
import com.example.client_leger.utils.Resource;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SocketSingleton.get(getApplicationContext()).OnDisconnect(onDisconnect);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fragment_container, new MainLobbyFragment(), getString(R.string.main_lobby_fragment))
                    .add(R.id.social_fragment_container, new SocialFragment())
                    .commit();
        }

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, new Observer<Resource<User>>()
        {
            @Override
            public void onChanged(Resource<User> userResource)
            {
                if(userResource.getData().userStats.activityHistory.isEmpty())
                {
                    DialogFragment newTutorialFragment = new TutorialDialogFragment();
                    newTutorialFragment.show(getSupportFragmentManager(), "tutorial");
                }

                userViewModel.getUser().removeObserver(this);
            }
        });
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    @Override
    public void onBackPressed()
    {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment partyLobby = getSupportFragmentManager().findFragmentByTag("PARTY_LOBBY_FRAGMENT");
        Fragment liveGame = getSupportFragmentManager().findFragmentByTag("GAME_FRAGMENT");

        if (liveGame != null && !liveGame.isHidden())
        {
            return;
        }
        else if (partyLobby != null && !partyLobby.isHidden())
        {
            ((PartyLobbyFragment) partyLobby).leaveParty();
        }
        else if (count == 0)
        {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        else
        {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.view_profile:
                        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PROFILE_FRAGMENT");
                        if (profileFragment == null)
                            showProfile();
                        return true;
                    case R.id.logout:
                        logout();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.getMenuInflater().inflate(R.menu.main_activity_menu, popup.getMenu());
        popup.show();
    }

    public void showProfile()
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new ProfileFragment(), "PROFILE_FRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    public void logout()
    {
        SocketSingleton.get(getApplicationContext()).dispose();
        UserRepository.setInstanceToNull();
        ChannelsRepository.setInstanceToNull();
        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Snackbar.make(binding.getRoot(), "You seem to have a connection issue. Please log back in.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            logout();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
        }
    };
}