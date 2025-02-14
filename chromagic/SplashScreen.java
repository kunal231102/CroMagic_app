package com.example.chromagic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 3000;

    Animation topAnim, bottomAnim;
    ImageView appIcon, designerIcon;
    TextView appName;
    TextView appVersion;
    TextView designedBy;
    //SharedPreferences onBoardingScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.black));

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        appIcon = findViewById(R.id.appIcon);
        designerIcon = findViewById(R.id.designerLogo);
        appName = findViewById(R.id.appName);
        appVersion = findViewById(R.id.version);
        designedBy = findViewById(R.id.designer);


        appIcon.setAnimation(topAnim);
        appName.setAnimation(topAnim);
        appVersion.setAnimation(topAnim);
        designerIcon.setAnimation(bottomAnim);
        designedBy.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                //boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);
                //if(isFirstTime) {

                  //SharedPreferences.Editor editor = onBoardingScreen.edit();
                  //editor.putBoolean("firstTime", false);
                  //editor.commit();

                    Intent intent = new Intent(SplashScreen.this, OnboardingScreen.class);
                    startActivity(intent);
                    finish();
                //}
                //else {
                  //Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                  //startActivity(intent);
                  //finish();
                }

            //}
        },SPLASH_SCREEN);
    }
}