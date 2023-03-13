package com.example.fmoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {
    ImageView splashimg;
    TextView appname, made_by;
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    FirebaseUser firebaseUser;

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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if (width <= 720) {
            System.out.println("LOL");
            appname.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_huge));

        }

        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (firebaseUser != null) {
            db.collection("User").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user.getUid().equals(firebaseUser.getUid())) {
                            if (user.getIsVerified().equals("1")) {
                                if (firebaseUser.isEmailVerified()) {
                                    // When user already sign in
                                    // redirect to profile activity
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            startActivity(new Intent(SplashScreen.this, HomeScreenDashboard.class));
                                            finish();

                                        }
                                    }, 2000);
                                }
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        startActivity(new Intent(SplashScreen.this, SignInActivity.class));
                                        finish();

                                    }
                                }, 2000);
                                // displayToast("Your account is waiting for verification with admin!");
                            }
                        }

                    }
                }
            });
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(SplashScreen.this, SignInActivity.class));
                    finish();

                }
            }, 3000);
        }


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                startActivity(new Intent(SplashScreen.this, SignInActivity.class));
//                finish();
//
//            }
//        }, 3000);
    }
}