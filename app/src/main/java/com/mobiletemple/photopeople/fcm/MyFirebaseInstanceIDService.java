package com.mobiletemple.photopeople.fcm;


import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.mobiletemple.photopeople.ChatNotification.Token;
import com.mobiletemple.photopeople.session.SessionManager;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
SessionManager sessionManager;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //    String refreshedToken = FirebaseInstanceId.getInstance().getInstanceId ();


        Log.e("refreshedToken",s);
//
//        storeRegIdInPref(refreshedToken);
//
//        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
//        registrationComplete.putExtra("token", refreshedToken);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

        sendRegistrationToServer(s);

    }

    private void sendRegistrationToServer(String refreshedToken) {
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
