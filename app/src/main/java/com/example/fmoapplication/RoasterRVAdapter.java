package com.example.fmoapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

//v
public class RoasterRVAdapter extends RecyclerView.Adapter<RoasterRVAdapter.ViewHolder> {
    // creating variables for our ArrayList and context
    private ArrayList<Data> roasterArrayList;
    private ItemClickListner mItemListener;
    private Context context;
    private ALodingDialog aLodingDialog;
    private FirebaseFirestore db;
    private String Uid;
    final Calendar calendar = Calendar.getInstance();
    Calendar calendar1 = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH) + 1;
    int hour1;
    int minute1;
    int hour2;
    int minute2;
    final int day = calendar.get(Calendar.DAY_OF_MONTH);


    // creating constructor for our adapter class
    public RoasterRVAdapter(ArrayList<Data> roasterArrayList, Context context, ItemClickListner itemClickListner, String Uid) {
        this.roasterArrayList = roasterArrayList;
        this.context = context;
        aLodingDialog = new ALodingDialog(context);
        this.mItemListener = itemClickListner;
        this.Uid = Uid;
        db = FirebaseFirestore.getInstance();
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
        if (info.equals("")) {
            holder.idTVspcl_pur.setText("N/A");

        } else
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
        if (Uid.equals(data.getUid())) {
            holder.btn_edit.setVisibility(View.VISIBLE);
        }
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_edit_timesheet, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                EditText edit_text_date = dialogView.findViewById(R.id.edit_text_date);
                EditText edit_text_time_from = dialogView.findViewById(R.id.edit_text_time_from);
                EditText edit_text_time_to = dialogView.findViewById(R.id.edit_text_time_to);
                EditText edit_text_desc = dialogView.findViewById(R.id.edit_text_desc);


                edit_text_date.setText(data.getDate());
                edit_text_time_from.setText(data.getTime_FROM());
                edit_text_time_to.setText(data.getTime_to());
                edit_text_desc.setText(info);

                String oldDate = data.getDate();
                String oldTime_from = data.getTime_FROM();

                edit_text_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog dialog = new DatePickerDialog(context, R.style.TimePickerTheme, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                i1 = i1 + 1;
                                String date = i2 + "/" + i1 + "/" + i;
                                edit_text_date.setText(date);


                            }
                        }, year, month - 1, day);
                        Calendar calendar = Calendar.getInstance();
                        long currentTime = calendar.getTimeInMillis();
                        dialog.getDatePicker().setMinDate(currentTime);

                        // Set the maximum date to tomorrow's date
                        calendar.add(Calendar.DAY_OF_MONTH, 3);
                        long tomorrowTime = calendar.getTimeInMillis();
                        dialog.getDatePicker().setMaxDate(tomorrowTime);
                        dialog.show();
                    }
                });

                edit_text_time_from.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                calendar1.set(0, 0, 0, i, i1);
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    hour1 = timePicker.getHour();
                                }
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    minute1 = timePicker.getMinute();
                                }
                                edit_text_time_from.setText(sdf.format(calendar1.getTime()));
                            }
                        }, 12, 0, false);
                        timePickerDialog.show();
                    }
                });
                edit_text_time_to.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edit_text_time_to.setError(null);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                hour2 = timePicker.getHour();
                                minute2 = timePicker.getMinute();
                                calendar1.set(0, 0, 0, i, i1);
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                edit_text_time_to.setText(sdf.format(calendar1.getTime()));
                            }
                        }, 12, 0, false);
                        timePickerDialog.show();
                    }
                });


                Button btnReset = dialogView.findViewById(R.id.btnReset);
                btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edit_text_time_from.getText().equals("Time - From")) {

                            edit_text_time_from.setError("Please Select Time From");

                        } else if (edit_text_time_to.getText().equals("Time - To")) {
                            edit_text_time_to.setError("Please Select Time To");

                        } else {
                            if (!(hour1 < hour2 || (hour1 == hour2 && minute1 < minute2))) {
                                Toast.makeText(context, "Please Select Time To After Time From", Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(TimesheetActivity.this, "Please Select Time To After Time From", Toast.LENGTH_SHORT).show();
                                edit_text_time_to.setError("Please Select Time-To After Time-From");
                            } else {


                                String date = edit_text_date.getText().toString();
                                String timeNewFrom = edit_text_time_from.getText().toString();
                                String timeNewTo = edit_text_time_to.getText().toString();
                                String text_desc = edit_text_desc.getText().toString();

                                edit_text_date.setText(date);
                                Map<String, Object> dataEdit = new HashMap<>();
                                dataEdit.put("date", date);
                                dataEdit.put("approvalStatus", approval);
                                dataEdit.put("doneForToday", true);
                                dataEdit.put("name", data.getName());
                                dataEdit.put("time_FROM", timeNewFrom);
                                dataEdit.put("time_to", timeNewTo);
                                dataEdit.put("timesheet_info", text_desc);
                                dataEdit.put("uid", data.getUid());


                                String docName = edit_text_date.getText().toString();
                                String docname[] = docName.split("/");

                                String oldDatedocName[] = oldDate.split("/");
                                String finalDocName = data.getName() + docname[0] + docname[1] + docname[2] + timeNewFrom;
                                String oldDocname = data.getName() + oldDatedocName[0] + oldDatedocName[1] + oldDatedocName[2] + oldTime_from;
                                System.out.println("FinalDocName\t" + finalDocName);
                                System.out.println("OldDocName\t" + oldDocname);

                                db.collection("Data").document(finalDocName).set(dataEdit).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        holder.idTVdate.setText(date);
//                               data.getTime_FROM() + "\t | \t" + data.getTime_to();
                                        holder.idTVCtime.setText(timeNewFrom + "\t | \t" + timeNewTo);
                                        holder.idTVspcl_pur.setText(text_desc);

                                        data.setDate(date);
                                        data.setTime_FROM(timeNewFrom);
                                        data.setTime_to(timeNewTo);
                                        data.setTimesheet_info(text_desc);
                                        dialog.dismiss();
                                        Toast.makeText(context, "Your data has been successfully edited", Toast.LENGTH_SHORT).show();
                                        if (!oldDocname.equals(finalDocName)) {
                                            db.collection("Data").document(oldDocname).delete();

                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Your data failed to edit! Please try again later!" + "\nReason:-\t" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }


                    }
                });


                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
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
        private final Button btn_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            idTVempName = itemView.findViewById(R.id.idTVempName);
            idTVdate = itemView.findViewById(R.id.idTVdate);
            idTVCtime = itemView.findViewById(R.id.idTVCtime);
            card_rv_layout = itemView.findViewById(R.id.card_rv_layout);
            status = itemView.findViewById(R.id.status);
            idTVspcl_pur = itemView.findViewById(R.id.idTVspcl_pur);
            btn_edit = itemView.findViewById(R.id.btn_edit);
        }
    }
}
