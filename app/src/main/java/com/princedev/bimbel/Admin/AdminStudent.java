package com.princedev.bimbel.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class AdminStudent extends AppCompatActivity {

    private DatabaseReference userRef;
    private Context mContext = AdminStudent.this;
    private Util util;
    private ImageView backBtn;

    private RecyclerView usersList;
    private FloatingActionButton fab, fabPayment, fabConfirm;
    private EditText nisEdt, passwordEdt, namaPaymentEdt, totalPaymentEdt;
    private TextView noData;

    AlertDialog.Builder dialog, dialogPayment;
    LayoutInflater inflater;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student);

        util = new Util(mContext);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        noData = findViewById(R.id.text_nodata);
        fab = findViewById(R.id.fab_add_user);
        fabPayment = findViewById(R.id.fab_add_payment);
        fabConfirm = findViewById(R.id.fab_confirm);

        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AdminListConfirmation.class));
            }
        });

        fabPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddPayment();
            }
        });

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
        dialog.setCancelable(true);
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
                String id = getRef(position).getKey();
                viewHolder.setProfileimage(mContext, model.getProfilePhoto());
                viewHolder.setFullname(model.getNama());
                viewHolder.setNI(model.getNi());

                noData.setVisibility(View.GONE);

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
