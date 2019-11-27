package com.mobiletemple.photopeople.ViewAll;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.FeatureFreeDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeaturedViewAll extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<FeatureFreeDTO> freelancerList;
    Feature_viewall_adapter adapter;
    private LinearLayoutManager mLayoutManager;
    SessionManager sessionManager;
    RequestQueue queue;
    String offsetString;
    int offset=0;
    public static final int DISMISS_TIMEOUT = 5000;
    double lat;
    double lng;

    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_view_all);
        freelancerList = new ArrayList<>();
        queue = Volley.newRequestQueue(this);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Featured Freelancer");
        sessionManager = new SessionManager(getApplicationContext());
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FeaturedViewAll.this,HomePage.class);
                intent.putExtra("from","other");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });
        filter.setVisibility(View.GONE);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        adapter = new Feature_viewall_adapter(this, freelancerList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        offset=0;
        offsetString=String.valueOf(offset);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                freelancerFeatureList();
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



                    if (isLoading)
                    {

                        if (totalitemcount > previoustotal) {
                            isLoading = false;
                            previoustotal = totalitemcount;

                        }


                    }

                    if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold)) {
                        offset = offset + 1;
                        offsetString = String.valueOf(offset);
                        freelancerFeatureList();
                        isLoading = true;
                    }

                }



            }
        });
    }


    private void freelancerFeatureList()
    {
//        dialog.setMessage("Please Wait..");
//        dialog.setCancelable(true);
//        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.VIEWALL_FEATUREFREE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       // dialog.dismiss();
                        Log.e("VIEWALL_FEATUREFREE", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {

                                JSONArray freelancerjsonarray=obj.getJSONArray("feturde_freelancer_list");
                                JSONArray camer_listing=obj.getJSONArray("camera_list");

                                for (int x = 0; x < freelancerjsonarray.length(); x++)
                                {
                                    JSONObject freelancerObj = null;
                                    try {
                                        freelancerObj = freelancerjsonarray.getJSONObject(x);
                                        FeatureFreeDTO freelancer = new FeatureFreeDTO();

                                        freelancer.setId(freelancerObj.getString("id"));
                                        freelancer.setName(freelancerObj.getString("first_name")+" "+freelancerObj.getString("last_name"));
                                        freelancer.setStarting_price(freelancerObj.getString("starting_price"));
                                        if(! freelancerObj.getString("travel_by").isEmpty())
                                            freelancer.setTravel_by(Integer.parseInt(freelancerObj.getString("travel_by")));
                                        freelancer.setDob(freelancerObj.getString("dob"));
                                        freelancer.setProfile_image(freelancerObj.getString("profile_image"));
                                        freelancer.setRating(freelancerObj.getDouble("freelancer_rating"));
                                        String cameraid=freelancerObj.getString("camera_type");
                                        for (int j=0;j<camer_listing.length();j++)
                                        {
                                            JSONObject jsonObject=camer_listing.getJSONObject(j);
                                            if (cameraid.equalsIgnoreCase(jsonObject.getString("id")))
                                            {
                                                freelancer.setCameratype(jsonObject.getString("camera_name"));
                                            }
                                        }

                                        freelancerList.add(freelancer);


                                    }catch(Exception ee){
                                        //Log.e("### "+ee.getMessage(),x + " " +freelancerObj.getString("first_name"));
                                    }
                                }
                                adapter.notifyDataSetChanged();

                            } else{
//                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "No more featured frealancer available.", Toast.LENGTH_SHORT).show();}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("offset",offsetString);
                ob.put("lat",sessionManager.getMyLatLong().get(SessionManager.KEY_MYLATITUDE));
                ob.put("lng",sessionManager.getMyLatLong().get(SessionManager.KEY_MYLONGITUDE));

                Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

//    @Override
//    public void onStart() {
//        //Log.e("storyfragment>>","onStart");
//        freelancerList.clear();
//
//        freelancerFeatureList(offsetString);
//
//        super.onStart();
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(FeaturedViewAll.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }
}
