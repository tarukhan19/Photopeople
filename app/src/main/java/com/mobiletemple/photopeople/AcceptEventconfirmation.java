package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.PayuMoneyIntegration.ProdPurchaseActivity;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONObject;

public class AcceptEventconfirmation extends AppCompatActivity
{
    SessionManager sessionManager;
    private String mFirstName;
    private String mEmailId;
    private double mAmount;
    private String mPhone;
    private int stage;
    String freelancerId,quoteId;
    Intent intent;
    ProgressDialog pd;
    TextView paymentmsg,responsemsg;
    LinearLayout msgLL;
    Button doneBT;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_eventconfirmation);
        sessionManager=new SessionManager(this);
        pd = new ProgressDialog(AcceptEventconfirmation.this);

        paymentmsg=findViewById(R.id.paymentmsg);
        responsemsg=findViewById(R.id.responsemsg);
        msgLL=findViewById(R.id.msgLL);
        doneBT=findViewById(R.id.doneBT);

        paymentmsg.setVisibility(View.VISIBLE);
        msgLL.setVisibility(View.GONE);

        intent=getIntent();
        mFirstName=intent.getStringExtra("first_name");
        freelancerId=intent.getStringExtra("freelancerId");
        mPhone =intent.getStringExtra("Mobile_no");
        mEmailId=intent.getStringExtra("email");
        stage = intent.getIntExtra("stage", 0);
        mAmount = intent.getDoubleExtra("amount", 0.0);
        quoteId=intent.getStringExtra("quoteid");
        //Log.e("Payu Info", "" + mFirstName + " : " + mEmailId + " : " + mAmount + " : " + mPhone +"  "+stage);
        doneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(AcceptEventconfirmation.this, HomePage.class);
                in.putExtra("from","other");
                startActivity(in);


            }
        });
        AcceptEventTask task = new AcceptEventTask();
        task.execute();
    }


    class AcceptEventTask extends AsyncTask<String, Void, String>
    {



        @Override
        protected void onPreExecute()
        {
            pd.setMessage("Accepting Freelancer ...");
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
                ob.put("studiouser_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("quote_id", quoteId);

                String result = comm.getStringResponse(Endpoints.ACCEPT_QUOTE_BY_STUDIO, ob);
                //Log.e("Accept response", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pd.cancel();
            try {

                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equals("success"))
                    {
                        msgLL.setVisibility(View.GONE);
                        paymentmsg.setVisibility(View.VISIBLE);
                        Intent in = new Intent(AcceptEventconfirmation.this, ProdPurchaseActivity.class);
                        in.putExtra("amount", String.valueOf(mAmount));
                        in.putExtra("stage", stage);
                        in.putExtra("first_name",mFirstName );
                        in.putExtra("freelancerId",freelancerId );
                        in.putExtra("Mobile_no",mPhone );
                        in.putExtra("email",mEmailId );
                        in.putExtra("quoteid",quoteId );

                        in.putExtra("from","booking");
                        in.putExtra("prodname","Freelancer Booking");

                        startActivity(in);




                    }
                   else if (status==1 && message.equalsIgnoreCase("Sorry freelancer already occupy"))
                    {
                        paymentmsg.setVisibility(View.GONE);
                        msgLL.setVisibility(View.VISIBLE);
                        responsemsg.setText("Sorry Freelancer is already occupied");

                    }
                    else
                        {
                            msgLL.setVisibility(View.VISIBLE);
                            paymentmsg.setVisibility(View.GONE);
                            responsemsg.setText("Smething went wrong");
                        }

            } catch (Exception ex) {
                //Log.e("Accept Err", ex.getMessage());
            }
        }
    }
}
