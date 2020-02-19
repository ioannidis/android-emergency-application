package com.papei.instantservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.papei.instantservice.alerts.AlertsFragment;
import com.papei.instantservice.alerts.AlertsHelpActivity;
import com.papei.instantservice.doctor.DoctorFragment;
import com.papei.instantservice.doctor.DoctorHelpActivity;
import com.papei.instantservice.drive.MainActivity;
import com.papei.instantservice.panic.PanicActivity;

public class HomeActivity extends AppCompatActivity {
    private static String lastFragmentName;
    private FirebaseAuth firebaseAuth;
    private FragmentManager fragmentManager;
    private ActionBar actionBar;
    private FirebaseUser user;
    private BottomNavigationView bottomNavigationView;
    private String emergencyPhone;
    private String emergencyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.user = firebaseAuth.getCurrentUser();
        this.actionBar = getSupportActionBar();
        this.fragmentManager = getSupportFragmentManager();
        this.subscribeToTopics();
        this.getPreferences();

        this.bottomNavigationView = findViewById(R.id.bottomNavigationView);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this.createSelectionListener());
        this.navigateToLastFragment();
    }

    private void getPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        emergencyPhone = sharedPreferences.getString("emergency_phone", "");
        emergencyEmail = sharedPreferences.getString("emergency_email", "");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragments_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.helpMenuItem) {
            CharSequence title = getSupportActionBar().getTitle();

            if (title.equals(getString(R.string.home))) {
                Intent settingsIntent = new Intent(this, HomeHelpActivity.class);
                startActivity(settingsIntent);
            } else if (title.equals(getString(R.string.doctor))) {
                Intent settingsIntent = new Intent(this, DoctorHelpActivity.class);
                startActivity(settingsIntent);
            } else if (title.equals(getString(R.string.alerts))) {
                Intent settingsIntent = new Intent(this, AlertsHelpActivity.class);
                startActivity(settingsIntent);
            }
        } else if (item.getItemId() == R.id.settingsMenuItem) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (item.getItemId() == R.id.aboutMenuItem) {
            Intent settingsIntent = new Intent(this, AboutActivity.class);
            startActivity(settingsIntent);
        } else if (item.getItemId() == R.id.signOutMenuItem) {
            signOut();

            Intent settingsIntent = new Intent(this, SignInActivity.class);
            startActivity(settingsIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener createSelectionListener() {
        return item -> {
            switch (item.getItemId()) {
                case R.id.homeMenuItem:
                    navigateHomeFragment();
                    return true;
                case R.id.doctorMenuItem:
                    navigateDoctorFragment();
                    return true;
                case R.id.alertsMenuItem:
                    navigateAlertsFragment();
                    return true;
            }
            return false;
        };
    }

    private void navigateToLastFragment() {
        if (lastFragmentName == null || lastFragmentName.equals("home")) {
            this.bottomNavigationView.setSelectedItemId(R.id.homeMenuItem);
        } else if (lastFragmentName.equals("doctor")) {
            this.bottomNavigationView.setSelectedItemId(R.id.doctorMenuItem);
        } else if (lastFragmentName.equals("alerts")) {
            this.bottomNavigationView.setSelectedItemId(R.id.alertsMenuItem);
        }
    }

    private void navigateHomeFragment() {
        HomeFragment fragment = new HomeFragment();
        this.fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment).commit();
        this.actionBar.setTitle(R.string.home);
        this.actionBar.setSubtitle(this.user.getDisplayName());
        lastFragmentName = "home";
    }

    private void navigateDoctorFragment() {
        DoctorFragment fragment = new DoctorFragment();
        this.fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment).commit();
        this.actionBar.setTitle(R.string.doctor);
        this.actionBar.setSubtitle(null);
        lastFragmentName = "doctor";
    }

    private void navigateAlertsFragment() {
        AlertsFragment fragment = new AlertsFragment();
        this.fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment).commit();
        this.actionBar.setTitle(R.string.alerts);
        this.actionBar.setSubtitle(null);
        lastFragmentName = "alerts";
    }

    private void signOut() {
        this.firebaseAuth.signOut();
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
    }

    // Open driving model activity
    public void drivingModeActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    // Open panic activity
    public void panicActivity(View view) {
        Intent intent = new Intent(this, PanicActivity.class);
        this.startActivity(intent);
    }

    private void subscribeToTopics() {
        FirebaseMessaging messaging = FirebaseMessaging.getInstance();

        messaging.subscribeToTopic(getString(R.string.alerts_notif_topic));
    }
}
