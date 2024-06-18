package com.rr.recyclerally.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rr.recyclerally.R;
import com.rr.recyclerally.async.LoadImageTask;
import com.rr.recyclerally.async.UploadImageTask;
import com.rr.recyclerally.database.Callback;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.model.system.EItemType;
import com.rr.recyclerally.model.system.RecycledItem;
import com.squareup.picasso.Picasso;


import java.util.UUID;

public class AddPostActivity extends AppCompatActivity implements LoadImageTask.ImageLoadCallback, UploadImageTask.ImageUploadCallback {
    private static final String ADD_POST_TAG = "AddPostActivity";
    private FirebaseService firebaseService;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ShapeableImageView ivPost;
    private AppCompatSpinner spnItemType;
    private TextView tvAlgorithmClassification;
    private AppCompatButton btnInference;
    private AppCompatButton btnSavePost;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> launcherChooseFile;
    private String postId;
    String imageURL; // gets loaded into iv & database !


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        firebaseService = new FirebaseService();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("items_images");

        initComponents();
        // load pytorch model HERE
    }

    private void initComponents() {
        ivPost = findViewById(R.id.add_post_iv);
        spnItemType = findViewById(R.id.add_post_spn_type);
        initSpinner();
        tvAlgorithmClassification = findViewById(R.id.add_post_tv_algorithmClassification_result);
        btnInference = findViewById(R.id.add_post_btn_inference);
        btnSavePost = findViewById(R.id.add_post_btn_save);

        ActivityResultCallback<ActivityResult> callbackChooseImage = getChooseImageCallback();
        launcherChooseFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callbackChooseImage);

        ivPost.setOnClickListener(v -> openFileChooser());
        btnSavePost.setOnClickListener(v -> savePost());
    }


    private ActivityResultCallback<ActivityResult> getChooseImageCallback() {
        return result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                new LoadImageTask(this, ivPost, this).execute(imageUri);
            }
        };
    }

    private void confirmImageUpload() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_select_an_image_from_your_library)
                .setMessage(R.string.dialog_proceed_with_selected_item)
                .setPositiveButton(R.string.dialog_yes, ((dialog, which) -> {
                    Log.d(ADD_POST_TAG, "User confirmed image upload");
                    uploadImage();
                }))
                .setNegativeButton(R.string.dialog_no, ((dialog, which) -> {
                    Log.d(ADD_POST_TAG, "User canceled image upload");
                }))
                .show();
    }

    private void uploadImage() {
        if (imageUri != null) {
            postId = UUID.randomUUID().toString();
            Log.d(ADD_POST_TAG, "Attempting to upload image with postId: " + postId);
            new UploadImageTask(this, storageReference, postId, this).execute(imageUri);
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_please_select_an_image, Toast.LENGTH_SHORT).show();
        }
    }


    private void savePost() {
        if (imageURL == null) {
            Toast.makeText(this, "Image not uploaded yet", Toast.LENGTH_SHORT).show();
            Log.e(ADD_POST_TAG, "Image URL is null. Cannot save post.");
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e(ADD_POST_TAG, getString(R.string.log_failed_to_retrieve_user_data));
        }

        String userId = user.getUid();

        String selectedItemType = spnItemType.getSelectedItem().toString().toUpperCase();
        EItemType itemType = EItemType.valueOf(selectedItemType);
        String alorithmClassification = tvAlgorithmClassification.getText().toString().trim();
        RecycledItem recycledItem = new RecycledItem(imageURL, itemType, alorithmClassification);

        firebaseService.savePost(userId, recycledItem, postId, new Callback<Boolean>() {
            @Override
            public void runResultOnUiThread(Boolean result) {
                if (result) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_item_recycled, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_failed_to_upload_item, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // async callbacks
    @Override
    public void onImageLoaded(Uri uri) {
        confirmImageUpload();
    }

    @Override
    public void onImageUploaded(String URL) {
        imageURL = URL;
        Log.d(ADD_POST_TAG, "Image uploaded successfully. Image URL: " + imageURL);
        Picasso.get().load(imageURL).placeholder(R.drawable.add_photo_24px).into(ivPost);
    }



    //------------------------------------------------------------------------------------------------------
    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnItemType.setAdapter(adapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcherChooseFile.launch(intent);
    }
}