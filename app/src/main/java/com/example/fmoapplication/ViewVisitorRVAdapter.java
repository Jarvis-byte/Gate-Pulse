package com.example.fmoapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewVisitorRVAdapter extends RecyclerView.Adapter<ViewVisitorRVAdapter.ViewHolder> {
    // creating variables for our ArrayList and context
    private ArrayList<AddVisitor> roasterArrayList;
    private Context context;

    // creating constructor for our adapter class
    public ViewVisitorRVAdapter(ArrayList<AddVisitor> roasterArrayList, Context context) {
        this.roasterArrayList = roasterArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewVisitorRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewVisitorRVAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.visitor_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewVisitorRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        AddVisitor data = roasterArrayList.get(position);
        holder.idTVVisitorName.setText("Visitor\t:- " + data.getNameOfVisitor());
        holder.idTVpurpose.setText("Purpose\t:- " + data.getPurposeOfvisit());
        holder.idTVdate.setText("Date\t:- " + data.getDate());
        String time = "From " + data.getTime_from() + "\tTo " + data.getTime_to();
        holder.idTVCtime.setText(time);
        holder.idTVsubmittedBy.setText("Submitted By\t:- " + data.getNameofsubmitor());
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return roasterArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView idTVVisitorName;
        private final TextView idTVpurpose;
        private final TextView idTVdate;
        private final TextView idTVCtime;
        private final TextView idTVsubmittedBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            idTVVisitorName = itemView.findViewById(R.id.idTVVisitorName);
            idTVpurpose = itemView.findViewById(R.id.idTVpurpose);
            idTVdate = itemView.findViewById(R.id.idTVdate);
            idTVCtime = itemView.findViewById(R.id.idTVCtime);
            idTVsubmittedBy = itemView.findViewById(R.id.idTVsubmittedBy);
        }
    }
}
