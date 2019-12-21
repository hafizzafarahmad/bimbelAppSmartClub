package com.princedev.bimbel;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.princedev.bimbel.Model.ReportStudent;
import com.princedev.bimbel.Model.Student;
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class StudentList extends AppCompatActivity {

    private DatabaseReference userRef, classRef, reportStudentRef;
    private Context mContext = StudentList.this;
    private Util util;

    private RecyclerView usersList;
    private TextView noData;

    private String classID, option_selected;

    public final static String CLASS_ID = "classid";
    public final static String OPTION_SELECTED = "selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        util = new Util(mContext);

        classID = getIntent().getStringExtra(CLASS_ID);
        option_selected = getIntent().getStringExtra(OPTION_SELECTED);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        classRef = FirebaseDatabase.getInstance().getReference().child("Classrooms");
        reportStudentRef = FirebaseDatabase.getInstance().getReference().child("ReportStudents");

        noData = findViewById(R.id.text_nodata);

        //Recycleview
        usersList = findViewById(R.id.student_list);
        usersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        usersList.setLayoutManager(linearLayoutManager);

        if (option_selected.equals("view_student")){
            displayClassUsers();
        }else {
            displayAllUsers();
        }

    }

    //Tampil dan Tambah Siswa Ke Kelas
    private void displayAllUsers() {

        Query userQuery = userRef.orderByChild("status").equalTo("siswa");
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.all_users_item,
                UsersViewHolder.class,
                userQuery
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, final User model, int position) {
                Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                final String ni = getRef(position).getKey();
                String classroom = model.getClassroom();

                viewHolder.setProfileimage(mContext, model.getProfilePhoto());
                viewHolder.setFullname(model.getNama());
                viewHolder.setNI(model.getNi());

                noData.setVisibility(View.GONE);

                final ImageButton addStudent = viewHolder.mView.findViewById(R.id.add_studentlist_btn);
                final ImageButton deleteStudent = viewHolder.mView.findViewById(R.id.delete_student_btn);
                TextView classView = viewHolder.mView.findViewById(R.id.class_name);
                addStudent.setVisibility(View.VISIBLE);
                deleteStudent.setVisibility(GONE);
                classView.setVisibility(GONE);

                if (classroom.equals("")){
                    addStudent.setVisibility(View.VISIBLE);
                    classView.setVisibility(GONE);
                }else {
                    addStudent.setVisibility(GONE);
                    classView.setVisibility(View.VISIBLE);
                    classView.setText(classroom);
                }

                addStudent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap userMap = new HashMap();
                        userMap.put("classroom", classID);
                        userRef.child(ni).updateChildren(userMap);
                        ReportStudent reportStudent = new ReportStudent(classID, 0, ni);
                        reportStudentRef.child(ni)
                                .setValue(reportStudent);


                    }
                });
            }
        };
        usersList.setAdapter(firebaseRecyclerAdapter);
    }

    //Tampil Siswa dan Hapus Siswa Dari Kelas
    private void displayClassUsers(){

        Query userQuery = userRef.orderByChild("classroom").equalTo(classID);
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.all_users_item,
                UsersViewHolder.class,
                userQuery
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, final User model, int position) {
                Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                final String ni = getRef(position).getKey();

                viewHolder.setProfileimage(mContext, model.getProfilePhoto());
                viewHolder.setFullname(model.getNama());
                viewHolder.setNI(model.getNi());

                noData.setVisibility(View.GONE);

                final ImageButton addStudent = viewHolder.mView.findViewById(R.id.add_studentlist_btn);
                final ImageButton deleteStudent = viewHolder.mView.findViewById(R.id.delete_student_btn);
                TextView classView = viewHolder.mView.findViewById(R.id.class_name);

                addStudent.setVisibility(View.GONE);
                deleteStudent.setVisibility(View.VISIBLE);
                classView.setVisibility(GONE);


                deleteStudent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reportStudentRef.child(ni).removeValue();
                            userRef.child(ni).child("classroom").setValue("");
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
