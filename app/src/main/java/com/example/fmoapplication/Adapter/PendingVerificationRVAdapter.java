package com.example.fmoapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmoapplication.R;
import com.example.fmoapplication.UI.PendingVerification;
import com.example.fmoapplication.Model.User;

import java.util.ArrayList;

public class PendingVerificationRVAdapter extends RecyclerView.Adapter<PendingVerificationRVAdapter.ViewHolder> {
    private ArrayList<User> userDataList;
    private Context context;
    private RoasterRVAdapter.ItemClickListner mItemListener;

    public PendingVerificationRVAdapter(ArrayList<User> userDataList, Context context, RoasterRVAdapter.ItemClickListner mItemListener) {
        this.userDataList = userDataList;
        this.context = context;
        this.mItemListener = mItemListener;
    }

    public PendingVerificationRVAdapter() {
    }

    @NonNull
    @Override
    public PendingVerificationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PendingVerificationRVAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.pending_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PendingVerificationRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = userDataList.get(position);
        holder.idTVempName.setText(user.getName());
        holder.idTVemail.setText(user.getEmail());

        holder.Check_box_verify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                PendingVerification pendingVerification = new PendingVerification();
                pendingVerification.addSeen(isChecked, holder.Check_box_verify, context, user, position, userDataList, PendingVerificationRVAdapter.this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView idTVempName;
        private final TextView idTVemail;
        private final LinearLayout card_rv_layout;
        private final CheckBox Check_box_verify;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            idTVempName = itemView.findViewById(R.id.idTVempName);
            card_rv_layout = itemView.findViewById(R.id.card_rv_layout);
            idTVemail = itemView.findViewById(R.id.idTVemail);
            Check_box_verify = itemView.findViewById(R.id.Check_box_verify);
        }
    }
}
