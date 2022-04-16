package com.trashlocator.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.trashlocator.R;

import java.util.Timer;
import java.util.TimerTask;

public class Uploaded extends AppCompatActivity {

    public Timer timer;
    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded);


        titleText=findViewById(R.id.registered_caption_textView);

       /* Bundle bundle=getIntent().getExtras();
        if(bundle.getBoolean("orderplaced")){
            titleText.setText("ORDER PLACED");
        }else{
            titleText.setText("REGISTERED SUCCESSFULLY");
        }*/

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent(Uploaded.this, MainDashboard.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}