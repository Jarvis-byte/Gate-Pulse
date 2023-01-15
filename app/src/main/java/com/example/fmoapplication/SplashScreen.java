package com.example.fmoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {
    ImageView splashimg;
    TextView appname, made_by;
    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        appname = findViewById(R.id.app_name);
        splashimg = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);
        made_by = findViewById(R.id.made_by);
        splashimg.animate().translationY(-2500).setDuration(1000).setStartDelay(5000);
        appname.animate().translationY(2000).setDuration(1000).setStartDelay(5000);
        made_by.animate().translationY(1500).setDuration(1000).setStartDelay(5000);
        lottieAnimationView.animate().translationY(1500).setDuration(200).setStartDelay(5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreen.this, SignInActivity.class));
                finish();

            }
        }, 3000);
    }
}