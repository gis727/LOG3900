package com.example.client_leger.authentication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.client_leger.R;
import com.example.client_leger.SocketSingleton;
import com.example.client_leger.databinding.FragmentSignUpBinding;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import androidx.fragment.app.Fragment;

public class SignUpFragment extends Fragment {
    static final int REQUEST_IMAGE_GET = 1;
    private FragmentSignUpBinding binding;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SocketSingleton.get(getActivity().getApplicationContext()).OnCreateAccount(onCreateAccount);

        binding.firstName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCredentialsChanged();
            }
        });
        binding.lastName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCredentialsChanged();
            }
        });

        binding.lastName.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                if (i == EditorInfo.IME_ACTION_NEXT)
                    binding.username.requestFocus();

                return false;
            }
        });

        binding.username.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCredentialsChanged();
            }
        });

        binding.username.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                if (i == EditorInfo.IME_ACTION_NEXT)
                    binding.password.requestFocus();

                return false;
            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCredentialsChanged();
            }
        });

        binding.password.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                if (i == EditorInfo.IME_ACTION_DONE)
                    register();

                return false;
            }
        });

        binding.createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        binding.signUpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });

        binding.signUpAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        onCredentialsChanged();
    }

    public void onCredentialsChanged()
    {
        final String name = binding.firstName.getText().toString().trim();
        final String surname = binding.lastName.getText().toString().trim();
        final String username = binding.username.getText().toString().trim();
        final String password = binding.password.getText().toString().trim();

        boolean canRegister = username.length()>0 && password.length()>0 && name.length()>0 && surname.length()>0;

        if (!canRegister)
        {
            binding.createAccountButton.setEnabled(false);
        }
        else {
            binding.signUpError.setText("");
            binding.createAccountButton.setEnabled(true);
        }
    }

    public void register()
    {
        final String name = binding.firstName.getText().toString().trim();
        final String surname = binding.lastName.getText().toString().trim();
        final String password = binding.password.getText().toString().trim();
        final String username = binding.username.getText().toString().trim();

        //encode image to base64 string
        Bitmap bitmap;

        try
        {
            BitmapDrawable drawable = (BitmapDrawable) binding.signUpAvatar.getDrawable();
            bitmap = drawable.getBitmap();
        }
        catch (ClassCastException e)
        {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.realavatar);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        SocketSingleton.get(getActivity().getApplicationContext())
                .CreateAccount(name, surname, password, username, "data:image/png;base64," + imageString);
    }

    public void chooseImage() {
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
            binding.signUpAvatar.setPadding(0, 0, 0, 0);
            Glide.with(getActivity()).load(imageUri).apply(RequestOptions.circleCropTransform()).into(binding.signUpAvatar);
        }
    }

    Emitter.Listener onCreateAccount = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        int state = (new JSONObject((String) args[0])).getInt("state");

                        switch (state)
                        {
                            case -2:
                                binding.signUpError.setText(getString(R.string.server_error));
                                return;

                            case -1:
                                binding.signUpError.setText(getString(R.string.bad_format));
                                return;

                            case 1:
                                binding.signUpError.setText(getString(R.string.id_already_used));
                        }
                    }
                    catch (Exception e) {}
                }
            });
        }
    };
}
