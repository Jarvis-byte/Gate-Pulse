package com.example.fmoapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeScreenDashboard extends AppCompatActivity implements LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    TextView Welcome_User, curr_date, curr_location, greeting_text;
    boolean emailLogin = false;
    LinearLayout enterSchedule, add_visitor, view_visitor;
    String name;
    ArrayList<User> Userlist = new ArrayList<>();
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;
    private ALodingDialog aLodingDialog;
    private LinearLayout checkSchedule;
    ImageView btn_logOut, profile_pic;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new__home_screen_dashboard_activity);
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
        curr_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calendar, 0, 0, 0);
        curr_date.setCompoundDrawablePadding(15);
        curr_date.setGravity(Gravity.CENTER_VERTICAL);
        aLodingDialog = new ALodingDialog(this);

        db = FirebaseFirestore.getInstance();
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
                Welcome_User.setText(namearr[0] + " !");
            }

        } else {
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                                Welcome_User.setText(namearr[0] + " !");
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
        getLocation();
        setGreetingMessage();

        btn_logOut = findViewById(R.id.btn_logOut);
        //Logout
        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenDashboard.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // aLodingDialog.show();

                        FirebaseAuth.getInstance().signOut();
                        dialog.dismiss();
                        Intent intent = new Intent(HomeScreenDashboard.this, SignInActivity.class);
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


        profile_pic = findViewById(R.id.profile_pic);
        if(emailLogin==false){
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profile_pic);
        }


    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Share your location")
                        .setMessage("We need to know your location to show you current temperature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeScreenDashboard.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
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

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            retrieveLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    @SuppressLint("MissingPermission")
    private void retrieveLocation() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double lat = location.getLatitude();
            double longi = location.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(lat, longi, 1);
                System.out.println("Address" + addressList.get(0).getLocality());
                curr_location.setText(addressList.get(0).getLocality() + ",\t" + addressList.get(0).getCountryName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // curr_location.setText(lat + "-" + longi);
            System.out.println("latitute" + lat + "----" + longi);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            retrieveLocation();
        } else {
            curr_location.setText("Permission Denied");
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
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