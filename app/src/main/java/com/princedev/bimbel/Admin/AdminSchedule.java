package com.princedev.bimbel.Admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.princedev.bimbel.Model.Schedule;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.princedev.bimbel.R.id.input_date;
import static com.princedev.bimbel.StudentList.CLASS_ID;
import static com.princedev.bimbel.Utils.Util.isStringNull;

public class AdminSchedule extends AppCompatActivity {

    private DatabaseReference userRef, classRef, scheduleRef;
    private Context mContext = AdminSchedule.this;
    private Util util;

    private RecyclerView scheduleList;
    private FloatingActionButton fab;
    private Spinner inputClass, inputTime, inputTeacher;
    private String dateID;
    private ImageView backBtn;
    private TextView noData;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;


    @BindView(R.id.input_date)
    Button inputDate;
    @BindView(R.id.text_date)
    TextView textDate;

    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_schedule);

        util = new Util(mContext);

        classRef = FirebaseDatabase.getInstance().getReference().child("Classrooms");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        scheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule");

        noData = findViewById(R.id.text_nodata);

        fab = findViewById(R.id.fab_add_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForm();
            }
        });

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Recycleview
        scheduleList = findViewById(R.id.recycle_user);
        scheduleList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        scheduleList.setLayoutManager(linearLayoutManager);

        displayAllSchedule();
    }

    //Menampilkan Form Tambah Jadwal
    private void dialogForm(){

        dialog = new AlertDialog.Builder(mContext);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_schedule, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Tambah Jadwal");

        textDate = dialogView.findViewById(R.id.text_date);
        inputDate = dialogView.findViewById(input_date);
        inputTime = dialogView.findViewById(R.id.input_time);
        inputClass = dialogView.findViewById(R.id.input_classroom);
        inputTeacher = dialogView.findViewById(R.id.input_teacher);

        ButterKnife.bind(dialogView);
        myCalendar = Calendar.getInstance();

        //Input Date
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                        String formatTanggal = "dd-MMM-yyyy";
                        String formatTanggalID = "yyyyMMdd";
                        SimpleDateFormat sdf = new SimpleDateFormat(formatTanggal);
                        SimpleDateFormat sdfID = new SimpleDateFormat(formatTanggalID);

                        dateID = sdfID.format(myCalendar.getTime());
                        textDate.setText(sdf.format(myCalendar.getTime()));
                    }
                },
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Input Waktu
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(mContext, R.array.times_array, android.R.layout.simple_dropdown_item_1line);
        inputTime.setAdapter(arrayAdapter);

        //Input Kelas
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> arrayList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String data = snapshot.child("name").getValue(String.class);
                    arrayList.add(data);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, arrayList);
                inputClass.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Input Pengajar
        Query query = userRef.orderByChild("status").equalTo("pengajar");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> arrayList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String data = snapshot.child("nama").getValue(String.class);
                    arrayList.add(data);
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, arrayList);
                inputTeacher.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String date    = textDate.getText().toString();
                String time  = inputTime.getSelectedItem().toString();
                String classroom  = inputClass.getSelectedItem().toString();
                String teacher  = inputTeacher.getSelectedItem().toString();

                if(isStringNull(date) || isStringNull(time) || isStringNull(classroom) || isStringNull(teacher)){
                    Toast.makeText(mContext, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
                }else {
                    util.newSchedule(dateID, date, time, classroom, teacher);
                }
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Menampilkan Jadwal Login Admin
    private void displayAllSchedule() {

        FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>(
                Schedule.class,
                R.layout.all_schedule_item,
                ScheduleViewHolder.class,
                scheduleRef
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
