package com.rr.recyclerally.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rr.recyclerally.R;
import com.rr.recyclerally.database.Callback;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.model.adapter.ChallengeAdapter;
import com.rr.recyclerally.model.system.Challenge;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengesFragment extends Fragment {
    private static final String CHALLENGES_FRAGMENT_TAG = "ChallengesFragment";
    private FirebaseService firebaseService;
    private RecyclerView rvChallenges;

    private List<Challenge> challenges;
    private ChallengeAdapter challengeAdapter;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    public static ChallengesFragment newInstance() {
        return new ChallengesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseService = new FirebaseService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        rvChallenges = view.findViewById(R.id.challenges_rv);
        
        rvChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChallenges.setHasFixedSize(true);
        
        challenges = new ArrayList<>();
        challengeAdapter = new ChallengeAdapter(challenges, getLayoutInflater());
        rvChallenges.setAdapter(challengeAdapter);
        
        loadChallenges();
        
    }

    private void loadChallenges() {
        firebaseService.getChallenges(new Callback<List<Challenge>>() {
            @Override
            public void runResultOnUiThread(List<Challenge> result) {
                if (result != null) {
                    challenges.clear();
                    challenges.addAll(result);
                    challengeAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}