package com.papei.instantservice.panic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.papei.instantservice.R;

import java.util.Objects;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_help);
        setTitle(R.string.title_activity_help);

        // Enable back button on actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    // Add functionality to back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
