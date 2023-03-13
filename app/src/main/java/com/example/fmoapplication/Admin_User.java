package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Admin_User extends AppCompatActivity {
    private RecyclerView dataRV;
    private ArrayList<User> UserArrayList;
    private AdminUserRVAdapter pendingVerificationRVAdapter;
    private FirebaseFirestore db;
    private ALodingDialog aLodingDialog;
    ImageView back, imageView;
    TextView text_no_data;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user2);
        dataRV = findViewById(R.id.idRVdata);
        imageView = findViewById(R.id.imageView);
        text_no_data = findViewById(R.id.text_no_data);
        db = FirebaseFirestore.getInstance();
        UserArrayList = new ArrayList<>();
        dataRV.setHasFixedSize(true);
        aLodingDialog = new ALodingDialog(this);
        aLodingDialog.show();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UserID = currentFirebaseUser.getUid();
        pendingVerificationRVAdapter = new AdminUserRVAdapter(UserArrayList, this, new AdminUserRVAdapter.ItemClickListner() {
            @Override
            public void onItemClick(User data, int position) {
                //click
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String UserIDClick = currentFirebaseUser.getUid();
                System.out.println("User if from click " + UserIDClick);
                if (UserIDClick.equals("xIci8Tdyeaf4Kz7nl6rYDEzY2Qy2") || UserIDClick.equals("4j5QWfHitnYGRFa9P8Q0wGHhfi13")) {
                    if (UserArrayList.get(position).getIsAdmin().equals("1")) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_User.this);
                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_sure_admin, null);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                aLodingDialog.show();
                                db.collection("User").document(UserArrayList.get(position).getUid()).update("isAdmin", "0").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        aLodingDialog.cancel();
                                        data.setIsAdmin("0");
                                        UserArrayList.set(position, data);
                                        pendingVerificationRVAdapter.notifyDataSetChanged();
                                        Toast.makeText(Admin_User.this, "User " + UserArrayList.get(position).getName() + " has been removed from an Admin User", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(Admin_User.this, "Failed to remove!!! Please try again", Toast.LENGTH_SHORT).show();
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
                        return;
                    }
                }
            }
        });

        dataRV.setAdapter(pendingVerificationRVAdapter);
        dataRV.setLayoutManager(new LinearLayoutManager(this));
        getData();
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getData() {
        db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    aLodingDialog.cancel();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        User user = d.toObject(User.class);

                        //  System.out.println("USER ID IN ADMIN :- " + user.getUid());
                        assert user != null;
                        //x4eWms44LtYeykcpoVw7YD787tD2
                        if ((user.getIsAdmin().equals("0") && user.getIsVerified().equals("1")) || (user.getIsAdmin().equals("1") && user.getIsVerified().equals("1"))) {
                            if ((user.getUid().equals("XwTOMUYZGuRS1ixWCUP7ZSblCP73")) || (user.getUid().equals("444"))) {
                                System.out.println("USER ID IN ADMIN :- " + user.getUid());

                            } else {
                                UserArrayList.add(user);
                            }

                        }
                    }
                    if (UserArrayList.isEmpty()) {
                        imageView.setVisibility(View.VISIBLE);
                        text_no_data.setVisibility(View.VISIBLE);
                        dataRV.setVisibility(View.GONE);
                        Glide.with(Admin_User.this).load(R.drawable.empty_3).into(imageView);
                    }

                    Collections.sort(UserArrayList, new Comparator<User>() {
                        @Override
                        public int compare(User a, User b) {
                            return a.getIsAdmin().compareTo(b.getIsAdmin());
                        }
                    });
                    pendingVerificationRVAdapter.notifyDataSetChanged();
                } else {
                    aLodingDialog.cancel();
                    imageView.setVisibility(View.VISIBLE);
                    text_no_data.setVisibility(View.VISIBLE);
                    dataRV.setVisibility(View.GONE);
                    Glide.with(Admin_User.this).load(R.drawable.empty_3).into(imageView);
                    Toast.makeText(Admin_User.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Admin_User.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addSeen(boolean isChecked, CheckBox checkbox, Context context, User data, int position, ArrayList<User> userDataList, AdminUserRVAdapter pendingVerificationRVAdapter1,ImageView checkBox_Seen) {
        ALodingDialog LodingDialog = new ALodingDialog(context);
        db = FirebaseFirestore.getInstance();

        if (isChecked) {
            LodingDialog.show();
            addDataToFirestore(data.getUid(), data.getName(), data.getEmail(), LodingDialog, checkbox, context, position, userDataList, pendingVerificationRVAdapter1, checkBox_Seen);

        }


    }

    private void addDataToFirestore(String uId, String Name, String email, ALodingDialog lodingDialog, CheckBox checkbox, Context context, int position, ArrayList<User> userDataList, AdminUserRVAdapter pendingVerificationRVAdapter1,ImageView checkBox_Seen) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Name", Name);
        map.put("Uid", uId);
        map.put("isAdmin", "0");
        map.put("isVerified", "1");
        map.put("Email", email);

        db.collection("User").document(uId).update("isAdmin", "1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                lodingDialog.cancel();
                checkBox_Seen.setVisibility(View.VISIBLE);
                checkbox.setVisibility(View.GONE);
              //  System.out.println("Position verified\t" + position);
               // pendingVerificationRVAdapter1.notifyDataSetChanged();
                Toast.makeText(context, Name + " is now an Admin user", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                aLodingDialog.cancel();
                Toast.makeText(context, "Fail to make \t" + Name + "an Admin user as,  " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}