package com.mobiletemple.photopeople;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.appevents.AppEventsConstants;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.Freelancer.FreelancerProfileOne;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        AppEventsLogger logger = AppEventsLogger.newLogger(this);
//        logger.logEvent(AppEventsConstants.);

        getSupportActionBar().hide();
        final SessionManager session = new SessionManager(getApplicationContext());
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if (session.isLoggedIn()) {

                    startActivity(new Intent(SplashActivity.this, HomePage.class).putExtra("from","other"));
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);

                    finish();
                } else

                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                finish();
            }
        }, 1500);


    }

    @Override
    protected void onResume() {
        super.onResume();

// Clear all notification
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
///ash Key:: 5wV80G3ZmHWWxVkk4Ld8rH9jsL4=