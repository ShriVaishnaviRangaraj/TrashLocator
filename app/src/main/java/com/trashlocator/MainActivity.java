package com.trashlocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.trashlocator.ui.Auth.signup.SignUp;
import com.trashlocator.ui.MainDashboard;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    Timer timer;
    //Variables
    Boolean logStatus=false;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CHECK LOGGED IN STATUS
        sharedPreferences=getSharedPreferences("com.trashlocator.userdetails", Context.MODE_PRIVATE);
        logStatus=sharedPreferences.getBoolean("LOG_STATE",false);

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(logStatus){
                    Intent intent = new Intent(MainActivity.this, MainDashboard.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(MainActivity.this, SignUp.class);
                    startActivity(intent);
                    finish();
                }

            }
        },3000);
    }
}