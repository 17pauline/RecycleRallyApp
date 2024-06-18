package com.rr.recyclerally.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.storage.StorageReference;
import com.rr.recyclerally.R;
import com.rr.recyclerally.utils.ImageUtils;

import java.io.IOException;

public class UploadImageTask extends AsyncTask<Uri, Void, String> {
    public static final String UPLOAD_IMAGE_TASK_TAG = "UploadImageTask";
    private Context context;
    private StorageReference storageReference;
    private String postId;
    private ImageUploadCallback callback;

    public UploadImageTask(Context context, StorageReference storageReference, String postId, ImageUploadCallback callback) {
        this.context = context;
        this.storageReference = storageReference;
        this.postId = postId;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Uri... uris) {
        Uri uri = uris[0];
        StorageReference fileReference = storageReference.child(postId + ".jpg");

        try {
            byte[] compressedImage = ImageUtils.getCompressedImageBytes(context, uri, 80);

            fileReference.putBytes(compressedImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(UPLOAD_IMAGE_TASK_TAG, "Image upload succeeded... Retrieving download URL...");
                        fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageURL = downloadUri.toString();
                            Log.d(UPLOAD_IMAGE_TASK_TAG, "Download URL retrieved: " + imageURL);
                            callback.onImageUploaded(imageURL);
                        }).addOnFailureListener(e -> Log.e(UPLOAD_IMAGE_TASK_TAG, "Failed to get download URL", e));
                    })
                    .addOnFailureListener(e -> Log.e(UPLOAD_IMAGE_TASK_TAG, "Failed to upload image", e));
        } catch (IOException e) {
            Log.e(UPLOAD_IMAGE_TASK_TAG, "Error compressing image", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Log.e(UPLOAD_IMAGE_TASK_TAG, "Failed to upload image, result null");
        } else {
            Log.d(UPLOAD_IMAGE_TASK_TAG, "Successful upload");
        }
    }

    public interface ImageUploadCallback {
        void onImageUploaded(String URL);
    }
}
