package com.papei.instantservice.drive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.papei.instantservice.R;
import com.papei.instantservice.SettingsActivity;
import com.papei.instantservice.drive.config.DatabaseConfig;
import com.papei.instantservice.drive.models.ViolationModel;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LocationListener, TextToSpeech.OnInitListener {

    Intent intent;
    Intent sRecIntent;

    ActionBar actionBar;

    TextView speedTextView;
    Button violationsButton;
    Button mapButton;
    Button enableButton;
    Button disableButton;
    FloatingActionButton speechRecognitionButton;
    Boolean isEnabled = true;

    private boolean speedViolation;

    private SharedPreferences sharedPreferences;
    private int speedLimit;

    private TextToSpeech textToSpeech;

    private SpeechRecognizer speechRecognizer;

    private DatabaseConfig dbHandler = new DatabaseConfig(this, null, null, 1);



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drive_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Check option selection from the menu and start the corresponding activity
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.help:
                intent = new Intent(this, HelpActivity.class);
                this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive_activity_main);
        actionBar = getSupportActionBar();

        // Enable back button on actionbar
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);


        speedViolation = false;

        // Initialize speech recognition
        initializeSpeechRecognition();

        // Initialize text to speech
        textToSpeech = new TextToSpeech(this, this);

        // Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speedLimit = Integer.valueOf(sharedPreferences.getString("speed_limit_value", "50"));

        speedTextView = findViewById(R.id.speedTextView);
        violationsButton = findViewById(R.id.violations_button);
        mapButton = findViewById(R.id.map_button);
        enableButton = findViewById(R.id.enableButton);
        disableButton = findViewById(R.id.disableButton);
        speechRecognitionButton = findViewById(R.id.speech_recognition_fab);

        // Violations button listener
        violationsButton.setOnClickListener((View view) -> {
            intent = new Intent(this, ViolationsActivity.class);
            this.startActivity(intent);
        });

        // Map button listener
        mapButton.setOnClickListener((View view) -> {
            intent = new Intent(this, MapsActivity.class);
            this.startActivity(intent);
        });

        // Enable button listener
        enableButton.setBackgroundColor(Color.parseColor("#00796b"));
        enableButton.setTextColor(Color.WHITE);
        enableButton.setOnClickListener((View view) -> {
            isEnabled = true;
            buttonBgColor();
            accessData();
            Toast.makeText(this, "Speed capture is enabled", Toast.LENGTH_SHORT).show();
        });

        // Disable button listener
        disableButton.setBackgroundColor(Color.LTGRAY);
        disableButton.setTextColor(Color.DKGRAY);
        disableButton.setOnClickListener((View view) -> {
            isEnabled = false;
            buttonBgColor();
            speedTextView.setText("---");
            Toast.makeText(this, "Speed capture is disabled", Toast.LENGTH_SHORT).show();
        });


        // FAB listener
        speechRecognitionButton.setOnClickListener((View view) -> {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2000);
            } else {
                Toast.makeText(this, "Please speak now!", Toast.LENGTH_LONG).show();
                startActivityForResult(sRecIntent,1001);

            }
        });

        // Check permission for location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            accessData();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onInit(int status) {
        // Setup speech to text
        if(status == TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage(Locale.ENGLISH);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("DEBUG" , "Language Not Supported");
            }
        }
        else{
            Log.i("DEBUG" , "MISSION FAILED");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reinitialize the speech recognizer and text to speech engines upon resuming from background
        initializeSpeechRecognition();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                accessData();
            else
                finish();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (isEnabled) {
                float currentSpeed = location.getSpeed();
                currentSpeed = SpeedConverter.mPerSecToKmPerHr(currentSpeed);
                speedTextView.setText(String.valueOf(currentSpeed));

                speedLimit = Integer.valueOf(sharedPreferences.getString("speed_limit_value", "50"));

                // there is no violation if current speed is lower or equal to speed limit
                if (currentSpeed <= speedLimit) {
                    speedViolation = false;
                    actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
                }

                // if there is a violation, create a new database record and inform the user
                if (currentSpeed > speedLimit) {
                    speedTextView.setTextColor(Color.RED);
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

                    textToSpeech.speak("Caution! You have exceeded the speed limit.", TextToSpeech.QUEUE_ADD, null, null);

                    if (!speedViolation) {
                        speedViolation = true;

                        // Create db record
                        ViolationModel violationModel = new ViolationModel(location.getLongitude(), location.getLatitude(), SpeedConverter.mPerSecToKmPerHr(currentSpeed), new Timestamp(System.currentTimeMillis()));

                        // Save the db record
                        dbHandler.addViolation(violationModel);

                        Toast.makeText(this, violationModel.toString(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    speedTextView.setTextColor(Color.DKGRAY);
                    textToSpeech.stop();
                }

            }
        } else {
            speedTextView.setText("---");
        }
    }

    // Access location manager
    @SuppressLint("MissingPermission")
    private void accessData() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && isEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection", Toast.LENGTH_SHORT).show();
    }

    private void buttonBgColor() {
        if (isEnabled) {
            enableButton.setBackgroundColor(Color.parseColor("#00796b"));
            enableButton.setTextColor(Color.WHITE);
            disableButton.setBackgroundColor(Color.LTGRAY);
            disableButton.setTextColor(Color.DKGRAY);
        } else {
            enableButton.setBackgroundColor(Color.LTGRAY);
            enableButton.setTextColor(Color.DKGRAY);
            disableButton.setBackgroundColor(Color.parseColor("#00796b"));
            disableButton.setTextColor(Color.WHITE);
        }
    }

    // Initialize speech recognition intent
    private void initializeSpeechRecognition() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            sRecIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            sRecIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            sRecIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            sRecIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    this.getPackageName());
        }
    }

    // Process the command gathered from microphone
    private void processSpeechResult(String command) {
        command = command.toLowerCase();
        System.out.println(command);

        if (command.contains("speed") || command.contains("speedometer")) {

            if (command.contains("start")) {
                enableButton.performClick();
            }else if (command.contains("stop")) {
                disableButton.performClick();
            }else if (command.contains("violations")) {
                violationsButton.performClick();
            }else if (command.contains("map")) {
                mapButton.performClick();
            } else {
                Toast.makeText(this, "Available commands are 'start', 'stop', 'violations', 'map'", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Initialize speech recognition say 'speed' or 'speedometer' following by your command!", Toast.LENGTH_LONG).show();
            textToSpeech.speak("Start with 'speed' or speedometer following by your command!", TextToSpeech.QUEUE_ADD, null, null);
        }


    }

    // Check activity results for speech recognition request code and then call the speech result
    // call processSpeechResult to process the results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                    );
            System.out.println(results);
            processSpeechResult(results.get(0));
        }
    }

    // Add functionality to back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
