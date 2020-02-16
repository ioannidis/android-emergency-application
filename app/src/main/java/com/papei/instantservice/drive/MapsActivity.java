package com.papei.instantservice.drive;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.papei.instantservice.R;
import com.papei.instantservice.drive.adapters.MapMarkerInfoviewAdapter;
import com.papei.instantservice.drive.config.DatabaseConfig;
import com.papei.instantservice.drive.models.ViolationModel;

import java.util.List;
import java.util.Objects;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<ViolationModel> items;

    private DatabaseConfig dbHandler = new DatabaseConfig(this, null, null, 1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_map);
        setContentView(R.layout.drive_activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Enable back button on actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set custom marker infoview
        mMap.setInfoWindowAdapter(new MapMarkerInfoviewAdapter(MapsActivity.this));

        // Get all violations from database
        items = dbHandler.getViolations();

        if (!items.isEmpty()) {
            // Add markers
            items.forEach(item -> {
                LatLng marker = new LatLng(item.getLatitude(), item.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(marker)
                        .snippet("Date: " + item.getTimestamp() + "\nLongitude: " + item.getLongitude() +  "\nLatitude: " + item.getLatitude() +  "\nSpeed: " + item.getSpeed() + " km/h");
                mMap.addMarker(markerOptions);
            });


            LatLng marker = new LatLng(items.get(0).getLatitude(), items.get(0).getLongitude());

            // Move screen to the latest marker
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 14));
        }

    }

    // Add functionality to back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
