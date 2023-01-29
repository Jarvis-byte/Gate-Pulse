package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    SignInButton googleLogin;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    TextView loginRedirectText;
    TextView forgotPassword;
    private TextView signupRedirectText;
    private ConstraintLayout loginButton;
    private EditText loginEmail, loginPassword;
    private ALodingDialog aLodingDialog;
    boolean mPasswordVisible;
    String msg;
    //right_arrow
    private FirebaseFirestore db;
    FirebaseUser firebaseUser;

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_login_activity);
        googleLogin = findViewById(R.id.googleLogin);
        setGooglePlusButtonText(googleLogin, "Sign in with Gmail Account");
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgot_password);
        //New user register
        loginRedirectText = findViewById(R.id.loginRedirectText);
        loginRedirectText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);
        loginRedirectText.setCompoundDrawablePadding(10);
        loginRedirectText.setGravity(Gravity.CENTER_VERTICAL);
        db = FirebaseFirestore.getInstance();
        aLodingDialog = new ALodingDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        //Login in
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(SignInActivity.this)) {
                    showCustomeDialog();
                } else {

                    String email = loginEmail.getText().toString();
                    String pass = loginPassword.getText().toString();
                    if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (!pass.isEmpty()) {
                            aLodingDialog.show();
                            firebaseAuth.signInWithEmailAndPassword(email, pass)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            aLodingDialog.cancel();
                                            Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignInActivity.this, HomeScreenDashboard.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            aLodingDialog.cancel();
                                            Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            loginPassword.setError("Empty fields are not allowed");
                        }
                    } else if (email.isEmpty()) {
                        loginEmail.setError("Empty fields are not allowed");
                    } else {
                        loginEmail.setError("Please enter correct email");
                    }
                }

            }
        });
        //forgot password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(SignInActivity.this)) {
                    showCustomeDialog();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                    EditText emailBox = dialogView.findViewById(R.id.emailBox);
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String userEmail = emailBox.getText().toString();
                            if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                                Toast.makeText(SignInActivity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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


            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("81820504467-suke0ktooc43tqidnhh6mjg81bq1vtst.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(SignInActivity.this
                , googleSignInOptions);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(SignInActivity.this)) {
                    showCustomeDialog();
                } else {
                    aLodingDialog.show();
                    Signin();
                }

            }
        });
        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        firebaseUser = firebaseAuth.getCurrentUser();
        //  Check condition
        if (firebaseUser != null) {
            // When user already sign in
            // redirect to profile activity
            startActivity(new Intent(SignInActivity.this, HomeScreenDashboard.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();


        }


        loginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (loginPassword.getRight() - loginPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (!mPasswordVisible) {
                            loginPassword.setTransformationMethod(null);
                            loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible, 0);
                            loginPassword.setCompoundDrawablePadding(10);
                            loginPassword.setGravity(Gravity.CENTER_VERTICAL);
                        } else {
                            loginPassword.setTransformationMethod(new PasswordTransformationMethod());
                            loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                            loginPassword.setCompoundDrawablePadding(10);
                            loginPassword.setGravity(Gravity.CENTER_VERTICAL);
                        }
                        mPasswordVisible = !mPasswordVisible;
                        return true;
                    }
                }
                return false;
            }
        });

        //check Internet

// startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    private void showCustomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
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

    private boolean isConnected(SignInActivity signInActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) signInActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }


    }

    private void Signin() {
        Intent intent = googleSignInClient.getSignInIntent();
        // Start activity for result
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // When request code is equal to 100
            // Initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful
                // Initialize string
                String s = "Google sign in successful";
                // Display Toast
                aLodingDialog.cancel();
                displayToast(s);
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null
                        // Initialize auth credential


                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        , null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if (task.isSuccessful()) {
                                            // When task is successful
                                            // Redirect to profile activity
                                            firebaseUser = firebaseAuth.getCurrentUser();
                                            setName(firebaseUser.getUid(), firebaseUser.getDisplayName());



                                        } else {
                                            // When task is unsuccessful
                                            // Display Toast
                                            displayToast("Authentication Failed :" + task.getException()
                                                    .getMessage());
                                        }
                                    }
                                });

                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    private void setName(String uId, String Name) {
        HashMap<String, String> map = new HashMap<>();

        map.put("Name", Name);
        map.put("Uid", uId);
       // System.out.println("UID_SignUp_setName" + uId);
        db.collection("User").document(uId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                aLodingDialog.cancel();
                System.out.println("UID_SignUp_setName" + uId);
                startActivity(new Intent(SignInActivity.this
                        , HomeScreenDashboard.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                // Display Toast
                displayToast("Welcome");
                Toast.makeText(SignInActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("UID_SignUp_setName" + e.getMessage());
                aLodingDialog.cancel();
                Toast.makeText(SignInActivity.this, "Fail to Login!! Please try again\n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}