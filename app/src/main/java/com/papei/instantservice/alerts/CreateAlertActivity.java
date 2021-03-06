package com.papei.instantservice.alerts;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.papei.instantservice.R;

public class CreateAlertActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private EditText alertTitleInput, alertDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        this.dbRef = FirebaseDatabase.getInstance().getReference().child("alerts");
        this.alertTitleInput = findViewById(R.id.alertTitleInput);
        this.alertDescriptionInput = findViewById(R.id.alertDescriptionInput);
        FloatingActionButton button = findViewById(R.id.createAlertButton);
        button.setOnClickListener(this.createClickListener());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private View.OnClickListener createClickListener() {
        return v -> {

            String title = this.alertTitleInput.getText().toString();
            String description = this.alertDescriptionInput.getText().toString();

            if (title.isEmpty() || description.isEmpty()) {
                return;
            }

            this.dbRef.push().setValue(new Alert(title, description));
            finish();
        };
    }
}
