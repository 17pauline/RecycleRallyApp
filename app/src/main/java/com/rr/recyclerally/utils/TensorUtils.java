package com.rr.recyclerally.utils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pytorch.IValue;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class TensorUtils {
    public static Tensor preprocessImage(Bitmap bitmap) {
        // image -> openCv
        Mat image_rgba = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_16UC3);
        Utils.bitmapToMat(bitmap, image_rgba);

        // remove alpha channel
        // convert to float
        Mat image_rgb = convertRGBAtoRGB(image_rgba);
        Mat image_float = new Mat();
        image_rgb.convertTo(image_float, CvType.CV_32F);

        // to float array
        // and axes swap
        float[][][] floatArray = convertMatToFloatArray(image_float);
        float[][][] transformedMatrix = transformMatrix(floatArray);

        // to tensor
        // + normalize
        Tensor inputTensor = convertToTensorAndNormalize(transformedMatrix);
        return inputTensor;
    }


    static Mat convertRGBAtoRGB(Mat image_rgba){
        Mat image_rgb = new Mat(image_rgba.rows(), image_rgba.cols(), CvType.CV_16UC3);
        List<Mat> channels = new ArrayList<>(3);
        Core.split(image_rgba, channels);
        List<Mat> targetChannels = channels.subList(0, 3); // keep channels rgb
        Core.merge(targetChannels, image_rgb);
        return image_rgb;
    }

    private static float[][][] transformMatrix(float[][][] originalMatrix) {
        int depth = originalMatrix.length;
        int rows = originalMatrix[0].length;
        int cols = originalMatrix[0][0].length;

        float[][][] transformedMatrix = new float[cols][depth][rows];

        for (int i = 0; i < originalMatrix.length; i++) {
            for (int j = 0; j < originalMatrix[i].length; j++) {
                // access the values at floatArray[i][j], assign them to new float array
                float value1 = originalMatrix[i][j][0];
                float value2 = originalMatrix[i][j][1];
                float value3 = originalMatrix[i][j][2];

                // assign values to the new array with swapped axes
                transformedMatrix[0][i][j] = value1;
                transformedMatrix[1][i][j] = value2;
                transformedMatrix[2][i][j] = value3;
            }
        }

        return transformedMatrix;
    }

    private static float[][][] convertMatToFloatArray(Mat originalMat) {
        int rows = originalMat.rows();
        int cols = originalMat.cols();
        int depth = originalMat.channels();

        float[][][] floatArray = new float[rows][cols][depth];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float[] pixel = new float[depth];
                originalMat.get(i, j, pixel);

                for (int k = 0; k < depth; k++) {
                    floatArray[i][j][k] = pixel[k];
                }
            }
        }

        return floatArray;
    }

    private static Tensor convertToTensorAndNormalize(float[][][] array) {
        int dim1 = array.length;
        int dim2 = array[0].length;
        int dim3 = array[0][0].length;

        // flatten the array -> 1D float array
        float[] flatArray = new float[dim1 * dim2 * dim3];
        int index = 0;
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                for (int k = 0; k < dim3; k++) {
                    flatArray[index++] = array[i][j][k] / 255.0f;
                }
            }
        }

        // Create a pytorch tensor from the flat array and shape
        return Tensor.fromBlob(flatArray, new long[]{dim1, dim2, dim3});
    }

}
