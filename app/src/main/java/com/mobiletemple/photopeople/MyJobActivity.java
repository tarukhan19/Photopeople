package com.mobiletemple.photopeople;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;

import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.fragment.MyJobComplte_Fragment;
import com.mobiletemple.photopeople.fragment.MyJobDeclineFragment;
import com.mobiletemple.photopeople.fragment.MyJobProcessFragment;
import com.mobiletemple.photopeople.fragment.MyJob_WaitingFragment;

import java.util.ArrayList;
import java.util.List;

public class MyJobActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Intent intent;
    int tabIndex = 0;
    boolean isConnected;
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_job);
        intent=getIntent();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("My Job");
        filter.setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        from=intent.getStringExtra("from");

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equalsIgnoreCase("notification")) {
                    Intent intent = new Intent(MyJobActivity.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "notification");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
                else  if (from.equalsIgnoreCase("profile")) {
                    Intent intent = new Intent(MyJobActivity.this, ProjectActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "profile");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
                else
                {
                    Intent intent = new Intent(MyJobActivity.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "other");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }

            }
        });
        tabIndex = getIntent().getIntExtra("tabIndex", 0);
        setupTabIcons();


    }

    @SuppressLint("ResourceType")
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Waiting");
//        tabLayout.getTabAt(0).setCustomView(tabOne);

        tabOne.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabOne.setTextSize(10);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("In Progress");
//        tabLayout.getTabAt(1).setCustomView(tabTwo);
        tabTwo.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabTwo.setTextSize(10);
        tabLayout.getTabAt(1).setCustomView(tabTwo);


        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Decline");
//        tabLayout.getTabAt(2).setCustomView(tabThree);
        tabThree.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabThree.setTextSize(10);
        tabLayout.getTabAt(2).setCustomView(tabThree);


        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Completed");
//        tabLayout.getTabAt(2).setCustomView(tabThree);
        tabFour.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabFour.setTextSize(10);
        tabLayout.getTabAt(3).setCustomView(tabFour);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MyJob_WaitingFragment(), "Waiting");
        adapter.addFrag(new MyJobProcessFragment(), "In Progress");
        adapter.addFrag(new MyJobDeclineFragment(), "Decline");
        adapter.addFrag(new MyJobComplte_Fragment(), "Completed");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equalsIgnoreCase("notification")) {
            Intent intent = new Intent(MyJobActivity.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "notification");
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
        else  if (from.equalsIgnoreCase("profile")) {
            Intent intent = new Intent(MyJobActivity.this, ProjectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "profile");
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
        else
        {
            Intent intent = new Intent(MyJobActivity.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "other");
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }

    }



    @Override
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }


        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(final boolean isConnected) {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;


        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected) {
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }

                    }
                });

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }

}
