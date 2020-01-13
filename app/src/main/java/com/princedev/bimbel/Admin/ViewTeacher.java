package com.princedev.bimbel.Admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class ViewTeacher extends AppCompatActivity {

    Context mContext = ViewTeacher.this;

    private DatabaseReference profileUserRef;
    private TextView changeProfileImage;
    private EditText nameEdt, addressEdt, ttlEdt, phoneEdt, studyEdt, parentEdt, phoneParentEdt, mataPeEdt;
    private CircleImageView imageEdt;
    private Util util;
    private ImageView backBtn, saveBtn;
    private Button jadwalbtn;


    private String name, address, ttl, phone, study, ni, pass, stat, mataPel,
            classroom, myProfileImage, parent, parentPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher);

        ni = getIntent().getStringExtra("id");

        util = new Util(mContext);
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(ni);

        imageEdt = findViewById(R.id.form_profileimage);
        changeProfileImage = findViewById(R.id.changeProfilePhoto);
        nameEdt = findViewById(R.id.form_edt_name);
        addressEdt = findViewById(R.id.form_edt_address);
        ttlEdt = findViewById(R.id.form_edt_ttl);
        phoneEdt = findViewById(R.id.form_edt_phone);
        studyEdt = findViewById(R.id.form_edt_study);
        mataPeEdt = findViewById(R.id.form_edt_matapel);
        backBtn = findViewById(R.id.back_btn);
        saveBtn = findViewById(R.id.save_btn);
        jadwalbtn = findViewById(R.id.btnjadwal);

        jadwalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTeacher.this, ViewJadwalActivity.class);
                intent.putExtra("id", ni);
                intent.putExtra("name", name);
                intent.putExtra("status", stat);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewData();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
                finish();
            }
        });
    }

    private void updateData(){
        String nameDialog    = nameEdt.getText().toString();
        String addressDialog  = addressEdt.getText().toString();
        String ttlDialog  = ttlEdt.getText().toString();
        String phoneDialog  = phoneEdt.getText().toString();
        String studyDialog  = studyEdt.getText().toString();

        if(isStringNull(nameDialog) || isStringNull(addressDialog) || isStringNull(ttlDialog) ||
                isStringNull(phoneDialog)|| isStringNull(studyDialog)){
            Toast.makeText(mContext, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
        }else {
            util.updateStudent(ni,pass, nameDialog, addressDialog, ttlDialog, myProfileImage,
                    phoneDialog, studyDialog, stat, classroom, "", "");
        }
    }

    private void viewData(){

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    pass = dataSnapshot.child("password").getValue().toString();
                    name = dataSnapshot.child("nama").getValue().toString();
                    address = dataSnapshot.child("address").getValue().toString();
                    ttl = dataSnapshot.child("ttl").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    study = dataSnapshot.child("study").getValue().toString();
                    ni = dataSnapshot.child("ni").getValue().toString();
                    classroom = dataSnapshot.child("classroom").getValue().toString();
                    myProfileImage = dataSnapshot.child("profilePhoto").getValue().toString();
                    stat = dataSnapshot.child("status").getValue().toString();

                    Picasso.with(mContext).load(myProfileImage).placeholder(R.drawable.profile).into(imageEdt);
                    nameEdt.setText(name);
                    addressEdt.setText(address);
                    ttlEdt.setText(ttl);
                    phoneEdt.setText(phone);
                    studyEdt.setText(study);
                    mataPeEdt.setText(classroom);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
