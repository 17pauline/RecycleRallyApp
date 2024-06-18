package com.rr.recyclerally.utils;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;
import android.graphics.Bitmap;

public class TensorUtils {
    public static Tensor preprocessImage(Bitmap bitmap) {
        // Convert bitmap to OpenCV Mat
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        // Ensure the image is in a supported format
        Mat convertedMat = new Mat();
        if (mat.type() != CvType.CV_8UC1 && mat.type() != CvType.CV_8UC3 && mat.type() != CvType.CV_8UC4) {
            Imgproc.cvtColor(mat, convertedMat, Imgproc.COLOR_RGBA2RGB);
        } else {
            convertedMat = mat;
        }

        // Resize the image to the required size
        Size size = new Size(224, 224); // Example size, adjust as needed
        Imgproc.resize(convertedMat, convertedMat, size);

        // Convert Mat to Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(convertedMat.cols(), convertedMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(convertedMat, resizedBitmap);

        // Convert Bitmap to Tensor
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

        return inputTensor;
    }
}
