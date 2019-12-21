package com.princedev.bimbel.User;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.princedev.bimbel.Admin.AdminStudent;
import com.princedev.bimbel.Model.ReportStudent;
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class ReportClass extends AppCompatActivity {

    private Context mContext = ReportClass.this;

    private DatabaseReference scheduleRef, userRef, classroomRef, reportStudentRef;
    private TextView classroom, date, time, noData;
    private EditText inputNote;
    private Button saveReport;
    private RecyclerView usersList;

    private String idSchedule, classDs, dateDs, timeDs, teacherDs;

    public static final String SCHEDULE_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_class);

        final Util util = new Util(mContext);

        reportStudentRef = FirebaseDatabase.getInstance().getReference().child("ReportStudents");
        classroomRef = FirebaseDatabase.getInstance().getReference().child("Classrooms");
        scheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        idSchedule = getIntent().getStringExtra(SCHEDULE_ID);

        noData = findViewById(R.id.text_nodata);
        classroom = findViewById(R.id.text_classreport);
        date = findViewById(R.id.text_datereport);
        time = findViewById(R.id.text_timereport);
        inputNote = findViewById(R.id.input_notereport);
        saveReport = findViewById(R.id.save_report_btn);

        //Recycleview
        usersList = findViewById(R.id.report_recycle);
        usersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        usersList.setLayoutManager(linearLayoutManager);

        saveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNote = inputNote.getText().toString();

                if (isStringNull(textNote)){
                    Toast.makeText(mContext, "Catatan Harus diisi", Toast.LENGTH_SHORT).show();
                }else {
                    onBackPressed();
                    util.addReport(idSchedule, dateDs, timeDs, classDs, teacherDs, textNote);
                    scheduleRef.child(idSchedule).removeValue();
                }
            }
        });

        displayInfo();

    }

    private void displayInfo(){
        scheduleRef.child(idSchedule).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    classDs = dataSnapshot.child("classroom").getValue().toString();
                    dateDs = dataSnapshot.child("date").getValue().toString();
                    timeDs = dataSnapshot.child("time").getValue().toString();
                    teacherDs = dataSnapshot.child("teacher").getValue().toString();

                    classroom.setText(classDs);
                    date.setText(dateDs);
                    time.setText(timeDs);

                    displayAllUsers();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayAllUsers() {

        Query query = userRef.orderByChild("classroom").equalTo(classDs);
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.all_users_item,
                UsersViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, final User model, int position) {
                Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                final String ni = model.getNi();
                viewHolder.setProfileimage(mContext, model.getProfilePhoto());
                viewHolder.setFullname(model.getNama());
                viewHolder.setNI(model.getNi());

                noData.setVisibility(View.GONE);

                final Button hadir = viewHolder.mView.findViewById(R.id.hadir_btn);
                hadir.setVisibility(View.VISIBLE);

                hadir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reportStudentRef.child(ni).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long tambahHadir = (long) dataSnapshot.child("hadir").getValue();
                                String jumlah = String.valueOf(tambahHadir + 1);


                                ReportStudent reportStudent = new ReportStudent(classDs, (int) (tambahHadir + 1), ni);
                                reportStudentRef.child(ni).setValue(reportStudent);

                                hadir.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };
        usersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        private void setProfileimage(Context ctx, String profileimage){
            CircleImageView myImage = mView.findViewById(R.id.all_userprofile);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        private void setFullname(String fullname){
            TextView myName = mView.findViewById(R.id.all_name);
            myName.setText(fullname);
        }

        private void setNI(String ni){
            TextView nis = mView.findViewById(R.id.all_ni);
            nis.setText(ni);
        }
    }
}
