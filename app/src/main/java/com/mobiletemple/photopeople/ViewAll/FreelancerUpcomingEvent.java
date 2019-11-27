package com.mobiletemple.photopeople.ViewAll;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.My_Job_ProcessDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FreelancerUpcomingEvent extends AppCompatActivity {
    RecyclerView myjob_proces_recyclerView;
    ArrayList<My_Job_ProcessDTO> my_job_processDTOS;
    Freelancer_upcomingevent_Adapter freelancerJob_processAdapter;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    String usertype;
    ImageView emptylist;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_upcoming_event);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Upcoming Event");
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FreelancerUpcomingEvent.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","other");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });
        filter.setVisibility(View.GONE);

        my_job_processDTOS = new ArrayList<My_Job_ProcessDTO>();
        freelancerJob_processAdapter=new Freelancer_upcomingevent_Adapter(this,my_job_processDTOS);
        requestQueue = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);
        emptylist=findViewById(R.id.emptylist);

        session = new SessionManager(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myjob_proces_recyclerView=(RecyclerView)findViewById(R.id.myjob_proces_recyclerView) ;
        myjob_proces_recyclerView.setLayoutManager(linearLayoutManager);

        waitingJobFreelancer();



    }


    private void waitingJobFreelancer()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PROCESS_FREELANCER_JOB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("PROCESS_FREELANCER_JOB", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success"))
                            {

                                my_job_processDTOS.clear();
                                emptylist.setVisibility(View.GONE);
                                myjob_proces_recyclerView.setVisibility(View.VISIBLE);
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject dataJSONObject = data.getJSONObject(x);

                                    String quoteId = dataJSONObject.getString("id");
                                    String freeId = dataJSONObject.getString("freelancer_id");
                                    String studioId = dataJSONObject.getString("studio_id");
                                    String eventType = dataJSONObject.getString("event_type");
                                    String shootType = dataJSONObject.getString("type");

                                    String startDate = dataJSONObject.getString("start_date");
                                    String endDate = dataJSONObject.getString("end_date");

                                    String quoteAmount = dataJSONObject.getString("amount");

                                    String venue = dataJSONObject.getString("venue");
                                    String location = dataJSONObject.getString("location");

                                    String quoteStatus = dataJSONObject.getString("status");
                                    String profile = dataJSONObject.getString("profile_image");
                                    String firstName = dataJSONObject.getString("first_name");
                                    String lastName = dataJSONObject.getString("last_name");

                                    String startPrice = dataJSONObject.getString("starting_price");
                                    String studio_name = dataJSONObject.getString("studio_name");
                                    String desc = dataJSONObject.getString("description");


                                    My_Job_ProcessDTO freelancer = new My_Job_ProcessDTO();
                                    freelancer.setQuoteId(quoteId);
                                    freelancer.setFreeId(freeId);
                                    freelancer.setStudioId(studioId);
                                    freelancer.setEventType(eventType);
                                    freelancer.setShootType(shootType);
                                    freelancer.setStartDate(startDate);
                                    freelancer.setEndDate(endDate);
                                    freelancer.setVenue(venue);
                                    freelancer.setLocation(location);
                                    freelancer.setAmount(quoteAmount);
                                    freelancer.setStatus(quoteStatus);
                                    freelancer.setProfile_image(profile);
                                    freelancer.setName(firstName);
                                    freelancer.setStarting_price(startPrice);
                                    freelancer.setStudioName(studio_name);
                                    freelancer.setDesc(desc);

                                    my_job_processDTOS.add(freelancer);
                                }
                                myjob_proces_recyclerView.setAdapter(freelancerJob_processAdapter);


                            }


                            else {
                                emptylist.setVisibility(View.VISIBLE);
                                myjob_proces_recyclerView.setVisibility(View.GONE);
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
                ob.put("freelancerid", session.getuserId().get(SessionManager.KEY_USERID));
                //Log.e("params", ob.toString());
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
        Intent intent=new Intent(FreelancerUpcomingEvent.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }
}
