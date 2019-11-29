package com.mobiletemple.photopeople;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mobiletemple.photopeople.adapter.WalletAdapter;
import com.mobiletemple.photopeople.model.WalletDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WalletDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
RecyclerView recyclerView;
    private ArrayList<WalletDTO> walletDTOArrayList;
    WalletAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    SessionManager sessionManager;
    ProgressDialog dialog;
    ImageView emptylist;
    RequestQueue queue;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_details);
        walletDTOArrayList = new ArrayList<>();
        sessionManager = new SessionManager(WalletDetails.this);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(WalletDetails.this);
        adapter = new WalletAdapter(this, walletDTOArrayList);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        emptylist=findViewById(R.id.emptylist);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Wallet History");
        filter.setVisibility(View.GONE);
        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WalletDetails.this,WalletActivity.class);
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
                    JSONArray user_data=obj.getJSONArray("user_data");

                    if (user_data.length()==0)
                    {
                        recyclerView.setVisibility(View.GONE);
                        emptylist.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptylist.setVisibility(View.GONE);
                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject jsonObject = user_data.getJSONObject(i);
                            WalletDTO walletDTO = new WalletDTO();
                            walletDTO.setName(jsonObject.getString("payment_sender_name"));
                            walletDTO.setEventType(jsonObject.getString("event_type"));
                            walletDTO.setStage1amnt(jsonObject.getString("stage1_amount"));
                            walletDTO.setStage2amnt(jsonObject.getString("stage2_amount"));
                            walletDTO.setStartDate(jsonObject.getString("start_date"));
                            walletDTO.setEndDate(jsonObject.getString("end_date"));
                            walletDTO.setLocation(jsonObject.getString("location"));

                            walletDTOArrayList.add(walletDTO);


                        }
                        recyclerView.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(WalletDetails.this,WalletActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                //  loadStudioWallet();
                emptylist.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else
            {
                loadFreelancerWallet();

            }
        }
        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(final boolean isConnected)
    {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            if (sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase("1"))
            {
                //  loadStudioWallet();
                emptylist.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
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
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected) {
                            loadFreelancerWallet();
                        }

                    }
                });




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
