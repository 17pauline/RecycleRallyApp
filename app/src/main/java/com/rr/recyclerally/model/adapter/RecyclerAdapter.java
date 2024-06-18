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
import com.rr.recyclerally.model.user.Recycler;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private List<Recycler> recyclers;
    private LayoutInflater layoutInflater;

    public RecyclerAdapter(List<Recycler> recyclers, LayoutInflater layoutInflater) {
        this.recyclers = recyclers;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_recycler, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Recycler recycler = recyclers.get(position);

        holder.tvPosition.setText(String.valueOf(position + 1));
        holder.tvUsername.setText(recycler.getUsername());
        holder.tvPoints.setText(String.format(holder.itemView.getContext().getString(R.string.item_recycler_points), recycler.getNumberOfPoints()));

        if (recycler.getImageURL() != null && !recycler.getImageURL().isEmpty()) {
            Picasso.get().load(recycler.getImageURL()).placeholder(R.drawable.star_24px).into(holder.ivProfileImage);
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.star_24px);
        }
    }

    @Override
    public int getItemCount() {
        return recyclers.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivProfileImage;
        TextView tvPosition, tvUsername, tvPoints;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.item_iv_profileImage);
            ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            tvPosition = itemView.findViewById(R.id.item_tv_position);
            tvUsername = itemView.findViewById(R.id.item_tv_username);
            tvPoints = itemView.findViewById(R.id.item_tv_points);
        }
    }

}
