package com.papei.instantservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GoogleAuth";
    private static final int SIGN_IN_CODE = 1;

    private GoogleSignInClient client;
    private FirebaseAuth auth;

    private String pref;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pref = sharedPreferences.getString("language_value","default");
        setApplicationLanguage(pref);
        setContentView(R.layout.activity_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        this.client = GoogleSignIn.getClient(this, gso);
        this.auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startFingerprintActivity();
        } else {
            Button signInButton = findViewById(R.id.signInButton);

            signInButton.setOnClickListener(v -> {
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, SIGN_IN_CODE);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != SIGN_IN_CODE) {
            return;
        }

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            auth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> startFingerprintActivity())
                    .addOnFailureListener(e -> {

                    });
        } catch (ApiException e) {
            Log.w(LOG_TAG, "Google sign in failed", e);
        }
    }

    private void startFingerprintActivity() {
        Intent fingerprintIntent = new Intent(getApplicationContext(), FingerprintActivity.class);
        startActivity(fingerprintIntent);
        finish();
    }
    private void setApplicationLanguage(String newLanguage) {
        Resources activityRes = getResources();
        Configuration activityConf = activityRes.getConfiguration();
        Locale newLocale = new Locale(newLanguage);
        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        Resources applicationRes = getApplicationContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf,
                applicationRes.getDisplayMetrics());
    }
}
