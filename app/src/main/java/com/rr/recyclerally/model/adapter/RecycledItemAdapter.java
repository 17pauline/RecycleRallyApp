package com.rr.recyclerally.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.recyclerally.R;
import com.rr.recyclerally.model.system.RecycledItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecycledItemAdapter extends RecyclerView.Adapter<RecycledItemAdapter.ViewHolder>{
    List<RecycledItem> items;

    public RecycledItemAdapter(List<RecycledItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycled, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecycledItem item = items.get(position);
        Picasso.get().load(item.getImageURL()).into(holder.ivItem);
        holder.tvItemType.setText(item.getItemType().toString());
        holder.tvDatePosted.setText(item.getDatePosted().toString());
        holder.tvRegisteredInChallenge.setText(item.isRegisteredInChallenge() ? "YES" : "NO");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView ivItem;
        public TextView tvItemType;
        public TextView tvDatePosted;
        public TextView tvRegisteredInChallenge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.item_recycled_iv);
            tvItemType = itemView.findViewById(R.id.item_recycled_tv_type);
            tvDatePosted = itemView.findViewById(R.id.item_recycled_tv_datePosted);
            tvRegisteredInChallenge = itemView.findViewById(R.id.item_recycled_tv_RegisteredInChallenge);
        }
    }
}
