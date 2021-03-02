package com.example.client_leger.MainLobby;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.client_leger.R;
import com.example.client_leger.databinding.DialogFragmentPartiesFilterBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PartyFilterDialogFragment extends DialogFragment {
    // Party creation data
    public PartyModel.GameMode partyMode;

    private DialogFragmentPartiesFilterBinding binding;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(PartyModel.GameMode partyMode);
    }

    NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogFragmentPartiesFilterBinding.inflate(inflater, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(binding.getRoot()).create();

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.filter_item,
                getResources().getStringArray(R.array.profile_filter_options));

        binding.partyModeFilterField.setAdapter(modeAdapter);
        binding.partyModeFilterField.setText("All", false);
        binding.filterDialogModeHelper.setText(getString(R.string.all_filter_helper));
        binding.partyModeFilterField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        binding.filterDialogModeHelper.setText(getString(R.string.all_filter_helper));
                        break;

                    case 1:
                        binding.filterDialogModeHelper.setText(getString(R.string.free_for_all_filter_helper));
                        break;

                    case 2:
                        binding.filterDialogModeHelper.setText(getString(R.string.sprint_solo_filter_helper));
                        break;

                    case 3:
                        binding.filterDialogModeHelper.setText(getString(R.string.sprint_coop_filter_helper));
                        break;
                }
            }
        });


        binding.dialogFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onDialogPositiveClick(getGameModeFromString(binding.partyModeFilterField.getText().toString()));
            }
        });

        return dialog;
    }

    private PartyModel.GameMode getGameModeFromString(String gameModeString)
    {
        switch (gameModeString) {
            case "Free-for-all":
                return  PartyModel.GameMode.ffa;

            case "Sprint solo":
                return  PartyModel.GameMode.solo;

            case "Sprint coop":
                return  PartyModel.GameMode.coop;

            default:
                return  PartyModel.GameMode.none;
        }
    }

    private PartyModel.Difficulty getDifficultyFromString(String difficultyString)
    {
        switch (difficultyString) {
            case "Easy":
                return PartyModel.Difficulty.Easy;

            case "Medium":
                return PartyModel.Difficulty.Medium;

            case "Hard":
                return PartyModel.Difficulty.Hard;

            default:
                return PartyModel.Difficulty.none;
        }
    }
}
