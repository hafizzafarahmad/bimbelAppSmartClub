package com.princedev.bimbel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.princedev.bimbel.Utils.SaveDataPreference;

public class SplashScreen extends AppCompatActivity {

    private Context mContext = SplashScreen.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(SaveDataPreference.getUserNI(mContext).length() == 0) {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), UserMain.class));
                    finish();
                }

            }
        }, 3000);
    }
}
