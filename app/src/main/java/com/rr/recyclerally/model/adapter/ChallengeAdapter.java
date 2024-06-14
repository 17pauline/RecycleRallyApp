package com.rr.recyclerally.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.recyclerally.R;
import com.rr.recyclerally.model.system.Challenge;

import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>{
    private List<Challenge> challenges;
    private LayoutInflater layoutInflater;

    public ChallengeAdapter(List<Challenge> challenges, LayoutInflater layoutInflater) {
        this.challenges = challenges;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);

        holder.tvChallengeDetails.setText(String.format(holder.itemView.getContext().getString(R.string.item_challenge_text), challenge.getItemsNumber(), challenge.getItemType()));
        holder.tvStartDate.setText(String.format(holder.itemView.getContext().getString(R.string.item_challenge_start_date), challenge.getStartDate()));
        holder.tvEndDate.setText(String.format(holder.itemView.getContext().getString(R.string.item_challenge_end_date), challenge.getEndDate()));
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView tvChallengeDetails, tvStartDate, tvEndDate;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChallengeDetails = itemView.findViewById(R.id.item_challenge_tv_challengeDetails);
            tvStartDate = itemView.findViewById(R.id.item_challenge_tv_start_date);
            tvEndDate = itemView.findViewById(R.id.item_challenge_tv_end_date);
        }
    }
}
