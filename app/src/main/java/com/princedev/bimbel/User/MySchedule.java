package com.princedev.bimbel.User;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.princedev.bimbel.Model.Schedule;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.SaveDataPreference;

import static com.princedev.bimbel.User.ReportClass.SCHEDULE_ID;

public class MySchedule extends AppCompatActivity {

    private DatabaseReference scheduleRef, userRef;
    private Context mContext = MySchedule.this;

    private ImageView backBtn;
    private TextView noData;

    private RecyclerView scheduleList;
    private String classroom, name, status, ni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);

        scheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        classroom = SaveDataPreference.getUserClass(mContext);
        name = SaveDataPreference.getUserName(mContext);
        status = SaveDataPreference.getUserStatus(mContext);
        ni = SaveDataPreference.getUserNI(mContext);

        noData = findViewById(R.id.text_nodata);
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Recycleview
        scheduleList = findViewById(R.id.recycle_myschedule);
        scheduleList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        scheduleList.setLayoutManager(linearLayoutManager);

        if (status.equals("pengajar")){
            myScheduleTeacher();
        }else {
            myScheduleStudent();
        }
    }

    //Tampil Jadwal Untuk Login Siswa
    private void myScheduleStudent() {

        Query query = scheduleRef.orderByChild("classroom").equalTo(classroom);
        FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>(
                Schedule.class,
                R.layout.all_schedule_item,
                ScheduleViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final ScheduleViewHolder viewHolder, Schedule model, int position) {

                viewHolder.setDate(model.getDate());
                viewHolder.setTeacher(model.getTeacher());
                viewHolder.setTime(model.getTime());
                viewHolder.setClassname(model.getClassroom());

                noData.setVisibility(View.GONE);

                userRef.orderByChild("nama").equalTo(model.getTeacher()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String matpel = ds.child("classroom").getValue().toString();

                            viewHolder.setMatPel(matpel);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        scheduleList.setAdapter(firebaseRecyclerAdapter);
    }

    //Tampil Jadwal Untuk Login Pengajar
    private void myScheduleTeacher() {

        Query query = scheduleRef.orderByChild("teacher").equalTo(name);
        FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>(
                Schedule.class,
                R.layout.all_schedule_item,
                ScheduleViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final ScheduleViewHolder viewHolder, Schedule model, int position) {
                final String idSchedule = getRef(position).getKey();

                viewHolder.setDate(model.getDate());
                viewHolder.setTeacher(model.getTeacher());
                viewHolder.setTime(model.getTime());
                viewHolder.setClassname(model.getClassroom());

                noData.setVisibility(View.GONE);

                userRef.child(ni).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String matpel = dataSnapshot.child("classroom").getValue().toString();

                        if (matpel.isEmpty()){
                            matpel = "";
                        }

                        viewHolder.setMatPel(matpel);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ReportClass.class);
                        intent.putExtra(SCHEDULE_ID, idSchedule);
                        startActivity(intent);

                    }
                });
            }
        };
        scheduleList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ScheduleViewHolder(View itemView) {
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

        private void setTeacher(String teacher){
            TextView teacherTxt = mView.findViewById(R.id.all_teacher);
            teacherTxt.setText(teacher);
        }

        private void setMatPel(String pelajaran){
            TextView matpelTxt = mView.findViewById(R.id.all_matpel);
            matpelTxt.setText(pelajaran);
        }

        private void setClassname(String classname){
            TextView classTxt = mView.findViewById(R.id.all_class);
            classTxt.setText(classname);
        }

    }
}
