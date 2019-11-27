package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ContactUsActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    EditText nameET,emailET,msgET;
    LinearLayout saveBT;
    SessionManager sessionManager;
    ProgressDialog dialog;
    RequestQueue queue;
    boolean isConnected;
    String from;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        sessionManager = new SessionManager(ContactUsActivity.this);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(ContactUsActivity.this);
        getSupportActionBar().hide();
        intent=getIntent();
        from=intent.getStringExtra("from");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Contact Us");
        filter.setVisibility(View.GONE);
        nameET = (EditText) findViewById(R.id.nameET) ;
        emailET = (EditText) findViewById(R.id.emailET) ;
        msgET = (EditText) findViewById(R.id.msgET) ;

        saveBT = (LinearLayout) findViewById(R.id.saveBT) ;
        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ContactUsActivity.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        saveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidate = true;

                String name = nameET.getText().toString();
                if(name.isEmpty()){
                    isValidate = false;
                    nameET.setError("Please fill name");
                }
                String email = emailET.getText().toString();
                if(email.isEmpty()){
                    isValidate = false;
                    emailET.setError("Please fill name");
                }
                String msg = msgET.getText().toString();
                if(msg.isEmpty()){
                    isValidate = false;
                    msgET.setError("Please fill name");
                }

                if(isValidate)
                {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }

                    else {contactUs(name,email,msg);}
                }
                }

        });
    }

    private void contactUs(final String name, final String email, final String msg)
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CONTACT_US,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success")) {
                                final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(ContactUsActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitleText("Thank You for contacting us!");
                                sweetAlertDialog.show();

                                Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(ContactUsActivity.this,R.color.colorPrimary));

                                sweetAlertDialog.setConfirmText("Ok");
                                btn.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        startActivity(new Intent(ContactUsActivity.this, HomePage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .putExtra("from","profile"));
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);

                                        finish();
                                    }
                                });
                                sweetAlertDialog.show();


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
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("user_type", sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("name",name);
                ob.put("email",email);
                ob.put("message",msg);
                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent=new Intent(ContactUsActivity.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","profile");
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
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
