package com.trashlocator.ui.Auth.signup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trashlocator.R;
import com.trashlocator.ui.Auth.signin.ExistingUserSignIn;

import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_LONG;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    public TextView alreadyuser;
    public Button Verify;
    public EditText Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        alreadyuser = (TextView) findViewById(R.id.textView3);

        alreadyuser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v1) {

                Intent i = new Intent(SignUp.this, ExistingUserSignIn.class);
                startActivity(i);
            }
        });

        Verify = findViewById(R.id.send_otp);
        Verify.setOnClickListener(this);

        Phone = findViewById(R.id.phone_edittext);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_otp: {

                if (isValidMobile()) {
                    new AlertDialog.Builder(SignUp.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Please confirm")
                            .setMessage("You might not be able to change your phone number again")
                            .setPositiveButton("Yes, I understand",new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(SignUp.this, UserVerification.class);
                                    String phone = Phone.getText().toString();
                                    intent.putExtra("Phone",phone);
                                    startActivity(intent);
                                }

                            })
                            .show();
                } else {
                    Log.d("TAG","***********NEITHER EMAIL NOR PHONE NUMBER************");
                    Toast.makeText(SignUp.this, "Invalid phone number", LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public boolean isValidMobile() {
        String phone = Phone.getText().toString();
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() == 10) {
                check = true;
            } else
                Toast.makeText(getApplicationContext(), "Incorrect phone number", LENGTH_LONG);

        } else
            Toast.makeText(getApplicationContext(), "Invalid phone number", LENGTH_LONG);
        return check;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}