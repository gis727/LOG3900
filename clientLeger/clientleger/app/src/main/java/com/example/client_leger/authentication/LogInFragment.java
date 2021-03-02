package com.example.client_leger.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.client_leger.MainActivity;
import com.example.client_leger.R;
import com.example.client_leger.SocketSingleton;
import com.example.client_leger.databinding.FragmentLogInBinding;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LogInFragment extends Fragment {
    private FragmentLogInBinding binding;
    private Activity activity;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLogInBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SocketSingleton.get(activity.getApplicationContext()).OnLogin(onLogin);

        binding.username.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                onCredentialsChanged();
            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                onCredentialsChanged();
            }
        });
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        binding.password.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                if (i == EditorInfo.IME_ACTION_DONE)
                    login();

                return false;
            }
        });

        onCredentialsChanged();
    }

    public void login()
    {
        final String username = binding.username.getText().toString().trim();
        final String password = binding.password.getText().toString().trim();

        boolean canLogin = username.length() > 0 && password.length() > 0;
        if (canLogin)
            binding.loginButton.setEnabled(false);
            SocketSingleton.get(activity.getApplicationContext()).Login(username, password);
    }

    public void signUp()
    {
        ((AuthenticationActivity) activity).showSignUp();
    }

    private void onCredentialsChanged() {
        final String username = binding.username.getText().toString().trim();
        final String password = binding.password.getText().toString().trim();
        boolean canLogin = username.length() > 0 && password.length() > 0;

        if (!canLogin) {
            binding.loginButton.setEnabled(false);
        }
        else {
            binding.loginError.setText("");
            binding.loginButton.setEnabled(true);
        }
    }

    Emitter.Listener onLogin = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        int state = (new JSONObject((String) args[0])).getInt("state");

                        switch (state)
                        {
                            case 0:
                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                                activity.finish();
                                return;

                            case 2:
                                binding.loginError.setText(getString(R.string.already_connected));
                                binding.loginButton.setEnabled(true);
                                return;

                            case 3:
                                binding.loginError.setText(getString(R.string.wrong_credential));
                                binding.loginButton.setEnabled(true);
                        }
                    }
                    catch (Exception e) {}
                }
            });
        }
    };
}
