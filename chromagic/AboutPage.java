package com.example.chromagic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class AboutPage extends AppCompatActivity {

    TextView gmailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        gmailLink = findViewById(R.id.contactUs_desc);

        gmailLink.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this

                // Setting recipient, subject, and body
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"chromagic.developers@gmail.com"}); // Recipient
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query regarding ChroMagic app"); // Subject
                emailIntent.putExtra(Intent.EXTRA_TEXT, ""); // Body (optional)

                // Specify Gmail package
                emailIntent.setPackage("com.google.android.gm");

                // Log available activities for debugging
                Log.d("EmailIntent", "Available email apps: " + getPackageManager().queryIntentActivities(emailIntent, 0));

                // Check if Gmail app is available
                try {
                    startActivity(emailIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutPage.this, "Gmail app is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}