package com.rr.recyclerally.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rr.recyclerally.R;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.database.UserSession;
import com.rr.recyclerally.model.user.AUser;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String PROFILE_FRAGMENT_TAG = "ProfileFragment";
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth auth;
    private FirebaseService firebaseService;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageUri;
    private ShapeableImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvPoints;

    private ActivityResultLauncher<Intent> launcherChooseFile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("profile_images");
        firebaseService = new FirebaseService();

        ActivityResultCallback<ActivityResult> callbackChooseImage = getChooseImageCallback();
        launcherChooseFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callbackChooseImage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponents(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateUserDetails(getView());
    }

    private void initComponents(View view) {
        ivProfileImage = view.findViewById(R.id.profile_iv_pfp);
        tvUsername = view.findViewById(R.id.profile_tv_username);
        tvPoints = view.findViewById(R.id.profile_tv_points);

        ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        populateUserDetails(view);

        ivProfileImage.setOnClickListener(v -> openFileChooser());

    }

    private ActivityResultCallback<ActivityResult> getChooseImageCallback() {
        return result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                ivProfileImage.setImageURI(imageUri);
                confirmImageUpload();
            }
        };
    }

    private void confirmImageUpload() {
        new AlertDialog.Builder(getContext())
                .setTitle("Upload new profile image")
                .setMessage("Apply changes?")
                .setPositiveButton(R.string.dialog_yes, ((dialog, which) -> uploadImage()))
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }

    private void uploadImage() {
        if (imageUri != null) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference fileReference = storageReference.child(userId + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageURL = uri.toString();
                            Log.d(PROFILE_FRAGMENT_TAG, "Image URL: " + imageURL);
                            firebaseService.updateUserProfileImage(userId, imageURL, result -> {
                                if (result) {
                                    Toast.makeText(getContext(),
                                            R.string.toast_profile_image_updated, Toast.LENGTH_SHORT).show();
                                    Picasso.get().load(imageURL).into(ivProfileImage);
                                    UserSession.getInstance().getUser().setImageURL(imageURL);
                                    Log.d(PROFILE_FRAGMENT_TAG, "UserSession Image URL: " + UserSession.getInstance().getUser().getImageURL());
                                } else {
                                    Toast.makeText(getContext(),
                                            R.string.toast_failed_to_update_profile_image, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }))
                        .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } else {
            Toast.makeText(getContext(),
                    R.string.toast_no_file_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcherChooseFile.launch(intent);
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

            if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
                // load image into iv
                Log.d(PROFILE_FRAGMENT_TAG, "Loaded image URL on profile " + user.getImageURL());
                Picasso.get().load(user.getImageURL()).placeholder(R.drawable.pfp_100px).into(ivProfileImage);

            } else {
                Log.d(PROFILE_FRAGMENT_TAG, "Image URL is empty or null");
                ivProfileImage.setImageResource(R.drawable.pfp_100px);
            }
        } else {
            Log.d(PROFILE_FRAGMENT_TAG, "User is unavailable");
        }
    }
}