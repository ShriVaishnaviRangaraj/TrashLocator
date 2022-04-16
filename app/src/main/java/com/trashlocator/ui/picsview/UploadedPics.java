package com.trashlocator.ui.picsview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.trashlocator.R;
import com.trashlocator.ui.firebase.FirebaseInit;

public class UploadedPics extends AppCompatActivity {

    String phonenumber;
    DatabaseReference dbRef;
    UploadedPicsAdapter adapter;
    //
    RecyclerView recyclerView;
    //
    static boolean adapterSet=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_pics);

        //PHONE NUMBER from SHARED PREFERENCES
        SharedPreferences sharedPreferences=getSharedPreferences("com.trashlocator.userdetails", Context.MODE_PRIVATE);
        phonenumber=sharedPreferences.getString("PHONE",null);

        recyclerView=findViewById(R.id.uploadedPics_recyclerview);
        //Firebase option query
        Log.d("***b4 QUERY****", "Phone"+phonenumber );
        dbRef = FirebaseInit.getDatabase().getReference().child("USERS/"+phonenumber+"/photos");
        dbRef.keepSynced(true);
        FirebaseRecyclerOptions<UploadedPicsViewModel> options =
                new FirebaseRecyclerOptions.Builder<UploadedPicsViewModel>()
                        .setQuery(dbRef, UploadedPicsViewModel.class)
                        .setLifecycleOwner(this)
                        .build();
        Log.d("***AFTER QUERY****", "SNAP" );
        //

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("***AFTER QUERY****", snapshot.toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(UploadedPics.this);
        recyclerView.setLayoutManager(mLayoutManager);

        //Pass adapter
        adapterSet=true;
        adapter = new UploadedPicsAdapter(options);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(adapterSet) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
        }

    }
}