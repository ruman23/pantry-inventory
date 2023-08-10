package com.example.pantryinventory;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.squareup.picasso.Picasso;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<ItemData> itemDataList;
    private OnItemListener onItemListener;
    private boolean isNotification;

    public RecyclerViewAdapter(List<ItemData> itemDataList, boolean isNotification, OnItemListener onItemListener) {
        this.itemDataList = itemDataList;
        this.onItemListener = onItemListener;
        this.isNotification = isNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        Picasso.get().load(itemData.getImageUrl()).into(holder.imageView);
        holder.title.setText(itemData.getFoodName());

        // Parse the date from the subtitle text
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date subtitleDate = sdf.parse(itemData.getExpDate());

            // Get the current date without time
            Calendar currentCal = Calendar.getInstance();
            currentCal.set(Calendar.HOUR_OF_DAY, 0);
            currentCal.set(Calendar.MINUTE, 0);
            currentCal.set(Calendar.SECOND, 0);
            currentCal.set(Calendar.MILLISECOND, 0);

            long diff = subtitleDate.getTime() - currentCal.getTimeInMillis();
            long diffDays = TimeUnit.MILLISECONDS.toDays(diff);

            if (isNotification) {
                if (diffDays < 0) {
                    holder.subtitle.setText("Expired " + Math.abs(diffDays) + " days ago");
                } else if (diffDays == 0) {
                    holder.subtitle.setText("Expires today");
                } else {
                    holder.subtitle.setText("Will expire in " + diffDays + " days");
                }
            } else {
                // Set the original value as a day count from the current date
                if (diffDays < 0) {
                    holder.subtitle.setText(Math.abs(diffDays) + " days ago");
                } else if (diffDays == 0) {
                    holder.subtitle.setText("Today");
                } else {
                    holder.subtitle.setText("In " + diffDays + " days");
                }
            }

            // Background color logic
            if (diffDays <= 0) {
                // The date is either today or the previous day
                holder.itemView.setBackgroundColor(Color.parseColor("#ff4d4d"));
            } else if (diffDays <= 7) {
                // The date is within the next 7 days
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFF7F"));
            } else {
                // All other cases
                holder.itemView.setBackgroundColor(Color.parseColor("#80ff80"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            // Optionally, you can set a default background color here if the date parsing fails
            holder.itemView.setBackgroundColor(Color.GRAY);
        }
    }


    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title;
        TextView subtitle;
        Button deleteButton;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            deleteButton = itemView.findViewById(R.id.delete_button);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete item
                    onItemListener.onDeleteClick(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }
}