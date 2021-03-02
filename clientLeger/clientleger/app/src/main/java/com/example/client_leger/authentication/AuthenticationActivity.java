package com.example.client_leger.authentication;

import android.os.Bundle;

import com.example.client_leger.R;
import com.example.client_leger.databinding.ActivityAuthenticationBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class AuthenticationActivity extends AppCompatActivity {
    private ActivityAuthenticationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentById(R.id.sign_up_fragment))
                    .commit();
        }
    }

    public void showSignUp() {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .show(getSupportFragmentManager().findFragmentById(R.id.sign_up_fragment))
                .addToBackStack(null)
                .commit();
    }
}
