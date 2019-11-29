package com.mobiletemple.photopeople.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.FreelancerJob_ProcessAdapter;
import com.mobiletemple.photopeople.adapter.Studiojob_ProcessAdapter;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.model.My_Job_ProcessDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyJobProcessFragment extends Fragment  implements ConnectivityReceiver.ConnectivityReceiverListener {
    RecyclerView myjob_proces_recyclerView;
    ArrayList<My_Job_ProcessDTO> my_job_processDTOS;
    Studiojob_ProcessAdapter studiojob_processAdapter;
    FreelancerJob_ProcessAdapter freelancerJob_processAdapter;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    String usertype;
    ImageView emptylist;
    boolean isConnected;

    public MyJobProcessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_job_process, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        my_job_processDTOS = new ArrayList<My_Job_ProcessDTO>();
        studiojob_processAdapter = new Studiojob_ProcessAdapter(getActivity(), my_job_processDTOS);
        freelancerJob_processAdapter=new FreelancerJob_ProcessAdapter(getActivity(),my_job_processDTOS);
        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        emptylist=view.findViewById(R.id.emptylist);

        session = new SessionManager(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        myjob_proces_recyclerView=(RecyclerView)view.findViewById(R.id.myjob_proces_recyclerView) ;
        myjob_proces_recyclerView.setLayoutManager(linearLayoutManager);




        //waitingInbox();

    }

    private void waitingJobStudio()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PROCESS_EVENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("WAITING_EVENT", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success"))
                            {
                                emptylist.setVisibility(View.GONE);
                                myjob_proces_recyclerView.setVisibility(View.VISIBLE);
                                my_job_processDTOS.clear();
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject eventObj = data.getJSONObject(x);




                                    My_Job_ProcessDTO event = new My_Job_ProcessDTO();
                                    event.setEventType(eventObj.getString("event_type"));
                                    event.setLocation(eventObj.getString("location"));
                                    event.setVenue(eventObj.getString("venue"));
                                    event.setStartDate(eventObj.getString("start_date"));
                                    event.setEndDate(eventObj.getString("end_date"));
                                    event.setShootType(eventObj.getString("type"));
                                    event.setDesc(eventObj.getString("description"));
                                    event.setPrice(eventObj.getInt("price"));

                                    event.setEventId(eventObj.getString("event_id"));
                                    event.setAmount(eventObj.getString("amount"));
                                    event.setEndUserId(eventObj.getString("end_user_id"));
                                    event.setQuoteId(eventObj.getString("quote_id"));
                                    //  event.setQuoteMsg(eventObj.getString("quote_msg"));
                                    event.setStudioId(eventObj.getString("studio_id"));
                                    event.setQuoteStatus(eventObj.getString("quote_status"));

                                    my_job_processDTOS.add(event);
                                }
                                myjob_proces_recyclerView.setAdapter(studiojob_processAdapter);


                            }


                            else {
                                emptylist.setVisibility(View.VISIBLE);
                                myjob_proces_recyclerView.setVisibility(View.GONE);                            }

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
                ob.put("studio_user_id", session.getuserId().get(SessionManager.KEY_USERID));
                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
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
                                    //Log.e("freeId",freeId);
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
                                    String recivertype = dataJSONObject.getString("reqiest_user_type");
                                    //Log.e("recivertype",recivertype);


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
                                    freelancer.setType(recivertype);

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
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        if (isConnected)
        {
            if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.FREELANCER_TYPE))
            {waitingJobFreelancer();}

            else if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.STUDIO_TYPE))
            {waitingJobStudio();}
        }
        super.onStart();
    }

//    // Showing the status in Snackbar
//    private void showSnack(boolean isConnected) {
//        String message;
//        int color;
//
//        //Log.e("showSnackisConnected",isConnected+"");
//        if (isConnected) {
//            message = "Good! Connected to Internet";
//            color = Color.WHITE;
//            if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.FREELANCER_TYPE))
//            {waitingJobFreelancer();}
//
//            else if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.STUDIO_TYPE))
//            {waitingJobStudio();}
//        } else {
//            message = "Sorry! Not connected to internet";
//            color = Color.RED;
//
//        }
//
//        Snackbar snackbar = Snackbar
//                .make(getView().findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);
//
//        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(color);
//        snackbar.show();
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // register connection status listener
//        MyApplication.getInstance().setConnectivityListener(this);
//    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        //showSnack(isConnected);



    }

}
