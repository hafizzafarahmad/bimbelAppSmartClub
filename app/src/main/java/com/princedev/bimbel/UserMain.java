package com.princedev.bimbel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.princedev.bimbel.Admin.AdminClassRoom;
import com.princedev.bimbel.Admin.AdminSchedule;
import com.princedev.bimbel.Admin.AdminStudent;
import com.princedev.bimbel.Admin.AdminTeacher;
import com.princedev.bimbel.User.MyInfo;
import com.princedev.bimbel.User.MyProfile;
import com.princedev.bimbel.User.MyReportStudent;
import com.princedev.bimbel.User.MyReportTeacher;
import com.princedev.bimbel.User.MySchedule;
import com.princedev.bimbel.Utils.SaveDataPreference;

import static com.princedev.bimbel.StudentList.CLASS_ID;
import static com.princedev.bimbel.StudentList.OPTION_SELECTED;

public class UserMain extends AppCompatActivity {

    private Context mContext = UserMain.this;
    private ImageView myprofileBtn, teacherBtn, reportBtn, scheduleBtn;
    private String status;
    private TextView welcomeName, profileMenu, teacherMenu, reportMenu;
    private Spinner logoutSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        status = SaveDataPreference.getUserStatus(mContext);

        myprofileBtn = findViewById(R.id.myprofile_btn);
        teacherBtn = findViewById(R.id.teacher_btn);
        reportBtn = findViewById(R.id.report_btn);
        reportMenu = findViewById(R.id.text_report);
        scheduleBtn = findViewById(R.id.schedule_btn);
        profileMenu = findViewById(R.id.text_profile);
        teacherMenu = findViewById(R.id.text_info);
        welcomeName = findViewById(R.id.welcome_name);
        logoutSpinner = findViewById(R.id.spinner_logout);

        welcomeName.setText(SaveDataPreference.getUserName(mContext));

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(mContext, R.array.logout_array, android.R.layout.simple_dropdown_item_1line);
        logoutSpinner.setAdapter(arrayAdapter);
        logoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ((TextView)view).setText(null);
                switch (position){
                    case 0:
                        break;
                    case 1:
                        SaveDataPreference.clearAllPref(mContext);
                        startActivity(new Intent(mContext, Login.class));
                        finish();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (status.equals("admin")){
            profileMenu.setText(getString(R.string.text_siswa));
            teacherMenu.setText(getString(R.string.text_guru));
            reportMenu.setText(getString(R.string.text_kelas));

            myprofileBtn.setImageDrawable(getResources().getDrawable(R.drawable.tambah_siswa));
            teacherBtn.setImageDrawable(getResources().getDrawable(R.drawable.tambah_guru));
        }

        initButton();

    }

    private void initButton(){

        myprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("admin")){
                    startActivity(new Intent(mContext, AdminStudent.class));
                }else {
                    Intent intent = new Intent(mContext, MyProfile.class);
                    startActivity(intent);
                }
            }
        });

        teacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("admin")){
                    startActivity(new Intent(mContext, AdminTeacher.class));
                }else {
                    startActivity(new Intent(mContext, MyInfo.class));
                }
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("admin")){
                    startActivity(new Intent(mContext, AdminClassRoom.class));
                }else if (status.equals("pengajar")){
                    startActivity(new Intent(mContext, MyReportTeacher.class));
                }else {
                    startActivity(new Intent(mContext, MyReportStudent.class));
                }
            }
        });

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("admin")){
                    startActivity(new Intent(mContext, AdminSchedule.class));
                }else {
                    Intent intent = new Intent(mContext, MySchedule.class);
                    startActivity(intent);
                }
            }
        });
    }
}
