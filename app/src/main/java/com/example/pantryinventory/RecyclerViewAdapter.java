package com.example.pantryinventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        if (isNotification) {
            // Set a custom string for the subtitle if it's a notification
            holder.subtitle.setText("Will expire on "+ itemData.getExpDate());
        } else {
            // Set the original value from the ItemData object for the subtitle
            holder.subtitle.setText(itemData.getExpDate());
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