package com.papei.instantservice;


import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.papei.instantservice.drive.MainActivity;
import com.papei.instantservice.panic.PanicActivity;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private Button panicButton;
    private SharedPreferences sharedPreferences;
    private TextToSpeech textToSpeech;
    private boolean textToSpeechAvailable;
    private String emergencyPhone;
    private String message;
    private String emergencyEmail;

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
                Toast.makeText(getContext(), R.string.sms_success, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.sms_fail, Toast.LENGTH_SHORT).show();
            }

            try {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + emergencyPhone));
                if (checkPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1002);
                }
                startActivity(intent);
            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling a Phone Number", "Call failed", activityException);
            }
        });

        this.setupSpeechButton(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != 2) {
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        HomeActivity activity = (HomeActivity) getActivity();
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        String command = results.get(0).toLowerCase();

        if (command.contains("doctor")) {
            bottomNavigationView.setSelectedItemId(R.id.doctorMenuItem);
        } else if (command.contains("alerts")) {
            bottomNavigationView.setSelectedItemId(R.id.alertsMenuItem);
        } else if (command.contains("driving mode")) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            this.startActivity(intent);
        } else if (command.contains("disability mode")) {
            Intent intent = new Intent(getContext(), PanicActivity.class);
            this.startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.home_unknown_speech_command, Toast.LENGTH_LONG).show();

            if (this.textToSpeechAvailable) {
                this.textToSpeech.speak(getText(R.string.home_unknown_speech_command), TextToSpeech.QUEUE_ADD, null, null);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void setupSpeechButton(View view) {
        FloatingActionButton speechButton = view.findViewById(R.id.speechButton);

        if (!SpeechRecognizer.isRecognitionAvailable(view.getContext())) {
            speechButton.setVisibility(View.GONE);
        } else {
            textToSpeech = new TextToSpeech(getContext(), this.createTextToSpeechInitListener());
            speechButton.setOnClickListener(this.createSpeechButtonClickListener());
        }
    }

    private TextToSpeech.OnInitListener createTextToSpeechInitListener() {
        return status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);

                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeechAvailable = true;
                }
            }
        };
    }

    private View.OnClickListener createSpeechButtonClickListener() {
        return view -> {
            if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
                startActivityForResult(intent, 2);
            }
        };
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
