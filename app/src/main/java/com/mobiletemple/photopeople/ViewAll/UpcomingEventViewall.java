package com.mobiletemple.photopeople.ViewAll;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.UpcomingEventDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpcomingEventViewall extends AppCompatActivity{
    private RecyclerView recyclerView;
    private ArrayList<UpcomingEventDTO> upcomingEventDTOArrayList;
    Upcomingevent_viewall_adapter adapter;
    private LinearLayoutManager mLayoutManager;
    SessionManager sessionManager;
    ProgressDialog dialog;
    RequestQueue queue;
    String offsetString;
    int offset=0;
    public static final int DISMISS_TIMEOUT = 5000;

    boolean isConnected;

    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_event_viewall);

        isConnected = ConnectivityReceiver.isConnected();

        upcomingEventDTOArrayList = new ArrayList<>();
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Upcoming Event");
        sessionManager = new SessionManager(getApplicationContext());
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpcomingEventViewall.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","other");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });
        filter.setVisibility(View.GONE);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new Upcomingevent_viewall_adapter(this, upcomingEventDTOArrayList);
        recyclerView.setAdapter(adapter);
        if (isConnected) {
            offset = 0;
            offset = offset + 1;
            offsetString = String.valueOf(offset);
            loadStudioEvent(offsetString);

        }
        if (isConnected) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }


                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    visibleItemCount = mLayoutManager.getChildCount();
                    totalitemcount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (dy > 0) //check for scroll down
                    {



                        if (isLoading) {

                            if (totalitemcount > previoustotal) {
                                isLoading = false;
                                previoustotal = totalitemcount;

                            }


                        }

                        if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold)) {
                            offset = offset + 1;
                            offsetString = String.valueOf(offset);
                            loadStudioEvent(offsetString);
                            isLoading = true;
                        }

                    }



                }
            });
        }


    }



    private void loadStudioEvent(final String offsetString)

    {
//        dialog.setMessage("Loading Please Wait...");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.VIEWALL_UPCOMINGEVENT, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                Log.e("VIEWALL_UPCOMINGEVENT",response);
                try {
                    upcomingEventDTOArrayList.clear();
                   // dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status=obj.getInt("status");
                    String message=obj.getString("message");
                    if (status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONArray ProductList=obj.getJSONArray("data");

                        for (int i=0;i<ProductList.length();i++)
                        {
                            JSONObject ProductListobj = ProductList.getJSONObject(i);
                            String event_type=ProductListobj.getString("event_name");
                            String location=ProductListobj.getString("location");
                            String start_date=ProductListobj.getString("start_date");
                            String end_date=ProductListobj.getString("end_date");
                            String user_name=ProductListobj.getString("user_name");
                            String mobile_no=ProductListobj.getString("mobile_no");
                            String price=ProductListobj.getString("budget");
                            String desc=ProductListobj.getString("description");


                            UpcomingEventDTO hotdeals=new UpcomingEventDTO();
                            hotdeals.setEventType(event_type);
                            hotdeals.setLocation(location);
                            hotdeals.setStartDate(start_date);
                            hotdeals.setEndDate(end_date);
                            hotdeals.setAmount(price);
                            hotdeals.setDesc(desc);
                            hotdeals.setUser_name(user_name);
                            hotdeals.setUser_phone(mobile_no);


                            upcomingEventDTOArrayList.add(hotdeals);

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
                ob.put("offset",offsetString);



                Log.e("params", ob.toString());
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
        Intent intent=new Intent(UpcomingEventViewall.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }


}
