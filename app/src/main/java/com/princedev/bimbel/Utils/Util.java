package com.princedev.bimbel.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.princedev.bimbel.Model.Classroom;
import com.princedev.bimbel.Model.Confirmation;
import com.princedev.bimbel.Model.Payment;
import com.princedev.bimbel.Model.Report;
import com.princedev.bimbel.Model.Schedule;
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Util {
    private static final String TAG = "Util";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef, scheduleRef, userRef;
    private StorageReference mStorageReference;
    private String userID;

    private Context mContext;

    public Util(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        scheduleRef = mFirebaseDatabase.getReference().child("Schedule");
        userRef = mFirebaseDatabase.getReference().child("Users");
        mContext = context;


    }

    public static boolean isStringNull(String string){
        return string.equals("");
    }

    public void registerNewStudent(final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.filled_register,
                                    Toast.LENGTH_SHORT).show();
                        }else if (task.isSuccessful()){

                            Log.d(TAG, "onComplete: " + mAuth.getCurrentUser().getEmail());
                            Toast.makeText(mContext, "Berhasil di Tambahkan", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void newUser(final String nis, String password, String name, String address , String ttl,
                        String profileimage, String phone, String study, String status, String classroom, String parent, String phoneParent) {
        final User student = new User(nis, password, name, address, ttl, profileimage, phone, study, status, classroom, parent, phoneParent);
        userRef.child(nis).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d(TAG, "SISWA TERDAFTAR");
                }else {
                    myRef.child("Users")
                            .child(nis)
                            .setValue(student);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateStudent (String nis, String password, String name, String address , String ttl,
                            String profileimage, String phone, String study, String status, String classroom, String parent, String phoneParent) {
        User student = new User(nis, password, name, address, ttl, profileimage, phone, study, status, classroom, parent, phoneParent);
        myRef.child("Users")
                .child(nis)
                .setValue(student);
    }

    public void newSchedule(String dateID, final String date, final String time, String classroom, final String teacher) {
        final Schedule schedule = new Schedule(date, time, classroom, teacher);
        final String idSchedule = dateID+time+classroom;

//        scheduleRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.child(idSchedule).exists()){
//                    Toast.makeText(mContext, "Kelas ini telah ada jadwal", Toast.LENGTH_SHORT).show();
//
//                }else {
//                    for (DataSnapshot ds: dataSnapshot.getChildren()){
//                        String teacherCheck = ds.child("teacher").getValue().toString();
//                        String dateCheck = ds.child("date").getValue().toString();
//                        String timeCheck = ds.child("time").getValue().toString();
//
//                        if (teacherCheck.equals(teacher) && timeCheck.equals(time) && dateCheck.equals(date)){
//                            Toast.makeText(mContext, "Jadwal Bentrok", Toast.LENGTH_SHORT).show();
//
//                        }else {
//                            myRef.child("Schedule")
//                                    .child(idSchedule)
//                                    .setValue(schedule);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        scheduleRef.child(idSchedule).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d(TAG, "TELAH ADA JADWAL");
                }else {
                    myRef.child("Schedule")
                            .child(idSchedule)
                            .setValue(schedule);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void addReport (String id, String date, String time, String classroom, String teacher, String note) {
        Report report = new Report(date, time, classroom, teacher, note);
        myRef.child("Reports")
                .child(id)
                .setValue(report);
    }

    public void addPayment(final String name, final String total, final String status){

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMdd");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime1 = new SimpleDateFormat("HHmmss");
        String saveCurrentTime = currentTime1.format(calForTime.getTime());

        final String randomName = saveCurrentDate + saveCurrentTime;

        userRef.orderByChild("status").equalTo("siswa").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String id = ds.getKey();
                    Payment payment = new Payment(name, total, status);
                    myRef.child("Payments").child(id).child(randomName).setValue(payment);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
