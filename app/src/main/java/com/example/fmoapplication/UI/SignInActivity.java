package com.example.fmoapplication.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

import com.example.fmoapplication.Dialog.ALodingDialog;
import com.example.fmoapplication.Model.User;
import com.example.fmoapplication.R;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

                                            if (authResult.getUser().isEmailVerified()) {
                                                aLodingDialog.cancel();
                                                String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                // System.out.println("UID from shared pref" + Uid);
                                                db.collection("User").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {

                                                            User user = documentSnapshot.toObject(User.class);
                                                            System.out.println("Document from shared pref" + user.getName());
                                                            if (user.getIsVerified().equals("1")) {
                                                                Toast.makeText(SignInActivity.this, "Sign In Successfully, Welcome !", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(SignInActivity.this
                                                                        , HomeScreenDashboard.class)
                                                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                                finish();
                                                            } else {
                                                                displayToast("Your account is waiting for verification with admin!");
                                                            }


                                                        }
                                                    }
                                                });
                                            } else {
                                                aLodingDialog.cancel();
                                                Toast.makeText(SignInActivity.this, "Email verification pending! Please verify your email", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            aLodingDialog.cancel();
                                            Toast.makeText(SignInActivity.this, "Login Failed:- " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
//if (firebaseUser != null) {
//    db.collection("User").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//        @Override
//        public void onSuccess(DocumentSnapshot documentSnapshot) {
//            if (documentSnapshot.exists()) {
//                User user = documentSnapshot.toObject(User.class);
//                if (user.getUid().equals(firebaseUser.getUid())) {
//                    if (user.getIsVerified().equals("1")) {
//                        if (firebaseUser.isEmailVerified()) {
//                            // When user already sign in
//                            // redirect to profile activity
//                            startActivity(new Intent(SignInActivity.this, HomeScreenDashboard.class)
//                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                            finish();
//                        }
//                    } else {
//                        // displayToast("Your account is waiting for verification with admin!");
//                    }
//                }
//
//            }
//        }
//    });
//}


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
            System.out.println(signInAccountTask.getException());

            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful
                // Initialize string
                // Display Toast
                aLodingDialog.cancel();

                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null
                        // Initialize auth credential

                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if (task.isSuccessful()) {
                                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                            // When task is successful
                                            // Redirect to profile activity
                                            firebaseUser = firebaseAuth.getCurrentUser();
                                            if (isNewUser) {
                                                setName(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
                                            } else {
                                                //check if user is verified or not;

                                                db.collection("User").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            User user = documentSnapshot.toObject(User.class);
                                                            if (user != null && user.getUid().equals(firebaseUser.getUid())) {
                                                                if (user.getIsVerified().equals("1")) {
                                                                    System.out.println("IS VERIFIED STATUS" + user.getIsVerified());
                                                                    displayToast("Sign In Successfully, Welcome !");
                                                                    startActivity(new Intent(SignInActivity.this
                                                                            , HomeScreenDashboard.class)
                                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                                    finish();
                                                                } else {
                                                                    displayToast("Your account is waiting for verification with admin!");
                                                                }
                                                            } else {
                                                                displayToast("User data not found.");
                                                            }
                                                        } else {
                                                            displayToast("User document does not exist.");
                                                        }
                                                    }
                                                });

                                            }

                                        } else {
                                            displayToast("Authentication Failed: " + task.getException().getMessage());
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Firebase Authentication", "SignInWithCredential failed: " + e.getMessage());
                                        displayToast("Authentication Failed: " + e.getMessage());
                                    }
                                });

                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                    Log.e("Google Sign-In", "ApiException: " + e.getMessage());
                    displayToast("Google Sign-In ApiException: " + e.getMessage());
                }
            } else {
                displayToast("signInAccountTask is not successful");
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

    private void setName(String uId, String Name, String email) {
        HashMap<String, String> map = new HashMap<>();

        map.put("Name", Name);
        map.put("Uid", uId);
        map.put("isAdmin", "0");
        map.put("isVerified", "0");
        map.put("Email", email);
        // System.out.println("UID_SignUp_setName" + uId);
        db.collection("User").document(uId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                aLodingDialog.cancel();
                Toast.makeText(SignInActivity.this, "Register Successfully. Please wait till account is verified by Admin", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                aLodingDialog.cancel();
                Toast.makeText(SignInActivity.this, "Fail to Login!! Please try again\n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}