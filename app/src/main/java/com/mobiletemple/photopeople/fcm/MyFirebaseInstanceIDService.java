package com.mobiletemple.photopeople.fcm;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mobiletemple.photopeople.ChatNotification.Token;
import com.mobiletemple.photopeople.session.SessionManager;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
SessionManager sessionManager;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        sessionManager = new SessionManager(getApplicationContext());

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken=FirebaseInstanceId.getInstance().getToken();
        Log.e("toekn",refreshToken);
        if (firebaseUser!=null)
        {
            updateToken(refreshToken);
        }

        storeRegIdInPref(refreshToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(refreshToken);
        reference.child(sessionManager.getLoginSession().get(SessionManager.KEY_USERID)).setValue(token);

    }
    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
