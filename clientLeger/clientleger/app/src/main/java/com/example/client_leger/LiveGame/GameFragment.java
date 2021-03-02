package com.example.client_leger.LiveGame;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.example.client_leger.LiveGame.Drawer.DrawerFragment;
import com.example.client_leger.MainLobby.MainLobbyFragment;
import com.example.client_leger.R;
import com.example.client_leger.SocketSingleton;
import com.example.client_leger.Utilities.Converter;
import com.example.client_leger.databinding.FragmentGameBinding;
import com.example.client_leger.tutorial.TutorialDialogFragment;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import petrov.kristiyan.colorpicker.ColorPicker;

public class GameFragment extends Fragment {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final String PARTY_ID = "";
    public static Game GAME = null;
    private String mGameId;

    // Views
    private FragmentGameBinding binding;
    private DrawerFragment drawerFragment;
    private ColorPicker colorPicker;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(String gameId, Game game) {
        GAME = game;
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(PARTY_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameId = getArguments().getString(PARTY_ID);
        }
        final Context context = requireContext();
        // LISTENERS
        SocketSingleton.get(context).OnStatsUpdate(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                GameStats newStats = (GameStats)args[0];
                updateStats(newStats);
                if (newStats.timeLeft == 1 && drawerFragment != null) {
                    drawerFragment.mDrawingView.setTouchable(false);
                }
            }
        });

        SocketSingleton.get(context).OnAnswer(new Emitter.Listener() {
            @Override
            public void call(Object... args)
            {
                Log.d("adf", "call: vibrateur1");
                boolean isRight = Boolean.parseBoolean((String) args[0]);
                if (isRight) {
                    Log.d("adf", "call: vibrateur2");
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] vibrationPattern = {0, 500, 500, 1500};
                    if (vibrator != null)
                    {
                        Log.d("adf", "call: vibrateur3");
                        vibrator.vibrate(vibrationPattern, -1);
                    }

                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding = FragmentGameBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar();
        indicateDrawer(GAME);
        drawerFragment = (DrawerFragment) getChildFragmentManager().findFragmentById(R.id.drawer_fragment);

        colorPicker = new ColorPicker(getActivity()).setOnFastChooseColorListener(
                new ColorPicker.OnFastChooseColorListener()
        {
            @Override
            public void setOnFastChooseColorListener(int position, int color)
            {
                binding.drawerToolBar.colorPickerButton.setBackgroundTintList(ColorStateList.valueOf(color));
                drawerFragment.setBrushColor(color);
            }

            @Override
            public void onCancel()
            {
                colorPicker.dismissDialog();
            }
        }).setColors(R.array.color_picker_colors).setDefaultColorButton(Color.BLACK).setColumns(5).
                setTitle("Choose Color").setRoundColorButton(true);

        binding.gameTutorialButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getParentFragmentManager().findFragmentByTag("tutorial") == null)
                {
                    DialogFragment newTutorialFragment = new TutorialDialogFragment();
                    newTutorialFragment.show(getParentFragmentManager(), "tutorial");
                }
            }
        });

        binding.drawerToolBar.pointerTypeToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked)
            {
                if (isChecked)
                {
                    switch (checkedId)
                    {
                        case R.id.pen_tool:
                            drawerFragment.activatePenTool();
                            return;

                        case R.id.eraser_tool:
                            drawerFragment.activateEraserTool();
                            return;

                        case R.id.stroke_eraser_tool:
                            drawerFragment.activateStrokeEraserTool();
                            return;
                    }
                }
            }
        });

        binding.drawerToolBar.pointerTipToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked)
            {
                if (isChecked)
                {
                    switch (checkedId)
                    {
                        case R.id.circle_brush:
                            drawerFragment.activateCircleBrush();
                            binding.drawerToolBar.pointerTypeToggleGroup.check(R.id.pen_tool);
                            return;

                        case R.id.square_brush:
                            drawerFragment.activateSquareBrush();
                            binding.drawerToolBar.pointerTypeToggleGroup.check(R.id.pen_tool);
                            return;
                    }
                }
            }
        });

        binding.drawerToolBar.brushSizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                drawerFragment.setBrushSize(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.drawerToolBar.colorPickerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                colorPicker = new ColorPicker(getActivity()).setOnFastChooseColorListener(
                        new ColorPicker.OnFastChooseColorListener()
                        {
                            @Override
                            public void setOnFastChooseColorListener(int position, int color)
                            {
                                view.setBackgroundTintList(ColorStateList.valueOf(color));
                                drawerFragment.setBrushColor(color);
                            }

                            @Override
                            public void onCancel()
                            {
                                colorPicker.dismissDialog();
                            }
                        }).setColors(R.array.color_picker_colors).setDefaultColorButton(Color.BLACK).setColumns(5).
                        setTitle("Choose Color").setRoundColorButton(true);
                colorPicker.show();
            }
        });
    }

    public ColorPicker getColorPicker()
    {
        return colorPicker;
    }

    private void setUpToolbar() {
        getActivity().setTitle("");
    }

    private void updateStats(final GameStats newStats) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (binding.gameScore != null) binding.gameScore.setText(String.valueOf(newStats.score));
                if (binding.gameTimeLeft != null) binding.gameTimeLeft.setText(Converter.ToTimeString(newStats.timeLeft));
                if (binding.gameTrialsLeft != null) binding.gameTrialsLeft.setText(String.valueOf(newStats.trialsLeft));
            }
        });
    }

    public void showGameFinishedScreen()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                colorPicker.dismissDialog();

                String finalScore = binding.gameScore.getText().toString();
                binding.mainGameLayoutGroup.setVisibility(View.GONE);
                binding.gameFinishedMessage.setVisibility(View.VISIBLE);
                binding.gameFinishedPoints.setText(finalScore);

                Snackbar.make(binding.snackbarLayout, "Party is over!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("LEAVE", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                ((MainLobbyFragment) getParentFragmentManager().
                                        findFragmentByTag(getString(R.string.main_lobby_fragment)))
                                        .closeParty();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
            }
        });
    }

    public void indicateDrawer(Game game)
    {
        switch (game.selectedDrawer)
        {
            case CurrentClient:
                currentClientIsDrawing(game.secretWord);
                break;

            case AnotherClient:
                anotherClientIsDrawing();
                break;

            case AVirtualPlayer:
                aVirtualPlayerIsDrawing();
                break;
        }
    }

    private void currentClientIsDrawing(String secretWord)
    {
        mHandler.post(() ->
        {
            setDrawingToolsEnable(true);
            binding.drawerIndicatorText.setVisibility(View.INVISIBLE);
            binding.tipsText.setVisibility(View.INVISIBLE);
            binding.clientIsDrawingText.setVisibility(View.VISIBLE);
            binding.wordToDraw.setVisibility(View.VISIBLE);
            binding.wordToDraw.setText(secretWord);
        });
    }

    private void anotherClientIsDrawing()
    {
        mHandler.post(() ->
        {
            setDrawingToolsEnable(false);
            binding.drawerIndicatorText.setVisibility(View.VISIBLE);
            binding.tipsText.setVisibility(View.VISIBLE);
            binding.clientIsDrawingText.setVisibility(View.INVISIBLE);
            binding.wordToDraw.setVisibility(View.INVISIBLE);
            binding.drawerIndicatorText.setText("Someone else is drawing...");
        });
    }

    private void aVirtualPlayerIsDrawing()
    {
        mHandler.post(() ->
        {
            setDrawingToolsEnable(false);
            binding.drawerIndicatorText.setVisibility(View.VISIBLE);
            binding.tipsText.setVisibility(View.VISIBLE);
            binding.clientIsDrawingText.setVisibility(View.INVISIBLE);
            binding.wordToDraw.setVisibility(View.INVISIBLE);
            binding.drawerIndicatorText.setText("A virtual player is drawing...");
        });
    }

    private void setDrawingToolsEnable(boolean enabled)
    {
        if (!enabled)
        {
            binding.drawerToolBar.pointerTypeToggleGroup.clearChecked();
            binding.drawerToolBar.pointerTipToggleGroup.clearChecked();
            binding.drawerToolBar.brushSizeSlider.setProgress(3);
            drawerFragment.setBrushSize(3);
            binding.drawerToolBar.colorPickerButton.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            drawerFragment.setBrushColor(Color.BLACK);
        }

        binding.drawerToolBar.penTool.setEnabled(enabled);
        binding.drawerToolBar.eraserTool.setEnabled(enabled);
        binding.drawerToolBar.strokeEraserTool.setEnabled(enabled);

        binding.drawerToolBar.circleBrush.setEnabled(enabled);
        binding.drawerToolBar.squareBrush.setEnabled(enabled);

        binding.drawerToolBar.brushSizeSlider.setEnabled(enabled);
        binding.drawerToolBar.colorPickerButton.setEnabled(enabled);

        if (enabled)
        {
            binding.drawerToolBar.pointerTypeToggleGroup.check(R.id.pen_tool);
            binding.drawerToolBar.pointerTipToggleGroup.check(R.id.circle_brush);
        }
    }
}
