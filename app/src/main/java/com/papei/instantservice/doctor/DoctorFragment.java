package com.papei.instantservice.doctor;

import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
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

import static android.content.Context.VIBRATOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class DoctorFragment extends Fragment {
    private FirebaseUser user;
    private ArrayList<Message> messages = new ArrayList<>();
    private DatabaseReference dbRef;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private EditText messageEditText;
    private ValueEventListener valueEventListener;
    private LinearLayout doctorProgressLinearLayout, messagesLinearLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.dbRef = FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid() + "/messages");
        this.messageRecyclerView = view.findViewById(R.id.messagesRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        this.messageRecyclerView.setLayoutManager(manager);
        this.messageAdapter = new MessageAdapter(messages);
        this.messageRecyclerView.setAdapter(this.messageAdapter);
        this.valueEventListener = this.createMessageListener();
        this.messageEditText = view.findViewById(R.id.messageEditText);
        this.messageEditText.setOnEditorActionListener(createEditorActionListener());
        this.doctorProgressLinearLayout = view.findViewById(R.id.doctorProgressLinearLayout);
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
                    doctorProgressLinearLayout.setVisibility(View.GONE);
                    messagesLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    messages.clear();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }

                Message last = messages.get(messages.size() - 1);

                if (last.checkDoctor()) {
                    vibrate();
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
        return (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                sendMessage();
                return true;
            } else {
                return false;
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
        dbRef.push().setValue(new Message(username, messageValue, timestamp));
        messageEditText.getText().clear();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}
