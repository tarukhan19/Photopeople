package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Chat.ChatActivity;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EventDetails extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
Intent intent;
String pic,eventtype,shoottype,price,startdate,enddate,location,description,status,freeid,quoteid,studioid,recievertype,recieverid;
ImageView iv_pic;
TextView eventtypetv,shottypetv,priceTV,timeTV,locationtv,desctv,payrecievetv,totalbudgettv;
LinearLayout accept,reject;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager mSessionManager;
    String enddateS,startdateS;
    RelativeLayout rl;
    TextView msg;
    String from;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        rl=findViewById(R.id.rl);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        msg=findViewById(R.id.msg);
        mTitle.setText("Event Details");
        ImageView filterpic=toolbar.findViewById(R.id.filterpic);
        filterpic.setImageResource(R.drawable.inbox_fre_inact);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        intent=getIntent();
        from=intent.getStringExtra("from");
        eventtypetv=findViewById(R.id.eventtypeTV);
        shottypetv=findViewById(R.id.shottype);
        priceTV=findViewById(R.id.priceTV);
        timeTV=findViewById(R.id.timeTV);
        locationtv=findViewById(R.id.locationtv);
        desctv=findViewById(R.id.desc);
        payrecievetv=findViewById(R.id.payrecieve);
        totalbudgettv=findViewById(R.id.totalbudget);
        iv_pic=findViewById(R.id.iv_pic);
        mSessionManager = new SessionManager(getApplicationContext());

        pic=intent.getStringExtra("pic");
        eventtype=intent.getStringExtra("eventtype");
        price=intent.getStringExtra("price");
        shoottype=intent.getStringExtra("shoottype");
        startdate=intent.getStringExtra("startdate");
        enddate=intent.getStringExtra("enddate");
        location=intent.getStringExtra("location");
        description=intent.getStringExtra("description");
        status=intent.getStringExtra("status");
        freeid=intent.getStringExtra("freelancerId");
        quoteid=intent.getStringExtra("quoteid");
        studioid=intent.getStringExtra("studioID");

        recievertype=intent.getStringExtra("recivertype");
        recieverid=intent.getStringExtra("RECIVERid");
        accept=findViewById(R.id.accept);
        reject=findViewById(R.id.reject);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equalsIgnoreCase("job"))
                {
                    Intent intent=new Intent(EventDetails.this,MyJobActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","profile");

                    startActivity(intent);

                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }

                else if (from.equalsIgnoreCase("home"))
                {
                    Intent intent=new Intent(EventDetails.this,HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","other");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }

            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (from.equalsIgnoreCase("home"))
                {
                    Intent in = new Intent(EventDetails.this, ChatActivity.class);
                    in.putExtra("studioID", studioid);
                    in.putExtra("freelancerId", freeid);
                    in.putExtra("quoteId", quoteid);

                    in.putExtra("from",  "eventdetail");
                    in.putExtra("from1",  "home");


                    in.putExtra("eventtype",eventtype);
                    //Log.e("eventtype",eventtype);
                    in.putExtra("shoottype",shoottype);
                    in.putExtra("price",price);
                    in.putExtra("startdate",startdate);
                    in.putExtra("enddate",enddate);
                    in.putExtra("location",location);
                    in.putExtra("description",description);
                    in.putExtra("status",status);
                    ////Log.e("adapterpass",freelancer.getStudioId()+" "+freelancer.getId()+" "+freelancer.getQuoteId()+" "+Constants.STUDIO_TYPE);
                    in.putExtra("RECIVERid",recieverid);
                    in.putExtra("recivertype",recievertype);
                    startActivity(in);
                }
               else if (from.equalsIgnoreCase("job"))
                {
                    Intent in = new Intent(EventDetails.this, ChatActivity.class);
                    in.putExtra("studioID", studioid);
                    in.putExtra("freelancerId", freeid);
                    in.putExtra("quoteId", quoteid);
                    in.putExtra("from",  "eventdetail");
                    in.putExtra("from1",  "job");


                    in.putExtra("eventtype",eventtype);
                    //Log.e("eventtype",eventtype);
                    in.putExtra("shoottype",shoottype);
                    in.putExtra("price",price);
                    in.putExtra("startdate",startdate);
                    in.putExtra("enddate",enddate);
                    in.putExtra("location",location);
                    in.putExtra("description",description);
                    in.putExtra("status",status);
                    ////Log.e("adapterpass",freelancer.getStudioId()+" "+freelancer.getId()+" "+freelancer.getQuoteId()+" "+Constants.STUDIO_TYPE);
                    in.putExtra("RECIVERid",recieverid);
                    in.putExtra("recivertype",recievertype);
                    startActivity(in);
                }

            }
        });





//        if (!pic.isEmpty())
//        { Picasso.with(this).load(pic)
//                .into(iv_pic);}
//        else {iv_pic.setImageResource(R.mipmap.register_profile_default);}
        switch(eventtype)
        {
            case "1": iv_pic.setImageResource(R.drawable.birthday_default);
                eventtypetv.setText("Birthday"); break;
            case "2":iv_pic.setImageResource(R.drawable.wedding);
                eventtypetv.setText("Wedding"); break;
            case "3":iv_pic.setImageResource(R.drawable.pre_wedding);
                eventtypetv.setText("Pre Wedding"); break;
            case "4": iv_pic.setImageResource(R.drawable.engagement);
                eventtypetv.setText("Engagement"); break;
            case "5": iv_pic.setImageResource(R.drawable.maternity);
                eventtypetv.setText("Maternity"); break;
            case "6":iv_pic.setImageResource(R.drawable.kids);
                eventtypetv.setText("Kids"); break;
        }
        parseDateToddMMyyyy(startdate,enddate);

        timeTV.setText(startdateS+ " to "+enddateS);
        priceTV.setText(price);
        locationtv.setText(location);
        desctv.setText(description);
        totalbudgettv.setText(price);



        if (shoottype.equalsIgnoreCase("1"))
        {shottypetv.setText("Photo Shoot");}
        else if (shoottype.equalsIgnoreCase("2"))
        {shottypetv.setText("Video Shoot");}
        else if (shoottype.equalsIgnoreCase("3"))
        {shottypetv.setText("Photo/Video Shoot");}


        if (status.equalsIgnoreCase("1"))
        {accept.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
            payrecievetv.setText("0");
        }
        else if (status.equalsIgnoreCase("2"))
        {accept.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
            payrecievetv.setText("0");
        }
        else if (status.equalsIgnoreCase("3"))
        {
            double value = Double.valueOf(price);
            double amount = ((double) value * 25) / 100;
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
            payrecievetv.setText(String.valueOf(amount));
        }
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else
                {
                    acceptJob();
                }

            }
        });
        
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else
                {
                    rejectjob();
                }
            }
        });




    }

    private void rejectjob() {

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.DECLAIN_FREELANCER_QUOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            final int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {

                                final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(EventDetails.this, SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitleText("Job Rejected!");
                                sweetAlertDialog.show();



                                Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(EventDetails.this,R.color.colorPrimary));

                                sweetAlertDialog.setConfirmText("Ok");
                                btn.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        accept.setVisibility(View.GONE);
                                        sweetAlertDialog.dismissWithAnimation();
                                        finish();
                                    }
                                });
                                sweetAlertDialog.show();


                            } else{}
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("freelancerid",freeid );
                params.put("quote_id",quoteid );

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void acceptJob()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.ACCEPT_FREELANCER_QUOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            final int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {


                                final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(EventDetails.this, SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitleText("Job Accepted!");
                                sweetAlertDialog.show();
                                Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(EventDetails.this,R.color.colorPrimary));

                                sweetAlertDialog.setConfirmText("Ok");
                                btn.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        accept.setVisibility(View.GONE);
                                        sweetAlertDialog.dismissWithAnimation();
                                        finish();

                                    }
                                });
                                sweetAlertDialog.show();


                            } else{}
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("freelancerid",freeid );
                params.put("quote_id",quoteid );

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public void parseDateToddMMyyyy(String startdate,String enddate) {

        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "ddMMMyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;

        try {
            date = inputFormat.parse(startdate);
            startdateS = outputFormat.format(date);

            date = inputFormat.parse(enddate);
            enddateS = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //return startdate,enddate;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equalsIgnoreCase("job"))
        {
            Intent intent=new Intent(EventDetails.this,MyJobActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","profile");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }

        else if (from.equalsIgnoreCase("home"))
        {
            Intent intent=new Intent(EventDetails.this,HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","other");
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
    }

    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }


        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
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
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }
}
