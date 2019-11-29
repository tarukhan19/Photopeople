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
import com.mobiletemple.photopeople.adapter.Freelancerjob_completeAdapter;
import com.mobiletemple.photopeople.adapter.StudioJob_completeAdapter;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.model.Myjob_completeDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyJobComplte_Fragment extends Fragment  implements ConnectivityReceiver.ConnectivityReceiverListener{
    // TODO: Rename parameter arguments, choose names that match
    RecyclerView myjob_complt_recyclerView;
    ArrayList<Myjob_completeDTO> myjob_completeDTOS;
    StudioJob_completeAdapter studioJob_completeAdapter;
    Freelancerjob_completeAdapter freelancerjob_completeAdapter;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    String usertype;
    ImageView emptyimage;
    boolean isConnected;

    public MyJobComplte_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_job_complte_, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        myjob_completeDTOS = new ArrayList<Myjob_completeDTO>();
        studioJob_completeAdapter = new StudioJob_completeAdapter(getActivity(), myjob_completeDTOS);
        freelancerjob_completeAdapter=new Freelancerjob_completeAdapter(getActivity(),myjob_completeDTOS);
        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        emptyimage=view.findViewById(R.id.emptyimage);
        session = new SessionManager(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        myjob_complt_recyclerView=(RecyclerView)view.findViewById(R.id.myjob_complt_recyclerView) ;
        myjob_complt_recyclerView.setLayoutManager(linearLayoutManager);




        //waitingInbox();

    }

    private void waitingJobStudio()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.COMPLETE_EVENT,
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
                                emptyimage.setVisibility(View.GONE);
                                myjob_complt_recyclerView.setVisibility(View.VISIBLE);
                                myjob_completeDTOS.clear();
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject eventObj = data.getJSONObject(x);




                                    Myjob_completeDTO event = new Myjob_completeDTO();
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

                                    myjob_completeDTOS.add(event);
                                }
                                myjob_complt_recyclerView.setAdapter(studioJob_completeAdapter);


                            }


                            else {
                                emptyimage.setVisibility(View.VISIBLE);
                                myjob_complt_recyclerView.setVisibility(View.GONE);
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

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.COMPLETE_FREELANCER_JOB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("COMPLETE_FREELANCER_JOB", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success"))
                            {

                                myjob_completeDTOS.clear();
                                emptyimage.setVisibility(View.GONE);
                                myjob_complt_recyclerView.setVisibility(View.VISIBLE);
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


                                    Myjob_completeDTO freelancer = new Myjob_completeDTO();
                                    freelancer.setQuoteId(quoteId);
                                    freelancer.setId(freeId);
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

                                    myjob_completeDTOS.add(freelancer);
                                }
                                myjob_complt_recyclerView.setAdapter(freelancerjob_completeAdapter);


                            }


                            else {
                                emptyimage.setVisibility(View.VISIBLE);
                                myjob_complt_recyclerView.setVisibility(View.GONE);
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
    public void onStart() {
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


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");


    }


}
