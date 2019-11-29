package com.mobiletemple.photopeople.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.HiredFreeWaitAdapter;
import com.mobiletemple.photopeople.model.Hire_free_waitDTO;
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
public class Hired_Waiting_Frag extends Fragment  {
RecyclerView hire_wait_recyclerView;
ArrayList<Hire_free_waitDTO> hire_free_waitDTOS;
HiredFreeWaitAdapter hiredFreeWaitAdapter;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    boolean isConnected;
    ImageView emptylist;

SessionManager session;
    public Hired_Waiting_Frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hired__waiting_, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        hire_free_waitDTOS = new ArrayList<Hire_free_waitDTO>();
        hiredFreeWaitAdapter = new HiredFreeWaitAdapter(getActivity(), hire_free_waitDTOS);
        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        emptylist=view.findViewById(R.id.emptylist);
        session = new SessionManager(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        hire_wait_recyclerView=(RecyclerView)view.findViewById(R.id.hire_wait_recyclerView) ;
        hire_wait_recyclerView.setLayoutManager(linearLayoutManager);
        hire_wait_recyclerView.setAdapter(hiredFreeWaitAdapter);

        waitingInbox();
    }

    private void waitingInbox()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.WAITING_INBOX,
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
                                hire_free_waitDTOS.clear();
                                emptylist.setVisibility(View.GONE);
                                hire_wait_recyclerView.setVisibility(View.VISIBLE);

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
                                    String dob = dataJSONObject.getString("dob");
                                    String travel_by = dataJSONObject.getString("travel_by");

                                    Hire_free_waitDTO freelancer = new Hire_free_waitDTO();
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
                                    freelancer.setTravel_by(travel_by);
                                    freelancer.setDob(dob);

                                    hire_free_waitDTOS.add(freelancer);
                                }
                                hire_wait_recyclerView.setAdapter(hiredFreeWaitAdapter);


                                //Log.e("wait free size", hire_free_waitDTOS.size() + "");


                            }


                            else {
                                emptylist.setVisibility(View.VISIBLE);
                                hire_wait_recyclerView.setVisibility(View.GONE);
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
                ob.put("reqiest_user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));

                Log.e("params", ob.toString());
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
//            waitingInbox();
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
//    @Override
//    public void onNetworkConnectionChanged(boolean isConnected) {
//        this.isConnected=isConnected;
//        //Log.e("onNetworkConnectionconn",isConnected+"");
//
//        showSnack(isConnected);
//
//
//
//    }
//

}
