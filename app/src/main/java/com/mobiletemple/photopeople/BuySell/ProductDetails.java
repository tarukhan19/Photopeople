package com.mobiletemple.photopeople.BuySell;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.facebook.login.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.PayuMoneyIntegration.ProdPurchaseActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.BannerPagerAdapter;
import com.mobiletemple.photopeople.customStyle.AutoScrollViewPager;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.DynamiclinkCreate;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private AutoScrollViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    TextView prodname, price, desc, location, name, email, mobile;
    LinearLayout delete, buynow, promocode, contactme;
    ImageView iv_pic;
    ProgressDialog progressDialog;
    RequestQueue queue;
    String flag = "deeplink", productId, prodcondition, prodprice, msg, image,promocode_status;
    SessionManager mSessionManager;
    Intent intent;
    boolean isConnected;
    private ArrayList<String> photoBMList;
    String moileNo,userid;
    String url, prodtype,user_type,country,amount,promo_codeS;
    Uri deepLink = null;


    private void bindView(ArrayList<String> list) {
        viewPager = (AutoScrollViewPager) findViewById(R.id.autoPager);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        viewPager.startAutoScroll();
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setStopScrollWhenTouch(true);
        BannerPagerAdapter adp = new BannerPagerAdapter(this, list);
        viewPager.setAdapter(adp);
        pageIndicator.setViewPager(viewPager, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        prodname = findViewById(R.id.prodname);
        price = findViewById(R.id.price);
        desc = findViewById(R.id.desc);
        location = findViewById(R.id.location);
        name = findViewById(R.id.name);
        delete = findViewById(R.id.delete);
        contactme = findViewById(R.id.contactme);
        buynow = findViewById(R.id.buynow);
        promocode = findViewById(R.id.promocode);
        iv_pic = findViewById(R.id.iv_pic);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(ProductDetails.this);
        mSessionManager = new SessionManager(getApplicationContext());

        photoBMList = new ArrayList<String>();

        intent = getIntent();
        isConnected = ConnectivityReceiver.isConnected();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        ImageView filter = (ImageView) toolbar.findViewById(R.id.filterpic);
        ImageView share = toolbar.findViewById(R.id.share);
        mTitle.setText("Product Detail");

        share.setVisibility(View.VISIBLE);
        filter.setVisibility(View.GONE);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prodtype = null;
                if (prodcondition.equalsIgnoreCase("1")) {
                    prodtype = "admin";
                } else {
                    prodtype = "user";

                }
                try {
                    DynamiclinkCreate.sharelink(image, ProductDetails.this, "Have a look on " +
                            msg, Endpoints.PRODUCT_DETAILS + "?" + "product_id=" + productId
                            + "&product_type=" + prodtype, "https://photopeoplehotdeal.page.link/");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (getIntent().hasExtra("flag"))
        {
            flag = intent.getStringExtra("flag");
            productId = intent.getStringExtra("productId");
            prodcondition = intent.getStringExtra("prodCondition");
            prodprice = intent.getStringExtra("amount");


            if (prodcondition.equalsIgnoreCase("1")) {
                prodtype = "admin";
            } else {
                prodtype = "user";
            }

            if (!isConnected) {
                showSnack(isConnected);
            } else {

                url = Endpoints.PRODUCT_DETAILS + "?" + "product_id=" + productId
                        + "&product_type=" + prodtype;
                prodDetails();
            }

            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (flag.equalsIgnoreCase("myproduct")) {
                        Intent intent = new Intent(ProductDetails.this, SellProductActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else if (flag.equalsIgnoreCase("hotdeal")) {
                        Intent intent = new Intent(ProductDetails.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "other");

                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else if (flag.equalsIgnoreCase("buysell")) {
                        Intent intent = new Intent(ProductDetails.this, BuyProductsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
//                    else {
//                        Intent intent = new Intent(ProductDetails.this, BuyProductsActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                    }
                }
            });


            if (flag.equalsIgnoreCase("myproduct")) {
                delete.setVisibility(View.VISIBLE);
                buynow.setVisibility(View.GONE);
                promocode.setVisibility(View.GONE);
                contactme.setVisibility(View.GONE);





            } else {

                if (prodcondition.equalsIgnoreCase("1")) {
                    delete.setVisibility(View.GONE);
                    buynow.setVisibility(View.VISIBLE);
                    promocode.setVisibility(View.VISIBLE);
                    contactme.setVisibility(View.GONE);
                } else {
                    delete.setVisibility(View.GONE);
                    buynow.setVisibility(View.GONE);
                    promocode.setVisibility(View.GONE);
                    contactme.setVisibility(View.VISIBLE);
                }
            }

        } else
            {





            if (!mSessionManager.isLoggedIn())

            {
                Intent intent = new Intent(ProductDetails.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                finish();
            } else

            {

                FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(getIntent())
                        .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                if (pendingDynamicLinkData != null) {
                                    deepLink = pendingDynamicLinkData.getLink();
                                }

                                if (deepLink != null) {
                                    Log.e("deeeplinkkk", deepLink.toString());
                                    url = deepLink.toString();
                                    prodDetails();

                                } else {
                                }
                                // [END_EXCLUDE]
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });


            }

                backImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(ProductDetails.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "other");
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    }
                });

        }


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDial();
            }
        });

        contactme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + moileNo));
                if (ActivityCompat.checkSelfPermission(ProductDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(callIntent);
            }
        });

                buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else {
                    if (mSessionManager.getMyLatLong().get(SessionManager.KEY_MYCOUNTRY).equalsIgnoreCase(country)) {
                        generateQuoteId(productId,amount,msg);

                    }

                    else
                    {
                        Toast.makeText(ProductDetails.this, "You are not able to purchase this product because it is from different country.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

                promocode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCouponDialog();
                    }
                });


    }


    private void openCouponDialog()
    {
        final Dialog dialog = new Dialog(ProductDetails.this, R.style.CustomDialog);
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
                    Toast.makeText(ProductDetails.this, "Enter valid coupon.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    promo_codeS=couponcodeET.getText().toString().trim();
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

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PROMOCODE_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response);
                        try {

                            JSONObject obj = new JSONObject(response);
                            String status=obj.getString("status");
                            String message=obj.getString("message");

                            if (status.equalsIgnoreCase("1") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject dataObj=obj.getJSONObject("data");
                               amount=dataObj.getString("Total price");
                                price.setText(amount);

                            }

                            ///{"status":1,"message":"success","data":{"product price":"15500","discount price":155,"Total price":15345}}





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
                ob.put("promo_code",promo_codeS );
                ob.put("user_type",mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("user_id", mSessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("product_id", productId+"");
//promo_code ,user_type, user_id , product_id
                Log.e("params",ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }




    private void generateQuoteId(final String prdId, final String amnt, final String prodName)
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.GENERATE_PRODUCID,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                String quoteId=obj.getString("printe_quota_id");
                                Intent intent=new Intent(ProductDetails.this, ProdPurchaseActivity.class);
                                intent.putExtra("quoteid",quoteId);
                                intent.putExtra("amount",amnt);
                                Toast.makeText(ProductDetails.this, amnt, Toast.LENGTH_SHORT).show();
                                intent.putExtra("from","buysell");
                                intent.putExtra("prodname",prodName);
                                startActivity(intent);

                            } else
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<>();
                params.put("product_id", prdId);
                params.put("user_id",mSessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type",mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                params.put("product_quentity","1");
                params.put("amount",amnt);

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }



    private void openConfirmDial() {

        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ProductDetails.this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("You want to delete it");
        sweetAlertDialog.show();


        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(ProductDetails.this, R.color.colorPrimary));

        sweetAlertDialog.setConfirmText("Yes");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                deleteProd();

            }
        });

        Button canbtn = (Button) sweetAlertDialog.findViewById(R.id.cancel_button);
        btn.setBackgroundColor(ContextCompat.getColor(ProductDetails.this, R.color.colorPrimary));

        sweetAlertDialog.setCancelText("No");
        canbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();

            }
        });
        sweetAlertDialog.show();

    }

    private void deleteProd() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PRODUCT_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success")) {

                                new SweetAlertDialog(ProductDetails.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Successfully")
                                        .setContentText("Product Deleted!")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                Intent in = new Intent(ProductDetails.this, SellProductActivity.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                startActivity(in);

                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            } else
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<>();
                params.put("product_id", productId);
                params.put("user_id", mSessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type", mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void prodDetails() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("url", url);

                        Log.e("PRODUCT_DETAILS", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("success")) {
                                JSONObject jsonObject = obj.getJSONObject("product_list");
                                JSONArray product_image = jsonObject.getJSONArray("product_image");

                                productId = jsonObject.getString("product_id");
                                prodcondition = jsonObject.getString("product_condition");
                                promocode_status=jsonObject.getString("promocode_status");


                                if (promocode_status.equalsIgnoreCase("0"))
                                {
                                    promocode.setVisibility(View.GONE);
                                }
                                else
                                {
                                    promocode.setVisibility(View.VISIBLE);

                                }
                                msg = jsonObject.getString("product_title");


                                for (int i = 0; i < product_image.length(); i++) {
                                    String path = product_image.getString(i);
                                    image = product_image.getString(0);
                                    photoBMList.add(path);
                                }
                                bindView(photoBMList);


                                prodname.setText(jsonObject.getString("product_title"));

                                String product_discription = jsonObject.getString("product_discription");
                                if (!product_discription.isEmpty()) {
                                    desc.setText(product_discription);
                                } else {
                                    desc.setText("Not Mentioned");
                                }

                                amount=jsonObject.getString("product_price");
                                price.setText("₹ " + jsonObject.getString("product_price"));

                                if (prodcondition.equalsIgnoreCase("1")) {
                                    JSONArray user_data = jsonObject.getJSONArray("user_data");
                                    JSONObject userobj = user_data.getJSONObject(0);
                                    name.setText(userobj.getString("fullname"));
                                    email.setText(userobj.getString("email"));
                                    mobile.setText(userobj.getString("mobile"));
                                    location.setText(userobj.getString("address"));
                                    moileNo = userobj.getString("mobile");
                                    country=userobj.getString("country");
                                } else {
                                    JSONArray user_data = jsonObject.getJSONArray("user_data");
                                    JSONObject userobj = user_data.getJSONObject(0);
                                    name.setText(userobj.getString("first_name"));
                                    email.setText(userobj.getString("email"));
                                    mobile.setText(jsonObject.getString("user_phone"));
                                    location.setText(userobj.getString("location"));
                                    moileNo = jsonObject.getString("user_phone");
                                    userid=userobj.getString("id");
                                    user_type=jsonObject.getString("user_type");
                                }


                                if (prodcondition.equalsIgnoreCase("1"))
                                {
                                    delete.setVisibility(View.GONE);
                                    buynow.setVisibility(View.VISIBLE);
                                    promocode.setVisibility(View.VISIBLE);
                                    contactme.setVisibility(View.GONE);
                                } else
                                {
                                    if (!userid.equalsIgnoreCase(mSessionManager.getLoginSession().get(SessionManager.KEY_USERID))
                                        && (!user_type.equalsIgnoreCase(mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE)))) {
                                        delete.setVisibility(View.GONE);
                                        buynow.setVisibility(View.GONE);
                                        promocode.setVisibility(View.GONE);
                                        contactme.setVisibility(View.VISIBLE);
                                    }

                                    else {
                                        delete.setVisibility(View.VISIBLE);
                                        buynow.setVisibility(View.GONE);
                                        promocode.setVisibility(View.GONE);
                                        contactme.setVisibility(View.GONE);

                                    }


                                }


                                String profile = jsonObject.getString("user_profile_pic");
                                if (!profile.isEmpty()) {
                                    Picasso.with(ProductDetails.this).load(profile).placeholder(R.drawable.default_new_img)
                                            .into(iv_pic);
                                } else {
                                    iv_pic.setImageResource(R.drawable.default_new_img);
                                }


                            } else
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        if (getIntent().hasExtra("flag")) {
            if (flag.equalsIgnoreCase("myproduct")) {
                Intent intent = new Intent(ProductDetails.this, SellProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            } else if (flag.equalsIgnoreCase("hotdeal")) {
                Intent intent = new Intent(ProductDetails.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "other");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            } else if (flag.equalsIgnoreCase("buysell")) {
                Intent intent = new Intent(ProductDetails.this, BuyProductsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
//            else {
//                Intent intent = new Intent(ProductDetails.this, BuyProductsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//            }
        } else {
            Intent intent = new Intent(ProductDetails.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "other");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }


    }


    @Override
    public void onStart() {


        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            url = Endpoints.PRODUCT_DETAILS + "?" + "product_id=" + productId
                    + "&product_type=" + prodtype;
            prodDetails();
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.rl), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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

}
