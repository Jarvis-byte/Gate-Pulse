package com.example.fmoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roaster);
        dataRV = findViewById(R.id.idRVdata);

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
        courseRVAdapter = new RoasterRVAdapter(coursesArrayList, this, new RoasterRVAdapter.ItemClickListner() {
            @Override
            public void onItemClick(Data data, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Roaster.this);
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
                            Toast.makeText(Roaster.this, "Enter your Admin PIN", Toast.LENGTH_SHORT).show();
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
                                            String firstName = data.getName();
                                            String namearr[] = firstName.split(" ");
                                            verifyRoaster(data.getUid(), data.getDate(), namearr[0], data.getApprovalStatus(), data.getTime_FROM(), data.getTime_to(), position);
                                            Toast.makeText(Roaster.this, position + "Yes!!!!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            aLodingDialog.cancel();
                                            emailBox.setText("");
                                            Toast.makeText(Roaster.this, "Wrong Pin!!! Please enter correct PIN", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                aLodingDialog.cancel();
                                Toast.makeText(Roaster.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    private void verifyRoaster(String uid, String date, String name, int approvalStatus, String time_from, String time_to, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Roaster.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_schedule, null);
        Spinner dropdown = dialogView.findViewById(R.id.spinner1);
        String[] items = new String[]{"Approved", "Rejected"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        choiceSpinner = String.valueOf(adapterView.getItemAtPosition(i));
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        choiceSpinner = String.valueOf(adapterView.getItemAtPosition(i));
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // EditText emailBox = dialogView.findViewById(R.id.emailBox);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();


        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int choice = 0;
                if (choiceSpinner.equals("Approved")) {
                    choice = 1;
                } else {
                    choice = 2;
                }
                String datearr[] = date.split("/");
                String finalDate = datearr[0] + datearr[1] + datearr[2];
                Data data = new Data(uid, name, date, time_from, time_to, true, choice);

                db.collection("Data").document(name + finalDate).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Roaster.this, "Your Data has been added", Toast.LENGTH_SHORT).show();
                        System.out.println("Position" + position);
                        coursesArrayList.set(position, data);
                        System.out.println("ArrayList Size" + coursesArrayList.size());
                        courseRVAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Roaster.this, "Fail to add data!! Please try again \n" + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();

    }

    public void getData() {

        System.out.println("DATA from Firebase" + "\tCalled");

        db.collection("Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // if the snapshot is not empty we are
                    // hiding our progress bar and adding
                    // our data in a list.
                    aLodingDialog.cancel();
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
                    aLodingDialog.cancel();
                    Toast.makeText(Roaster.this, "No data found in Database", Toast.LENGTH_SHORT).show();
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