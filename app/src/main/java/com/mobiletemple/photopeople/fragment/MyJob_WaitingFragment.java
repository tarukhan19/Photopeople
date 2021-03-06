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
import com.mobiletemple.photopeople.adapter.Freelancerjob_waitAdapter;
import com.mobiletemple.photopeople.adapter.Studiojob_waitAdapter;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.model.Myjob_waitDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyJob_WaitingFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    RecyclerView myjob_wait_recyclerView;
    ArrayList<Myjob_waitDTO> myjob_waitDTOS;
    Freelancerjob_waitAdapter freelancerjob_waitAdapter;
    Studiojob_waitAdapter studiojob_waitAdapter;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    ImageView emptylist;
    boolean isConnected;

    String usertype;

    public MyJob_WaitingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_job__waiting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myjob_waitDTOS = new ArrayList<Myjob_waitDTO>();
        freelancerjob_waitAdapter = new Freelancerjob_waitAdapter(getActivity(), myjob_waitDTOS);
        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        emptylist = view.findViewById(R.id.emptylist);

        studiojob_waitAdapter = new Studiojob_waitAdapter(getActivity(), myjob_waitDTOS);
        session = new SessionManager(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        myjob_wait_recyclerView = (RecyclerView) view.findViewById(R.id.myjob_wait_recyclerView);
        myjob_wait_recyclerView.setLayoutManager(linearLayoutManager);
        myjob_wait_recyclerView.setAdapter(freelancerjob_waitAdapter);
        myjob_wait_recyclerView.setAdapter(studiojob_waitAdapter);
//

        //waitingInbox();

    }

    private void waitingJobStudio() {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.WAITING_EVENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("WAITING_EVENT", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success")) {

                                myjob_waitDTOS.clear();
                                emptylist.setVisibility(View.GONE);
                                myjob_wait_recyclerView.setVisibility(View.VISIBLE);
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject eventObj = data.getJSONObject(x);

                                    Myjob_waitDTO event = new Myjob_waitDTO();
                                    event.setEventType(eventObj.getString("event_type"));
                                    event.setLocation(eventObj.getString("location"));
                                    event.setVenue(eventObj.getString("venue"));
                                    event.setStartDate(eventObj.getString("start_date"));
                                    event.setEndDate(eventObj.getString("end_date"));
                                    event.setShootType(eventObj.getString("type"));
                                    event.setDesc(eventObj.getString("description"));
                                    event.setPrice(eventObj.getString("price"));


                                    event.setId(eventObj.getString("studio_id"));

                                    event.setEventId(eventObj.getString("event_id"));
                                    event.setAmount(eventObj.getString("amount"));
                                    event.setEndUserId(eventObj.getString("end_user_id"));
                                    event.setQuoteId(eventObj.getString("quote_id"));
                                    //  event.setQuoteMsg(eventObj.getString("quote_msg"));
                                    event.setStudioId(eventObj.getString("studio_id"));
                                    event.setQuoteStatus(eventObj.getString("quote_status"));

                                    myjob_waitDTOS.add(event);
                                }
                                myjob_wait_recyclerView.setAdapter(studiojob_waitAdapter);
                                studiojob_waitAdapter.notifyDataSetChanged();

                            } else {
                                emptylist.setVisibility(View.VISIBLE);
                                myjob_wait_recyclerView.setVisibility(View.GONE);
                                studiojob_waitAdapter.notifyDataSetChanged();

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

    private void waitingJobFreelancer() {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.WAITING_FREELANCER_JOB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("WAITING_FREELANCER_JOB", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success")) {
                                emptylist.setVisibility(View.GONE);
                                myjob_wait_recyclerView.setVisibility(View.VISIBLE);
                                myjob_waitDTOS.clear();
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject dataJSONObject = data.getJSONObject(x);
                                    //Log.e("x",x+"");
                                    //Log.e("length",data.length()+"");
                                    String quoteId = dataJSONObject.getString("id");
                                    String freeId = dataJSONObject.getString("freelancer_id");
                                    String studioId = dataJSONObject.getString("studio_id");
                                    String eventType = dataJSONObject.getString("event_type");
                                    String shootType = dataJSONObject.getString("type");
                                    String startDate = dataJSONObject.getString("start_date");
                                    String endDate = dataJSONObject.getString("end_date");
                                    String venue = dataJSONObject.getString("venue");
                                    String location = dataJSONObject.getString("location");
                                    String desc = dataJSONObject.getString("description");
                                    String quoteStatus = dataJSONObject.getString("status");
                                    String profile = dataJSONObject.getString("profile_image");
                                    String firstName = dataJSONObject.getString("first_name");
                                    String amount = dataJSONObject.getString("amount");
                                    String studio_name = dataJSONObject.getString("studio_name");
                                    String recivertype = dataJSONObject.getString("reqiest_user_type");
                                    //Log.e("recivertype",recivertype);

                                    Myjob_waitDTO freelancer = new Myjob_waitDTO();
                                    freelancer.setQuoteId(quoteId);
                                    freelancer.setId(freeId);
                                    freelancer.setStudioId(studioId);
                                    freelancer.setFreeId(freeId);
                                    freelancer.setEventType(eventType);
                                    freelancer.setShootType(shootType);
                                    freelancer.setStartDate(startDate);
                                    freelancer.setEndDate(endDate);
                                    freelancer.setVenue(venue);
                                    freelancer.setLocation(location);
                                    freelancer.setAmount(amount);
                                    freelancer.setStatus(quoteStatus);
                                    freelancer.setProfile_image(profile);
                                    freelancer.setName(firstName);
                                    freelancer.setType(recivertype);
                                    freelancer.setStudioName(studio_name);
                                    freelancer.setDesc(desc);

                                    myjob_waitDTOS.add(freelancer);
                                }
                                myjob_wait_recyclerView.setAdapter(freelancerjob_waitAdapter);


                            } else {
                                emptylist.setVisibility(View.VISIBLE);
                                myjob_wait_recyclerView.setVisibility(View.GONE);
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
        requestQueue.getCache().clear();
    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.FREELANCER_TYPE)) {
                waitingJobFreelancer();
            } else if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.STUDIO_TYPE)) {
                waitingJobStudio();
            }

        }
        super.onStart();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        //showSnack(isConnected);


    }


}
