package com.example.smartdustbin.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.smartdustbin.MainActivity;
import com.example.smartdustbin.R;
import com.example.smartdustbin.notification.MyBroadCastReceiver;
import com.example.smartdustbin.userManagement.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent openHome = new Intent(SplashScreenActivity.this, MainActivity.class);
                Intent logIn = new Intent(SplashScreenActivity.this, LoginActivity.class);

                //this starts notification.
                Intent intent = new Intent(getApplicationContext(), MyBroadCastReceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 2, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {
                    long triggerTime = 2 * 1000; //set time here to run the notifications.

                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1, triggerTime, sender);
                }

                if (mAuth.getCurrentUser() != null) {
                    startActivity(openHome);

                } else {
                    startActivity(logIn);
                }
                finish();
            }
        }, 1750);
    }
}