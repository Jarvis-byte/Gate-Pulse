package com.example.fmoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SplashScreen extends AppCompatActivity {
    ImageView splashimg;
    TextView appname, made_by;

    //LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        appname = findViewById(R.id.textView);
        splashimg = findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.weather_bg).into(splashimg);
        // lottieAnimationView = findViewById(R.id.lottie);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);
        made_by = findViewById(R.id.textView2);
        splashimg.setAnimation(topAnim);
        appname.setAnimation(bottomAnim);
        made_by.setAnimation(bottomAnim);
        splashimg.animate().translationY(-2500).setDuration(1000).setStartDelay(5000);
        appname.animate().translationY(2000).setDuration(1000).setStartDelay(5000);
        made_by.animate().translationY(1500).setDuration(1000).setStartDelay(5000);


        //   lottieAnimationView.animate().translationY(1500).setDuration(200).setStartDelay(5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreen.this, SignInActivity.class));
                finish();

            }
        }, 3000);
    }
}