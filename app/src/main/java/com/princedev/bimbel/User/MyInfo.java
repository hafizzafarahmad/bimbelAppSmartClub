package com.princedev.bimbel.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.princedev.bimbel.Admin.AdminStudent;
import com.princedev.bimbel.Model.Info;
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.R;

public class MyInfo extends AppCompatActivity {

    private DatabaseReference infoRef;
    ImageView backBtn;
    private RecyclerView infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        infoRef = FirebaseDatabase.getInstance().getReference().child("Informations");

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Recycleview
        infoList = findViewById(R.id.recycle_info);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false);
        linearLayoutManager.setReverseLayout(false);
        infoList.setLayoutManager(linearLayoutManager);

        displayAllInfo();

    }

    //Menampilkan Seluruh Siswa
    private void displayAllInfo() {

        FirebaseRecyclerAdapter<Info, InfoViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Info, InfoViewHolder>(
                Info.class,
                R.layout.all_info_item,
                InfoViewHolder.class,
                infoRef
        ) {
            @Override
            protected void populateViewHolder(final InfoViewHolder viewHolder, Info model, int position) {
                Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                String id = getRef(position).getKey();

                viewHolder.setJudul(model.getName());
                viewHolder.setInfo(model.getInfo());

            }
        };
        infoList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public InfoViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        private void setJudul(String judul){
            TextView myJudul = (TextView) mView.findViewById(R.id.all_judul);
            int lenght = judul.length();
            if (lenght > 60){
                myJudul.setText(judul.substring(0,59) + "...");
            }else {
                myJudul.setText(judul);
            }

        }

        private void setInfo(String info){
            TextView myInfo = (TextView) mView.findViewById(R.id.all_informasi);
            myInfo.setText(info);
        }

    }
}
