package com.papei.instantservice;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.papei.instantservice.panic.PanicActivity;

public class HomeFragment extends Fragment {

    private Button panicButton;
    private SharedPreferences sharedPreferences;

    private String emergencyPhone;
    private String message;
    private String emergencyEmail ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getPreferences();

        message = "I need help, this is urgent!!!";

        panicButton = rootView.findViewById(R.id.panic_button);
        panicButton.setOnClickListener((View v) -> {

            try {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(emergencyPhone,null, message,null,null);
                Toast.makeText(getContext(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
            }

            try {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + emergencyPhone));
                if (checkPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1002);
                }
                startActivity(intent);
            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling a Phone Number", "Call failed", activityException);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
    }

    // Check permission
    public int checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(getContext(), permission);
        return (check == PackageManager.PERMISSION_GRANTED) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
    }

    private void getPreferences() {
        emergencyPhone = sharedPreferences.getString("emergency_phone", "");
        emergencyEmail = sharedPreferences.getString("emergency_email", "");
    }

    // Placeholder
    public void drivingModeActivity(View view) {
    }
    public void panicActivity(View view) {
    }
}
