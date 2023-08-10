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

            // Get the previous day's date
            Calendar previousDayCal = (Calendar) currentCal.clone();
            previousDayCal.add(Calendar.DAY_OF_YEAR, -1);

            // Get the date for 7 days from now
            Calendar sevenDaysLaterCal = (Calendar) currentCal.clone();
            sevenDaysLaterCal.add(Calendar.DAY_OF_YEAR, 7);

            if (subtitleDate != null) {
                if (isNotification) {
                    if (!subtitleDate.after(currentCal.getTime()) && !subtitleDate.before(previousDayCal.getTime())) {
                        holder.subtitle.setText("Expired on " + itemData.getExpDate());
                    } else {
                        holder.subtitle.setText("Will expire on " + itemData.getExpDate());
                    }
                } else {
                    // Set the original value from the ItemData object for the subtitle
                    holder.subtitle.setText(itemData.getExpDate());
                }

                if (!subtitleDate.after(currentCal.getTime()) && !subtitleDate.before(previousDayCal.getTime())) {
                    // The date is either today or the previous day
                    holder.itemView.setBackgroundColor(Color.parseColor("#ff4d4d"));
                } else if (subtitleDate.before(sevenDaysLaterCal.getTime())) {
                    // The date is within the next 7 days
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFF7F"));
                } else {
                    // All other cases
                    holder.itemView.setBackgroundColor(Color.parseColor("#80ff80"));
                }
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