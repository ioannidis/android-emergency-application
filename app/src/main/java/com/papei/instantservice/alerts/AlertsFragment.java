package com.papei.instantservice.alerts;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.papei.instantservice.R;

import java.util.ArrayList;
import java.util.List;

public class AlertsFragment extends Fragment {
    private DatabaseReference dbRef;
    private List<Alert> alerts;
    private RecyclerView alertsRecyclerView;
    private AlertAdapter alertAdapter;
    private ValueEventListener alertListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        this.dbRef = FirebaseDatabase.getInstance().getReference().child("alerts");

        this.alerts = new ArrayList<>();

        this.alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        this.alertsRecyclerView.setLayoutManager(manager);
        this.alertAdapter = new AlertAdapter(this.alerts);
        this.alertsRecyclerView.setAdapter(this.alertAdapter);

        this.alertListener = createAlertsListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.dbRef.addValueEventListener(this.alertListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.dbRef.removeEventListener(this.alertListener);
    }

    private ValueEventListener createAlertsListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alerts.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Alert alert = snapshot.getValue(Alert.class);
                    alerts.add(alert);
                }

                alertAdapter.notifyDataSetChanged();
                alertsRecyclerView.scrollToPosition(alerts.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        };
    }
}
