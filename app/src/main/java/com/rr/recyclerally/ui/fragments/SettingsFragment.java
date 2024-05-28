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
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private TextView tvEmail;

    public SettingsFragment() {
        // Required empty public constructor
    }
    
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        tvEmail = view.findViewById(R.id.settings_tv_email);
        populateTvEmail(view);
    }

    private void populateTvEmail(View view) {
        AUser user = UserSession.getInstance().getUser();
        String strEmail = null;
        if (user != null) {
            strEmail = view.getContext().getString(R.string.settings_email, user.getEmail());
            tvEmail.setText(strEmail);
        }
    }
}