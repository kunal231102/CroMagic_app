package com.example.chromagic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class OnboardingScreen extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button getStarted;
    Button next;
    Button skip;
    Animation animation;
    int currentPosition;
    //private static final int REQUEST_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_onboarding_screen);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        getStarted = findViewById(R.id.btn_get_started);
        next = findViewById(R.id.next_Btn);
        skip = findViewById(R.id.btn_skip);

        sliderAdapter = new SliderAdapter(this);

        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);

    }

    public void skip(View view) {
        startMainActivity();
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPosition + 1);
    }

    public void getStarted(View view) {
        startMainActivity();
    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();

        for(int i=0; i<dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.yellow));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPosition = position;

            if(position == 0) {
                getStarted.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
            }
            else if(position == 1) {
                getStarted.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
            }
            else {
                animation = AnimationUtils.loadAnimation(OnboardingScreen.this, R.anim.bottom_animation);
                getStarted.setAnimation(animation);
                getStarted.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
                skip.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    //private void checkPermissions() {
      //  if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        //        ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
          //  startMainActivity();
        //} else {
          //  ActivityCompat.requestPermissions(this, new String[]{
            //        android.Manifest.permission.READ_EXTERNAL_STORAGE,
              //      Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        //}
    //}

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

   // @Override
    //public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //if (requestCode == REQUEST_PERMISSION_CODE) {
          //  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //    startMainActivity();
            //}
        //}
    //}
}