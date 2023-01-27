package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewVisitor extends AppCompatActivity {
    private RecyclerView dataRV;
    private ArrayList<AddVisitor> coursesArrayList;
    private ViewVisitorRVAdapter courseRVAdapter;
    private FirebaseFirestore db;
    ImageView back,imageView;
    private ALodingDialog aLodingDialog;
    TextView text_no_data;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visitor);
        dataRV = findViewById(R.id.idRVdata);
        text_no_data = findViewById(R.id.text_no_data);
        imageView = findViewById(R.id.imageView);
        aLodingDialog = new ALodingDialog(this);
        aLodingDialog.show();
        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // creating our new array list
        coursesArrayList = new ArrayList<>();
        dataRV.setHasFixedSize(true);
        dataRV.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        courseRVAdapter = new ViewVisitorRVAdapter(coursesArrayList, this, new ViewVisitorRVAdapter.ItemClickListner() {
            @Override
            public void onItemClick(AddVisitor data, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewVisitor.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_pin, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        aLodingDialog.show();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            aLodingDialog.cancel();
                            Toast.makeText(ViewVisitor.this, "Enter your Admin PIN", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        db.collection("Pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        System.out.println(list);
                                        Pin pin = d.toObject(Pin.class);
                                        System.out.println("PIN" + pin.getAuthCode());
                                        String enterpin = emailBox.getText().toString();

                                        if (pin.getAuthCode().equals(enterpin)) {
                                            dialog.dismiss();
                                            aLodingDialog.cancel();
                                          //  String firstName = data.getName();
                                          //  String namearr[] = firstName.split(" ");
                                            // verifyRoaster(data.getUid(), data.getDate(), namearr[0], data.getApprovalStatus(), data.getTime_FROM(), data.getTime_to(), position);
                                            Toast.makeText(ViewVisitor.this, "SuccessFul", Toast.LENGTH_SHORT).show();
                                        } else {
                                            aLodingDialog.cancel();
                                            emailBox.setText("");
                                            Toast.makeText(ViewVisitor.this, "Wrong Pin!!! Please enter correct PIN", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                aLodingDialog.cancel();
                                Toast.makeText(ViewVisitor.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        });

        // setting adapter to our recycler view.
        dataRV.setAdapter(courseRVAdapter);
        getData();
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getData() {

        System.out.println("DATA from Firebase" + "\tCalled");

        db.collection("Visitor Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // if the snapshot is not empty we are
                    // hiding our progress bar and adding
                    // our data in a list.
                    imageView.setVisibility(View.GONE);
                    text_no_data.setVisibility(View.GONE);
                    dataRV.setVisibility(View.VISIBLE);

                    aLodingDialog.cancel();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        AddVisitor data = d.toObject(AddVisitor.class);
                        coursesArrayList.add(data);


                    }
                    Collections.sort(coursesArrayList, new Comparator<AddVisitor>() {
                        @Override
                        public int compare(AddVisitor a, AddVisitor b) {
                            if (a.getDate().equals(b.getDate())) {
                                return b.getNameOfVisitor().compareTo(a.getNameOfVisitor());
                            } else {
                                return b.getDate().compareTo(a.getDate());
                            }
                        }
                    });

                    courseRVAdapter.notifyDataSetChanged();
                } else {
                    aLodingDialog.cancel();
                    imageView.setVisibility(View.VISIBLE);
                    text_no_data.setVisibility(View.VISIBLE);
                    dataRV.setVisibility(View.GONE);
                    Glide.with(ViewVisitor.this).load(R.drawable.empty_3).into(imageView);
                 //   Toast.makeText(ViewVisitor.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewVisitor.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}