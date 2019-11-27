package com.mobiletemple.photopeople;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    Intent intent;
    SessionManager sessionManager;
    //double walletamount,mycash,bonus,total;
    TextView mycashTV,walletamountTV,bonusTV, totalTV,usableamnt,grossstotal;
    ProgressDialog dialog;
    RequestQueue queue;
    boolean isConnected;
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        intent=getIntent();
        from=intent.getStringExtra("from");

        sessionManager = new SessionManager(WalletActivity.this);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(WalletActivity.this);
        mycashTV=(TextView)findViewById(R.id.mycash);
        walletamountTV=(TextView)findViewById(R.id.walletamount);
        bonusTV=(TextView)findViewById(R.id.bonus);
        totalTV=(TextView)findViewById(R.id.total);
        usableamnt=(TextView)findViewById(R.id.usableamnt);
        grossstotal=(TextView)findViewById(R.id.grossstotal);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Wallet");
        ImageView filterpic=toolbar.findViewById(R.id.filterpic);
        filterpic.setImageResource(R.drawable.detailicons);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WalletActivity.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WalletActivity.this,WalletDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

            }
        });



    }

    private void loadFreelancerWallet()
    {
        dialog.setMessage("Loading Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.WALLET_FREE, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                //Log.e("WALLET_FREE",response);
                try {
                    dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    double bonus=obj.getDouble("bounas");
                    double wallet=obj.getDouble("wallet");
                    double totol=obj.getDouble("totol");
                    double usable=obj.getDouble("usable");
                    double grand_total=obj.getDouble("grand_total");

                    //mycashTV.setText();
                    walletamountTV.setText(String.valueOf(wallet));
                    bonusTV.setText(String.valueOf(bonus));
                    totalTV.setText(String.valueOf(totol));
                    usableamnt.setText(String.valueOf(usable));
                    grossstotal.setText(String.valueOf(grand_total));

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
                ob.put("user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));

                //Log.e("params", ob.toString());
                return ob;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    private void loadStudioWallet()
    {
        dialog.setMessage("Loading Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.WALLET_STUD, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                //Log.e("WALLET_FREE",response);
                try {

                    dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status=obj.getInt("status");
                    String message=obj.getString("message");
                    if (status==200)
                    {
                        //mycashTV.setText("0");
                        walletamountTV.setText("0");
                        bonusTV.setText("0");
                        usableamnt.setText("0");
                        totalTV.setText(obj.getString("wallet_amt"));
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
                ob.put("studio_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(WalletActivity.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","profile");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }




    @Override
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
        {
            if (sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase("1"))
            {
                loadStudioWallet();
            }
            else
            {
                loadFreelancerWallet();

            }

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
            if (sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase("1"))
            {
                loadStudioWallet();
            }
            else
            {
                loadFreelancerWallet();

            }

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
