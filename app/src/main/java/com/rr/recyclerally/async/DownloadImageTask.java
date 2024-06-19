package com.rr.recyclerally.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    public interface ImageDownloadCallback {
        void onImageDownloaded(Bitmap bitmap);
    }

    private ImageDownloadCallback callback;

    public DownloadImageTask(ImageDownloadCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mBitmap = null;
        try {
            InputStream in = new URL(urlDisplay).openStream();
            mBitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (callback != null) {
            callback.onImageDownloaded(result);
        }
    }
}
