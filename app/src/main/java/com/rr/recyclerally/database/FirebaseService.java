package com.rr.recyclerally.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rr.recyclerally.model.system.Challenge;
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
    public static final String CHALLENGES_REFERENCE = "challenges";

    private DatabaseReference databaseReference;

    private ValueEventListener challengesListener;

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // USERS
    // insert / update user
    public void saveUser(AUser user,String userId, Callback<Boolean> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).setValue(user)
                .addOnCompleteListener(task -> callback.runResultOnUiThread(task.isSuccessful()));
    }

    // update user profile image
    public void updateUserProfileImage(String userId, String imageURL, Callback<Boolean> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).child("imageURL").setValue(imageURL)
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
                Log.e(FIREBASE_SERVICE_TAG, "User data unavailable", error.toException());
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
                Log.e(FIREBASE_SERVICE_TAG, "Recyclers data unavailable", error.toException());
                callback.runResultOnUiThread(null);
            }
        });
    }



    // POSTS - RECYCLED ITEMS
    // insert / update post
    public void savePost(String userId, RecycledItem recycledItem, String postId, Callback<Boolean> callback) {
        databaseReference.child(POSTS_REFERENCE).child(userId).child(postId).setValue(recycledItem)
                .addOnCompleteListener(task -> callback.runResultOnUiThread(task.isSuccessful()));
    }

    // retrieve posts for a user
    public void getPosts(String userId, Callback<List<RecycledItem>> callback) {
        databaseReference.child(POSTS_REFERENCE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.e(FIREBASE_SERVICE_TAG, "Post data unavailable", error.toException());
                callback.runResultOnUiThread(null);
            }
        });
    }

    // retrieve all posts with verifiedByAdmin set to false (posts that show up in Admin feed)
    public void getUnverifiedPosts(Callback<List<RecycledItem>> callback) {
        databaseReference.child(POSTS_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RecycledItem> posts = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        RecycledItem recycledItem = postSnapshot.getValue(RecycledItem.class);
                        if (recycledItem != null && !recycledItem.isVerifiedByAdmin()) {
                            posts.add(recycledItem);
                        }
                    }
                }
                callback.runResultOnUiThread(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(FIREBASE_SERVICE_TAG, "Post data unavailable", error.toException());
                callback.runResultOnUiThread(null);
            }
        });
    }




    // CHALLENGES
    public void addChallenge(Challenge challenge, Callback<Boolean> callback) {
        String challengeId = databaseReference.child(CHALLENGES_REFERENCE).push().getKey();
        if (challengeId != null) {
            databaseReference.child(CHALLENGES_REFERENCE).child(challengeId).setValue(challenge)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.runResultOnUiThread(true);
                        } else {
                            callback.runResultOnUiThread(false);
                        }
                    });
        } else {
            callback.runResultOnUiThread(false);
        }
    }

    public void getChallenges(Callback<List<Challenge>> callback) {
        challengesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Challenge> challenges = new ArrayList<>();
                for (DataSnapshot challengeSnapshot : snapshot.getChildren()) {
                    Challenge challenge = challengeSnapshot.getValue(Challenge.class);
                    challenges.add(challenge);
                }
                callback.runResultOnUiThread(challenges);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(FIREBASE_SERVICE_TAG, "Challenges data unavailable", error.toException());
                callback.runResultOnUiThread(null);
            }
        };
        databaseReference.child(CHALLENGES_REFERENCE).addValueEventListener(challengesListener);
    }

    public void removeChallengesListener() {
        if (challengesListener != null) {
            databaseReference.child(CHALLENGES_REFERENCE).removeEventListener(challengesListener);
        }
    }


    // update recycler points
    // Retrieve user points
    public void getUserPoints(String userId, Callback<Integer> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).child("numberOfPoints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer points = snapshot.getValue(Integer.class);
                if (points != null) {
                    callback.runResultOnUiThread(points);
                } else {
                    callback.runResultOnUiThread(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(FIREBASE_SERVICE_TAG, "Failed to retrieve user points", error.toException());
                callback.runResultOnUiThread(null);
            }
        });
    }

    // Update user points
    public void updateUserPoints(String userId, int newPoints, Callback<Boolean> callback) {
        databaseReference.child(USERS_REFERENCE).child(userId).child("numberOfPoints").setValue(newPoints)
                .addOnCompleteListener(task -> callback.runResultOnUiThread(task.isSuccessful()));
    }
}
