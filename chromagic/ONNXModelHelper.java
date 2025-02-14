package com.example.chromagic;

import android.content.Context;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ai.onnxruntime.*;

public class ONNXModelHelper {
    private OrtEnvironment env;
    private OrtSession session;
    private String modelPath;
    public boolean isModelLoaded = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public boolean isModelReady() {
        return isModelLoaded;
    }

    // Listener for model loading
    public interface OnModelLoadListener {
        void onModelLoaded(boolean success);
    }

    public ONNXModelHelper(Context context, String modelFileName) {
        try {
            this.modelPath = new File(context.getFilesDir(), modelFileName).getAbsolutePath();
            this.env = OrtEnvironment.getEnvironment();
            copyAssetToFile(context, modelFileName, new File(modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadModelAsync(OnModelLoadListener listener) {
        executorService.execute(() -> {
            try {
                if (!isModelLoaded) {
                    this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
                    isModelLoaded = true;
                    System.out.println("ONNX Model Loaded Successfully!");
                    listener.onModelLoaded(true);
                }
            } catch (OrtException e) {
                e.printStackTrace();
                listener.onModelLoaded(false);
            }
        });
    }

    public float[] runInference(float[] inputData, int batchSize, int width, int height, int channels) throws OrtException {
        if (!isModelLoaded) throw new IllegalStateException("Model is not loaded yet!");

        long[] inputShape = {batchSize, channels, height, width};

        // Correct way to create OnnxTensor (Fixed)
        try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputData), inputShape);
             OrtSession.Result result = session.run(Collections.singletonMap(session.getInputNames().iterator().next(), inputTensor))) {

            return (float[]) result.get(0).getValue();
        }
    }

    public void unloadModel() {
        executorService.execute(() -> {
            try {
                if (session != null) {
                    session.close();
                    session = null;
                    isModelLoaded = false;
                    System.out.println("ONNX Model Unloaded to Free Memory");
                }
            } catch (OrtException e) {
                e.printStackTrace();
            }
        });
    }

    private void copyAssetToFile(Context context, String assetName, File outputFile) throws Exception {
        try (InputStream inputStream = context.getAssets().open(assetName);
             OutputStream outputStream = Files.newOutputStream(outputFile.toPath())) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }
    }
}
