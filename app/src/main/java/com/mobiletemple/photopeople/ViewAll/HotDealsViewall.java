package com.mobiletemple.photopeople.ViewAll;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.Hotdeals;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HotDealsViewall extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Hotdeals> hotdealsList;
    Hotdeal_viewall_adapter adapter;
    private LinearLayoutManager mLayoutManager;
    SessionManager sessionManager;
    RequestQueue queue;
    String offsetString;
    int offset = 0;
    public static final int DISMISS_TIMEOUT = 5000;

    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_deals_viewall);

        hotdealsList = new ArrayList<>();
        queue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Hot Deals");
        sessionManager = new SessionManager(getApplicationContext());
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HotDealsViewall.this, HomePage.class);
                intent.putExtra("from", "other");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        filter.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new Hotdeal_viewall_adapter(this, hotdealsList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        offset = 0;
        offsetString = String.valueOf(offset);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hotDeals();

            }
        }, 0);


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

                        hotDeals();


                        isLoading = true;
                    }

                }


            }
        });
    }


    private void hotDeals() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.VIEWALL_HOTDEALS, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                Log.e("VIEWALL_HOTDEALS", response);
                try {

                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray ProductList = obj.getJSONArray("ProductList");

                        for (int i = 0; i < ProductList.length(); i++) {
                            JSONObject ProductListobj = ProductList.getJSONObject(i);
                            String product_id = ProductListobj.getString("product_id");
                            String product_condition = ProductListobj.getString("product_condition");
                            String product_title = ProductListobj.getString("product_title");
                            String product_discription = ProductListobj.getString("product_discription");
                            String product_image = ProductListobj.getString("product_image");
                            String username = ProductListobj.getString("username");
                            String user_type = ProductListobj.getString("user_type");
                            String price = ProductListobj.getString("product_price");

                            Hotdeals hotdeals = new Hotdeals();
                            hotdeals.setdiscountmsg(product_title);
                            hotdeals.settermscondition(product_discription);
                            hotdeals.setstudioname(username);
                            hotdeals.setThumbnail(product_image);
                            hotdeals.setProdCondition(product_condition);
                            hotdeals.setprodId(product_id);
                            hotdeals.setPrice(price);


                            hotdealsList.add(hotdeals);

                        }
                        adapter.notifyDataSetChanged();
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("offset", offsetString);
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
    public void onStart() {
        //Log.e("storyfragment>>","onStart");
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HotDealsViewall.this, HomePage.class);
        intent.putExtra("from", "other");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }
}
