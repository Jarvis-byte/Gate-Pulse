package com.example.fmoapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private boolean mPasswordVisible;
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
        aLodingDialog = new ALodingDialog(SignUpActivity.this);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(SignUpActivity.this)) {
                    showCustomeDialog();
                } else {
                    aLodingDialog.show();
                    String user = signupEmail.getText().toString().trim();
                    String pass = signupPassword.getText().toString().trim();
                    String name = signup_name.getText().toString().trim();
                    if (user.isEmpty()) {
                        signupEmail.setError("Email cannot be empty");
                        aLodingDialog.cancel();
                    }
                    if (pass.isEmpty()) {
                        signupPassword.setError("Password cannot be empty");
                        aLodingDialog.cancel();
                    } else if (name.isEmpty()) {

                        signup_name.setError("Name cannot be empty");
                        aLodingDialog.cancel();
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
                                    finish();
                                } else {
                                    aLodingDialog.cancel();
                                    Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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
        signupPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (signupPassword.getRight() - signupPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (!mPasswordVisible) {
                            signupPassword.setTransformationMethod(null);
                            signupPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible, 0);
                            signupPassword.setCompoundDrawablePadding(10);
                            signupPassword.setGravity(Gravity.CENTER_VERTICAL);
                        } else {
                            signupPassword.setTransformationMethod(new PasswordTransformationMethod());
                            signupPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                            signupPassword.setCompoundDrawablePadding(10);
                            signupPassword.setGravity(Gravity.CENTER_VERTICAL);
                        }
                        mPasswordVisible = !mPasswordVisible;
                        return true;
                    }
                }
                return false;
            }
        });
        
        
    }

    private void setName(String uId, String Name) {

        HashMap<String, String> map = new HashMap<>();
        map.put("Name", Name);
        map.put("Uid", uId);
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

    private void showCustomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aLodingDialog.show();

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                dialog.dismiss();


            }
        });
        dialogView.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aLodingDialog.cancel();
    }

    private boolean isConnected(SignUpActivity signInActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) signInActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }


    }
}