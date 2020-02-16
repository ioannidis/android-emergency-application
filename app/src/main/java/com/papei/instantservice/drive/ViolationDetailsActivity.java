package com.papei.instantservice.drive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.papei.instantservice.R;

import java.util.Objects;

public class ViolationDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    Intent intent;

    TextView timestampTextView;
    TextView longitudeTextView;
    TextView latitudeTextView;
    TextView speedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_violation_details);
        setContentView(R.layout.drive_activity_violation_details);

        // Enable back button on actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Get intents that passed from the selected violation
        intent = getIntent();

        // Load map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set timestamp textView field
        timestampTextView = findViewById(R.id.violation_details_timestamp);
        timestampTextView.setText(intent.getStringExtra("timestamp"));

        // Set longitude textView field
        longitudeTextView = findViewById(R.id.violation_details_long);
        longitudeTextView.setText(String.valueOf(intent.getDoubleExtra("longitude", 0)));

        // Set latitude textView field
        latitudeTextView = findViewById(R.id.violation_details_lat);
        latitudeTextView.setText(String.valueOf(intent.getDoubleExtra("latitude", 0)));

        // Set speed textView field
        speedTextView = findViewById(R.id.violation_details_speed);
        speedTextView.setText(intent.getStringExtra("speed") + " km/h");

    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Create marker
        LatLng marker = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));

        // Add marker on map
        map
                .addMarker(new MarkerOptions().position(marker));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));
    }

    // Add functionality to back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
