package com.rr.recyclerally.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rr.recyclerally.R;
import com.rr.recyclerally.database.UserSession;
import com.rr.recyclerally.model.user.AUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private TextView tvUsername;
    private TextView tvPoints;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        tvUsername = view.findViewById(R.id.profile_tv_username);
        tvPoints = view.findViewById(R.id.profile_tv_points);
        populateUserDetails(view);
    }

    private void populateUserDetails(View view) {
        AUser user = UserSession.getInstance().getUser();
        String strUsername = null;
        String strPoints = null;
        if (user != null) {
            strUsername = view.getContext().getString(R.string.profile_username, user.getUsername());
            tvUsername.setText(strUsername);

            if (UserSession.getInstance().isRecycler()) {
                strPoints = view.getContext().getString(R.string.profile_points, UserSession.getInstance().getRecycler().getNumberOfPoints());
                tvPoints.setText(strPoints);
            }
        }
    }
}