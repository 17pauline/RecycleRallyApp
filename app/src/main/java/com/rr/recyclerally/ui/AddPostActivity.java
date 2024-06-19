package com.rr.recyclerally.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.rr.recyclerally.async.DownloadImageTask;
import com.rr.recyclerally.async.LoadImageTask;
import com.rr.recyclerally.async.UploadImageTask;
import com.rr.recyclerally.database.Callback;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.model.system.EItemType;
import com.rr.recyclerally.model.system.RecycledItem;
import com.rr.recyclerally.utils.ImageUtils;
import com.rr.recyclerally.utils.ModelUtils;
import com.rr.recyclerally.utils.TensorUtils;
import com.squareup.picasso.Picasso;


import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.util.UUID;

public class AddPostActivity extends AppCompatActivity implements LoadImageTask.ImageLoadCallback, UploadImageTask.ImageUploadCallback, DownloadImageTask.ImageDownloadCallback {
    private static final String ADD_POST_TAG = "AddPostActivity";
    public static final String RESNET50_PATH = "resnet50_model_lite.ptl";
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

    private Module resnetModel;
    private String[] classes = {"cardboard", "glass", "metal", "paper", "plastic", "trash"};
    private int argmax = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        firebaseService = new FirebaseService();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("items_images");

        initComponents();
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
        btnInference.setOnClickListener(v -> inference());
        btnSavePost.setOnClickListener(v -> savePost());
    }

    private void inference() {
        if (imageURL != null) {
            loadPyTorchModel();
            new DownloadImageTask(this).execute(imageURL);
        } else {
            Toast.makeText(getApplicationContext(), R.string.toast_image_not_yet_uploaded, Toast.LENGTH_SHORT).show();
        }
    }


    private void loadPyTorchModel() {
        try {
            resnetModel = ModelUtils.loadModel(this, RESNET50_PATH);
            if (resnetModel == null) {
                Toast.makeText(this,
                        R.string.toast_failed_to_load_model, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(ADD_POST_TAG, "Exception loading model", e);
            finish();
        }
    }

    private void runPyTorchModel(Bitmap bitmap) {
        try {
            // from imageURL to bitmap, bitmap needs to be populated with whatever image is in imageURL right now
            Tensor inputTensor = TensorUtils.preprocessImage(bitmap);

            IValue output = resnetModel.forward(IValue.from(inputTensor));
            float[] preds = output.toTensor().getDataAsFloatArray();

            // prediction with highest score
            float max = preds[0];
            for (int i = 1; i < preds.length; i++) {
                if (preds[i] > max) {
                    max = preds[i];
                    argmax = i;
                }
            }

            String algorithmClassification = classes[argmax];
            tvAlgorithmClassification.setText(algorithmClassification);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    uploadImage();
                }))
                .setNegativeButton(R.string.dialog_no, ((dialog, which) -> {
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
            Toast.makeText(getApplicationContext(), R.string.toast_image_not_yet_uploaded, Toast.LENGTH_SHORT).show();
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

                    addPointToUser(userId);
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_failed_to_upload_item, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addPointToUser(String userId) {
        firebaseService.getUserPoints(userId, new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer points) {
                int newPoints = points + 1;
                firebaseService.updateUserPoints(userId, newPoints, new Callback<Boolean>() {
                    @Override
                    public void runResultOnUiThread(Boolean result) {
                        if (result) {
                            Toast.makeText(getApplicationContext(), R.string.toast_point_awarded, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
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
        Picasso.get().load(imageURL).placeholder(R.drawable.add_photo_24px).into(ivPost);
    }

    @Override
    public void onImageDownloaded(Bitmap bitmap) {
        runPyTorchModel(bitmap);
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