package com.example.client_leger.MainLobby;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.LiveGame.Drawers;
import com.example.client_leger.LiveGame.Game;
import com.example.client_leger.LiveGame.GameFragment;
import com.example.client_leger.MainActivity;
import com.example.client_leger.MainLobby.PartyLobby.PartyLobbyFragment;
import com.example.client_leger.R;
import com.example.client_leger.SocketSingleton;
import com.example.client_leger.chat.view.SocialFragment;
import com.example.client_leger.databinding.FragmentMainLobbyBinding;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.viewmodel.UserViewModel;
import com.example.client_leger.tutorial.TutorialDialogFragment;
import com.example.client_leger.utils.Resource;
import com.github.nkzawa.emitter.Emitter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

public class MainLobbyFragment extends Fragment implements PartyCreationDialogFragment.NoticeDialogListener,
PartyFilterDialogFragment.NoticeDialogListener {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private FragmentMainLobbyBinding binding;
    private View mMainLobby;
    private RecyclerView mPartiesContainer;
    private PartyFragmentAdapter mPartiesAdapter;
    private FragmentManager mFragmentManager;
    private Button mCreatePartyBtn;
    private UserViewModel userViewModel;

    // Fragments
    private PartyLobbyFragment mPartyLobbyFragment;
    private MainLobbyFragment mMainLobbyFragment;
    private GameFragment mGameFragment;

    public MainLobbyFragment() {
        // Required empty public constructor
    }

    public static MainLobbyFragment newInstance() {
        return new MainLobbyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getParentFragmentManager();

        // LISTENERS
        setListeners();
    }

    private void setListeners() {
        SocketSingleton.get(getContext()).OnJoinParty(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String partyId = (String)args[0];
                enterParty(partyId);
            }
        });
        SocketSingleton.get(getContext()).OnLeaveParty(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                leaveParty(true);
            }
        });
        SocketSingleton.get(getContext()).OnStartGame(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Game game = (Game)args[0];
                startGame(game);
            }
        }, true);
        SocketSingleton.get(getContext()).OnPartyFinished(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                showPartyFinished();
            }
        });
        SocketSingleton.get(getContext()).OnCreateParty(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("LISTENER", "CLICKED");
                String partyId = (String)args[0];
                enterParty(partyId);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        binding = FragmentMainLobbyBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        setUpToolbar();
        // set up Fragments
        mPartiesAdapter = new PartyFragmentAdapter(new OnPartyClicked(), PartyModel.PARTIES);
        binding.partiesContainer.setAdapter(mPartiesAdapter);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), avatarObserver);

        binding.mainLobbyAppBar.setViewModel(userViewModel);
        binding.mainLobbyAppBar.setMainActivity((MainActivity) getActivity());
        binding.setLifecycleOwner(getViewLifecycleOwner());

        mMainLobbyFragment = (MainLobbyFragment) mFragmentManager.findFragmentByTag(getString(R.string.main_lobby_fragment));
        // set up the Party model
        PartyModel.setUp(getContext(), new FragmentUpdater());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden)
            setUpToolbar();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_lobby_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!isAlreadyShowingDialog())
        {
            switch(item.getItemId()) {
                case R.id.create_party_button:
                    DialogFragment newFragment = new PartyCreationDialogFragment();
                    newFragment.setTargetFragment(MainLobbyFragment.this, 0);
                    newFragment.show(getParentFragmentManager(), "partyCreator");
                    return true;

                case R.id.show_tutorial_button:
                    DialogFragment newTutorialFragment = new TutorialDialogFragment();
                    newTutorialFragment.show(getParentFragmentManager(), "tutorial");
                    return true;

                case R.id.filter_parties_button:
                    DialogFragment newFilterFragment = new PartyFilterDialogFragment();
                    newFilterFragment.setTargetFragment(MainLobbyFragment.this, 0);
                    newFilterFragment.show(getParentFragmentManager(), "filter");
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void enterParty(final String partyId) {
        // Leave the party chat
        final SocialFragment socialFragment = (SocialFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.social_fragment_container);
        if (socialFragment != null) {
            socialFragment.displayChatByName(partyId, false);
        }

        // Open the party lobby with the provided party
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPartyLobbyFragment = PartyLobbyFragment.newInstance(partyId);
                Fade enterFade = new Fade();
                enterFade.setDuration(1000);
                mPartyLobbyFragment.setEnterTransition(enterFade);
                mFragmentManager.beginTransaction()
                        .hide(mMainLobbyFragment)
                        .add(R.id.main_fragment_container, mPartyLobbyFragment, "PARTY_LOBBY_FRAGMENT")
                        .commitAllowingStateLoss();
            }
        });
    }

    private void leaveParty(final boolean showMainFragment) {
        // Leave the party chat
        final SocialFragment socialFragment = (SocialFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.social_fragment_container);
        if (socialFragment != null) {
            socialFragment.setGameMode(false, "");
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPartyLobbyFragment != null) { mFragmentManager.beginTransaction().remove(mPartyLobbyFragment).commitAllowingStateLoss(); mPartyLobbyFragment = null;}
                if (mGameFragment != null) { mFragmentManager.beginTransaction().remove(mGameFragment).commitAllowingStateLoss(); mGameFragment = null;}
                if (showMainFragment) mFragmentManager.beginTransaction().show(mMainLobbyFragment).commitAllowingStateLoss();
            }
        });
        SocketSingleton.get(getContext()).OnStartGame(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Game game = (Game)args[0];
                startGame(game);
            }
        }, true);
    }

    private void startGame(final Game game) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mGameFragment == null) {
                    mGameFragment = GameFragment.newInstance(mPartyLobbyFragment.mPartyId, game);
                    Fade enterFade = new Fade();
                    enterFade.setDuration(100);
                    mGameFragment.setEnterTransition(enterFade);
                    mFragmentManager.beginTransaction().hide(mMainLobbyFragment).commitAllowingStateLoss();
                    mFragmentManager.beginTransaction().hide(mPartyLobbyFragment).commitAllowingStateLoss();
                    mFragmentManager.beginTransaction().add(R.id.main_fragment_container, mGameFragment, "GAME_FRAGMENT").commitAllowingStateLoss();
                    final SocialFragment socialFragment = (SocialFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.social_fragment_container);
                    if (socialFragment != null && socialFragment.chatFragment != null) {
                        socialFragment.setGameMode(true, socialFragment.chatFragment.channelName);
                    }
                }
            }
        });
    }

    private void showPartyFinished()
    {
        Drawers.StopAllAnimationsAtOnce();
        ((GameFragment) mFragmentManager.findFragmentByTag("GAME_FRAGMENT")).showGameFinishedScreen();
    }


    public void closeParty()
    {
        leaveParty(false);
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Fade enterFade = new Fade();
                enterFade.setDuration(1000);
                mMainLobbyFragment.setEnterTransition(enterFade);
                mFragmentManager.beginTransaction().show(mMainLobbyFragment).commitAllowingStateLoss();
                getActivity().invalidateOptionsMenu();
            }
        });
    }

    private void setUpToolbar()
    {
        getActivity().setTitle("Draw me something");
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.mainLobbyAppBar.appBar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        ((MaterialToolbar) getActivity().findViewById(R.id.app_bar)).setTitleTextAppearance(getContext(), R.style.TextAppearance_MainToolBarTitle);
//        ((MaterialToolbar) getActivity().findViewById(R.id.app_bar)).setTitleTextColor(getResources().getColor(R.color.colorWhite));
//        getActivity().findViewById(R.id.app_bar_layout).setElevation(10);
    }

    @Override
    public void onDialogPositiveClick(PartyCreationDialogFragment dialog)
    {
        SocketSingleton.get(getContext()).CreateParty(dialog.partyName, dialog.partyMode);
    }

    @Override
    public void onDialogPositiveClick(PartyModel.GameMode filterMode)
    {
        mPartiesAdapter.setFilterMode(filterMode);
    }

    // Inner Fragment listener
    class OnPartyClicked implements PartyFragmentAdapter.OnListFragmentInteractionListener
    {
        @Override
        public void onListFragmentInteraction(PartyModel.Party party)
        {
            if (!party.started) {
                SocketSingleton.get(getContext()).JoinParty(party.id);
            }
        }
    }

    /**
     * It updates all fragments adapters
     */
    public class FragmentUpdater
    {
        public void update()
        {
            if (mPartiesAdapter != null) mPartiesAdapter.update();
            if (mPartyLobbyFragment != null) mPartyLobbyFragment.update();
            mHandler.post(() -> binding.noPartiesMessage.setVisibility(PartyModel.PARTIES.size() > 0 ? View.GONE : View.VISIBLE));

        }
    }

    final Observer<Resource<User>> avatarObserver = new Observer<Resource<User>>()
    {
        @Override
        public void onChanged(Resource<User> user)
        {
            if (user.getData() != null)
                Glide.with(getActivity()).load(user.getData().avatar)
                        .apply(RequestOptions.circleCropTransform()).into(binding.mainLobbyAppBar.appBarMenuAvatar);
        }
    };

    private boolean isAlreadyShowingDialog()
    {
        return  getParentFragmentManager().findFragmentByTag("partyCreator") != null ||
                getParentFragmentManager().findFragmentByTag("tutorial") != null     ||
                getParentFragmentManager().findFragmentByTag("filter") != null;
    }
}
