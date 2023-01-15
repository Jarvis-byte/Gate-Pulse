package com.example.fmoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Roaster extends AppCompatActivity {
    private RecyclerView dataRV;
    private ArrayList<Data> coursesArrayList;
    private RoasterRVAdapter courseRVAdapter;
    private FirebaseFirestore db;

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
        courseRVAdapter = new RoasterRVAdapter(coursesArrayList, this);

        // setting adapter to our recycler view.
        dataRV.setAdapter(courseRVAdapter);
        getData();
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