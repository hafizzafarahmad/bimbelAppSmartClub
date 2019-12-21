package com.princedev.bimbel.User;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.princedev.bimbel.Model.Report;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.Util;

public class MyReportTeacher extends AppCompatActivity {

    private DatabaseReference reportRef;
    private Context mContext = MyReportTeacher.this;
    private Util util;
    private TextView noData;

    private RecyclerView usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        reportRef = FirebaseDatabase.getInstance().getReference().child("Reports");

        noData = findViewById(R.id.text_nodata);

        //Recycleview
        usersList = findViewById(R.id.recycle_report);
        usersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        usersList.setLayoutManager(linearLayoutManager);

        displayAllReport();
    }

    private void displayAllReport() {

        FirebaseRecyclerAdapter<Report, ClassViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Report, ClassViewHolder>(
                Report.class,
                R.layout.all_report_item,
                ClassViewHolder.class,
                reportRef
        ) {
            @Override
            protected void populateViewHolder(final ClassViewHolder viewHolder, Report model, int position) {
                final String idClass = getRef(position).getKey();

                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setClassroom(model.getClassroom());
                viewHolder.setNote(model.getNote());

                noData.setVisibility(View.GONE);

            }
        };
        usersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ClassViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        private void setDate(String date){
            TextView dateTxt = mView.findViewById(R.id.all_date);
            dateTxt.setText(date);
        }

        private void setTime(String time){
            TextView timeTxt = mView.findViewById(R.id.all_time);
            timeTxt.setText(time);
        }

        private void setClassroom(String classroom){
            TextView classTxt = mView.findViewById(R.id.all_class);
            classTxt.setText(classroom);
        }

        private void setNote(String note){
            TextView noteTxt = mView.findViewById(R.id.all_note);
            noteTxt.setText(note);
        }

    }
}
