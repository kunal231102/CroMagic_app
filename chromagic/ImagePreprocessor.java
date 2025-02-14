package com.example.chromagic;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImagePreprocessor {
    public static float[] preprocessImage(Bitmap bitmap, int width, int height, int modelCode) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        int[] pixels = new int[width * height];
        resizedBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // ONNX Model expects NHWC (batch, height, width, channels)
        float[] floatArray = new float[3 * width * height];
        int redIndex = 0;
        int greenIndex = width * height;
        int blueIndex = 2 * width * height;

        if(modelCode == 1) {
            for (int pixel : pixels) {
                float r = (Color.red(pixel) / 127.5f) - 1.0f;
                float g = (Color.green(pixel) / 127.5f) - 1.0f;
                float b = (Color.blue(pixel) / 127.5f) - 1.0f;

                floatArray[redIndex++] = r;
                floatArray[greenIndex++] = g;
                floatArray[blueIndex++] = b;
            }
        }
        else {
            for (int pixel : pixels) {
                float r = (Color.red(pixel) / 255.0f);
                float g = (Color.green(pixel) / 255.0f);
                float b = (Color.blue(pixel) / 255.0f);

                floatArray[redIndex++] = r;
                floatArray[greenIndex++] = g;
                floatArray[blueIndex++] = b;
            }
        }
        return floatArray;
    }
}
