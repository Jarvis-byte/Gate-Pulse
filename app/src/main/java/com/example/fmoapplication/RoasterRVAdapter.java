package com.example.fmoapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
        holder.idTVdate.setText(data.getDate());
        String time = data.getTime_FROM() + "\t | \t" + data.getTime_to();
        holder.idTVCtime.setText(time);
        String info = data.getTimesheet_info();
        holder.idTVspcl_pur.setText(info);
        int approval = data.getApprovalStatus();
        if (approval == 1) {
            holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.remove_approved_round));
        } else if (approval == 2) {
            holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.remove_reject_round));
        }
        holder.itemView.setOnClickListener(v -> {
            mItemListener.onItemClick(data, position);
        });

    }


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
        private final TextView idTVspcl_pur;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            idTVempName = itemView.findViewById(R.id.idTVempName);
            idTVdate = itemView.findViewById(R.id.idTVdate);
            idTVCtime = itemView.findViewById(R.id.idTVCtime);
            card_rv_layout = itemView.findViewById(R.id.card_rv_layout);
            status = itemView.findViewById(R.id.status);
            idTVspcl_pur = itemView.findViewById(R.id.idTVspcl_pur);
        }
    }
}
