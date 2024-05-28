package com.rr.recyclerally.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rr.recyclerally.model.system.RecycledItem;
import com.rr.recyclerally.model.user.AUser;
import com.rr.recyclerally.model.user.Admin;
import com.rr.recyclerally.model.user.EUserType;
import com.rr.recyclerally.model.user.Recycler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FirebaseService {
    public static final String FIREBASE_SERVICE_TAG = "FirebaseService";
    public static final String USERS_REFERENCE = "users";
    public static final String POSTS_REFERENCE = "posts";

    private DatabaseReference databaseReference;

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // USERS
    // insert / update user
    public void saveUser(AUser user,String userId, Callback<Boolean> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).setValue(user)
                .addOnCompleteListener(task -> callback.runResultOnUiThread(task.isSuccessful()));
    }

    // retrieve user
    public void getUser(String userId, Callback<AUser> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userTypeStr = snapshot.child("userType").getValue(String.class);
                    EUserType userType = EUserType.valueOf(userTypeStr);
                    AUser user;
                    if (userType == EUserType.RECYCLER) {
                        user = snapshot.getValue(Recycler.class);
                    } else {
                        user = snapshot.getValue(Admin.class);
                    }
                    callback.runResultOnUiThread(user);
                } else {
                    Log.e(FIREBASE_SERVICE_TAG, "User snapshot unavailable");
                    callback.runResultOnUiThread(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(FIREBASE_SERVICE_TAG, "User data unavailable");
                callback.runResultOnUiThread(null);
            }
        });
    }

    // retrieve and sort Recyclers
    public void getRecyclers(Callback<List<Recycler>> callback) {
        databaseReference.child(USERS_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recycler> recyclers = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userTypeStr = userSnapshot.child("userType").getValue(String.class);
                    EUserType userType = EUserType.valueOf(userTypeStr);
                    if (userType == EUserType.RECYCLER) {
                        Recycler recycler = userSnapshot.getValue(Recycler.class);
                        recyclers.add(recycler);
                    }
                }

                // sorting - leaderboard
                Collections.sort(recyclers, new Comparator<Recycler>() {
                    @Override
                    public int compare(Recycler o1, Recycler o2) {
                        return Integer.compare(o2.getNumberOfPoints(), o1.getNumberOfPoints());
                    }
                });
                callback.runResultOnUiThread(recyclers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(FIREBASE_SERVICE_TAG, "Recyclers data unavailable");
                callback.runResultOnUiThread(null);
            }
        });
    }



    // POSTS - RECYCLED ITEMS
    // insert / update post
    public void savePost(String userId, RecycledItem recycledItem, String postId, Callback<Boolean> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).child(POSTS_REFERENCE).child(postId).setValue(postId)
                .addOnCompleteListener(task -> callback.runResultOnUiThread(task.isSuccessful()));
    }

    // retrieve posts for a user
    public void getPosts(String userId, Callback<List<RecycledItem>> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).child(POSTS_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RecycledItem> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecycledItem recycledItem = postSnapshot.getValue(RecycledItem.class);
                    posts.add(recycledItem);
                }
                callback.runResultOnUiThread(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(FIREBASE_SERVICE_TAG, "Post data unavailable");
                callback.runResultOnUiThread(null);
            }
        });
    }

}
