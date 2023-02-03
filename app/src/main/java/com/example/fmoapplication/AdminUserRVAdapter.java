package com.example.fmoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminUserRVAdapter extends RecyclerView.Adapter<AdminUserRVAdapter.ViewHolder> {

    private ArrayList<User> userDataList;
    private Context context;
    private RoasterRVAdapter.ItemClickListner mItemListener;

    public AdminUserRVAdapter(ArrayList<User> userDataList, Context context, RoasterRVAdapter.ItemClickListner mItemListener) {
        this.userDataList = userDataList;
        this.context = context;
        this.mItemListener = mItemListener;
    }

    public AdminUserRVAdapter() {
    }

    @NonNull
    @Override
    public AdminUserRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminUserRVAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = userDataList.get(position);
        holder.idTVempName.setText(user.getName());
        holder.idTVemail.setText(user.getEmail());

        int isadmin = Integer.parseInt(user.getIsAdmin());
        if (isadmin == 1) {
            holder.seenImage.setVisibility(View.VISIBLE);
            holder.Check_box_verify.setVisibility(View.GONE);
        }

        holder.Check_box_verify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Admin_User admin_user = new Admin_User();
                admin_user.addSeen(isChecked, holder.Check_box_verify, context, user, position, userDataList, AdminUserRVAdapter.this, holder.seenImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView idTVempName;
        private final TextView idTVemail;
        private final LinearLayout card_rv_layout;
        private final CheckBox Check_box_verify;
        public final ImageView seenImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTVempName = itemView.findViewById(R.id.idTVempName);
            card_rv_layout = itemView.findViewById(R.id.card_rv_layout);
            idTVemail = itemView.findViewById(R.id.idTVemail);
            Check_box_verify = itemView.findViewById(R.id.Check_box_verify);
            seenImage = itemView.findViewById(R.id.seenImage);

        }
    }
}
