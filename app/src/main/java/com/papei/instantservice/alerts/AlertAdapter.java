package com.papei.instantservice.alerts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.papei.instantservice.R;

import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertViewHolder> {
    private List<Alert> alerts;

    public AlertAdapter(List<Alert> alerts) {
        this.alerts = alerts;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert, parent, false);

        TextView alertTitleTextView = view.findViewById(R.id.alertTitleTextView);
        TextView alertDescriptionTextView = view.findViewById(R.id.alertDescriptionTextView);

        return new AlertViewHolder(view, alertTitleTextView, alertDescriptionTextView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert alert = this.alerts.get(position);

        holder.getAlertTitleTextView().setText(alert.getTitle());
        holder.getAlertDescriptionTextView().setText(alert.getDescription());
    }

    @Override
    public int getItemCount() {
        return this.alerts.size();
    }
}
