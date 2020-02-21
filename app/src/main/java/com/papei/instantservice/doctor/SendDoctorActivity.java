package com.papei.instantservice.doctor;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.papei.instantservice.R;

public class SendDoctorActivity extends AppCompatActivity {
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_doctor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        this.dbRef = FirebaseDatabase.getInstance().getReference().child("users/PtgXgznZf8Rv7Eb4lnXCKMuym693/messages");

        FloatingActionButton sendDoctorMessageButton = findViewById(R.id.sendDoctorMessageButton);
        EditText doctorMessageInput = findViewById(R.id.doctorMessageInput);

        sendDoctorMessageButton.setOnClickListener(v -> {
            String text = doctorMessageInput.getText().toString();

            if (text.isEmpty()) {
                return;
            }

            long timestamp = System.currentTimeMillis();
            dbRef.push().setValue(new Message("Doctor", text, timestamp));
            doctorMessageInput.getText().clear();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
