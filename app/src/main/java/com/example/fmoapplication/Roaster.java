package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Roaster extends AppCompatActivity {
    private RecyclerView dataRV;
    private ArrayList<Data> coursesArrayList;
    private RoasterRVAdapter courseRVAdapter;
    private FirebaseFirestore db;
    String choiceSpinner;
    private ALodingDialog aLodingDialog;
    ImageView back, imageView;
    TextView text_no_data;
    String User_Name;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roaster);
        dataRV = findViewById(R.id.idRVdata);
        imageView = findViewById(R.id.imageView);
        text_no_data = findViewById(R.id.text_no_data);
        aLodingDialog = new ALodingDialog(this);
        aLodingDialog.show();
        // initializing our variable for firebase
        // firestore and getting its instance.
        Intent intent = getIntent();
        User_Name = intent.getStringExtra("User_Name");

        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        System.out.println("ADMIN:-\t" + isAdmin);
        // creating our new array list
        coursesArrayList = new ArrayList<>();
        dataRV.setHasFixedSize(true);
        dataRV.setLayoutManager(new LinearLayoutManager(this));
        //+ coursesArrayList.size()
        System.out.println("ArrayList Size" + coursesArrayList.size());
        // adding our array list to our recycler view adapter class.
        courseRVAdapter = new RoasterRVAdapter(coursesArrayList, this, new RoasterRVAdapter.ItemClickListner() {
            @Override
            public void onItemClick(Data data, int position) {
                if (isAdmin) {
                    aLodingDialog.cancel();
                    String firstName = data.getName();
                    String namearr[] = firstName.split(" ");
                    verifyRoaster(data.getUid(), data.getDate(), namearr[0], data.getApprovalStatus(), data.getTime_FROM(), data.getTime_to(), position, data.getTimesheet_info());

//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(Roaster.this);
//                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_pin, null);
//                    EditText emailBox = dialogView.findViewById(R.id.emailBox);
//                    builder.setView(dialogView);
//                    AlertDialog dialog = builder.create();
//                    dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String userEmail = emailBox.getText().toString();
//                            aLodingDialog.show();
//                            if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
//                                aLodingDialog.cancel();
//                                Toast.makeText(Roaster.this, "Enter your Admin PIN", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            db.collection("Pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    if (!queryDocumentSnapshots.isEmpty()) {
//                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                                        for (DocumentSnapshot d : list) {
//                                            System.out.println(list);
//                                            Pin pin = d.toObject(Pin.class);
//                                            System.out.println("PIN" + pin.getAuthCode());
//                                            String enterpin = emailBox.getText().toString();
//
//                                            if (pin.getAuthCode().equals(enterpin)) {
//
//                                                // Toast.makeText(Roaster.this, "", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                aLodingDialog.cancel();
//                                                emailBox.setText("");
//                                                Toast.makeText(Roaster.this, "Wrong Pin!!! Please enter correct PIN", Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        }
//                                    }
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    aLodingDialog.cancel();
//                                    Toast.makeText(Roaster.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//
//                        }
//                    });
//                    dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            dialog.dismiss();
//                        }
//                    });
//                    if (dialog.getWindow() != null) {
//                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//                    }
//                    dialog.show();
                }
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

    private void verifyRoaster(String uid, String date, String name, int approvalStatus, String time_from, String time_to, int position, String timesheet_info_str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Roaster.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_schedule, null);
        TextView nameFor = dialogView.findViewById(R.id.Name);
        nameFor.setText(name);

        AutoCompleteTextView dropdown = dialogView.findViewById(R.id.spinner2);
        String[] items = new String[]{"Approved", "Rejected", "Delete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        choiceSpinner = String.valueOf(adapterView.getItemAtPosition(i));
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        choiceSpinner = String.valueOf(adapterView.getItemAtPosition(i));
                        break;
                    case 2:
                        // Whatever you want to happen when the second item gets selected
                        choiceSpinner = String.valueOf(adapterView.getItemAtPosition(i));
                        break;

                }
            }

        });
        // EditText emailBox = dialogView.findViewById(R.id.emailBox);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();


        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.show();
                String datearr[] = date.split("/");
                String finalDate = datearr[0] + datearr[1] + datearr[2] + time_from;


                int choice = 0;

                if (choiceSpinner.equals("Approved")) {
                    choice = 1;
                } else if (choiceSpinner.equals("Delete")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Roaster.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_sure_delete, null);
                    builder.setView(dialogView);
                    AlertDialog dialog1 = builder.create();
                    dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            aLodingDialog.show();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            System.out.println("Final Date\t" + finalDate);
                            DocumentReference docRef = db.collection("Data").document(name + finalDate);

                            CollectionReference newCollRef = db.collection("Archive_Roaster");
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    newCollRef.document(name + finalDate).set(documentSnapshot.getData());
                                    System.out.println("User Name in view Schedule" + User_Name);
                                    newCollRef.document(name + finalDate).update("DeletedBy", User_Name);
                                    docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                aLodingDialog.cancel();
                                                Toast.makeText(Roaster.this, "Your Data Deleted", Toast.LENGTH_SHORT).show();
                                                coursesArrayList.remove(position);
                                                courseRVAdapter.notifyDataSetChanged();
                                                dialog1.dismiss();
                                                dialog.dismiss();

                                            } else {
                                                aLodingDialog.cancel();
                                                Toast.makeText(Roaster.this, "Not Deleted", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            });

                            return;
                        }
                    });
                    dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                            aLodingDialog.cancel();
                        }
                    });
                    if (dialog1.getWindow() != null) {
                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    dialog1.show();
                    return;
                } else {
                    choice = 2;
                }

                Data data = new Data(uid, name, date, time_from, time_to, timesheet_info_str, true, choice);

                db.collection("Data").document(name + finalDate).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Roaster.this, "Your Data has been added", Toast.LENGTH_SHORT).show();
                        System.out.println("Position" + position);
                        coursesArrayList.set(position, data);
                        System.out.println("ArrayList Size" + coursesArrayList.size());
                        courseRVAdapter.notifyDataSetChanged();
                        aLodingDialog.cancel();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        aLodingDialog.cancel();
                        Toast.makeText(Roaster.this, "Fail to add data!! Please try again \n" + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.cancel();
                dialog.dismiss();
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();

    }

    public void getData() {

        //System.out.println("DATA from Firebase" + "\tCalled");

        db.collection("Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // if the snapshot is not empty we are
                    // hiding our progress bar and adding
                    // our data in a list.
                    aLodingDialog.cancel();
                    // image view gone and recycler view show
                    imageView.setVisibility(View.GONE);
                    text_no_data.setVisibility(View.GONE);
                    dataRV.setVisibility(View.VISIBLE);
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        Data data = d.toObject(Data.class);
                        coursesArrayList.add(data);
                    }
                    Collections.sort(coursesArrayList, new Comparator<Data>() {
                        @Override
                        public int compare(Data a, Data b) {
                            if (a.getDate().equals(b.getDate())) {
                                return b.getName().compareTo(a.getName());
                            } else {
                                return b.getDate().compareTo(a.getDate());
                            }
                        }
                    });
                    courseRVAdapter.notifyDataSetChanged();
                } else {

                    imageView.setVisibility(View.VISIBLE);
                    text_no_data.setVisibility(View.VISIBLE);
                    dataRV.setVisibility(View.GONE);
                    Glide.with(Roaster.this).load(R.drawable.empty_3).into(imageView);

                    aLodingDialog.cancel();
                    //Toast.makeText(Roaster.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Roaster.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}