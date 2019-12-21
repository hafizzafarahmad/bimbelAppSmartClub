package com.princedev.bimbel.User;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.princedev.bimbel.Admin.AdminStudent;
import com.princedev.bimbel.Model.Payment;
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.SaveDataPreference;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class MyReportStudent extends AppCompatActivity {

    private DatabaseReference userRef, paymentRef, reportRef, confirmRef;
    private StorageReference confirmImageRef;
    private Context mContext = MyReportStudent.this;
    private Util util;
    private List<String> arrayList;

    private ImageView imageConfirm, backBtn;
    private TextView kehadiran, anEdt, noData;
    private RecyclerView paymentList;
    private String ni, paymentType, downloadUrl, an, data;
    private Button confirmationBtn;
    private Spinner paymentSpinner;

    private ProgressDialog loadingBar;
    private Uri imageUri;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report_student);

        util = new Util(mContext);
        loadingBar = new ProgressDialog(this);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        paymentRef = FirebaseDatabase.getInstance().getReference().child("Payments");
        reportRef = FirebaseDatabase.getInstance().getReference().child("ReportStudents");
        confirmRef = FirebaseDatabase.getInstance().getReference().child("Confirmations");
        confirmImageRef = FirebaseStorage.getInstance().getReference().child("Confirm Images");

        ni = SaveDataPreference.getUserNI(mContext);

        kehadiran = findViewById(R.id.text_kehadiran);
        noData = findViewById(R.id.text_nodata);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        confirmationBtn = findViewById(R.id.confirmation_btn);
        confirmationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFormConfirm();
            }
        });

        paymentRef.child(ni).orderByChild("status").equalTo("Belum Lunas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList = new ArrayList<>();
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        data = ds.child("name").getValue(String.class);

                    }
                    confirmationBtn.setVisibility(View.VISIBLE);
                }else {
                    confirmationBtn.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Recycleview
        paymentList = findViewById(R.id.payment_recycle);
        paymentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        paymentList.setLayoutManager(linearLayoutManager);

        displayReport();

    }

    //Memunculkan Dialog Konfirmasi Pembayaran
    private void dialogFormConfirm(){

        dialog = new AlertDialog.Builder(mContext);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_confirmation, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Kirim Konfirmasi");

        anEdt = dialogView.findViewById(R.id.form_an);
        imageConfirm = dialogView.findViewById(R.id.edt_image_confirm);
        paymentSpinner = dialogView.findViewById(R.id.spinner_payment_confrm);

        imageConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        arrayList.add(data);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, arrayList);
        paymentSpinner.setAdapter(arrayAdapter);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                an    = anEdt.getText().toString();
                paymentType  = paymentSpinner.getSelectedItem().toString();

                if(isStringNull(an) || isStringNull(paymentType) || isStringNull(imageUri.toString())){
                    Toast.makeText(mContext, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
                }else {
                    saveData();
                    Toast.makeText(mContext, "Konfirmasi Telah Terkirim", Toast.LENGTH_SHORT).show();
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

    //Menampilkan Kehadiran dan Semua Pembayaran
    private void displayReport(){

        reportRef.child(ni).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String hadirData = dataSnapshot.child("hadir").getValue().toString();
                    kehadiran.setText(hadirData + "/12 Petemuan");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Payment, UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Payment, UsersViewHolder>(
                Payment.class,
                R.layout.all_payment_item,
                UsersViewHolder.class,
                paymentRef.child(ni)
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Payment model, int position) {
                Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                viewHolder.setNamePayment(model.getName());
                viewHolder.setTotalPayment(model.getTotal());
                viewHolder.setStatusPayment(model.getStatus());

                noData.setVisibility(View.GONE);

            }
        };
        paymentList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }


        private void setNamePayment(String name){
            TextView myName = mView.findViewById(R.id.all_name_payment);
            myName.setText(name);
        }

        private void setTotalPayment(String total){
            TextView totalText = mView.findViewById(R.id.all_payment);
            totalText.setText(total);
        }

        private void setStatusPayment(String status){
            TextView statusText = mView.findViewById(R.id.all_status_payment);
            statusText.setText(status);
        }

    }

    //Membuka Gallery Foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageConfirm.setImageURI(imageUri);
        }
    }

    //Menyimpan Foto Ke Database
    private void saveData(){

        StorageReference filePath = confirmImageRef.child(ni).child(paymentType + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    util.addConfirmation(ni, an, paymentType, downloadUrl);
                }
            }
        });
    }
}
