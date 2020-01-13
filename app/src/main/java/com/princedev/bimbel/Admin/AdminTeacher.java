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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.princedev.bimbel.Model.User;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class AdminTeacher extends AppCompatActivity {

    private DatabaseReference userRef, reportRef, scheduleRef;
    private RecyclerView usersList;
    private Context mContext = AdminTeacher.this;
    private Util util;

    private FloatingActionButton fab;
    private EditText nisEdt, passwordEdt;
    private ImageView backBtn;
    private TextView noData;

    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    private EditText searchInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_teacher);

        util = new Util(mContext);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        reportRef = FirebaseDatabase.getInstance().getReference().child("Reports");
        scheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule");

        noData = findViewById(R.id.text_nodata);
        fab = findViewById(R.id.fab_add_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForm();
            }
        });

        usersList = findViewById(R.id.recycle_user);
        usersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        usersList.setLayoutManager(linearLayoutManager);

        searchInputText = findViewById(R.id.search_input);
        searchInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchBoxInput = searchInputText.getText().toString();
                searchFriends(searchBoxInput);
            }
        });

        DisplayAllUsers();
    }

    private void searchFriends(String searchBoxInput) {

        if (searchBoxInput.length() == 0 ){
            DisplayAllUsers();
        }else {
            Query searchFriendsQuery = userRef.orderByChild("nama")
                    .startAt(searchBoxInput)
                    .endAt(searchBoxInput + "\uf8ff");
            FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter
                    = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                    User.class,
                    R.layout.all_users_item,
                    UsersViewHolder.class,
                    searchFriendsQuery
            ) {
                @Override
                protected void populateViewHolder(final UsersViewHolder viewHolder, final User model, int position) {
                    Log.d("data", "populateViewHolder: " + getRef(position).getKey());
                    final String id = getRef(position).getKey();

                    if (model.getStatus().equals("siswa")){
                        viewHolder.mView.setVisibility(View.GONE);
                    }

                    viewHolder.setProfileimage(mContext, model.getProfilePhoto());
                    viewHolder.setFullname(model.getNama());
                    viewHolder.setNI(model.getNi());

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ViewTeacher.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });

                    viewHolder.deleteBtn.setVisibility(View.VISIBLE);
                    viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog = new AlertDialog.Builder(mContext);
                            dialog.setCancelable(true);
                            dialog.setTitle("Hapus Pengajar");
                            dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reportRef.orderByChild("teacher").equalTo(model.getNama()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String idReport = ds.getKey();
                                                reportRef.child(idReport).removeValue();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    scheduleRef.orderByChild("teacher").equalTo(model.getNama()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String idSchedule = ds.getKey();
                                                scheduleRef.child(idSchedule).removeValue();
                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

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


    }

    private void dialogForm(){

        dialog = new AlertDialog.Builder(mContext);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_data, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Tambah Pengajar");

        nisEdt = dialogView.findViewById(R.id.form_nis);
        passwordEdt = dialogView.findViewById(R.id.form_password);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String nis    = nisEdt.getText().toString();
                String password  = passwordEdt.getText().toString();

                if(isStringNull(nis) || isStringNull(password)){
                    Toast.makeText(mContext, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
                }else {
                    util.newUser(nis, password, "", "", "", "default",
                            "", "", "pengajar", "", "", "");
                }

                dialog.dismiss();
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

    private void DisplayAllUsers() {

        Query userQuery = userRef.orderByChild("status").equalTo("pengajar");
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
                final String id = getRef(position).getKey();
                viewHolder.setProfileimage(mContext, model.getProfilePhoto());
                viewHolder.setFullname(model.getNama());
                viewHolder.setNI(model.getNi());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewTeacher.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                });

                viewHolder.deleteBtn.setVisibility(View.VISIBLE);
                viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog = new AlertDialog.Builder(mContext);
                        dialog.setCancelable(true);
                        dialog.setTitle("Hapus Pengajar");
                        dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                reportRef.orderByChild("teacher").equalTo(model.getNama()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            String idReport = ds.getKey();
                                            reportRef.child(idReport).removeValue();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                scheduleRef.orderByChild("teacher").equalTo(model.getNama()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            String idSchedule = ds.getKey();
                                            scheduleRef.child(idSchedule).removeValue();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

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
            CircleImageView myImage = mView.findViewById(R.id.all_userprofile);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        private void setFullname(String fullname){
            TextView myName = mView.findViewById(R.id.all_name);
            myName.setText(fullname);
        }

        private void setNI(String ni){
            TextView friendsDate = mView.findViewById(R.id.all_ni);
            friendsDate.setText(ni);
        }

    }
}
