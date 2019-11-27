package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.mobiletemple.photopeople.adapter.NotificationAdapter;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.model.NotificationListDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    LinearLayout notification,timeline,main,chatLL,home;
    Intent intent;
    boolean isConnected;

    RecyclerView recyclerView;
    ArrayList<NotificationListDTO> notificationListDTOS;
    NotificationAdapter notificationAdapter;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    ImageView emptylist;
    SessionManager session;
    String url,parameter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notification=findViewById(R.id.notification);
        timeline=findViewById(R.id.timeline);
        main=findViewById(R.id.main);
        chatLL=findViewById(R.id.chatLL);
        home=findViewById(R.id.home);

        notificationListDTOS = new ArrayList<NotificationListDTO>();
        notificationAdapter = new NotificationAdapter(this, notificationListDTOS);
        requestQueue = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);
        emptylist=findViewById(R.id.emptylist);
        session = new SessionManager(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView) ;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(notificationAdapter);

        if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.STUDIO_TYPE))
        {
            url= Endpoints.STUDIO_NOTIFICATION_LIST;
            parameter="studio_user_id";
        }
        else
        {
            url= Endpoints.FREELANCER_NOTIFICATION_LIST;
            parameter="freelancer_id";

        }

        loadNotificationList(url,parameter);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NotificationActivity.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","other");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });
        filter.setVisibility(View.GONE);
        mTitle.setText("Notifications");
        timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                intent=new Intent(NotificationActivity.this,WalletActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_in,
//                        R.anim.trans_left_out);

                Toast.makeText(NotificationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();

            }
        });
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent=new Intent(NotificationActivity.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from","other");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });
        chatLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(NotificationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                intent=new Intent(NotificationActivity.this,InboxActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_in,
//                        R.anim.trans_left_out);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(NotificationActivity.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from","other");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


    }

    private void loadNotificationList(String url, final String parameter)
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("hirewaiting", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success"))
                            {
                                notificationListDTOS.clear();
                                emptylist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject dataJSONObject = data.getJSONObject(x);

                                    NotificationListDTO notification = new NotificationListDTO();
                                    notification.setOrderId(dataJSONObject.getString("quote_id"));
                                    notification.setMsg(dataJSONObject.getString("msg"));
                                    notification.setNoOfDays(dataJSONObject.getString("date"));
                                    notificationListDTOS.add(notification);
                                }
                                recyclerView.setAdapter(notificationAdapter);


                                //Log.e("wait free size", hire_free_waitDTOS.size() + "");


                            }


                            else {
                                emptylist.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                        } catch (Exception ex) {
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
                ob.put(parameter, session.getuserId().get(SessionManager.KEY_USERID));
                Log.e("params", ob.toString()+" "+parameter);
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(NotificationActivity.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");

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
