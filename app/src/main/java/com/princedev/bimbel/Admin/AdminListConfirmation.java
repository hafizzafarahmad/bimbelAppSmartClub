package com.princedev.bimbel.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.princedev.bimbel.Model.Confirmation;
import com.princedev.bimbel.Model.Payment;
import com.princedev.bimbel.R;
import com.princedev.bimbel.User.MyProfile;
import com.princedev.bimbel.User.MyReportStudent;
import com.princedev.bimbel.Utils.SaveDataPreference;
import com.squareup.picasso.Picasso;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class AdminListConfirmation extends AppCompatActivity {

    private DatabaseReference confirmRef, paymentRef;
    private StorageReference confirmImageRef;
    private Context mContext = AdminListConfirmation.this;

    private ImageView imageConfirmView;
    private TextView pembayaranTxt, totalTxt, anView, paymentView, noData, paymentNi;
    private RecyclerView confirmList;
    private String ni, paymentDialog, an, image, id, idPayment;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_confirmation);

        confirmRef = FirebaseDatabase.getInstance().getReference().child("Confirmations");
        paymentRef = FirebaseDatabase.getInstance().getReference().child("Payments");
        confirmImageRef = FirebaseStorage.getInstance().getReference().child("Confirm Images");

        noData = findViewById(R.id.text_nodata);

        //Recycleview
        confirmList = findViewById(R.id.confirm_recycle);
        confirmList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        confirmList.setLayoutManager(linearLayoutManager);


        displayConfirmation();

    }

    private void displayConfirmation(){

        FirebaseRecyclerAdapter<Confirmation, UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Confirmation, UsersViewHolder>(
                Confirmation.class,
                R.layout.all_payment_item,
                UsersViewHolder.class,
                confirmRef
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Confirmation model, int position) {
                Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                id = getRef(position).getKey();

                viewHolder.setNamePayment(": " + model.getAn());
                viewHolder.setNi();
                viewHolder.setPayment(": " + model.getPayment());

                noData.setVisibility(View.GONE);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogViewConfirm(id);

                    }
                });

                pembayaranTxt = viewHolder.mView.findViewById(R.id.text_pembayaran);
                totalTxt = viewHolder.mView.findViewById(R.id.text_total);

                pembayaranTxt.setText(getString(R.string.text_AN));
                totalTxt.setText(getString(R.string.text_pembayaran));

            }
        };
        confirmList.setAdapter(firebaseRecyclerAdapter);
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

        private void setPayment(String total){
            TextView totalText = mView.findViewById(R.id.all_payment);
            totalText.setText(total);
        }

        private void setNi(){
            TextView niText = mView.findViewById(R.id.all_status_payment);
            niText.setVisibility(View.GONE);
        }

    }

    private void dialogViewConfirm(String idConfrm){

        dialog = new AlertDialog.Builder(mContext);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_view_confirm, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Konfirmasi");

        anView = dialogView.findViewById(R.id.text_anView);
        paymentView = dialogView.findViewById(R.id.text_paymentView);
        imageConfirmView = dialogView.findViewById(R.id.confirm_imageView);
        paymentNi = dialogView.findViewById(R.id.text_paymentni);

        confirmRef.child(idConfrm).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ni = dataSnapshot.child("ni").getValue().toString();

                    String nameDialog = dataSnapshot.child("an").getValue().toString();
                    paymentDialog = dataSnapshot.child("payment").getValue().toString();
                    String imageDialog = dataSnapshot.child("image").getValue().toString();

                    anView.setText(" : " + nameDialog);
                    paymentView.setText(" : " + paymentDialog);
                    paymentNi.setText(" : " + ni);
                    Picasso.with(mContext).load(imageDialog).placeholder(R.drawable.profile).into(imageConfirmView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog.setPositiveButton("TERIMA", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                paymentRef.child(ni).orderByChild("name").equalTo(paymentDialog).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            idPayment = ds.getKey();
                        }

                        confirmRef.child(id).removeValue();
                        confirmImageRef.child(ni).child(paymentDialog + ".jpg").delete();
                        paymentRef.child(ni).child(idPayment).child("status").setValue("Lunas");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                noData.setVisibility(View.VISIBLE);
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
}
