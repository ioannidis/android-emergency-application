package com.papei.instantservice.doctor;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    private TextView usernameTextView, messageTextView, timestampTextView;

    public MessageViewHolder(@NonNull View itemView,
                             TextView usernameTextView,
                             TextView messageTextView,
                             TextView timestampTextView) {
        super(itemView);

        this.usernameTextView = usernameTextView;
        this.messageTextView = messageTextView;
        this.timestampTextView = timestampTextView;
    }

    public TextView getUsernameTextView() {
        return usernameTextView;
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }

    public TextView getTimestampTextView() {
        return timestampTextView;
    }
}
