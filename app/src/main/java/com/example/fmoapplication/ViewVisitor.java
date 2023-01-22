package com.example.fmoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class ViewVisitor extends AppCompatActivity {
    private RecyclerView dataRV;
    private ArrayList<AddVisitor> coursesArrayList;
    private ViewVisitorRVAdapter courseRVAdapter;
    private FirebaseFirestore db;
    ImageView back;
    private ALodingDialog aLodingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visitor);
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
        courseRVAdapter = new ViewVisitorRVAdapter(coursesArrayList, this);

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
                    Toast.makeText(ViewVisitor.this, "No data found in Database", Toast.LENGTH_SHORT).show();
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