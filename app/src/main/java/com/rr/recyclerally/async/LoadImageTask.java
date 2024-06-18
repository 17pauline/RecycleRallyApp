package com.rr.recyclerally.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

public class LoadImageTask extends AsyncTask<Uri, Void, Uri> {
    private Context context;
    private ShapeableImageView imageView;
    private ImageLoadCallback callback;

    public LoadImageTask(Context context, ShapeableImageView imageView, ImageLoadCallback callback) {
        this.context = context;
        this.imageView = imageView;
        this.callback = callback;
    }

    @Override
    protected Uri doInBackground(Uri... uris) {
        return uris[0];
    }

    @Override
    protected void onPostExecute(Uri uri) {
        Picasso.get().load(uri).into(imageView);
        callback.onImageLoaded(uri);
    }

    public interface ImageLoadCallback {
        void onImageLoaded(Uri uri);
    }
}
