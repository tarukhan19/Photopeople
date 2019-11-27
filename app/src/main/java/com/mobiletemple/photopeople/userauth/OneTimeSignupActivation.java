package com.mobiletemple.photopeople.userauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OneTimeSignupActivation extends AppCompatActivity {
    private String amount;
    String first_name,paymentid,userid,usertype,randomString,email, profile_image, feelancer_type, photoscount,from,mobileno;
    Intent intent;
    ProgressDialog pd;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time_signup_activation);
        getSupportActionBar().hide();
        pd = new ProgressDialog(OneTimeSignupActivation.this);
        intent=getIntent();
        sessionManager=  new SessionManager(getApplicationContext());
        userid=intent.getStringExtra("userid");
        usertype=intent.getStringExtra("usertype");
        amount = intent.getStringExtra("amount");
        randomString = intent.getStringExtra("randomString");
        paymentid=intent.getStringExtra("paymentid");
        from=intent.getStringExtra("from");
        mobileno = intent.getStringExtra("mobileno");
        first_name = intent.getStringExtra("name");

        if (from.equalsIgnoreCase("login"))
        {
            email=intent.getStringExtra("email");
            profile_image=intent.getStringExtra("profile_image");
            feelancer_type=intent.getStringExtra("feelancer_type");
            photoscount=intent.getStringExtra("photoscount");

            Log.e("data",userid+" "+usertype+" "+amount+" "+randomString+" "+paymentid+" "+from+" "+mobileno
            +" "+email+" "+profile_image+" "+feelancer_type+" "+photoscount+" "+first_name);
        }

        Log.e("data",userid+" "+usertype+" "+amount+" "+randomString+" "+paymentid+" "+from+" "+mobileno+" "+first_name);
        //  paymentID=intent.getStringExtra("paymentID");


        Button doneBT = (Button) findViewById(R.id.doneBT);
        doneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equalsIgnoreCase("otp"))
                {
                    Intent intent = new Intent(OneTimeSignupActivation.this, LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    sessionManager.setLoginSession(email, userid, first_name, usertype,mobileno
                            , profile_image, feelancer_type, photoscount);
                    sessionManager.setImageSession(profile_image);
                    sessionManager.setuserid(userid);
                    startActivity(new Intent(OneTimeSignupActivation.this, HomePage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("from", "other"));
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }

//               finish();
            }
        });


        PaymentEntryTask task = new PaymentEntryTask();
        task.execute();
    }


    class PaymentEntryTask extends AsyncTask<String, Void, String>
    {


        @Override
        protected void onPreExecute() {
            //Log.e("Payu PaymentEntryTask", "PaymentEntryTask RUN");
            pd.setMessage("Payment Detail Saving ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Endpoints comm = new Endpoints();

            try
            {
                JSONObject ob = new JSONObject();

                ob.put("user_id", userid);
                ob.put("transection_id", randomString);
                ob.put("amount", amount);
                ob.put("user_type", usertype);
                ob.put("payment_id",paymentid);

                String result = comm.getStringResponse(Endpoints.ONE_TIME_FEE_ENTRY, ob);
                Log.e("OneTimeSignup_PARA", ob.toString());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            //  return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pd.cancel();
            try {
                Log.e("OneTimeSignup_result", s);

                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        new SweetAlertDialog(OneTimeSignupActivation.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Successfully Done !")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                }).show();
                    } else {
                        Toast.makeText(OneTimeSignupActivation.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                //Log.e("Accept Stage 1 err", ex.getMessage());
            }
        }
    }
}
