package com.example.fmoapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class HomeScreenDashboard extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    TextView Welcome_User, curr_date, curr_location, greeting_text, textView3, textView4, textView2, textView1;
    boolean emailLogin = false;
    LinearLayout enterSchedule, add_visitor, view_visitor;
    String name;
    ArrayList<User> Userlist = new ArrayList<>();
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;
    private ALodingDialog aLodingDialog;
    private LinearLayout checkSchedule;
    ImageView btn_logOut, profile_pic, btn_menu;
    private final static int REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;

    //ConstraintLayout constraint;
    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new__home_screen_dashboard_activity);

        //checking for admin user
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myEdit.clear();
        myEdit.commit();
        db = FirebaseFirestore.getInstance();
        db.collection("User").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user.getIsAdmin().equals("1")) {
                    System.out.println("ADMIN USER");
                    myEdit.putBoolean("isAdmin", true);
                    myEdit.commit();
                }
            }
        });

        btn_menu = findViewById(R.id.btn_menu);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
                System.out.println("isAdmin123" + isAdmin);
                if (isAdmin) {
                    btn_menu.setVisibility(View.GONE);
                }
            }
        }, 1000);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myEdit.clear();
                myEdit.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeScreenDashboard.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Welcome_User = findViewById(R.id.Welcome_User);
        enterSchedule = findViewById(R.id.enterSchedule);
        checkSchedule = findViewById(R.id.checkSchedule);
        add_visitor = findViewById(R.id.add_visitor);
        view_visitor = findViewById(R.id.view_visitor);
        greeting_text = findViewById(R.id.greeting_text);


        curr_date = findViewById(R.id.curr_date);
        curr_location = findViewById(R.id.curr_location);
        curr_location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.placeholder, 0, 0, 0);
        curr_location.setCompoundDrawablePadding(10);
        curr_location.setGravity(Gravity.CENTER_VERTICAL);
        curr_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calendar_24, 0, 0, 0);
        curr_date.setCompoundDrawablePadding(15);
        curr_date.setGravity(Gravity.CENTER_VERTICAL);
        aLodingDialog = new ALodingDialog(this);


        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("password")) {
                System.out.println("User is signed in with email/password");
                emailLogin = true;
            }
        }


        GoogleSignInAccount account = null;
        if (emailLogin == false) {
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            account = GoogleSignIn.getLastSignedInAccount(this);

            if (account != null) {
                name = account.getDisplayName();
                String namearr[] = name.split(" ");
                Welcome_User.setText(name + " !");
            }

        } else {
            // String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println("UID ELSE" + Uid);
            db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // if the snapshot is not empty we are
                        // hiding our progress bar and adding
                        // our data in a list.

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        System.out.println("LIST" + list);
                        for (DocumentSnapshot d : list) {
                            //  d.getId();
                            User user = d.toObject(User.class);
                            System.out.println("USER ID" + d.getId());
                            if (Uid.equals(d.getId())) {
                                System.out.println(user.getName());
                                name = user.getName();
                                Userlist.add(user);
                                String namearr[] = name.split(" ");
                                Welcome_User.setText(name+ " !");
                            }

                        }


                    } else {
                        Toast.makeText(HomeScreenDashboard.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                    Toast.makeText(HomeScreenDashboard.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        enterSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreenDashboard.this, TimesheetActivity.class);
                startActivity(intent);

            }
        });
        checkSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Roaster.class);
                startActivity(intent);

            }
        });

        add_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreenDashboard.this, AddNewVisitor.class);
                startActivity(intent);
            }
        });
        view_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreenDashboard.this, ViewVisitor.class);
                startActivity(intent);
            }
        });

        fetchDate();
        // getLocation();
        setGreetingMessage();

        btn_logOut = findViewById(R.id.btn_logOut);
        //Logout
        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeScreenDashboard.this, PendingVerification.class);
                startActivity(intent);

                //finish();
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenDashboard.this);
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
//                builder.setView(dialogView);
//                AlertDialog dialog = builder.create();
//                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // aLodingDialog.show();
//
//                        FirebaseAuth.getInstance().signOut();
//                        dialog.dismiss();
//                        Intent intent = new Intent(HomeScreenDashboard.this, SignInActivity.class);
//                        startActivity(intent);
//                        finish();
//
//                    }
//                });
//                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//                if (dialog.getWindow() != null) {
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//                }
//                dialog.show();

            }
        });


        profile_pic = findViewById(R.id.profile_pic);
        if (emailLogin == false) {
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profile_pic);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText("Click here to Log Out")
                .setShape(ShapeType.CIRCLE)
                .setIdempotent(true)
                .setTarget(btn_logOut)
                .setUsageId("logout")
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String materialIntroViewId) {
                        new MaterialIntroView.Builder(HomeScreenDashboard.this)
                                .enableDotAnimation(true)
                                .enableIcon(false)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.MINIMUM)
                                .setDelayMillis(500)
                                .enableFadeAnimation(true)
                                .performClick(true)
                                .setInfoText("Click here to input your timesheet")
                                .setShape(ShapeType.CIRCLE)
                                .setIdempotent(true)
                                .setTarget(enterSchedule)
                                .setUsageId("intro_card")
                                .setListener(new MaterialIntroListener() {
                                    @Override
                                    public void onUserClicked(String materialIntroViewId) {
                                        new MaterialIntroView.Builder(HomeScreenDashboard.this)
                                                .enableDotAnimation(true)
                                                .enableIcon(false)
                                                .setFocusGravity(FocusGravity.CENTER)
                                                .setFocusType(Focus.MINIMUM)
                                                .setDelayMillis(500)
                                                .enableFadeAnimation(true)
                                                .performClick(true)
                                                .setInfoText("Click here to input Visitor details")
                                                .setShape(ShapeType.CIRCLE)
                                                .setTarget(add_visitor)
                                                .setUsageId("visitor") //THIS SHOULD BE UNIQUE ID
                                                .setListener(new MaterialIntroListener() {
                                                    @Override
                                                    public void onUserClicked(String materialIntroViewId) {
                                                        new MaterialIntroView.Builder(HomeScreenDashboard.this)
                                                                .enableDotAnimation(true)
                                                                .enableIcon(false)
                                                                .setFocusGravity(FocusGravity.CENTER)
                                                                .setFocusType(Focus.MINIMUM)
                                                                .setDelayMillis(500)
                                                                .enableFadeAnimation(true)
                                                                .performClick(true)
                                                                .setInfoText("View Your Schedule here")
                                                                .setShape(ShapeType.CIRCLE)
                                                                .setIdempotent(true)
                                                                .setTarget(checkSchedule)
                                                                .setUsageId("schedule")
                                                                .setListener(new MaterialIntroListener() {
                                                                    @Override
                                                                    public void onUserClicked(String materialIntroViewId) {
                                                                        new MaterialIntroView.Builder(HomeScreenDashboard.this)
                                                                                .enableDotAnimation(true)
                                                                                .enableIcon(false)
                                                                                .setFocusGravity(FocusGravity.CENTER)
                                                                                .setFocusType(Focus.MINIMUM)
                                                                                .setDelayMillis(500)
                                                                                .enableFadeAnimation(true)
                                                                                .performClick(true)
                                                                                .setInfoText("Click here to View Visitor details")
                                                                                .setShape(ShapeType.CIRCLE)
                                                                                .setTarget(view_visitor)
                                                                                .setUsageId("visitor_schedule")
                                                                                .show();

                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                })
                                                .show();
                                    }
                                })//THIS SHOULD BE UNIQUE ID
                                .show();
                    }
                }).show();
        textView3 = findViewById(R.id.textView3);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView4 = findViewById(R.id.textView4);
        ConstraintLayout constraint = findViewById(R.id.constraint);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        System.out.println("Height" + "\t" + height);

        if (width <= 720) {
            System.out.println("LOL");
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
            textView4.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
            //   curr_date.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
            curr_location.setText("Asansol, India");
//            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(50, 50);
//            constraint.setLayoutParams(params);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("PushNotifications");

//        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        db.collection("User").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                User user = documentSnapshot.toObject(User.class);
//                if (user.getIsAdmin().equals("1")) {
//                    myEdit.putBoolean("isAdmin", true);
//                    myEdit.commit();
//                }
//            }
//        });

    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         //   System.out.println("Location first if");
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            System.out.println("Location" + location);

                            try {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                                StrictMode.setThreadPolicy(policy);
                                Geocoder geocoder = new Geocoder(HomeScreenDashboard.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                curr_location.setText(addresses.get(0).getLocality() + ",\t" + addresses.get(0).getCountryName());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
//            fusedLocationProviderClient.getCurrentLocation()
//                    .addOnSuccessListener(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            System.out.println("Location before if" + location);
//                            if (location != null) {
//                                System.out.println("Location" + location);
//
//                                try {
//                                    Geocoder geocoder = new Geocoder(HomeScreenDashboard.this, Locale.getDefault());
//                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//
//                                    curr_location.setText(addresses.get(0).getAddressLine(0));
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//
//
//                            }
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            System.out.println("Error Location"+e.getMessage());
//                        }
//                    });


        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Location");
        } else {

            askPermission();


        }
    }

    private void askPermission() {

        ActivityCompat.requestPermissions(HomeScreenDashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) || grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {


                getLastLocation();

            } else {

                curr_location.setText("Permission Denied");
                Toast.makeText(HomeScreenDashboard.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();

            }


        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void fetchDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String day = dayFormat.format(calendar.getTime());
        String date = dateFormat.format(calendar.getTime());
        String suffix;
        int dayToday = calendar.get(Calendar.DAY_OF_MONTH);
        if (dayToday >= 11 && dayToday <= 13) {
            suffix = "th";
        } else {
            switch (dayToday % 10) {
                case 1:
                    suffix = "st";
                    break;
                case 2:
                    suffix = "nd";
                    break;
                case 3:
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
            }
        }

        curr_date.setText(day + ",\t" + date + suffix);
        // System.out.println("Today is " + day + " and the date is " + date + suffix);
    }

    public void setGreetingMessage() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {

            greeting_text.setText(" Good Morning");

        //   greeting_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sunrise, 0, 0, 0);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting_text.setText(" Good Afternoon");


         //   greeting_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.afternoon, 0, 0, 0);
        } else if (timeOfDay >= 16 && timeOfDay < 21) {

            greeting_text.setText(" Good Evening");


           // greeting_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sunset, 0, 0, 0);

        } else if (timeOfDay >= 21 && timeOfDay < 24) {

            greeting_text.setText(" Good Night");
           // greeting_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.night, 0, 0, 0);

        }
    }
}