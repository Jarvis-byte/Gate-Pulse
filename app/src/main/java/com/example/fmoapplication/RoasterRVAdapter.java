package com.example.fmoapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//v
public class RoasterRVAdapter extends RecyclerView.Adapter<RoasterRVAdapter.ViewHolder> {
    // creating variables for our ArrayList and context
    private ArrayList<Data> roasterArrayList;
    private Context context;

    // creating constructor for our adapter class
    public RoasterRVAdapter(ArrayList<Data> roasterArrayList, Context context) {
        this.roasterArrayList = roasterArrayList;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return roasterArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView idTVempName;
        private final TextView idTVdate;
        private final TextView idTVCtime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            idTVempName = itemView.findViewById(R.id.idTVempName);
            idTVdate = itemView.findViewById(R.id.idTVdate);
            idTVCtime = itemView.findViewById(R.id.idTVCtime);
        }
    }
}
