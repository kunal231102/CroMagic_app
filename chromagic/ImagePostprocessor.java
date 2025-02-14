package com.example.chromagic;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImagePostprocessor {
    public static Bitmap postprocessImage(float[] output, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[width * height];
        int index = 0;

        for (int i = 0; i < pixels.length; i++) {
            int r = Math.min(255, Math.max(0, (int) (output[index++] * 255)));
            int g = Math.min(255, Math.max(0, (int) (output[index++] * 255)));
            int b = Math.min(255, Math.max(0, (int) (output[index++] * 255)));

            pixels[i] = Color.rgb(r, g, b);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
