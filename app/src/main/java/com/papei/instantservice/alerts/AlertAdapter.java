package com.papei.instantservice.alerts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.papei.instantservice.R;
import com.squareup.picasso.Picasso;

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

        ImageView alertImageView = view.findViewById(R.id.alertImageView);
        TextView alertTitleTextView = view.findViewById(R.id.alertTitleTextView);
        TextView alertDescriptionTextView = view.findViewById(R.id.alertDescriptionTextView);

        return new AlertViewHolder(view, alertImageView, alertTitleTextView, alertDescriptionTextView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert alert = this.alerts.get(position);

        Picasso.get().load(alert.getUrl()).into(holder.getAlertImageView());
        holder.getAlertTitleTextView().setText(alert.getTitle());
        holder.getAlertDescriptionTextView().setText(alert.getDescription());
    }

    @Override
    public int getItemCount() {
        return this.alerts.size();
    }
}
