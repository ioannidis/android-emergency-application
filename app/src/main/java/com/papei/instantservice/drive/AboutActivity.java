package com.papei.instantservice.drive;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.papei.instantservice.R;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_about);
//        setTitle(R.string.title_activity_about);
//
//        // Enable back button on actionbar
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//    }

    // Add functionality to back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
