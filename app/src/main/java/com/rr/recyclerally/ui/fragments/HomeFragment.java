package com.rr.recyclerally.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rr.recyclerally.R;
import com.rr.recyclerally.database.Callback;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.database.UserSession;
import com.rr.recyclerally.model.adapter.RecyclerAdapter;
import com.rr.recyclerally.model.user.AUser;
import com.rr.recyclerally.model.user.Recycler;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String HOME_FRAGMENT_TAG = "HomeFragment";
    private FirebaseService firebaseService;
    private TextView tvHello;
    private RecyclerView rvUsers;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        tvHello = view.findViewById(R.id.home_tv_hello_username);
        rvUsers = view.findViewById(R.id.home_rv_leaderboard);

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setHasFixedSize(true);

        populateTvHello(view);
        loadRecyclers();
    }

    private void loadRecyclers() {
        firebaseService.getRecyclers(new Callback<List<Recycler>>() {
            @Override
            public void runResultOnUiThread(List<Recycler> result) {
                if (result != null && !result.isEmpty()) {
                    Log.d(HOME_FRAGMENT_TAG, "Recyclers data retrieved, setting adapter");
                    RecyclerAdapter adapter = new RecyclerAdapter(result, LayoutInflater.from(getContext()));
                    rvUsers.setAdapter(adapter);
                } else {
                    Log.e(HOME_FRAGMENT_TAG, "Error retrieving recyclers");
                }
            }
        });
    }

    private void populateTvHello(View view) {
        AUser user = UserSession.getInstance().getUser();
        String strUsername = null;
        if (user != null) {
            strUsername = view.getContext().getString(R.string.home_hello_username, user.getUsername());
            tvHello.setText(strUsername);
        }
    }


}