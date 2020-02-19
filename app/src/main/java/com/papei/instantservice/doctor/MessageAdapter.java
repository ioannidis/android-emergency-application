package com.papei.instantservice.doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.papei.instantservice.R;

import java.text.DateFormat;
import java.util.ArrayList;

class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private ArrayList<Message> messages;

    MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }


    @Override
    public int getItemViewType(int position) {
        Message message = this.messages.get(position);

        if (message.checkDoctor()) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == 0 ? R.layout.message_doctor : R.layout.message_user;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        TextView timestampTextView = view.findViewById(R.id.timestampTextView);

        return new MessageViewHolder(view, usernameTextView, messageTextView, timestampTextView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = this.messages.get(position);

        holder.getUsernameTextView().setText(message.getUsername());
        holder.getMessageTextView().setText(message.getMessage());
        holder.getTimestampTextView().setText(this.formatTimestamp(message.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    private String formatTimestamp(long timestamp) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        return dateFormat.format(timestamp);
    }
}
