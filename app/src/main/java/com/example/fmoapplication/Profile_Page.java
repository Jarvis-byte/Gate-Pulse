package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class Profile_Page extends AppCompatActivity {
    ImageView btn_logOut, profile_pic;
    AppCompatButton btn_user_verification, btn_make_admin, btn_make_logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        profile_pic = findViewById(R.id.profile_pic);
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profile_pic);
        btn_user_verification = findViewById(R.id.btn_user_verification);
        btn_user_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile_Page.this, PendingVerification.class);
                startActivity(intent);
            }
        });
    }
}