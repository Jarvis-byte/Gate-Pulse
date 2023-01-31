package com.example.fmoapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        aLodingDialog = new ALodingDialog(this);
        aLodingDialog.show();
        db = FirebaseFirestore.getInstance();
        UserArrayList = new ArrayList<>();
        dataRV.setHasFixedSize(true);


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
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // if the snapshot is not empty we are
                    // hiding our progress bar and adding
                    // our data in a list.
                    aLodingDialog.cancel();
                    // image view gone and recycler view show
//                    imageView.setVisibility(View.GONE);
//                    text_no_data.setVisibility(View.GONE);
//                    dataRV.setVisibility(View.VISIBLE);
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        User user = d.toObject(User.class);
                        if(user.getIsVerified().equals("0"))
                        UserArrayList.add(user);

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

//                    imageView.setVisibility(View.VISIBLE);
//                    text_no_data.setVisibility(View.VISIBLE);
//                    dataRV.setVisibility(View.GONE);
//                    Glide.with(PendingVerification.this).load(R.drawable.empty_3).into(imageView);

                    aLodingDialog.cancel();
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
}
