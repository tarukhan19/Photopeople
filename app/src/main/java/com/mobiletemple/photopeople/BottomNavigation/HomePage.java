package com.mobiletemple.photopeople.BottomNavigation;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mobiletemple.photopeople.ChatNotification.Token;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.TimeLine.TimelineFragment;
import com.mobiletemple.photopeople.databinding.ActivityHomeScreenBinding;
import com.mobiletemple.photopeople.session.SessionManager;

public class HomePage extends AppCompatActivity {

    ActivityHomeScreenBinding binding;
    Intent intent;
    BottomNavigationItemView itemView;

    String from;
    View badge;
    TextView text;
    SessionManager sessionManager;
    static HomePage homeActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(binding.navigation);
        sessionManager = new SessionManager(this);
        homeActivity = this;

        binding.navigation.getMenu().findItem(R.id.navigation_home).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);
        binding.navigation.setItemIconTintList(null);
        itemView = binding.navigation.findViewById(R.id.navigation_notify);
        badge = LayoutInflater.from(this).inflate(R.layout.layout_news_badge, binding.navigation, false);
        text = badge.findViewById(R.id.badge_text_view);
        intent = getIntent();
        from = intent.getStringExtra("from");


        if (from.equalsIgnoreCase("profile")) {
            if (savedInstanceState == null) {
                loadFragment(new CenterFragment());
            }
            binding.navigation.setSelectedItemId(R.id.navigation_center);

        }

       else if (from.equalsIgnoreCase("timeline")) {
            if (savedInstanceState == null) {
                loadFragment(new TimelineFragment());
            }
            binding.navigation.setSelectedItemId(R.id.navigation_timeline);

        }

        else if (from.equalsIgnoreCase("notification")) {
            if (savedInstanceState == null) {
                loadFragment(new NotificationFragment());
            }
            binding.navigation.setSelectedItemId(R.id.navigation_notify);

        }

        else if (from.equalsIgnoreCase("chat")) {
            if (savedInstanceState == null) {
                loadFragment(new ChattingFragment());
            }
            binding.navigation.setSelectedItemId(R.id.navigation_chat);

        }

        else {
            if (savedInstanceState == null) {
                loadFragment(new HomeFragment());
            }
//==            binding.navigation.setSelectedItemId(R.id.navigation_home);

        }
        uodateToken(FirebaseInstanceId.getInstance().getToken());
        setNotiIcon(sessionManager.getNotificationCount());


    }

    public static HomePage getInstance() {
        return homeActivity;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);

                    binding.navigation.getMenu().findItem(R.id.navigation_home).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_chat).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_notify).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_timeline).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    break;
                case R.id.navigation_chat:
                    fragment = new ChattingFragment();
                    loadFragment(fragment);
                   // Toast.makeText(HomePage.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                    binding.navigation.getMenu().findItem(R.id.navigation_home).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_chat).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_notify).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_timeline).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);

                    break;
                case R.id.navigation_notify:
                    fragment = new NotificationFragment();
                    loadFragment(fragment);
                    binding.navigation.getMenu().findItem(R.id.navigation_home).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_chat).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_notify).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_timeline).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    sessionManager.setNotificationCount(0);
                    showBadge(String.valueOf(0));

                    break;
                case R.id.navigation_center:
                    fragment = new CenterFragment();
                    loadFragment(fragment);
                    binding.navigation.getMenu().findItem(R.id.navigation_home).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_chat).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_notify).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_timeline).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    break;

                case R.id.navigation_timeline:
                    fragment = new TimelineFragment();
                    loadFragment(fragment);
                 //   Toast.makeText(HomePage.this, "Coming Soon", Toast.LENGTH_SHORT).show();

                    binding.navigation.getMenu().findItem(R.id.navigation_home).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_chat).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_notify).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
                    binding.navigation.getMenu().findItem(R.id.navigation_timeline).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    break;
            }
            return loadFragment(fragment);
        }
    };

    public boolean loadFragment(Fragment fragment)
    {
        if (fragment != null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (binding.navigation.getSelectedItemId() == R.id.navigation_home) {
            super.onBackPressed();
        } else {
            loadFragment(new HomeFragment());
           // binding.navigation.getMenu().findItem(R.id.navigation_home).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
           // binding.navigation.getMenu().findItem(R.id.navigation_chat).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
            //binding.navigation.getMenu().findItem(R.id.navigation_notify).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
           // binding.navigation.getMenu().findItem(R.id.navigation_timeline).getIcon().setColorFilter(getResources().getColor(R.color.featured), PorterDuff.Mode.SRC_IN);
            binding.navigation.setSelectedItemId(R.id.navigation_home);

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setNotiIcon(final int notificationCount) {

        Log.e("notificationCount", notificationCount + "");
        showBadge(String.valueOf(notificationCount));
    }


    public void runThread(final int notificationCount) {

        new Thread() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            Log.e("notificationCount", notificationCount + "");
                            showBadge(String.valueOf(notificationCount));
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showBadge(String value) {

        if (value.equalsIgnoreCase("0"))
        {
            if (itemView != null) {
                itemView.removeView(badge);
            }
            if (text.getVisibility() == View.VISIBLE)
                text.setVisibility(View.INVISIBLE);

        } else {
            if (Integer.parseInt(value) > 1) {
                if (itemView != null) {
                    itemView.removeView(badge);
                }
            }
            text.setVisibility(View.VISIBLE);
            text.setText(value);
            itemView.addView(badge);
        }

    }

    private void uodateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(sessionManager.getLoginSession().get(SessionManager.KEY_USERID)).setValue(token1);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


}
