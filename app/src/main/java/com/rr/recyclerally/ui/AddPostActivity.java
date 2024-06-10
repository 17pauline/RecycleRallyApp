package com.rr.recyclerally.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rr.recyclerally.R;
import com.rr.recyclerally.database.Callback;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.database.UserSession;
import com.rr.recyclerally.model.system.EItemType;
import com.rr.recyclerally.model.system.RecycledItem;

import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {
    private static final String ADD_POST_TAG = "AddPostActivity";
    private FirebaseService firebaseService;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ShapeableImageView ivPost;
    private TextView tvResult;
    private AppCompatSpinner spnItemType;
    private AppCompatButton btnSavePost;
    private Uri imageUri;

    private ActivityResultLauncher<Intent> launcherChooseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        initComponents();
    }

    private void initComponents() {
        ivPost = findViewById(R.id.add_post_iv);
        tvResult = findViewById(R.id.add_post_tv_result);
        spnItemType = findViewById(R.id.add_post_spn_type);
        initSpinner();
        btnSavePost = findViewById(R.id.add_post_btn_save);

        firebaseService = new FirebaseService();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("items_images");

        ActivityResultCallback<ActivityResult> callbackChooseImage = getChooseImageCallback();
        launcherChooseFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callbackChooseImage);
        ivPost.setOnClickListener(v -> openFileChooser());

        btnSavePost.setOnClickListener(v -> savePost());
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnItemType.setAdapter(adapter);
    }

    private ActivityResultCallback<ActivityResult> getChooseImageCallback() {
        return result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                ivPost.setImageURI(imageUri);

                // aici as parsa imaginea modelului .ptl
                // si apoi s-ar popula textview-ul cu rezultatul
                // deocamdata - placeholder
                String mockResult = "plastic";
                tvResult.setText(mockResult);

            }
        };
    }

    private void savePost() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        String userId = user.getUid();
        String postId = UUID.randomUUID().toString();
        StorageReference fileReference = storageReference.child(postId + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageURL = imageUri.toString();

                    String selectedItemType = spnItemType.getSelectedItem().toString().toUpperCase();
                    EItemType itemType = EItemType.valueOf(selectedItemType);
                    RecycledItem recycledItem = new RecycledItem(imageURL, itemType);

                    firebaseService.savePost(userId, recycledItem, postId, new Callback<Boolean>() {
                        @Override
                        public void runResultOnUiThread(Boolean result) {
                            if (result) {
                                Toast.makeText(getApplicationContext(), "Item saved successfully in database", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to save item in database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        })).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcherChooseFile.launch(intent);
    }
}