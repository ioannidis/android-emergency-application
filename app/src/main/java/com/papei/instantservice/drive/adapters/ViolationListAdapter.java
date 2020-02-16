package com.papei.instantservice.drive.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.papei.instantservice.R;
import com.papei.instantservice.drive.ViolationDetailsActivity;
import com.papei.instantservice.drive.models.ViolationModel;

import java.util.List;


public class ViolationListAdapter extends RecyclerView.Adapter<ViolationListAdapter.ViewHolder> {

    private static final String TAG = "ViolationListAdapter";

    private List<ViolationModel> items;

    private Context context;

    public ViolationListAdapter(List<ViolationModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drive_violation_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        // Update speed and timestamp text view values
        holder.speedTextView.setText(String.valueOf(items.get(position).getSpeed()));
        holder.timestampTextView.setText(items.get(position).getTimestamp().toString());

        // On click "more" button start violation detail activity
        holder.moreButton.setOnClickListener((View view) -> {
            Log.d(TAG, "Violation item button: click.");

            Context context = view.getContext();
            Intent intent = new Intent(context, ViolationDetailsActivity.class);
            intent
                    .putExtra("timestamp", items.get(position).getTimestamp().toString())
                    .putExtra("longitude", items.get(position).getLongitude())
                    .putExtra("latitude", items.get(position).getLatitude())
                    .putExtra("speed", String.valueOf(items.get(position).getSpeed()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout LinearLayout;
        TextView timestampTextView;
        TextView speedTextView;
        Button moreButton;

        public ViewHolder(View itemView) {
            super(itemView);
            LinearLayout = itemView.findViewById(R.id.violation_list_parent);
            timestampTextView = itemView.findViewById(R.id.violation_timestamp);
            speedTextView = itemView.findViewById(R.id.violation_speed);
            moreButton = itemView.findViewById(R.id.violation_details);
        }
    }


}
