package com.mobiletemple.photopeople.AlbumPrinting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PrinterWelcomepage extends AppCompatActivity {
    private String amount;
    private int stage;
    String first_name,freelancerId,Mobile_no,email,quoteId,paymentid;
    String randomString;
    Intent intent;
    ProgressDialog pd;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_welcomepage);

        getSupportActionBar().hide();
        pd = new ProgressDialog(PrinterWelcomepage.this);

        intent=getIntent();
        sessionManager=  new SessionManager(getApplicationContext());
        first_name=intent.getStringExtra("first_name");
        freelancerId=intent.getStringExtra("freelancerId");
        Mobile_no=intent.getStringExtra("Mobile_no");
        email=intent.getStringExtra("email");
        stage = intent.getIntExtra("stage", 0);
        amount = intent.getStringExtra("amount");
        randomString = intent.getStringExtra("randomString");
        quoteId=intent.getStringExtra("quoteid");
        paymentid=intent.getStringExtra("paymentid");


        Button doneBT = (Button) findViewById(R.id.doneBT);
        doneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrinterWelcomepage.this, HomePage.class);
                intent.putExtra("usertype",sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                intent.putExtra("from","other");

                startActivity(intent);
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
                ob.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("user_type",sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("transection_id", randomString);
                ob.put("amount", amount);
                ob.put("quote_id", quoteId);
                ob.put("payumoney_id",paymentid);

                String result = comm.getStringResponse(Endpoints.PRINTER_PAYMENT, ob);
                Log.e("PRINTER_PAYMENT_para", ob.toString());
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
                if (s != null) {
                    Log.e("PRINTER_PAYMENT",s);
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        new SweetAlertDialog(PrinterWelcomepage.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Successfully Done !")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                }).show();
                    } else {
                        Toast.makeText(PrinterWelcomepage.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                //Log.e("Accept Stage 1 err", ex.getMessage());
            }
        }
    }
}
