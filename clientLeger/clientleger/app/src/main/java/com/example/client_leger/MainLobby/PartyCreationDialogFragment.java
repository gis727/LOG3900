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
import com.example.client_leger.databinding.FragmentPartyCreationDialogBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PartyCreationDialogFragment extends DialogFragment {
    // Party creation data
    public String partyName;
    public PartyModel.GameMode partyMode;

    private FragmentPartyCreationDialogBinding binding;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(PartyCreationDialogFragment dialog);
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

        binding = FragmentPartyCreationDialogBinding.inflate(inflater, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(binding.getRoot()).create();

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.filter_item,
                getResources().getStringArray(R.array.party_modes));

        binding.partyModeField.setAdapter(modeAdapter);
        binding.partyModeField.setText("Free for all", false);
        binding.partyModeField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        binding.dialogModeHelper.setText(getString(R.string.free_for_all_helper));
                        break;

                    case 1:
                        binding.dialogModeHelper.setText(getString(R.string.sprint_solo_helper));
                        break;

                    case 2:
                        binding.dialogModeHelper.setText(getString(R.string.sprint_coop_helper));
                        break;
                }
            }
        });


        binding.dialogCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                partyName = binding.dialogPartyName.getText().toString();
                partyMode = getGameModeFromString(binding.partyModeField.getText().toString());
                dialog.dismiss();
                listener.onDialogPositiveClick(PartyCreationDialogFragment.this);
            }
        });

        return dialog;
    }

    private PartyModel.GameMode getGameModeFromString(String gameModeString)
    {
        switch (gameModeString) {
            case "Sprint solo":
                return  PartyModel.GameMode.solo;

            case "Sprint coop":
                return  PartyModel.GameMode.coop;

            default:
                return  PartyModel.GameMode.ffa;
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
