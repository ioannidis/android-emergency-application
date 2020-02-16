package com.papei.instantservice.drive.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.papei.instantservice.R;

// Marker bubble configuration
public class MapMarkerInfoviewAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mView;

    private Context context;

    public MapMarkerInfoviewAdapter(Context context) {
        this.context = context;
        this.mView = LayoutInflater.from(context).inflate(R.layout.drive_map_marker_infoview, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderViewText(marker, mView);
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderViewText(marker, mView);
        return mView;
    }

    private void renderViewText(Marker marker, View view) {
        String snippet = marker.getSnippet();
        TextView markerDetails = view.findViewById(R.id.infoview_details);

        markerDetails.setText(snippet);
    }

}
