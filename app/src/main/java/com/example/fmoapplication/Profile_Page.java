package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile_Page extends AppCompatActivity {
    ImageView profile_pic;
    AppCompatButton btn_user_verification, btn_make_admin, btn_make_logout, back;
    TextView user_name, user_email;
    private FirebaseFirestore db;
    boolean emailLogin = false;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    String name;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        //Init
        profile_pic = findViewById(R.id.profile_pic);
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        btn_make_logout = findViewById(R.id.btn_make_logout);
        db = FirebaseFirestore.getInstance();
        btn_user_verification = findViewById(R.id.btn_user_verification);

        //back
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_user_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile_Page.this, PendingVerification.class);
                startActivity(intent);
            }
        });

        //username and email
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("password")) {
                System.out.println("User is signed in with email/password");
                emailLogin = true;
                profile_pic.setBackgroundResource(R.drawable.man);
            } else {
                Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profile_pic);
            }
        }

        GoogleSignInAccount account = null;
        if (emailLogin == false) {
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            account = GoogleSignIn.getLastSignedInAccount(this);

            if (account != null) {
                name = account.getDisplayName();
                user_name.setText(name);
            }

        } else {
            // String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println("UID ELSE" + Uid);
            db.collection("User").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {

                        User user = documentSnapshot.toObject(User.class);
                        name = user.getName();
                        user_name.setText(name);

                    } else {
                        Toast.makeText(Profile_Page.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                    Toast.makeText(Profile_Page.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //logout
        btn_make_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Page.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // aLodingDialog.show();

                        FirebaseAuth.getInstance().signOut();
                        dialog.dismiss();
                        Intent intent = new Intent(Profile_Page.this, SignInActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}