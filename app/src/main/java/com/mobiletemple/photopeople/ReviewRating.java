package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReviewRating extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    SessionManager sessionManager;
    RatingBar ratingBar;
    EditText msgET;
    LinearLayout saveBT;
    String jobid,userid,review;
    Intent intent;
    ProgressDialog dialog;
    RequestQueue queue;
    boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_rating);
        intent=getIntent();
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        jobid=intent.getStringExtra("jobid");
        userid=intent.getStringExtra("id");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        filter.setVisibility(View.GONE);
        mTitle.setText("Feedback");

        ratingBar=findViewById(R.id.ratingBar);
        msgET=findViewById(R.id.msgET);
        saveBT=findViewById(R.id.saveBT);
        sessionManager = new SessionManager(getApplicationContext());
        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(ReviewRating.this,InboxActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        saveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review=msgET.getText().toString();
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else {
                    ratingTask(ratingBar.getRating(), review);
                }
            }
        });



    }

    private void ratingTask(final float rating, final String review)

    {
        dialog.setMessage("Loading Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,Endpoints.RATING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e("RATING",response);
                dialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
//          {"status":200,"message":"succese","data":"data successfully add"}

                    if (status == 200 && message.equalsIgnoreCase("succese")) {
                        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(ReviewRating.this, SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Thank You for Rating my work!");
                        sweetAlertDialog.show();

                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(ReviewRating.this,R.color.colorPrimary));

                        sweetAlertDialog.setConfirmText("Ok");
                        btn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();
                                startActivity(new Intent(ReviewRating.this, InboxActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);

                                finish();
                            }
                        });
                        sweetAlertDialog.show();


                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("reting_send_user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("reting_send_user_type",sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("user_id",userid);
                ob.put("job_quote",jobid);
                ob.put("review", review);
                ob.put("reting", String.valueOf(rating));
                //Log.e("params", ob.toString());
                return ob;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
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
                .make(findViewById(R.id.rl), message, Snackbar.LENGTH_LONG);

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
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }

}
