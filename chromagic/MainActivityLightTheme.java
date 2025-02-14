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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivityLightTheme extends AppCompatActivity {

    private static final int CAPTURE_IMAGE = 2;
    private static final int SELECT_IMAGE = 1;
    private ImageView imageViewOriginal;
    private ImageView imageViewProcessed;
    private Bitmap processedImage;
    ImageView menuOptions;
    ImageView selectFromGallery;
    ImageView takePhotoUsingCamera;
    TextView upload;
    TextView metaData;
    ImageView settings;

    public static String metadata;

    public static Uri currImage_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_light_theme);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        menuOptions = findViewById(R.id.dropdown_menu);
        selectFromGallery = findViewById(R.id.selectFromGallery);
        takePhotoUsingCamera = findViewById(R.id.takeAPictureWithCamera);
        imageViewOriginal = findViewById(R.id.dynamicImageView);
        upload = findViewById(R.id.addImageButton);
        settings = findViewById(R.id.settings);

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


    private void showSettingsPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.settings_menu_light_theme, popupMenu.getMenu());

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

    public void darkMode(MenuItem view) {
        startActivity(new Intent(MainActivityLightTheme.this, MainActivity.class));
        finish();
    }

    public void showMetaData(MenuItem view) {
        if (currImage_uri == null || metadata == null) {
            Toast.makeText(this, "Photo details not available", Toast.LENGTH_SHORT).show();
            return;
        }

        ShowImageAndMetaDataForLightTheme dataDialogBox = new ShowImageAndMetaDataForLightTheme(MainActivityLightTheme.this, currImage_uri, metadata);
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
            Bitmap originalImage;
            if (requestCode == SELECT_IMAGE && data != null) {
                Uri imageUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    originalImage = BitmapFactory.decodeStream(inputStream);
                    imageViewOriginal.setVisibility(View.VISIBLE);
                    imageViewOriginal.setImageBitmap(originalImage);
                    currImage_uri = imageUri;
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
                // Capture metadata from intent data
            }
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

                if (size == null) size = "Unknown";
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
        startActivity(new Intent(MainActivityLightTheme.this, ChatBotActivity.class));
    }

    public void reportBug(MenuItem view) {
        String url = "https://forms.gle/76DTSW1beALjrsXm6";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void about(MenuItem view) {
        Toast.makeText(this, "Download selected", Toast.LENGTH_SHORT).show();
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



}
