package com.mobiletemple.photopeople.Freelancer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mobiletemple.photopeople.AcceptEventconfirmation;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Chat.ChatActivity;
import com.mobiletemple.photopeople.FilterActivity;
import com.mobiletemple.photopeople.InboxActivity;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.PayuMoneyIntegration.ProdPurchaseActivity;
import com.mobiletemple.photopeople.ProfileUpdate;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.SendQuote;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.fragment.FreelancerPersonalDetal_Frag;
import com.mobiletemple.photopeople.fragment.Freelancer_Imagedetail_frag;
import com.mobiletemple.photopeople.fragment.Freelancer_review_frag;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.DynamiclinkCreate;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;

public class FreelancerDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Intent intent;
    ImageView iv_pic;
    String freelancerId, dateofbirth, first_name, expS = "0", email, Mobile_no, quoteId, studioid, sender;
    RequestQueue queue;
    ProgressDialog progressDialog;
    SessionManager mSessionManager;
    TextView quote, username, age, exp, MakePayment, per75amount, per25amount, editprofile;
    String price = "", jobstatus;
    String from = "deeplink";
    private int paymentStage = 0;
    double amountinDouble = 0.0;
    private RatingBar ratingBar;
    LinearLayout msgll;
    TextView msg;
    TextView applycoupon;
    boolean isConnected;
    LinearLayout afterbooking;
    String profile, url, promocode_status,promo_code;
    Uri deepLink;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_details);

        quote = findViewById(R.id.quote);
        afterbooking = findViewById(R.id.afterbooking);
        MakePayment = findViewById(R.id.MakePayment);
        editprofile = findViewById(R.id.editprofile);
        iv_pic = findViewById(R.id.iv_pic);
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        mSessionManager = new SessionManager(this.getApplicationContext());
        username = findViewById(R.id.username);
        exp = findViewById(R.id.exp);
        age = findViewById(R.id.age);
        per75amount = findViewById(R.id.per75amount);
        per25amount = findViewById(R.id.per25amount);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        msgll = findViewById(R.id.msgll);
        msg = findViewById(R.id.msg);
        applycoupon = findViewById(R.id.applycoupon);
        intent = getIntent();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        ImageView filter = (ImageView) toolbar.findViewById(R.id.filterpic);
        ImageView share = toolbar.findViewById(R.id.share);
        ImageView filterpic = toolbar.findViewById(R.id.filterpic);
        filterpic.setImageResource(R.drawable.inbox_fre_inact);

        share.setVisibility(View.VISIBLE);
        mTitle.setText("Freelancer Details");

        setupViewPager(viewPager);

        setupTabIcons();
        mayRequestPermissions();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DynamiclinkCreate.sharelink("", FreelancerDetails.this, "Have a look on " +
                        first_name + " (Freelancer) profile.", Endpoints.FREELANCE_PERSONAL_DETAIL + "?user_type=2" + "&freelancer_id="
                        + freelancerId, "https://photopeoplefreelancerdetails.page.link/");
            }
        });


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(FreelancerDetails.this, ChatActivity.class);
                in.putExtra("studioID", studioid);
                in.putExtra("freelancerId", freelancerId);
                in.putExtra("quoteId", quoteId);
                in.putExtra("from", "freedetail");
                in.putExtra("status", jobstatus);
                in.putExtra("recivertype", "2");
                in.putExtra("RECIVERid", freelancerId);
                ////Log.e("adapterpass",freelancer.getStudioId()+" "+freelancer.getId()+" "+freelancer.getQuoteId()+" "+Constants.STUDIO_TYPE);

                startActivity(in);
            }
        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.hasExtra("from")) {

                    if (from.equalsIgnoreCase("profile")) {
                        Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "profile");
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    } else if (from.equalsIgnoreCase("hire")) {
                        Intent intent = new Intent(FreelancerDetails.this, InboxActivity.class);
                        intent.putExtra("from", "hire");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (from.equalsIgnoreCase("homepage") || from.equalsIgnoreCase("featurehome")
                    ) {
                        Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "other");

                        startActivity(intent);
                    } else if (from.equalsIgnoreCase("timeline")) {
                        Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "timeline");

                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(FreelancerDetails.this, FilterActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "other");

                    startActivity(intent);
                }
            }
        });


        if (intent.hasExtra("from")) {

            freelancerId = intent.getStringExtra("freelancerId");
            first_name = intent.getStringExtra("first_name");
            from = intent.getStringExtra("from");
            jobstatus = intent.getStringExtra("status");
            quoteId = intent.getStringExtra("quoteid");
            studioid = intent.getStringExtra("studioID");

            if (from.equalsIgnoreCase("featurehome") || from.equalsIgnoreCase("homepage")
                    || from.equalsIgnoreCase("timeline")) {
                afterbooking.setVisibility(View.VISIBLE);
                msgll.setVisibility(View.GONE);
                filter.setVisibility(View.GONE);
                MakePayment.setVisibility(View.GONE);
                if (freelancerId.equalsIgnoreCase(mSessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {
                    quote.setVisibility(View.GONE);
                    editprofile.setVisibility(View.VISIBLE);
                    applycoupon.setVisibility(View.INVISIBLE);

                } else {
                    quote.setVisibility(View.VISIBLE);
                    editprofile.setVisibility(View.GONE);
                    applycoupon.setVisibility(View.INVISIBLE);

                }
            }

            if (from.equalsIgnoreCase("filter")) {
                afterbooking.setVisibility(View.VISIBLE);
                msgll.setVisibility(View.GONE);
                filter.setVisibility(View.GONE);
                price = intent.getStringExtra("quoteAmount");
                MakePayment.setVisibility(View.GONE);

                if (freelancerId.equalsIgnoreCase(mSessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {
                    quote.setVisibility(View.GONE);
                    editprofile.setVisibility(View.VISIBLE);
                    applycoupon.setVisibility(View.INVISIBLE);

                } else {
                    quote.setVisibility(View.VISIBLE);
                    editprofile.setVisibility(View.GONE);
                    applycoupon.setVisibility(View.INVISIBLE);

                }
            }


            if (from.equalsIgnoreCase("hire")) {

                if (jobstatus.equalsIgnoreCase("1")) {
                    afterbooking.setVisibility(View.GONE);
                    msgll.setVisibility(View.VISIBLE);
                    msg.setText("Your Quote sent successfully. Please wait for freelancer to accept it.");

                } else if (jobstatus.equalsIgnoreCase("2")) {
                    afterbooking.setVisibility(View.VISIBLE);
                    quote.setVisibility(View.GONE);
                    editprofile.setVisibility(View.GONE);
                    MakePayment.setVisibility(View.VISIBLE);
                    msgll.setVisibility(View.GONE);
                    applycoupon.setVisibility(View.VISIBLE);


                } else if (jobstatus.equalsIgnoreCase("3")) {
                    afterbooking.setVisibility(View.VISIBLE);
                    quote.setVisibility(View.GONE);
                    editprofile.setVisibility(View.GONE);
                    MakePayment.setVisibility(View.VISIBLE);
                    msgll.setVisibility(View.GONE);
                    applycoupon.setVisibility(View.VISIBLE);


                }


            }

            if (from.equalsIgnoreCase("profile")) {

                afterbooking.setVisibility(View.VISIBLE);
                mTitle.setText("My Profile");
                filter.setVisibility(View.GONE);
                applycoupon.setVisibility(View.INVISIBLE);
                quote.setVisibility(View.GONE);
                MakePayment.setVisibility(View.GONE);
                editprofile.setVisibility(View.VISIBLE);
                msgll.setVisibility(View.GONE);
            }
        } else {
            afterbooking.setVisibility(View.VISIBLE);
            msgll.setVisibility(View.GONE);
            filter.setVisibility(View.GONE);
            MakePayment.setVisibility(View.GONE);
        }


        quote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(FreelancerDetails.this, SendQuote.class);
                in.putExtra("freelancerId", freelancerId);
                in.putExtra("starting_price", String.valueOf(amountinDouble));
                if (intent.hasExtra("from")) {
                    if (from.equalsIgnoreCase("featurehome") || from.equalsIgnoreCase("homepage")
                            || from.equalsIgnoreCase("timeline")) {
                        in.putExtra("selection", "1");
                        in.putExtra("from", from);

                    } else if (from.equalsIgnoreCase("filter")) {
                        in.putExtra("selection", "2");
                        in.putExtra("from", from);

                    }

                } else {
                    in.putExtra("selection", "1");
                    in.putExtra("from", from);

                }

                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FreelancerDetails.this, ProfileUpdate.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });
        MakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jobstatus.equalsIgnoreCase("2")) {

                    double amd = ((double) amountinDouble * 25) / 100;

                    String am=String.format("%.2f", amd);
                    double amount= Double.parseDouble(am);
                    int stage = 1;
                    //Log.e("Stage 1 AMT", amount + "");
                    Intent in = new Intent(FreelancerDetails.this, AcceptEventconfirmation.class);
                    in.putExtra("amount", amount);
                    in.putExtra("stage", stage);
                    in.putExtra("first_name", first_name);
                    in.putExtra("freelancerId", freelancerId);
                    in.putExtra("Mobile_no", Mobile_no);
                    in.putExtra("email", email);
                    in.putExtra("quoteid", quoteId);

                    Log.e("AcceptEventconfirmation",amount+" "+stage+" "+first_name+" "+freelancerId+" "+Mobile_no+" "+email+" "+quoteId);

                    startActivity(in);
                    finish();
                }

                if (jobstatus.equalsIgnoreCase("3")) {

                    double amd = ((double) amountinDouble * 75) / 100;
                    String am=String.format("%.2f", amd);
                    double amount= Double.parseDouble(am);
                    int stage = 2;
                    Intent in = new Intent(FreelancerDetails.this, ProdPurchaseActivity.class);
                    in.putExtra("amount", String.valueOf(amount));
                    in.putExtra("stage", stage);
                    in.putExtra("first_name", first_name);
                    in.putExtra("freelancerId", freelancerId);
                    in.putExtra("Mobile_no", Mobile_no);
                    in.putExtra("email", email);
                    in.putExtra("quoteid", quoteId);
                    in.putExtra("from", "booking");
                    in.putExtra("prodname", "Freelancer Booking");

                    startActivity(in);
                    finish();
                }

            }
        });


        if (!mSessionManager.isLoggedIn()) {
            Intent intent = new Intent(FreelancerDetails.this, Login.class);
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
                                Intent intent = getIntent();
                                Uri data = intent.getData();
                                String param1 = data.getQueryParameter("studio_id");

                            }

                            if (deepLink != null) {
                                Log.e("deeeplinkkk", deepLink.toString());
                                url = deepLink.toString();
                                loadfreelancer();

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

        applycoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCouponDialog();
            }
        });
    }

    private void openCouponDialog()
    {
        final Dialog dialog = new Dialog(FreelancerDetails.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_couponcode);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout crossImage = (LinearLayout) dialog.findViewById(R.id.crossImage) ;
        LinearLayout submit = (LinearLayout) dialog.findViewById(R.id.submit) ;
        final EditText couponcodeET=dialog.findViewById(R.id.couponcodeET);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponcodeET.getText().toString().isEmpty())
                {
                    Toast.makeText(FreelancerDetails.this, "Enter valid coupon.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    promo_code=couponcodeET.getText().toString().trim();
                    dialog.dismiss();
                    submitCouponCode();
                }
            }
        });


        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void submitCouponCode()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PROMOCODE_FREELANCER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            amountinDouble = obj.getDouble("paid_price");
                            double amount25perc = ((double) amountinDouble * 25) / 100;
                            double amount75perc = ((double) amountinDouble * 75) / 100;
                            per75amount.setText(String.valueOf(amount75perc));
                            per25amount.setText(String.valueOf(amount25perc));
                            applycoupon.setText("Coupon applied successfully.");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("promo_code",promo_code );
                ob.put("user_type",mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("user_id", mSessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("freelancer_id", freelancerId+"");

                Log.e("params",ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
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
        adapter.addFrag(new FreelancerPersonalDetal_Frag(), "Personal Info");
        adapter.addFrag(new Freelancer_Imagedetail_frag(), "Gallery");
        adapter.addFrag(new Freelancer_review_frag(), "Review");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

    }


    private void loadfreelancer() {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("url", url);

                        progressDialog.dismiss();
                        Log.e("FREELANCE_PERSONAL", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("Message");

                            if (status == 200 && message.equalsIgnoreCase("success")) {
                                JSONObject object = obj.getJSONObject("Details");
//                                quoteId=object.getString("quoteId");
                                freelancerId = object.getString("user_id");
                                first_name = object.getString("first_name");
                                email = object.getString("email");
                                Mobile_no = object.getString("Mobile_no");
                                promocode_status = object.getString("promocode_status");
                                ratingBar.setRating((float) object.getDouble("freelancer_rating"));



                                dateofbirth = object.getString("dob");
                                expS = object.getString("exp_year");
                                profile = object.getString("profile");
                                amountinDouble = object.getDouble("starting_price");

                                username.setText(first_name);
                                exp.setText(expS + " Year Experience");

                                if (!intent.hasExtra("from"))
                                {
                                    if (freelancerId.equalsIgnoreCase(mSessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {
                                        quote.setVisibility(View.GONE);
                                        editprofile.setVisibility(View.VISIBLE);
                                        applycoupon.setVisibility(View.INVISIBLE);

                                    } else {
                                        quote.setVisibility(View.VISIBLE);
                                        editprofile.setVisibility(View.GONE);
                                        if (promocode_status.equalsIgnoreCase("0")) {
                                            applycoupon.setVisibility(View.INVISIBLE);
                                        } else {
                                            applycoupon.setVisibility(View.VISIBLE);
                                        }

                                    }
                                }

                                if (!profile.isEmpty()) {
                                    Glide.with(FreelancerDetails.this).load(profile).centerCrop().placeholder(R.drawable.default_new_img)
                                            .error(R.drawable.default_new_img)
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(iv_pic);
                                } else {
                                    Glide.with(FreelancerDetails.this).load(R.drawable.default_new_img).centerCrop().placeholder(R.drawable.default_new_img)
                                            .error(R.drawable.default_new_img)
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(iv_pic);
                                }


                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                                try {
                                    Date dob = sdf.parse(dateofbirth);
                                    Date dt = new Date();

                                    int diff = getDiffYears(dob, dt);
                                    age.setText("(" + diff + " age)");
                                    //Log.e("diff",diff+"");

                                } catch (Exception ex) {
                                    ////Log.e("DOB Parse Err",ex.getMessage());
                                    age.setText("");
                                }

                                double amount25perc = ((double) amountinDouble * 25) / 100;
                                double amount75perc = ((double) amountinDouble * 75) / 100;
                                per75amount.setText(String.valueOf(amount75perc));
                                per25amount.setText(String.valueOf(amount25perc));

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
                        progressDialog.dismiss();
                    }
                }
        );
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private int getDiffYears(Date dob, Date dt) {
        Calendar a = getCalendar(dob);
        Calendar b = getCalendar(dt);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;

    }

    public Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
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
        if (intent.hasExtra("from")) {

            if (from.equalsIgnoreCase("profile")) {
                Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "profile");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            } else if (from.equalsIgnoreCase("hire")) {
                Intent intent = new Intent(FreelancerDetails.this, InboxActivity.class);
                intent.putExtra("from", "hire");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (from.equalsIgnoreCase("homepage") || from.equalsIgnoreCase("featurehome")
            ) {
                Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "other");

                startActivity(intent);
            } else if (from.equalsIgnoreCase("timeline")) {
                Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "timeline");

                startActivity(intent);
            } else {
                Intent intent = new Intent(FreelancerDetails.this, FilterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(FreelancerDetails.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "other");

            startActivity(intent);
        }
    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            showSnack(isConnected);
        } else {
            url = Endpoints.FREELANCE_PERSONAL_DETAIL + "?freelancer_id="
                    + freelancerId + "&user_type=2&amount=" + price;
            loadfreelancer();
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
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        showSnack(isConnected);
    }


    private boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.permission_rationale);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage("Please confirm access to files & folders");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{CALL_PHONE},
                            Constants.PERMISSION_REQUEST);
                }
            });
            builder.show();
        } else {
            requestPermissions(new String[]{CALL_PHONE,
                    },
                    Constants.PERMISSION_REQUEST);
        }
        return false;
    }


}
