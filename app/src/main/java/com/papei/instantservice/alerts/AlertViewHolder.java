package com.papei.instantservice.alerts;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlertViewHolder extends RecyclerView.ViewHolder {
    private TextView alertTitleTextView;
    private TextView alertDescriptionTextView;

    public AlertViewHolder(@NonNull View itemView, TextView alertTitleTextView, TextView alertDescriptionTextView) {
        super(itemView);

        this.alertTitleTextView = alertTitleTextView;
        this.alertDescriptionTextView = alertDescriptionTextView;
    }

    public TextView getAlertTitleTextView() {
        return alertTitleTextView;
    }

    public TextView getAlertDescriptionTextView() {
        return alertDescriptionTextView;
    }
}
