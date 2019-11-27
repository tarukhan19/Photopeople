package com.mobiletemple.photopeople.fragment;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.HireDeclineAdapter;
import com.mobiletemple.photopeople.model.Hired_free_declineDTO;
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
public class Hired_Decline_Fragment extends Fragment {
    RecyclerView hire_complete_recyclerView;
    ArrayList<Hired_free_declineDTO> hired_free_declineDTOS;
    HireDeclineAdapter hireDeclineAdapter;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    boolean isConnected;
    ImageView emptylist;
    public Hired_Decline_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hired__decline_, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        hired_free_declineDTOS = new ArrayList<Hired_free_declineDTO>();
        hireDeclineAdapter = new HireDeclineAdapter(getActivity(), hired_free_declineDTOS);
        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        emptylist=view.findViewById(R.id.emptylist);

        session = new SessionManager(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        hire_complete_recyclerView=(RecyclerView)view.findViewById(R.id.hire_decline_recyclerView) ;
        hire_complete_recyclerView.setLayoutManager(linearLayoutManager);
        hire_complete_recyclerView.setAdapter(hireDeclineAdapter);

            waitingInbox();


    }

    private void waitingInbox()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.DECLAIN_INBOX,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("hiredecline", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success"))
                            {
                                emptylist.setVisibility(View.GONE);
                                hire_complete_recyclerView.setVisibility(View.VISIBLE);
                                //hired_free_declineDTOS.clear();
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject dataJSONObject = data.getJSONObject(x);

                                    String quoteId = dataJSONObject.getString("id");
                                    String freeId = dataJSONObject.getString("freelancer_id");
                                    String studioId = dataJSONObject.getString("studio_id");
                                    String event_type = dataJSONObject.getString("event_type");
                                    String shootType = dataJSONObject.getString("type");
                                    String startDate = dataJSONObject.getString("start_date");
                                    String endDate = dataJSONObject.getString("end_date");
                                    String quoteAmount = dataJSONObject.getString("amount");
                                    String venue = dataJSONObject.getString("venue");
                                    String location = dataJSONObject.getString("location");
                                    String quoteStatus = dataJSONObject.getString("status");
                                    String profile = dataJSONObject.getString("profile_image");
                                    String firstName = dataJSONObject.getString("first_name");
                                    String phone = dataJSONObject.getString("phone");
                                    String startPrice = dataJSONObject.getString("starting_price");
                                  //  String dob = dataJSONObject.getString("dob");

                                    Hired_free_declineDTO freelancer = new Hired_free_declineDTO();
                                    freelancer.setQuoteId(quoteId);
                                    freelancer.setId(freeId);
                                    freelancer.setStudioId(studioId);
                                    freelancer.setPhone(phone);
                                    freelancer.setEventType(event_type);
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
                                   // freelancer.setTravel_by(travel_by);
                                  //  freelancer.setDob(dob);

                                    hired_free_declineDTOS.add(freelancer);
                                }
                                hire_complete_recyclerView.setAdapter(hireDeclineAdapter);
                            }


                            else {
                                emptylist.setVisibility(View.VISIBLE);
                                hire_complete_recyclerView.setVisibility(View.GONE);
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
                ob.put("studiouser_id", session.getuserId().get(SessionManager.KEY_USERID));
                ob.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));

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
//        isConnected = ConnectivityReceiver.isConnected();

   //     else {

   //     }

        super.onStart();
    }




}
