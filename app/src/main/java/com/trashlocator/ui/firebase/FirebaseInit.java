package com.trashlocator.ui.firebase;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInit {


    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;

    }


}