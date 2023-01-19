package com.example.fmoapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

//v
public class RoasterRVAdapter extends RecyclerView.Adapter<RoasterRVAdapter.ViewHolder> {
    // creating variables for our ArrayList and context
    private ArrayList<Data> roasterArrayList;
    private ItemClickListner mItemListener;
    private Context context;
    private ALodingDialog aLodingDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // creating constructor for our adapter class
    public RoasterRVAdapter(ArrayList<Data> roasterArrayList, Context context, ItemClickListner itemClickListner) {
        this.roasterArrayList = roasterArrayList;
        this.context = context;
        aLodingDialog = new ALodingDialog(context);
        this.mItemListener = itemClickListner;
    }

    @NonNull
    @Override
    public RoasterRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.roaster_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoasterRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Data data = roasterArrayList.get(position);
        holder.idTVempName.setText(data.getName());
        holder.idTVdate.setText("Date\t:- " + data.getDate());
        String time = "From " + data.getTime_FROM() + "\tTo " + data.getTime_to();
        holder.idTVCtime.setText(time);
        int approval = data.getApprovalStatus();
        if (approval == 1) {
            holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.remove_approved_round));
        } else if (approval == 2) {
            holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.remove_reject_round));
        }
        holder.itemView.setOnClickListener(v -> {
            mItemListener.onItemClick(data, position);
        });
//        holder.card_rv_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
//                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_pin, null);
//                EditText emailBox = dialogView.findViewById(R.id.emailBox);
//                builder.setView(dialogView);
//                AlertDialog dialog = builder.create();
//                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String userEmail = emailBox.getText().toString();
//                        aLodingDialog.show();
//                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
//                            aLodingDialog.cancel();
//                            Toast.makeText(v.getRootView().getContext(), "Enter your Admin PIN", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        db.collection("Pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                if (!queryDocumentSnapshots.isEmpty()) {
//                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                                    for (DocumentSnapshot d : list) {
//                                        System.out.println(list);
//                                        Pin pin = d.toObject(Pin.class);
//                                        System.out.println("PIN" + pin.getAuthCode());
//                                        String enterpin = emailBox.getText().toString();
//
//                                        if (pin.getAuthCode().equals(enterpin)) {
//                                            dialog.dismiss();
//                                            aLodingDialog.cancel();
//                                            verify(v);
//                                            Toast.makeText(v.getRootView().getContext(), "Yes!!!!", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            aLodingDialog.cancel();
//                                            emailBox.setText("");
//                                            Toast.makeText(v.getRootView().getContext(), "Wrong Pin!!! Please enter correct PIN", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    }
//                                }
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                aLodingDialog.cancel();
//                                Toast.makeText(v.getRootView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//
//                    }
//                });
//                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//                if (dialog.getWindow() != null) {
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//                }
//                dialog.show();
//
//            }
//        });
    }

//    private void verify(View v) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
//        View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_edit_schedule, null);
//        EditText emailBox = dialogView.findViewById(R.id.emailBox);
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String userEmail = emailBox.getText().toString();
//                aLodingDialog.show();
//                if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
//                    aLodingDialog.cancel();
//                    Toast.makeText(v.getRootView().getContext(), "Enter your Admin PIN", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                db.collection("Pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                System.out.println(list);
//                                Pin pin = d.toObject(Pin.class);
//                                System.out.println("PIN" + pin.getAuthCode());
//                                String enterpin = emailBox.getText().toString();
//
//                                if (pin.getAuthCode().equals(enterpin)) {
//                                    dialog.dismiss();
//                                    aLodingDialog.cancel();
//                                    verify(v);
//                                    Toast.makeText(v.getRootView().getContext(), "Yes!!!!", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    aLodingDialog.cancel();
//                                    emailBox.setText("");
//                                    Toast.makeText(v.getRootView().getContext(), "Wrong Pin!!! Please enter correct PIN", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        aLodingDialog.cancel();
//                        Toast.makeText(v.getRootView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//            }
//        });
//        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        }
//        dialog.show();
//    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return roasterArrayList.size();
    }

    public interface ItemClickListner {
        void onItemClick(Data data, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView idTVempName;
        private final TextView idTVdate;
        private final TextView idTVCtime;
        private final LinearLayout card_rv_layout;
        private final ImageView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            idTVempName = itemView.findViewById(R.id.idTVempName);
            idTVdate = itemView.findViewById(R.id.idTVdate);
            idTVCtime = itemView.findViewById(R.id.idTVCtime);
            card_rv_layout = itemView.findViewById(R.id.card_rv_layout);
            status = itemView.findViewById(R.id.status);
        }
    }
}
