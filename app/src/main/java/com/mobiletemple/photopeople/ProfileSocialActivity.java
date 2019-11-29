package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.snackbar.Snackbar;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileSocialActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private LinearLayout nextBT,step;
    private String userType;
    Intent in;
    private EditText fbET, webET, blogET, aboutET;
    private String fb="", web="", blog="", about="";
    SessionManager session;
    ProgressDialog dialog;
    String flag;
    TextView done;
    boolean isConnected;
    Toolbar toolbar;
    TextView mTitle;
    RequestQueue queue;
    LinearLayout backImage,filter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_social);
        fbET = (EditText) findViewById(R.id.fbET);
        webET = (EditText) findViewById(R.id.webET);
        blogET = (EditText) findViewById(R.id.blogET);
        aboutET = (EditText) findViewById(R.id.aboutET);
        session = new SessionManager(this);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(ProfileSocialActivity.this);
        nextBT = (LinearLayout) findViewById(R.id.nextBT);
        done=findViewById(R.id.done);
        step=findViewById(R.id.step);
        in = getIntent();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        backImage =  toolbar.findViewById(R.id.backImage);
        filter = (LinearLayout) toolbar.findViewById(R.id.filter);


        flag = in.getStringExtra("flag");

        userType=session.getLoginSession().get(SessionManager.KEY_USER_TYPE);

        nextBT.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                fb = fbET.getText().toString();
                web = webET.getText().toString();
                blog = blogET.getText().toString();
                about = aboutET.getText().toString();
                if (!isConnected)
                {
                 showSnack(isConnected);
                }
                else
                {
                    profilesocial();
                }
            }
        });



        if (flag.equalsIgnoreCase("update"))
        {
            filter.setVisibility(View.GONE);
            mTitle.setText("Update Social Profile");
            step.setVisibility(View.GONE);

            backImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ProfileSocialActivity.this,ProfileUpdate.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
            });


            done.setText("Update");

        }
        else {
            toolbar.setVisibility(View.GONE);
            step.setVisibility(View.VISIBLE);

            userType = in.getStringExtra("userType");
            done.setText("Done");
        }


    }
    private void profilesocial()
    {

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.SOCIAL_PROFILE_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success")) {


                                if (flag.equalsIgnoreCase("update"))
                                {
                                    final SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(ProfileSocialActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sweetAlertDialog.setTitleText("Successfully Updated !")
                                            .setConfirmText("OK");
                                    sweetAlertDialog.show();



                                    Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                    btn.setBackgroundColor(ContextCompat.getColor(ProfileSocialActivity.this,R.color.colorPrimary));

                                    btn.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            sweetAlertDialog.dismissWithAnimation();
                                            if (userType.equalsIgnoreCase("1")) {
                                                Intent in = new Intent(ProfileSocialActivity.this, ProfileUpdate.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                in.putExtra("userType", userType);

                                                startActivity(in);
                                                overridePendingTransition(R.anim.trans_left_in,
                                                        R.anim.trans_left_out);
                                            }
                                            if (userType.equalsIgnoreCase("2")) {
                                                Intent in = new Intent(ProfileSocialActivity.this, ProfileUpdate.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                in.putExtra("userType", userType);

                                                startActivity(in);
                                                overridePendingTransition(R.anim.trans_left_in,
                                                        R.anim.trans_left_out);
                                            }
                                        }
                                    });
                                }
                                else
                                    {

                                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ProfileSocialActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sweetAlertDialog.setTitleText("Successfully Registered !")
                                            .setConfirmText("OK");
                                    sweetAlertDialog.show();


                                    Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                    btn.setBackgroundColor(ContextCompat.getColor(ProfileSocialActivity.this, R.color.colorPrimary));

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            if (userType.equalsIgnoreCase("1")) {
                                                Intent in = new Intent(ProfileSocialActivity.this, LoginActivity.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                in.putExtra("userType", userType);

                                                startActivity(in);
                                                overridePendingTransition(R.anim.trans_left_in,
                                                        R.anim.trans_left_out);
                                            }
                                            if (userType.equalsIgnoreCase("2")) {
                                                Intent in = new Intent(ProfileSocialActivity.this, LoginActivity.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                in.putExtra("userType", userType);

                                                startActivity(in);
                                                overridePendingTransition(R.anim.trans_left_in,
                                                        R.anim.trans_left_out);
                                            }
                                        }
                                    });
                                }


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
            protected Map<String, String> getParams()
            {
                Map<String, String> ob = new HashMap<>();
                    ob.put("user_id", session.getuserId().get(SessionManager.KEY_USERID));
                    ob.put("user_type", String.valueOf(userType));
                    ob.put("fb_url", fb);
                    ob.put("web_url", web);
                    ob.put("blog_url", blog);
                    ob.put("about", about);

                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    class StudioProfileDataLoad extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(ProfileSocialActivity.this);
            pd.setMessage("Loading Profile...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            Endpoints comm = new Endpoints();
            try {

                JSONObject ob = new JSONObject();
                String url = "";



                ob.put("studio_id",session.getLoginSession().get(SessionManager.KEY_USERID));
                url = comm.LOAD_STUDIO_PROFILE;


                //Log.e("Profile para",ob.toString());
                String result = comm.getStringResponse(url,ob);
                //Log.e("Profile Load",result);
                return result;
            }catch(Exception ex) {
                //Log.e("GEO ERR",ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.cancel();
            try {
                if (s != null)
                {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if(status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONObject data = obj.getJSONObject("data");
                        fbET.setText(data.getString("fb_page"));
                        webET.setText(data.getString("web_url"));
                        blogET.setText(data.getString("blog_url"));
                        aboutET.setText(data.getString("about_me"));
                    }
                }
            }
            catch(Exception ex){}
            super.onPostExecute(s);
        }
    }




    class FreelancerProfileDataLoad extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(ProfileSocialActivity.this);
            pd.setMessage("Loading Profile...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            Endpoints comm = new Endpoints();
            try {

                JSONObject ob = new JSONObject();
                String url = "";



                ob.put("freelancer_id",session.getLoginSession().get(SessionManager.KEY_USERID));
                url = comm.LOAD_FREELANCER_PROFILE;


                //Log.e("Profile para",ob.toString());
                String result = comm.getStringResponse(url,ob);
                //Log.e("Profile Load",result);
                return result;
            }catch(Exception ex) {
                //Log.e("GEO ERR",ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.cancel();
            //Log.e("updateprofile",s);
            try {
                if (s != null)
                {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if(status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONObject data = obj.getJSONObject("data");
                        fbET.setText(data.getString("fb_page"));
                        webET.setText(data.getString("web_url"));
                        blogET.setText(data.getString("blog_url"));
                        aboutET.setText(data.getString("about_me"));
                    }
                }
            }
            catch(Exception ex){}
            super.onPostExecute(s);
        }
    }

    @Override
    public void onBackPressed() {
        if (flag.equalsIgnoreCase("update"))
        {
            Intent intent=new Intent(ProfileSocialActivity.this,ProfileUpdate.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
       else {
//            Intent intent=getIntent();
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            finish();
//            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            finishAffinity();
        }
        super.onBackPressed();
    }



    @Override
    public void onStart()
    {
        super.onStart();
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
        {
            if (userType.equalsIgnoreCase(Constants.STUDIO_TYPE))
            {
                StudioProfileDataLoad profileDataLoad=new StudioProfileDataLoad();
                profileDataLoad.execute();
            }
            else if (userType.equalsIgnoreCase(Constants.FREELANCER_TYPE))
            {
                FreelancerProfileDataLoad profileDataLoad1=new FreelancerProfileDataLoad();
                profileDataLoad1.execute();
            }


        }

    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected)
    {
        String message;
        int color;

        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            if (flag.equalsIgnoreCase("update"))
            {
            if (userType.equalsIgnoreCase(Constants.STUDIO_TYPE))
            {
                StudioProfileDataLoad profileDataLoad=new StudioProfileDataLoad();
                profileDataLoad.execute();
            }
            else if (userType.equalsIgnoreCase(Constants.FREELANCER_TYPE))
            {
                FreelancerProfileDataLoad profileDataLoad1=new FreelancerProfileDataLoad();
                profileDataLoad1.execute();
            }
            }


        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);

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
