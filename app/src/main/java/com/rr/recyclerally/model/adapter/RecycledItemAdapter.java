package com.rr.recyclerally.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.rr.recyclerally.R;
import com.rr.recyclerally.model.system.RecycledItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecycledItemAdapter extends RecyclerView.Adapter<RecycledItemAdapter.RecycledItemViewHolder>{
    private List<RecycledItem> items;
    private LayoutInflater layoutInflater;

    public RecycledItemAdapter(List<RecycledItem> items, LayoutInflater layoutInflater) {
        this.items = items;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public RecycledItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_recycled, parent, false);
        return new RecycledItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycledItemViewHolder holder, int position) {
        RecycledItem item = items.get(position);

        if (item.getImageURL() != null && !item.getImageURL().isEmpty()) {
            Picasso.get().load(item.getImageURL()).placeholder(R.drawable.add_photo_40px).into(holder.ivItem);
        } else {
            holder.ivItem.setImageResource(R.drawable.add_photo_40px);
        }
        holder.tvItemType.setText(item.getItemType().toString());
        holder.tvDatePosted.setText(item.getDatePosted().toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecycledItemViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView ivItem;
        public TextView tvItemType;
        public TextView tvDatePosted;

        public RecycledItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.item_recycled_iv);
            ivItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
            tvItemType = itemView.findViewById(R.id.item_recycled_tv_type);
            tvDatePosted = itemView.findViewById(R.id.item_recycled_tv_datePosted);
        }
    }
}
