package com.papei.instantservice.panic;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.papei.instantservice.R;

import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class PanicActivity extends AppCompatActivity implements SensorEventListener {

    private String emergencyPhone;
    private String message;
    private String emergencyEmail ;
    private Button btnMessages, btnHospital, btnCall;

    private ActionBar actionBar;

    private SharedPreferences sharedPreferences;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float x1, x2, x3;
    private static final float ERROR = (float) 7.0;
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

        message = "I need help, this is urgent!!!";

        btnMessages = findViewById(R.id.sosButton);
        btnHospital = findViewById(R.id.hospitalButton);
        btnCall = findViewById(R.id.callButton);

        if (checkPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 1001);
        }

        if (checkPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, 1002);
        }

        // On click send messages
        btnMessages.setOnClickListener(v -> {
            Log.i("Panic Activity", "Send messages button was clicked");

            // SMS
            try {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(emergencyPhone,null, message,null,null);
                Toast.makeText(PanicActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PanicActivity.this, "Email Sent Successfully", Toast.LENGTH_SHORT).show();
            }
            catch (ActivityNotFoundException ex) {
                Toast.makeText(PanicActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }


        });

        btnHospital.setOnClickListener(new View.OnClickListener() {
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


        btnCall.setOnClickListener(new View.OnClickListener() {
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
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1001:
            case 1002: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }

            }

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
                Log.d("mySensor", "Acceleration is " + acceleration + "m/s^2");

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;

                    Log.d("Accelerometer Sensor", "FALL DETECTED");

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("FALL DETECTED!");
                    alert.setMessage("Are you ok? Do you need help?");
                    alert.setPositiveButton("Need help", (dialogInterface, i) -> {
                        Toast.makeText(this,"Need help", Toast.LENGTH_LONG).show();
                    });
                    alert.setNegativeButton("I am ok", (dialogInterface, i) -> {
                        Toast.makeText(this,"I am ok", Toast.LENGTH_LONG).show();
                    });
                    alert.create().show();

                }
            }

        }
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        emergencyPhone = sharedPreferences.getString("emergency_phone", "");
        emergencyEmail = sharedPreferences.getString("emergency_email", "");
    }
}
