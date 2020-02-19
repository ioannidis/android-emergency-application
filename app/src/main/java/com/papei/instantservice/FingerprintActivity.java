package com.papei.instantservice;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.*;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.os.Bundle;

import java.util.Locale;
import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean fingerprintSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        fingerprintSensor = false;


        //Check to see if device has fingerpint sensor
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                fingerprintSensor = true;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "The user hasn't associated " +
                        "any biometric credentials with their account.");
                break;
        }
        if (!fingerprintSensor){
            //if not then go to home
            startHomeActivity();
        }
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.auth_fail) + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        R.string.auth_success, Toast.LENGTH_SHORT).show();
                startHomeActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), R.string.auth_fail,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_login))
                .setSubtitle(getString(R.string.biometric_credential))
                .setNegativeButtonText(getString(R.string.use_password))
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        Button biometricLoginButton = findViewById(R.id.signInBiometrics);
        biometricLoginButton.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }
    private void startHomeActivity() {
        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }



    }

