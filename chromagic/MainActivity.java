package com.example.chromagic;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.jsibbold.zoomage.ZoomageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE = 2;
    private static final int SELECT_IMAGE = 1;
    private static ZoomageView imageViewOriginal;
    private static Bitmap processedBitmap;
    private static Bitmap originalImage;
    ImageView menuOptions;
    ImageView selectFromGallery;
    ImageView takePhotoUsingCamera;
    TextView upload;
    ImageView settings;
    ImageView removeImage;
    ImageView history;
    ImageView undo;
    ImageView redo;
    ImageView colorizeBtn;
    ImageView enhancePixelsBtn;
    ImageView removeBgBtn;

    public static String metadata;
    public static Uri currImage_uri;
    static ONNXModelHelper deoldifyModel;
    static ONNXModelHelper realEsrganModel;
    static ONNXModelHelper modnetModel;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        menuOptions = findViewById(R.id.dropdown_menu);
        selectFromGallery = findViewById(R.id.selectFromGallery);
        takePhotoUsingCamera = findViewById(R.id.takeAPictureWithCamera);
        imageViewOriginal = findViewById(R.id.dynamicImageView);
        upload = findViewById(R.id.addImageButton);
        settings = findViewById(R.id.settings);
        removeImage = findViewById(R.id.removeImage);
        history = findViewById(R.id.history);
        undo = findViewById(R.id.undoBtn);
        redo = findViewById(R.id.redoBtn);
        colorizeBtn = findViewById(R.id.colorizeBtn);
        enhancePixelsBtn = findViewById(R.id.enhancePixelsBtn);
        removeBgBtn = findViewById(R.id.removeBg_Btn);

        // Load ONNX models
        //deoldifyModel = new ONNXModelHelper(this, "deoldify.quant.onnx");
        realEsrganModel = new ONNXModelHelper(this, "real_esrgan_pipeline_x4.onnx");
        // modnetModel = new ONNXModelHelper(this, "modnet_photographic_potrait_matting.onnx");

        //loadModels(deoldifyModel,1);
        loadModels(realEsrganModel,2);
        //loadModels(modnetModel,3);

        // Set button click listeners
        //colorizeBtn.setOnClickListener(v -> processImage(deoldifyModel, 256, 1));  // DeOldify
        enhancePixelsBtn.setOnClickListener(v -> processImage(realEsrganModel, 512, 2));  // Real-ESRGAN
        //removeBgBtn.setOnClickListener(v -> processImage(modnetModel, 512, 2));  // MODNet


        removeImage.setEnabled(imageViewOriginal.getDrawable() != null);

        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage();
                // Update button state after removing image
                removeImage.setEnabled(imageViewOriginal.getDrawable() != null);
            }
        });

        menuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsPopupMenu(v);
            }
        });

        selectFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoFromGallery();
            }
        });

        takePhotoUsingCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureUsingCamera();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoFromGallery();
            }
        });
    }


    private void loadModels(ONNXModelHelper obj, int modelCode) {
        obj.loadModelAsync(success -> {
            runOnUiThread(() -> {
                if (success) {
                    if (modelCode == 1) {
                        //Toast.makeText(MainActivity.this, "DeOldify model loaded!", Toast.LENGTH_SHORT).show();
                        System.out.println("DeOldify model loaded");
                    } else if (modelCode == 2) {
                        //Toast.makeText(MainActivity.this, "Real-ESRGAN model loaded!", Toast.LENGTH_SHORT).show();
                        System.out.println("Real-ESRGAN model loaded");
                    } else if (modelCode == 3) {
                        //Toast.makeText(MainActivity.this, "MODNet model loaded!", Toast.LENGTH_SHORT).show();
                        System.out.println("MODNet model loaded");
                    }
                } else {
                    //Toast.makeText(MainActivity.this, "Failed to load onnx models!", Toast.LENGTH_SHORT).show();
                    System.out.println("Failed to load onnx models");
                }
            });
        });
    }


    private void processImage(ONNXModelHelper model, int inputSize, int modelCode) {
        if (imageViewOriginal.getDrawable() == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!model.isModelReady()) {
            Toast.makeText(this, "Model is not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the selected image as a Bitmap
        //Bitmap originalBitmap = ((BitmapDrawable) imageViewOriginal.getDrawable()).getBitmap();

        // Preprocess the image
        float[] input = ImagePreprocessor.preprocessImage(originalImage, inputSize, inputSize, modelCode);

        // Run inference

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    float[] output = model.runInference(input, 1, inputSize, inputSize, 3);
                    return ImagePostprocessor.postprocessImage(output, inputSize, inputSize);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    imageViewOriginal.setImageBitmap(result);
                } else {
                    Toast.makeText(MainActivity.this, "Processing failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private void removeImage() {
        if (imageViewOriginal.getDrawable() != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Remove Image?")
                    .setMessage("Are you sure you want to remove this photo?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Remove the image from the screen
                        imageViewOriginal.setImageDrawable(null);
                        imageViewOriginal.setVisibility(View.INVISIBLE);
                        upload.setVisibility(View.VISIBLE);
                        // Disable the removeImage button
                        removeImage.setEnabled(false);

                        // Set the button to display a disabled icon
                        removeImage.setImageResource(R.drawable.remove_image_disabled);
                    })
                    .setNegativeButton("No", null) // Do nothing if "No" is pressed
                    .show();
        }
    }


    private void showSettingsPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, popupMenu.getMenu());

        // Force icons to show in the popup menu
        try {
            java.lang.reflect.Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            java.lang.reflect.Method setForceShowIcon = menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class);
            setForceShowIcon.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set a listener for item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);  // Call the actual method for item click handling
            }
        });

        popupMenu.show();
    }

    public void changeLanguage(MenuItem view) {
        // List of available languages
        final String[] languages = {"English", "Hindi", "Bengali"};

        // Show a dialog with language options
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");
        builder.setItems(languages, (dialog, which) -> {
            String selectedLanguage = "en"; // Default to English
            switch (which) {
                case 1: // Hindi
                    selectedLanguage = "hi";
                    break;
                case 2: // Bengali
                    selectedLanguage = "bn";
                    break;
            }
            // Change the language
            setLocale(selectedLanguage);
        });
        builder.create().show();
    }

    public void lightMode(MenuItem view) {
        startActivity(new Intent(MainActivity.this, MainActivityLightTheme.class));
        onDestroy();
    }

    public void showMetaData(MenuItem view) {
        if (currImage_uri == null || metadata == null) {
            Toast.makeText(this, "Photo details not available", Toast.LENGTH_SHORT).show();
            return;
        }

        ShowImageAndMetaData dataDialogBox = new ShowImageAndMetaData(MainActivity.this, currImage_uri, metadata);
        dataDialogBox.setCancelable(true);
        dataDialogBox.show();
    }


    private void captureUsingCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    private void selectPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE && data != null) {
                Uri imageUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    originalImage = BitmapFactory.decodeStream(inputStream);
                    imageViewOriginal.setVisibility(View.VISIBLE);
                    imageViewOriginal.setImageBitmap(originalImage);
                    currImage_uri = imageUri;
                    removeImage.setEnabled(imageViewOriginal.getDrawable() != null);
                    updateMetadata(imageUri);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAPTURE_IMAGE && data != null) {
                originalImage = (Bitmap) data.getExtras().get("data");
                imageViewOriginal.setVisibility(View.VISIBLE);
                imageViewOriginal.setImageBitmap(originalImage);
                currImage_uri = data.getData();
                metadata = "Metadata not available for captured image";
                removeImage.setEnabled(imageViewOriginal.getDrawable() != null);
                // Capture metadata from intent data
            }
            removeImage.setImageResource(R.drawable.remove_image);
            upload.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateMetadata(Uri uri) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                @SuppressLint("Range") String width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                @SuppressLint("Range") String height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                @SuppressLint("Range") long dateAddedInSeconds = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                // Use Uri to fetch the display name (filename)
                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                // Use Uri to derive file path (for display purposes only, not to access files directly)
                String path = uri.toString();
                size = String.valueOf(Float.parseFloat(size)/1000);

                if (width == null || height == null) {
                    metadata = "Resolution: Unknown\nSize: " + size + " kB\nDate: Unknown\nName: " + (displayName != null ? displayName : "Unknown") +
                            "\nPath: " + path;
                } else {
                    metadata = "Resolution: " + width + "x" + height +
                            "\nSize: " + size + " kB" +
                            "\nDate: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(dateAddedInSeconds * 1000L)) +
                            "\nName: " + (displayName != null ? displayName : "Unknown") +
                            "\nPath: " + path;
                }
            } catch (Exception e) {
                metadata = "Error retrieving metadata";
                e.printStackTrace();
            }
        } else {
            metadata = "No metadata found";
        }

        if (cursor != null) cursor.close();
    }



    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_dropdown, popupMenu.getMenu());

        // Force icons to show in the popup menu
        try {
            java.lang.reflect.Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            java.lang.reflect.Method setForceShowIcon = menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class);
            setForceShowIcon.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set a listener for item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);  // Call the actual method for item click handling
            }
        });

        popupMenu.show();  // Show the menu
    }


    public void download(MenuItem view) {
        Toast.makeText(this, "Download selected", Toast.LENGTH_SHORT).show();
    }

    public void share(MenuItem view) {
        Toast.makeText(this, "Download selected", Toast.LENGTH_SHORT).show();
    }

    public void help(MenuItem view) {
        startActivity(new Intent(MainActivity.this, ChatBotActivity.class));
    }

    public void reportBug(MenuItem view) {
        String url = "https://forms.gle/76DTSW1beALjrsXm6";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void about(MenuItem view) {
       startActivity(new Intent(MainActivity.this, AboutPage.class));
    }


    private void setLocale(String languageCode) {
        // Create a new Locale object
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        // Update configuration
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        // Apply the updated configuration
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Restart the activity to apply changes
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realEsrganModel != null) {
            realEsrganModel.unloadModel();  // Automatically unload the model when the app is closed
        }
        if (deoldifyModel != null) {
            deoldifyModel.unloadModel();  // Automatically unload the model when the app is closed
        }
        if (modnetModel != null) {
            modnetModel.unloadModel();  // Automatically unload the model when the app is closed
        }
    }

}
