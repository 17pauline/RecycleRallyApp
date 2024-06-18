package com.rr.recyclerally.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.rr.recyclerally.R;
import com.rr.recyclerally.database.Callback;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.model.adapter.RecycledItemAdapter;
import com.rr.recyclerally.model.system.RecycledItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsFragment extends Fragment {
    private static final String ITEMS_FRAGMENT_TAG = "ItemsFragment";
    private FirebaseService firebaseService;
    private RecyclerView rvItems;
    private List<RecycledItem> recycledItems;
    private RecycledItemAdapter recycledItemAdapter;

    public ItemsFragment() {
        // Required empty public constructor
    }

    public static ItemsFragment newInstance() {
        return new ItemsFragment();
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
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        rvItems = view.findViewById(R.id.items_rv);

        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItems.setHasFixedSize(true);

        recycledItems = new ArrayList<>();
        recycledItemAdapter = new RecycledItemAdapter(recycledItems, getLayoutInflater());
        rvItems.setAdapter(recycledItemAdapter);

        loadRecycledItems();
    }

    private void loadRecycledItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseService.getPosts(userId, new Callback<List<RecycledItem>>() {
            @Override
            public void runResultOnUiThread(List<RecycledItem> result) {
                if (isAdded()) {
                    if (result != null) {
                        Log.d(ITEMS_FRAGMENT_TAG, getString(R.string.log_items_data_retrieved_setting_adapter));
                        recycledItems.clear();
                        recycledItems.addAll(result);
                        recycledItemAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(ITEMS_FRAGMENT_TAG, getString(R.string.log_error_retrieving_items));
                    }
                }
            }
        });
    }
}