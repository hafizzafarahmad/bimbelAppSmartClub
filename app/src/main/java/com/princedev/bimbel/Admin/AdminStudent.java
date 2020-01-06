package com.princedev.bimbel.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.princedev.bimbel.Model.Info;
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class AdminStudent extends AppCompatActivity {

    private DatabaseReference userRef, infoRef, reportStudentRef, paymentsRef;
    private Context mContext = AdminStudent.this;
    private Util util;
    private ImageView backBtn;

    private RecyclerView usersList;
    private FloatingActionButton fab, fabPayment, fabConfirm;
    private EditText nisEdt, passwordEdt, namaPaymentEdt, totalPaymentEdt, judulEdt, infoEdt;
    private TextView noData;

    AlertDialog.Builder dialog, dialogPayment;
    LayoutInflater inflater;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student);

        util = new Util(AdminStudent.this);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        infoRef = FirebaseDatabase.getInstance().getReference().child("Informations");
        reportStudentRef = FirebaseDatabase.getInstance().getReference().child("ReportStudents");
        paymentsRef = FirebaseDatabase.getInstance().getReference().child("Payments");

        noData = findViewById(R.id.text_nodata);
        fab = findViewById(R.id.fab_add_user);

//        fabPayment = findViewById(R.id.fab_add_payment);
//        fabConfirm = findViewById(R.id.fab_confirm);

//        fabConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(mContext, AdminListConfirmation.class));
//            }
//        });
//
//        fabPayment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogAddPayment();
//            }
//        });
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogForm();
//            }
//        });

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_student);
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.konfirmasi:
                        startActivity(new Intent(mContext, AdminListConfirmation.class));
                        break;

                    case R.id.add_siswa:
                        dialogForm();
                        break;

                    case R.id.add_payment:
                        dialogAddPayment();
                        break;

                    case R.id.add_info:
                        dialogAddInfo();
                        break;
                }
                return false;
            }

            @Override
            public void onMenuClosed() {

            }
        });

        //Recycleview
        usersList = findViewById(R.id.recycle_user);
        usersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        usersList.setLayoutManager(linearLayoutManager);

        displayAllUsers();
    }

    //Form Tambah Pembayaran
    private void dialogAddPayment(){
        dialogPayment = new AlertDialog.Builder(AdminStudent.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_payment, null);
        dialogPayment.setView(dialogView);
        dialogPayment.setCancelable(true);
        dialogPayment.setIcon(R.mipmap.ic_launcher);
        dialogPayment.setTitle("Tambah Pembayaran");

        namaPaymentEdt = dialogView.findViewById(R.id.name_payment_edt);
        totalPaymentEdt = dialogView.findViewById(R.id.total_payment_edt);

        dialogPayment.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name    = namaPaymentEdt.getText().toString();
                String total  = totalPaymentEdt.getText().toString();

                if(isStringNull(name) || isStringNull(total)){
                    Toast.makeText(AdminStudent.this, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
                }else {
                    util.addPayment(name, total, "Belum Lunas");
                }
            }
        });

        dialogPayment.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogPayment.show();
    }

    //Form Tambah Siswa
    private void dialogForm(){

        dialog = new AlertDialog.Builder(AdminStudent.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_data, null);
        dialog.setView(dialogView);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Tambah Siswa");

        nisEdt = dialogView.findViewById(R.id.form_nis);
        passwordEdt = dialogView.findViewById(R.id.form_password);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String nis    = nisEdt.getText().toString();
                String password  = passwordEdt.getText().toString();

                if(isStringNull(nis) || isStringNull(password)){
                    Toast.makeText(AdminStudent.this, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
                }else {
                    util.newUser(nis, password, "", "", "", "default",
                            "", "", "siswa", "", "", "");
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

    private void dialogAddInfo(){

        dialog = new AlertDialog.Builder(AdminStudent.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_info, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Tambah Informasi");

        judulEdt = dialogView.findViewById(R.id.form_judul);
        infoEdt = dialogView.findViewById(R.id.form_info);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String judul    = judulEdt.getText().toString();
                String infoIsi  = infoEdt.getText().toString();

                Calendar myCalendar = Calendar.getInstance();
                String formatTanggalID = "yyyyMMddss";
                SimpleDateFormat sdfID = new SimpleDateFormat(formatTanggalID);

                String dateID = sdfID.format(myCalendar.getTime());

                if(isStringNull(judul) || isStringNull(infoIsi)){
                    Toast.makeText(AdminStudent.this, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
                }else {
                    Info info = new Info(dateID, judul, infoIsi);
                    infoRef.child(dateID).setValue(info);
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

    //Menampilkan Seluruh Siswa
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
            protected void populateViewHolder(final UsersViewHolder viewHolder, User model, int position) {
                Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                final String id = getRef(position).getKey();
                viewHolder.setProfileimage(mContext, model.getProfilePhoto());
                viewHolder.setFullname(model.getNama());
                viewHolder.setNI(model.getNi());

                viewHolder.deleteBtn.setVisibility(View.VISIBLE);
                viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog = new AlertDialog.Builder(AdminStudent.this);
                        dialog.setTitle("Hapus Siswa");
                        dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                paymentsRef.child(id).removeValue();
                                reportStudentRef.child(id).removeValue();
                                userRef.child(id).removeValue();
                            }
                        });
                        dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });



                noData.setVisibility(View.GONE);

            }
        };
        usersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton deleteBtn;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            deleteBtn = mView.findViewById(R.id.delete_btn);
        }

        private void setProfileimage(Context ctx, String profileimage){
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_userprofile);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        private void setFullname(String fullname){
            TextView myName = (TextView) mView.findViewById(R.id.all_name);
            myName.setText(fullname);
        }

        private void setNI(String ni){
            TextView nis = (TextView) mView.findViewById(R.id.all_ni);
            nis.setText(ni);
        }

    }
}
