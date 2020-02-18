package com.papei.instantservice.panic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.papei.instantservice.R;

public class PanicActivity extends AppCompatActivity {

    private String txtMobile;
    private String txtMessage;
    private String emailAddress;
    private Button btnSend, btnHospital, btnCall;
    private SharedPreferences preferences;
    private String PREF_NAME = "emergency_phone";
    private String PREF_NAME2 = "emergency_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        emailAddress = preferences.getString(PREF_NAME2,"");
        txtMobile = preferences.getString(PREF_NAME,"");
        txtMessage = "I need help this is urgent !!";
        btnSend = (Button) findViewById(R.id.sosButton);
        btnHospital = (Button) findViewById(R.id.hospitalButton);
        btnCall = (Button) findViewById(R.id.callButton);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Send email", "");


                try {
                    //SMS
                    //SmsManager smgr = SmsManager.getDefault();
                    //smgr.sendTextMessage(txtMobile.toString(),null,txtMessage.toString(),null,null);
                    Toast.makeText(PanicActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                    try {
                        //EMAIL
                        Toast.makeText(PanicActivity.this, "Sending mail", Toast.LENGTH_SHORT).show();
                        //TODO get the settings email and phone
                        new Thread(new Runnable() {

                            public void run() {

                                try {

                                    GMailSender sender = new GMailSender(

                                            "0eea83be8dea9c",

                                            "e3f515fb185655");

                                    //sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");

                                    sender.sendMail("Test mail", "This mail has been sent from android app along with attachment",

                                            "d3b1eb2a33-27551b@inbox.mailtrap.io",

                                            emailAddress);

                                } catch (Exception e) {

                                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();



                                }

                            }

                        }).start();
                        Toast.makeText(PanicActivity.this, "Sent mail", Toast.LENGTH_SHORT).show();
                    }
                    catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(PanicActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(PanicActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "166"));
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        ActivityCompat.requestPermissions(PanicActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.


                    }
                    startActivity(intent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }

        });


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtMobile));
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        ActivityCompat.requestPermissions(PanicActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.


                    }
                    startActivity(intent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
