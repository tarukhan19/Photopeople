package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.customStyle.AutoScrollViewPager;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaymentDetailActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{

    private EditText nameET, actNoET, bankNameET, ifscET;
    private String name, actNo, bank, ifsc;
    LinearLayout doneBT;
    SessionManager session;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
//        getSupportActionBar().hide();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Bank Details");
        filter.setVisibility(View.GONE);
        session = new SessionManager(getApplicationContext());

        nameET = (EditText) findViewById(R.id.accountholdername);
        actNoET = (EditText) findViewById(R.id.accountno);
        bankNameET = (EditText) findViewById(R.id.bankname);
        ifscET = (EditText) findViewById(R.id.ifsccode);
        doneBT = (LinearLayout) findViewById(R.id.nextBT);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PaymentDetailActivity.this,ProfileUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });


        doneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidate = true;

                name = nameET.getText().toString();
                if (name.isEmpty()) {
                    nameET.setError("Please fill Name.");
                    isValidate = false;
                } else {
                    String names[] = name.split(" ");
                    String finalName = "";
                    for (String nm : names) {
                        String first = nm.substring(0, 1);
                        String rem = nm.substring(1, nm.length());

                        finalName += first.toUpperCase() + rem.toLowerCase() + " ";
                    }
                    name = finalName.trim();
                }

                actNo = actNoET.getText().toString();
                if (actNo.isEmpty()) {
                    actNoET.setError("Please fill account.");
                    isValidate = false;
                }

                bank = bankNameET.getText().toString();
                if (bank.isEmpty()) {
                    bankNameET.setError("Please fill Bank Name.");
                    isValidate = false;
                } else {
                    String names[] = bank.split(" ");
                    String finalName = "";
                    for (String nm : names) {
                        String first = nm.substring(0, 1);
                        String rem = nm.substring(1, nm.length());

                        finalName += first.toUpperCase() + rem.toLowerCase() + " ";
                    }
                    bank = finalName.trim();
                }

                ifsc = ifscET.getText().toString();
                if (ifsc.isEmpty()) {
                    ifscET.setError("Please fill ifsc code.");
                    isValidate = false;
                }

                if (isValidate) {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else {
                        Task task = new Task();
                        task.execute();
                    }

                }
            }
        });

    }


    class GetTask extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(PaymentDetailActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Load Payment Detail  ...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Endpoints comm = new Endpoints();


            try {
                JSONObject ob = new JSONObject();
                ob.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                String result = comm.getStringResponse(Endpoints.BANK_DETAIL, ob);
                //Log.e("pay det response", result);
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
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONObject ob = obj.getJSONObject("data");
                        String account_holder_name = ob.getString("account_holder_name");
                        String bank_name = ob.getString("bank_name");
                        String account_no = ob.getString("account_no");
                        String bank_ifsc = ob.getString("bank_ifsc");

                        nameET.setText(account_holder_name);
                        actNoET.setText(account_no);
                        bankNameET.setText(bank_name);
                        ifscET.setText(bank_ifsc);
                    } else {
//                        new SweetAlertDialog(PaymentDetailActivity.this, SweetAlertDialog.ERROR_TYPE)
//                                .setTitleText("Account details not available")
//                                .setConfirmText("OK")
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        sDialog.dismissWithAnimation();
//                                    }
//                                }).show();
                        final SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(PaymentDetailActivity.this, SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitleText("Account details not available")
                                .setConfirmText("OK");
                        sweetAlertDialog.show();



                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(PaymentDetailActivity.this,R.color.colorPrimary));
                        btn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });

                    }

                }
            } catch (Exception ex) {
                //Log.e("Pay det err", ex.getMessage());
            }
            super.onPostExecute(s);
        }
    }


    class Task extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(PaymentDetailActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Payment Detail Saving ...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Endpoints comm = new Endpoints();


            try {
                JSONObject ob = new JSONObject();
                ob.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("account_holder_name", name);
                ob.put("bank_name", bank);
                ob.put("account_no", actNo);
                ob.put("ifsc_code", ifsc);

                String result = comm.getStringResponse(Endpoints.ADD_PAYMENT_DETAIL, ob);
                //Log.e("pay det response", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
//
                    final SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(PaymentDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText("Bank Details Added")
                            .setConfirmText("OK");
                    sweetAlertDialog.show();



                    Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                    btn.setBackgroundColor(ContextCompat.getColor(PaymentDetailActivity.this,R.color.colorPrimary));

                    btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            Intent in7 = new Intent(PaymentDetailActivity.this, ProfileUpdate.class);
                            in7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(in7);
                            overridePendingTransition(R.anim.trans_left_in,
                                    R.anim.trans_left_out);
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });


                }
            } catch (Exception ex) {
                //Log.e("Pay det err", ex.getMessage());
            }
            super.onPostExecute(s);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(PaymentDetailActivity.this,ProfileUpdate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        else
        {
            GetTask task = new GetTask();
            task.execute();
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
            GetTask task = new GetTask();
            task.execute();
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
