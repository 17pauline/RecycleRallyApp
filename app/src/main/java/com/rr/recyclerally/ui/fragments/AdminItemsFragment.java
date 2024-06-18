package com.rr.recyclerally.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rr.recyclerally.R;
import com.rr.recyclerally.database.Callback;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.model.adapter.RecycledItemAdapter;
import com.rr.recyclerally.model.system.RecycledItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminItemsFragment extends Fragment {

    private static final String ADMIN_ITEMS_FRAGMENT_TAG = "AdminItemsFragment";
    private FirebaseService firebaseService;
    private RecyclerView rvItems;
    private List<RecycledItem> recycledItems;
    private RecycledItemAdapter recycledItemAdapter;


    public AdminItemsFragment() {
        // Required empty public constructor
    }

    public static AdminItemsFragment newInstance() {
        return new AdminItemsFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_items, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        rvItems = view.findViewById(R.id.admin_items_rv);

        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItems.setHasFixedSize(true);

        recycledItems = new ArrayList<>();
        recycledItemAdapter = new RecycledItemAdapter(recycledItems, getLayoutInflater());
        rvItems.setAdapter(recycledItemAdapter);

        loadUnverifiedRecycledItems();
    }

    private void loadUnverifiedRecycledItems() {
        firebaseService.getUnverifiedPosts(new Callback<List<RecycledItem>>() {
            @Override
            public void runResultOnUiThread(List<RecycledItem> result) {
                if (isAdded()) {
                    if (result != null) {
                        Log.d(ADMIN_ITEMS_FRAGMENT_TAG, getString(R.string.log_items_data_retrieved_setting_adapter));
                        recycledItems.clear();
                        recycledItems.addAll(result);
                        recycledItemAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(ADMIN_ITEMS_FRAGMENT_TAG, getString(R.string.log_error_retrieving_items));
                    }
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseService.removeChallengesListener();
    }
}