package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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
import com.mobiletemple.photopeople.adapter.CameraRentalAdapter;
import com.mobiletemple.photopeople.model.CameraRentalDTO;
import com.mobiletemple.photopeople.model.TimelineDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CameraRentalListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<CameraRentalDTO> cameraRentalDTOArrayList;
    CameraRentalAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    String offsetString;
    int offset = 0;
    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;
    String lat,lng;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_rental_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        filter.setVisibility(View.GONE);
        mTitle.setText("Camera Rental List");

        intent=getIntent();
        lat=intent.getStringExtra("lat");
        lng=intent.getStringExtra("lng");

        dialog = new ProgressDialog(this);
        session = new SessionManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        cameraRentalDTOArrayList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new CameraRentalAdapter(this, cameraRentalDTOArrayList);
        recyclerView.setAdapter(adapter);

        offset = 0;
        offsetString = String.valueOf(offset);
        addDataToList();
        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(CameraRentalListActivity.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","other");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {

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

                    if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold))
                    {
                        offset = offset + 1;
                        offsetString = String.valueOf(offset);
                        Log.e("offset",offsetString);
                        addDataToList();
                        isLoading = true;
                    }

                }


            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(CameraRentalListActivity.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }

    private void addDataToList()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CAMERARENTAL_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            //  dialog.cancel();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success"))
                            {

                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++)
                                {

                                    final JSONObject dataJSONObject = data.getJSONObject(x);
                                    CameraRentalDTO cameraRentalDTO = new CameraRentalDTO();
                                    cameraRentalDTO.setId(dataJSONObject.getString("id"));
                                    cameraRentalDTO.setName(dataJSONObject.getString("name"));
                                    cameraRentalDTO.setMobile(dataJSONObject.getString("mobile"));
                                    cameraRentalDTO.setLocation(dataJSONObject.getString("location"));

                                    cameraRentalDTOArrayList.add(cameraRentalDTO);


                                }
                                adapter.notifyDataSetChanged();

                            } else if (status == 0 && message.equalsIgnoreCase("failed")) {


                            } else if (status == 0 && message.equalsIgnoreCase("Record Not Found")) {
                            }

                        } catch (Exception ex) {

                            Log.e("exception",ex.getMessage());
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
                Map<String, String> params = new HashMap<>();
                //user_id & offset  & buket_id

                params.put("offset", offsetString);
                params.put("latitude", lat);
                params.put("longitude", lng);

                Log.e("timelineparams", params + "");

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }
}
