package com.princedev.bimbel.Admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.princedev.bimbel.Model.Classroom;
import com.princedev.bimbel.R;
import com.princedev.bimbel.StudentList;
import com.princedev.bimbel.Utils.Util;

import static com.princedev.bimbel.StudentList.CLASS_ID;
import static com.princedev.bimbel.StudentList.OPTION_SELECTED;

public class AdminClassRoom extends AppCompatActivity {

    private DatabaseReference classRef, userRef, reportStudentRef;
    private Context mContext = AdminClassRoom.this;
    private Util util;

    private RecyclerView usersList;
    private Spinner optionSpinner;
    private ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_classroom);

        util = new Util(mContext);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        classRef = FirebaseDatabase.getInstance().getReference().child("Classrooms");
        reportStudentRef = FirebaseDatabase.getInstance().getReference().child("ReportStudents");

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Recycleview
        usersList = findViewById(R.id.recycle_class);
        usersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        usersList.setLayoutManager(linearLayoutManager);

        displayAllClass();
    }

    private void displayAllClass() {

        FirebaseRecyclerAdapter<Classroom, ClassViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Classroom, ClassViewHolder>(
                Classroom.class,
                R.layout.all_class_item,
                ClassViewHolder.class,
                classRef
        ) {
            @Override
            protected void populateViewHolder(final ClassViewHolder viewHolder, Classroom model, int position) {
                final String idClass = getRef(position).getKey();


                reportStudentRef.orderByChild("classroom").equalTo(idClass).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String total = String.valueOf(dataSnapshot.getChildrenCount());
                        viewHolder.setJumlah(total);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setName(model.getName());

                optionSpinner = viewHolder.mView.findViewById(R.id.option_class);
                ArrayAdapter<CharSequence> learn = ArrayAdapter.createFromResource(mContext, R.array.options_array, android.R.layout.simple_dropdown_item_1line);
                optionSpinner.setAdapter(learn);
                optionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        ((TextView)view).setText(null);
                        switch (position){
                            case 0:
                                break;
                            case 1:
                                Intent intentView = new Intent(mContext, StudentList.class);
                                intentView.putExtra(OPTION_SELECTED,"view_student");
                                intentView.putExtra(CLASS_ID,idClass);
                                startActivity(intentView);
                                break;
                            case 2:
                                Intent intent = new Intent(mContext, StudentList.class);
                                intent.putExtra(OPTION_SELECTED,"add_student");
                                intent.putExtra(CLASS_ID,idClass);
                                startActivity(intent);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
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

        private void setName(String name){
            TextView nameTxt = mView.findViewById(R.id.all_class);
            nameTxt.setText(name);
        }

        private void setJumlah(String jumlah){
            TextView jumlahTxt = mView.findViewById(R.id.all_jumlah);
            jumlahTxt.setText(jumlah);
        }

    }
}
