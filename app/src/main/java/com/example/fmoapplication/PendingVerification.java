package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PendingVerification extends AppCompatActivity {
    private RecyclerView dataRV;
    private ArrayList<User> UserArrayList;
    private PendingVerificationRVAdapter pendingVerificationRVAdapter;
    private FirebaseFirestore db;
    private ALodingDialog aLodingDialog;
    ImageView back, imageView;
    TextView text_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification);
        dataRV = findViewById(R.id.idRVdata);
        imageView = findViewById(R.id.imageView);
        text_no_data = findViewById(R.id.text_no_data);
        db = FirebaseFirestore.getInstance();
        UserArrayList = new ArrayList<>();
        dataRV.setHasFixedSize(true);
        aLodingDialog = new ALodingDialog(this);
        aLodingDialog.show();
        pendingVerificationRVAdapter = new PendingVerificationRVAdapter(UserArrayList, this, new RoasterRVAdapter.ItemClickListner() {
            @Override
            public void onItemClick(Data data, int position) {
                //click
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
                        assert user != null;
                        if (user.getIsVerified().equals("0")) {
                            UserArrayList.add(user);
                        }
                    }
                    if(UserArrayList.isEmpty()){
                        imageView.setVisibility(View.VISIBLE);
                        text_no_data.setVisibility(View.VISIBLE);
                        dataRV.setVisibility(View.GONE);
                        Glide.with(PendingVerification.this).load(R.drawable.empty_3).into(imageView);

                    }

//                    Collections.sort(coursesArrayList, new Comparator<Data>() {
//                        @Override
//                        public int compare(Data a, Data b) {
//                            if (a.getDate().equals(b.getDate())) {
//                                return b.getName().compareTo(a.getName());
//                            } else {
//                                return b.getDate().compareTo(a.getDate());
//                            }
//                        }
//                    });
                    pendingVerificationRVAdapter.notifyDataSetChanged();
                } else {
                    aLodingDialog.cancel();
                    imageView.setVisibility(View.VISIBLE);
                    text_no_data.setVisibility(View.VISIBLE);
                    dataRV.setVisibility(View.GONE);
                    Glide.with(PendingVerification.this).load(R.drawable.empty_3).into(imageView);
                    Toast.makeText(PendingVerification.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PendingVerification.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addSeen(boolean isChecked, CheckBox checkbox, Context context, User data, int position, ArrayList<User> userDataList, PendingVerificationRVAdapter pendingVerificationRVAdapter1) {
        ALodingDialog LodingDialog = new ALodingDialog(context);
        LodingDialog.show();
        db = FirebaseFirestore.getInstance();
        // System.out.println("Posistion" + position);
        if (isChecked) {
            // checkbox.setChecked(false);
            addDataToFirestore(data.getUid(), data.getName(), data.getEmail(), LodingDialog, checkbox, context, position, userDataList, pendingVerificationRVAdapter1);

        }


    }

    private void addDataToFirestore(String uId, String Name, String email, ALodingDialog lodingDialog, CheckBox checkbox, Context context, int position, ArrayList<User> userDataList, PendingVerificationRVAdapter pendingVerificationRVAdapter1) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Name", Name);
        map.put("Uid", uId);
        map.put("isAdmin", "0");
        map.put("isVerified", "1");
        map.put("Email", email);

        db.collection("User").document(uId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                lodingDialog.cancel();
                System.out.println("Position verified\t" + position);
                userDataList.remove(position);
                pendingVerificationRVAdapter1.notifyDataSetChanged();
                Toast.makeText(context, Name + " is now a verified user", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                aLodingDialog.cancel();
                Toast.makeText(context, "Fail to make \t" + Name + "a verified user as,  " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
