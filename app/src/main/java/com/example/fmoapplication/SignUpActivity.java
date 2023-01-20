package com.example.fmoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signup_name;
    private ConstraintLayout signupButton;
    private TextView loginRedirectText;
    private FirebaseFirestore db;
    private ALodingDialog aLodingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_signup_activity);
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signup_name = findViewById(R.id.signup_name);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        db = FirebaseFirestore.getInstance();
        //progressbar
        aLodingDialog = new ALodingDialog(this);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.show();
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String name = signup_name.getText().toString().trim();
                if (user.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                } else if (name.isEmpty()) {

                    signup_name.setError("Name cannot be empty");
                } else {
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                String uId = user.getUid();
                                System.out.println("UID_SignUp" + uId);


                                setName(uId, name);

                                Intent intent = new Intent(SignUpActivity.this, HomeScreenDashboard.class);
                                startActivity(intent);
                            } else {
                                aLodingDialog.cancel();
                                Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);


            }
        });
    }

    private void setName(String uId, String Name) {

        HashMap<String, String> map = new HashMap<>();
        map.put("Name", Name);
        System.out.println("UID_SignUp_setName" + uId);
        db.collection("User").document(uId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                aLodingDialog.cancel();
                System.out.println("UID_SignUp_setName" + uId);
                Toast.makeText(SignUpActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("UID_SignUp_setName" + e.getMessage());
                aLodingDialog.cancel();
                Toast.makeText(SignUpActivity.this, "Fail to Login!! Please try again\n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}