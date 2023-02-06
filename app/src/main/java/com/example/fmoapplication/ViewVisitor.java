package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    ImageView back, imageView;
    private ALodingDialog aLodingDialog;
    TextView text_no_data;
    LayoutInflater inflater;
    String User_Name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visitor);
        Intent intent = getIntent();
        User_Name = intent.getStringExtra("User_Name");
        System.out.println("User Name" + User_Name);
        dataRV = findViewById(R.id.idRVdata);
        text_no_data = findViewById(R.id.text_no_data);
        imageView = findViewById(R.id.imageView);
        aLodingDialog = new ALodingDialog(this);
        aLodingDialog.show();
        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();
        inflater = getWindow().getLayoutInflater();
        // creating our new array list
        coursesArrayList = new ArrayList<>();
        dataRV.setHasFixedSize(true);
        dataRV.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ViewVisitor.this);
//        SharedPreferences.Editor editor = prefs.edit();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        System.out.println("ADMIN:-\t" + isAdmin);


        courseRVAdapter = new ViewVisitorRVAdapter(coursesArrayList, isAdmin, User_Name, this, new ViewVisitorRVAdapter.ItemClickListner() {
            @Override
            public void onItemClick(AddVisitor data, int position) {
                if (isAdmin) {
                    String uid = coursesArrayList.get(position).getUid();
                    String visitorName = coursesArrayList.get(position).getNameOfVisitor();
                    String NameOfSub = coursesArrayList.get(position).getNameofsubmitor();
                    String docname[] = NameOfSub.split(" ");
                    String finalDocName = uid + visitorName + docname[0];
                    FirebaseFirestore db = FirebaseFirestore.getInstance();


                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewVisitor.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_sure_delete, null);
                    builder.setView(dialogView);
                    AlertDialog dialog1 = builder.create();

                    dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DocumentReference docRef = db.collection("Visitor Data").document(finalDocName);
                            CollectionReference newCollRef = db.collection("Archive_Visitor");
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    // Copy the document to the new collection
                                    newCollRef.document(finalDocName).set(documentSnapshot.getData());
                                    newCollRef.document(finalDocName).update("DeletedBy", User_Name);
                                    docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            coursesArrayList.remove(position);
                                            courseRVAdapter.notifyDataSetChanged();
                                            dialog1.dismiss();
                                            aLodingDialog.cancel();

                                            if (coursesArrayList.size() == 0) {
                                                imageView.setVisibility(View.VISIBLE);
                                                text_no_data.setVisibility(View.VISIBLE);
                                                dataRV.setVisibility(View.GONE);
                                                Glide.with(ViewVisitor.this).load(R.drawable.empty_3).into(imageView);
                                            }
                                            Toast.makeText(ViewVisitor.this, "Data has been successfully deleted", Toast.LENGTH_SHORT).show();
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

    public void addSeen(boolean isChecked, CheckBox checkbox, Context context, AddVisitor data, int position, ArrayList<AddVisitor> roasterArrayList, ImageView checkBox_Seen, String User_name_Seen) {
        ALodingDialog LodingDialog = new ALodingDialog(context);
        LodingDialog.show();
        db = FirebaseFirestore.getInstance();
        System.out.println("SEEN BY " + User_Name);
        if (isChecked) {
            addDataToFirestore(data.getUid(), data.getNameofsubmitor(), data.getNameOfVisitor(), data.getPurposeOfvisit(), data.getDate(), data.getTime_from(), data.getTime_to(), 1, context, LodingDialog, checkBox_Seen, checkbox, User_name_Seen);
        }


    }

    private void addDataToFirestore(String uid, String nameofsubmitor, String nameOfVisitor, String purposeOfvisit, String date, String time_from, String time_to, int i, Context context, ALodingDialog lodingDialog, ImageView checkBox_Seen, CheckBox checkbox, String User_name_Seen) {
        AddVisitor addVisitor = new AddVisitor(uid, nameofsubmitor, nameOfVisitor, purposeOfvisit, date, time_from, time_to, i, User_name_Seen);
        System.out.println("User Name seen" + User_name_Seen);
        String finalDocName = uid + nameOfVisitor + nameofsubmitor;
        // db.collection("Visitor Data").document(finalDocName).set("seenBy", User_Name);
        db.collection("Visitor Data").document(finalDocName).set(addVisitor).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                lodingDialog.cancel();
                checkBox_Seen.setVisibility(View.VISIBLE);
                checkbox.setVisibility(View.GONE);
                Toast.makeText(context, "You have seen :- " + nameOfVisitor, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                lodingDialog.cancel();
                Toast.makeText(context, "Fail to add data!! Please try again \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean performActivity() {
        return true;
    }
}