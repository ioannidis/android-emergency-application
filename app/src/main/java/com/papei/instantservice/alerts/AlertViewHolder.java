package com.papei.instantservice.alerts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlertViewHolder extends RecyclerView.ViewHolder {
    private ImageView alertImageView;
    private TextView alertTitleTextView;
    private TextView alertDescriptionTextView;

    public AlertViewHolder(@NonNull View itemView, ImageView alertImageView, TextView alertTitleTextView, TextView alertDescriptionTextView) {
        super(itemView);

        this.alertImageView = alertImageView;
        this.alertTitleTextView = alertTitleTextView;
        this.alertDescriptionTextView = alertDescriptionTextView;
    }

    public ImageView getAlertImageView() {
        return alertImageView;
    }

    public TextView getAlertTitleTextView() {
        return alertTitleTextView;
    }

    public TextView getAlertDescriptionTextView() {
        return alertDescriptionTextView;
    }
}
