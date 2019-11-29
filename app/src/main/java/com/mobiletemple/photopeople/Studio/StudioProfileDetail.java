package com.mobiletemple.photopeople.Studio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.DynamiclinkCreate;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudioProfileDetail extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Intent intent;
    ImageView iv_pic;
    String studioId, first_name, dateofbirth, expS = "0", email, url;
    RequestQueue queue;
    ProgressDialog dialog;
    SessionManager mSessionManager;
    TextView quote, username, age, exp, MakePayment;
    String starting_price, jobstatus, quoteAmount;
    String from;
    private int paymentStage = 0;
    double amountinDouble = 0.0;
    private RatingBar ratingBar;
    TextView per20amount, per30amount, per50amount;
    LinearLayout afterbooking;
    boolean isConnected;
    RelativeLayout ll;
    String profile;

    Uri deepLink = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_profile_detail);
        quote = findViewById(R.id.quote);
        afterbooking = findViewById(R.id.afterbooking);
        MakePayment = findViewById(R.id.MakePayment);
        iv_pic = findViewById(R.id.iv_pic);
        ll = findViewById(R.id.ll);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        mSessionManager = new SessionManager(this.getApplicationContext());
        username = findViewById(R.id.username);
        exp = findViewById(R.id.exp);
        age = findViewById(R.id.age);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        intent = getIntent();
        per20amount = findViewById(R.id.per20amount);
        per30amount = findViewById(R.id.per30amount);
        per50amount = findViewById(R.id.per50amount);
        from = intent.getStringExtra("from");
        studioId = intent.getStringExtra("studioId");



        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        ImageView filterpic = toolbar.findViewById(R.id.filterpic);
        ImageView share = toolbar.findViewById(R.id.share);
        filterpic.setVisibility(View.GONE);
        share.setVisibility(View.VISIBLE);
        mTitle.setText("Studio Details");


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamiclinkCreate.sharelink(profile, StudioProfileDetail.this, "Have a look on " +
                        first_name + " (Studio) profile.", Endpoints.LOAD_STUDIO_PROFILE + "?studio_id=" + studioId, "https://photopeoplestudiodetail.page.link/");

            }
        });




        if (!mSessionManager.isLoggedIn())
        {
            Intent intent = new Intent(StudioProfileDetail.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
            finish();
        } else {


            // [START get_deep_link]
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
//                                Intent intent = getIntent();
//                                Uri data = intent.getData();
//                                String param1 = data.getQueryParameter("studio_id");
//                                Toast.makeText(StudioProfileDetail.this, param1, Toast.LENGTH_SHORT).show();

                            }

                            if (deepLink != null) {
                                Log.e("deeeplinkkk", deepLink.toString());
                                url = deepLink.toString();
                                loadStudio();

                            } else {
                                Log.d("deeplink", "getDynamicLink: no link found");
                            }
                            // [END_EXCLUDE]
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("deeplink", "getDynamicLink:onFailure", e);
                        }
                    });
            // [END get_deep_link]
        }

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (intent.hasExtra("from")) {
                    if (from.equalsIgnoreCase("profile")) {
                        Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "profile");

                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    } else if (from.equalsIgnoreCase("homepage")) {
                        Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
                        intent.putExtra("from", "other");

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (from.equalsIgnoreCase("timeline")) {
                        Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "timeline");

                        startActivity(intent);
                    }
                }

                else
                {
                    Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
                    intent.putExtra("from", "other");

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }


            }
        });

        setupTabIcons();

    }


    @SuppressLint("ResourceType")
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Basic Info");
        tabOne.setTextSize(14);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Gallery");
        tabTwo.setTextSize(14);
        tabLayout.getTabAt(1).setCustomView(tabTwo);


        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Review");
        tabThree.setTextSize(14);
        tabLayout.getTabAt(2).setCustomView(tabThree);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new StudioPersonalDetal_Frag(), "Personal Info");
        adapter.addFrag(new Studio_Imagedetail_frag(), "Gallery");
        adapter.addFrag(new Studio_review_frag(), "Review");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

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


    private void loadStudio() {

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog.dismiss();
                        ll.setVisibility(View.VISIBLE);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success")) {
                                JSONObject jsonObject = obj.getJSONObject("data");
                                profile = jsonObject.getString("profile_image");

                                first_name = jsonObject.getString("first_name");
                                email = jsonObject.getString("email");
                                expS = jsonObject.getString("experiance");
                                amountinDouble = jsonObject.getDouble("starting_price");

                                username.setText(first_name);
                                if (expS.isEmpty()) {
                                    exp.setText("0 Year Experience");
                                } else {
                                    exp.setText(expS + " Year Experience");
                                }
                                if (!profile.isEmpty()) {
                                    Picasso.with(StudioProfileDetail.this).load(profile).placeholder(R.drawable.default_new_img)
                                            .into(iv_pic);
                                } else {
                                    iv_pic.setImageResource(R.drawable.default_new_img);
                                }
                                double amount20perc = ((double) amountinDouble * 20) / 100;
                                double amount30perc = ((double) amountinDouble * 30) / 100;
                                double amount50perc = ((double) amountinDouble * 50) / 100;

                                per20amount.setText(String.valueOf(amount20perc));
                                per30amount.setText(String.valueOf(amount30perc));
                                per50amount.setText(String.valueOf(amount50perc));


                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
                    }
                }
        );
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (intent.hasExtra("from")) {
            if (from.equalsIgnoreCase("profile")) {
                Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "profile");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            } else if (from.equalsIgnoreCase("homepage")) {
                Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
                intent.putExtra("from", "other");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (from.equalsIgnoreCase("timeline")) {
                Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "timeline");

                startActivity(intent);
            }
        }

        else
        {
            Intent intent = new Intent(StudioProfileDetail.this, HomePage.class);
            intent.putExtra("from", "other");

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            showSnack(isConnected);
        } else {
            url = Endpoints.LOAD_STUDIO_PROFILE + "?studio_id=" + studioId;
            loadStudio();
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
                            overridePendingTransition(0, 0);
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
        this.isConnected = isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);


    }

}
