package com.papei.instantservice.doctor;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.papei.instantservice.R;

import java.util.ArrayList;

public class DoctorFragment extends Fragment {
    private ArrayList<Message> messages = new ArrayList<>();
    private DatabaseReference dbRef;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private FirebaseUser user;
    private EditText messageEditText;
    private ValueEventListener valueEventListener;
    private LinearLayout progressBarLinearLayout, messagesLinearLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);

        this.dbRef = FirebaseDatabase.getInstance().getReference().child("messages");

        this.messageRecyclerView = view.findViewById(R.id.messagesRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        this.messageRecyclerView.setLayoutManager(manager);
        this.messageAdapter = new MessageAdapter(messages);
        this.messageRecyclerView.setAdapter(this.messageAdapter);

        this.valueEventListener = this.createMessageListener();

        this.user = FirebaseAuth.getInstance().getCurrentUser();

        this.messageEditText = view.findViewById(R.id.messageEditText);
        this.messageEditText.setOnEditorActionListener(createEditorActionListener());

        this.progressBarLinearLayout = view.findViewById(R.id.progressBarLinearLayout);
        this.messagesLinearLayout = view.findViewById(R.id.messagesLinearLayout);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.dbRef.addValueEventListener(this.valueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.dbRef.removeEventListener(this.valueEventListener);
    }

    private ValueEventListener createMessageListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (messages.isEmpty()) {
                    progressBarLinearLayout.setVisibility(View.GONE);
                    messagesLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    messages.clear();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }

                messageAdapter.notifyDataSetChanged();
                messageRecyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        };
    }

    private TextView.OnEditorActionListener createEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendMessage();
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    private void sendMessage() {
        String messageValue = messageEditText.getText().toString();

        if (messageValue.isEmpty()) {
            return;
        }

        String username = user.getDisplayName();
        long timestamp = System.currentTimeMillis();

        dbRef.push().setValue(
                new Message(username, messageValue, timestamp, false)
        );

        messageEditText.getText().clear();
    }
}
