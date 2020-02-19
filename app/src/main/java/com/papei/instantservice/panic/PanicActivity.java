package com.papei.instantservice.panic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.papei.instantservice.R;
import com.papei.instantservice.SettingsActivity;
import com.papei.instantservice.drive.HelpActivity;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class PanicActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {

    private String emergencyPhone;
    private String message;
    private String emergencyEmail ;
    private Button messagesButton, hospitalButton, callButton;
    private FloatingActionButton speechRecognitionButton;


    private Intent intent;
    private ActionBar actionBar;

    private SharedPreferences sharedPreferences;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;

    private static final float SHAKE_THRESHOLD = 15.00f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic);

        actionBar = getSupportActionBar();

        // Enable back button on actionbar
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        getPreferences();

        message = getString(R.string.need_help_urgent);

        messagesButton = findViewById(R.id.sosButton);
        hospitalButton = findViewById(R.id.hospitalButton);
        callButton = findViewById(R.id.callButton);
        speechRecognitionButton = findViewById(R.id.speech_recognition_fab_panic);


        if (checkPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 1001);
        }

        if (checkPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, 1002);
        }

        // Initialize speech recognition
        initializeSpeechRecognition();

        // Initialize text to speech
        textToSpeech = new TextToSpeech(this, this);

        // On click send messages
        messagesButton.setOnClickListener(v -> {
            Log.i("Panic Activity", "Send messages button was clicked");

            sendMessages();
        });

        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "166"));
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(PanicActivity.this, new String[]{Manifest.permission.CALL_PHONE},1002);
                    }
                    startActivity(intent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Panic Activity", "Call failed", activityException);
                }
            }

        });


        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + emergencyPhone));
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PanicActivity.this, new String[]{Manifest.permission.CALL_PHONE},1002);
                    }
                    startActivity(intent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

        // FAB listener
        speechRecognitionButton.setOnClickListener((View view) -> {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2000);
            } else {
                Toast.makeText(this, R.string.please_speak, Toast.LENGTH_LONG).show();
                startActivityForResult(intent,2000);

            }
        });

        // Init sensor
        Log.d("Panic Activity", "Init sensor manager");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1001:
            case 1002:
            case 2000:{

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    // Initialize speech recognition intent
    private void initializeSpeechRecognition() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    this.getPackageName());
        }
    }

    // Process the command gathered from microphone
    private void processSpeechResult(String command) {
        command = command.toLowerCase();

        if (command.contains("send message") || command.contains("send a message") || command.contains("send messages")) {
            messagesButton.performClick();
        }else if (command.contains("call hospital")) {
            hospitalButton.performClick();
        }else if (command.contains("call contact")) {
            callButton.performClick();
        } else {
            Toast.makeText(this, R.string.commands_panic, Toast.LENGTH_LONG).show();
            textToSpeech.speak(getString(R.string.TTS_panic), TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    // Sensor methods
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                Log.d("Panic Activity", "Acceleration is " + acceleration + "m/s^2");

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;

                    Log.d("Accelerometer Sensor", "FALL DETECTED");

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(R.string.fall_detected);
                    alert.setMessage(R.string.need_help_quest);

                    alert.setPositiveButton(R.string.need_help, (dialogInterface, i) -> {
                        sendMessages();
                    });
                    alert.setNegativeButton(R.string.no_thanks, (dialogInterface, i) -> {
                    });
                    alert.create().show();

                }
            }

        }
    }

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
    public void onAccuracyChanged(Sensor sensor, int i) { }

    // Check permission
    public int checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
    }

    // Add functionality to back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        emergencyPhone = sharedPreferences.getString("emergency_phone", "");
        emergencyEmail = sharedPreferences.getString("emergency_email", "");
    }

    // Check activity results for speech recognition request code and then call the speech result
    // call processSpeechResult to process the results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2000 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
            );
            System.out.println(results);
            processSpeechResult(results.get(0));
        }
    }

    private void sendMessages() {
        // SMS
        try {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(emergencyPhone,null, message,null,null);
            Toast.makeText(PanicActivity.this, R.string.sms_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.sms_fail, Toast.LENGTH_SHORT).show();
        }

        // Email
        try {
            new Thread(() -> {

                final String username = "53ded0fa773e92";
                final String password = "d9c7cc18061330";

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.mailtrap.io");
                props.put("mail.smtp.port", "2525");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("EmergencyApp@unipi.gr"));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(emergencyEmail));
                    message.setSubject("I need help!");
                    message.setText("I NEED HELP!!! Please come as soon as possible! Its urgent!!!");

                    Transport.send(message);

                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

            }).start();
            Toast.makeText(PanicActivity.this, R.string.email_sucess, Toast.LENGTH_SHORT).show();
        }
        catch (ActivityNotFoundException ex) {
            Toast.makeText(PanicActivity.this, R.string.no_client, Toast.LENGTH_SHORT).show();
        }
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
}
