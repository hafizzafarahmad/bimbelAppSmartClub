package com.princedev.bimbel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.princedev.bimbel.Utils.SaveDataPreference;

import static com.princedev.bimbel.Utils.Util.isStringNull;

public class Login extends AppCompatActivity {

    private DatabaseReference userRef;

    private Context mContext = Login.this;
    private EditText loginNI, loginPassword;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToast = new Toast(mContext);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        loginNI = findViewById(R.id.login_NI);
        loginPassword = findViewById(R.id.login_password);

        login();
    }

    private void login(){
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Mengambil data dari Firebas
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String nomor = ds.child("ni").getValue().toString();
                            String name = ds.child("nama").getValue().toString();
                            String pass = ds.child("password").getValue().toString();
                            String status = ds.child("status").getValue().toString();
                            String classroom = ds.child("classroom").getValue().toString();

                            //Mengambil data dari Inputan
                            final String nis = loginNI.getText().toString();
                            final String passwordLogin = loginPassword.getText().toString();

                            //Validasi Nomor induk dan Password
                            if(isStringNull(nis) || isStringNull(passwordLogin)){
                                Toast.makeText(mContext, "Masukan Nomor Induk dan Password", Toast.LENGTH_SHORT).show();
                            }else if (nomor.equals(nis) && pass.equals(passwordLogin)){

                                //Save data login dipenyimpanan Lokal
                                SaveDataPreference.setUserNI(mContext, nomor);
                                SaveDataPreference.setUserPassword(mContext, pass);
                                SaveDataPreference.setUserStatus(mContext, status);
                                SaveDataPreference.setUserClass(mContext, classroom);
                                SaveDataPreference.setUserName(mContext, name);

                                Intent intent = new Intent(mContext, UserMain.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
