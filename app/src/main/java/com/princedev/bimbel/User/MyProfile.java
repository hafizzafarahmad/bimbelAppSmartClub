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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.princedev.bimbel.R;
import com.princedev.bimbel.Utils.SaveDataPreference;
import com.princedev.bimbel.Utils.Util;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class MyProfile extends AppCompatActivity {

    private DatabaseReference profileUserRef;
    private StorageReference userProfileImageRef;
    private Context mContext = MyProfile.this;

    private TextView profileName, profileAddress, profileTtl, profileNI, profileMataPel,
                        profilePhone, profileStudy, changeProfileImage, profileParent, profileParentPhone;
    private CircleImageView profileImage, imageEdt;
    private Button editprofileBtn;
    private EditText nameEdt, addressEdt, ttlEdt, phoneEdt, studyEdt, parentEdt, phoneParentEdt, mataPeEdt;
    private ImageView backBtn;

    private ProgressDialog loadingBar;

    private String name, address, ttl, phone, study, ni, pass, stat, mataPel,
            classroom, myProfileImage, parent, parentPhone;

    final static int Gallery_Pick = 1;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        ni = SaveDataPreference.getUserNI(mContext);
        pass = SaveDataPreference.getUserPassword(mContext);
        stat = SaveDataPreference.getUserStatus(mContext);

        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(ni);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        util = new Util(mContext);
        loadingBar = new ProgressDialog(this);

        profileImage = findViewById(R.id.profileimage);
        profileName = findViewById(R.id.profilename);
        profileAddress = findViewById(R.id.profileaddress);
        profileNI = findViewById(R.id.profileemail);
        profileTtl = findViewById(R.id.profilettl);
        profilePhone = findViewById(R.id.profilephone);
        profileStudy = findViewById(R.id.profilestudy);
        profileParent = findViewById(R.id.profileparent);
        profileParentPhone = findViewById(R.id.profileparentphone);
        profileMataPel = findViewById(R.id.profilematapel);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (stat.equals("siswa")){
            profileParent.setVisibility(View.VISIBLE);
            profileParentPhone.setVisibility(View.VISIBLE);
            profileMataPel.setVisibility(View.GONE);
        }

        editprofileBtn = findViewById(R.id.editprofilebtn);
        editprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForm();
            }
        });

        showProfile();

    }

    private void dialogForm(){

        dialog = new AlertDialog.Builder(mContext);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_edit_profile, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Edit Profile");

        imageEdt = dialogView.findViewById(R.id.form_profileimage);
        changeProfileImage = dialogView.findViewById(R.id.changeProfilePhoto);

        nameEdt = dialogView.findViewById(R.id.form_edt_name);
        addressEdt = dialogView.findViewById(R.id.form_edt_address);
        ttlEdt = dialogView.findViewById(R.id.form_edt_ttl);
        phoneEdt = dialogView.findViewById(R.id.form_edt_phone);
        studyEdt = dialogView.findViewById(R.id.form_edt_study);
        parentEdt = dialogView.findViewById(R.id.form_edt_parent);
        phoneParentEdt = dialogView.findViewById(R.id.form_edt_parentphone);
        mataPeEdt = dialogView.findViewById(R.id.form_edt_matapel);

        parentEdt.setVisibility(View.GONE);
        phoneParentEdt.setVisibility(View.GONE);
        mataPeEdt.setVisibility(View.VISIBLE);

        if (stat.equals("siswa")){
            parentEdt.setVisibility(View.VISIBLE);
            phoneParentEdt.setVisibility(View.VISIBLE);
            mataPeEdt.setVisibility(View.GONE);
        }

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        Picasso.with(MyProfile.this).load(myProfileImage).placeholder(R.drawable.profile).into(imageEdt);
        nameEdt.setText(name);
        addressEdt.setText(address);
        ttlEdt.setText(ttl);
        phoneEdt.setText(phone);
        studyEdt.setText(study);
        parentEdt.setText(parent);
        phoneParentEdt.setText(parentPhone);
        mataPeEdt.setText(classroom);


        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String nameDialog    = nameEdt.getText().toString();
                String addressDialog  = addressEdt.getText().toString();
                String ttlDialog  = ttlEdt.getText().toString();
                String phoneDialog  = phoneEdt.getText().toString();
                String studyDialog  = studyEdt.getText().toString();
                String parentDialog  = parentEdt.getText().toString();
                String phoneParentDialog  = phoneParentEdt.getText().toString();
                String mataPelDialog = mataPeEdt.getText().toString();

                if(isStringNull(nameDialog) || isStringNull(addressDialog) || isStringNull(ttlDialog) ||
                        isStringNull(phoneDialog)|| isStringNull(studyDialog)){
                    Toast.makeText(mContext, "Semua Form Harus di Isi", Toast.LENGTH_SHORT).show();
                }else {
                    util.updateStudent(ni,pass, nameDialog, addressDialog, ttlDialog, "default",
                            phoneDialog, studyDialog, stat, mataPelDialog, parentDialog, phoneParentDialog);
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

    private void showProfile(){
        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    name = dataSnapshot.child("nama").getValue().toString();
                    address = dataSnapshot.child("address").getValue().toString();
                    ttl = dataSnapshot.child("ttl").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    study = dataSnapshot.child("study").getValue().toString();
                    ni = dataSnapshot.child("ni").getValue().toString();
                    classroom = dataSnapshot.child("classroom").getValue().toString();
                    myProfileImage = dataSnapshot.child("profilePhoto").getValue().toString();
                    parent = dataSnapshot.child("parent").getValue().toString();
                    parentPhone = dataSnapshot.child("phoneParent").getValue().toString();

                    Picasso.with(MyProfile.this).load(myProfileImage).placeholder(R.drawable.profile).into(profileImage);
                    profileName.setText(name);
                    profileAddress.setText(address);
                    profileNI.setText(ni);
                    profileTtl.setText(ttl);
                    profilePhone.setText(phone);
                    profileStudy.setText(study);
                    profileParent.setText(parent);
                    profileParentPhone.setText(parentPhone);
                    profileMataPel.setText(classroom);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // select profile
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){

                loadingBar.setTitle("Updating Profile Image");
                loadingBar.setMessage("Please Wait...");

                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();
                StorageReference filePath = userProfileImageRef.child(ni + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){
//                            Toast.makeText(SettingsActivity.this, "Profile Image Telah Disimpan.", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            profileUserRef.child("profilePhoto").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Intent selfIntent = new Intent(MyProfile.this, MyProfile.class);
                                                startActivity(selfIntent);

                                                loadingBar.dismiss();
//                                                Toast.makeText(SettingsActivity.this, "Profile Image Disimpan di database", Toast.LENGTH_SHORT).show();
                                            }else {
                                                loadingBar.dismiss();
                                                String message = task.getException().getMessage();
                                                Toast.makeText(MyProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }else {
                loadingBar.dismiss();
//                Toast.makeText(this, "Error : Gambar Tidak bisa di Crop", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
