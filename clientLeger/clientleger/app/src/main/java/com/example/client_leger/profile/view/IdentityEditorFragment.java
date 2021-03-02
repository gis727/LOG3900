package com.example.client_leger.profile.view;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.R;
import com.example.client_leger.databinding.FragmentIdentityEditorBinding;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.viewmodel.UserViewModel;
import com.example.client_leger.utils.Resource;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class IdentityEditorFragment extends Fragment {
    static final int REQUEST_IMAGE_GET = 1;


    private FragmentIdentityEditorBinding binding;
    private UserViewModel userViewModel;

    public IdentityEditorFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentIdentityEditorBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireParentFragment().requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), userObserver);

        binding.nicknameFieldEditor.setText(userViewModel.getUser().getData().nickname);
        binding.nameFieldEditor.setText(userViewModel.getUser().getData().name);
        binding.surnameFieldEditor.setText(userViewModel.getUser().getData().surname);

        Glide.with(getActivity()).load(userViewModel.getUser().getValue().getData().avatar).apply(RequestOptions.circleCropTransform()).into(binding.profileImageEditor);

        binding.changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(view);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_identity_editor_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.save_changes: {
                saveChanges();
                return true;
            }
            case R.id.discard_changes: {
                exitProfileEditor();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_IMAGE_GET);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
//            Bitmap image = data.getParcelableExtra("data");
            Uri imageUri = data.getData();
            Glide.with(getActivity()).load(imageUri).apply(RequestOptions.circleCropTransform()).into(binding.profileImageEditor);
        }
    }

    public void saveChanges() {
        Log.d("saveChanges", "NONE: ");
        userViewModel.modifyUser(
                binding.nicknameFieldEditor.getText().toString(),
                binding.nameFieldEditor.getText().toString(),
                binding.surnameFieldEditor.getText().toString(),
                binding.passwordFieldEditor.getText().toString(),
                getBase64Image());
    }

    public String getBase64Image() {
        BitmapDrawable drawable = (BitmapDrawable) binding.profileImageEditor.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64ImageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return "data:image/png;base64," + base64ImageString;
    }

    public void exitProfileEditor() {
        requireParentFragment().getChildFragmentManager().popBackStack();
    }

    final Observer<Resource<User>> userObserver = new Observer<Resource<User>>() {
        @Override
        public void onChanged(Resource<User> user) {
            switch (user.getStatus()) {
                case SUCCESS: {
                    userViewModel.viewUser("");
                    Snackbar.make(binding.identityEditorSnackbarLayout, "Profile changed successfully.", Snackbar.LENGTH_LONG).show();
                    exitProfileEditor();
                    return;
                }
                case ID_ALREADY_USED: {
                    Snackbar.make(binding.identityEditorSnackbarLayout, "This nickname is already in use.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                case SERVER_ERROR: {
                    Snackbar.make(binding.identityEditorSnackbarLayout, "An error occured.", Snackbar.LENGTH_LONG).show();
                    exitProfileEditor();
                    return;
                }
                case BAD_FORMAT: {
                    Snackbar.make(binding.identityEditorSnackbarLayout, "All fields must be filled.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                case NONE: {
                    return;
                }
                default:
                    exitProfileEditor();
                    return;
            }
        }
    };
}
