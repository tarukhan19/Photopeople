package com.mobiletemple.photopeople.BottomNavigation;


import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.NotificationAdapter;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.databinding.FragmentNotificationBinding;
import com.mobiletemple.photopeople.model.NotificationListDTO;
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
public class NotificationFragment extends Fragment {
    Intent intent;
    boolean isConnected;
    ArrayList<NotificationListDTO> notificationListDTOS;
    NotificationAdapter notificationAdapter;
    RequestQueue requestQueue;
  //  ProgressDialog dialog;
    SessionManager session;
    String url, parameter;
    FragmentNotificationBinding binding;

    public NotificationFragment()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        View view = binding.getRoot();
        notificationListDTOS = new ArrayList<NotificationListDTO>();
        notificationAdapter = new NotificationAdapter(getActivity(), notificationListDTOS);
        requestQueue = Volley.newRequestQueue(getActivity());
       // dialog = new ProgressDialog(getActivity());
        session = new SessionManager(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(notificationAdapter);

        if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.STUDIO_TYPE)) {
            url = Endpoints.STUDIO_NOTIFICATION_LIST;
            parameter = "studio_user_id";
        } else {
            url = Endpoints.FREELANCER_NOTIFICATION_LIST;
            parameter = "freelancer_id";

        }

        loadNotificationList(url, parameter);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        backImage.setVisibility(View.GONE);
//        backImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                startActivity(new Intent(getActivity(), HomePage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .putExtra("from", "other"));
//                getActivity().overridePendingTransition(0,0);
//
//            }
//        });


        filter.setVisibility(View.GONE);
        mTitle.setText("Notifications");
        return view;
    }


    private void loadNotificationList(String url, final String parameter) {
//        dialog.setMessage("Please Wait..");
//        dialog.setCancelable(true);
//        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // dialog.dismiss();
                        Log.e("loadNotificationList", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success")) {
                                notificationListDTOS.clear();
                                binding.emptylist.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.VISIBLE);


                                if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.FREELANCER_TYPE)) {
                                    JSONArray data = obj.getJSONArray("data");

                                    for (int x = 0; x < data.length(); x++) {
                                        JSONObject dataJSONObject = data.getJSONObject(x);

                                        NotificationListDTO notification = new NotificationListDTO();
                                        notification.setOrderId(dataJSONObject.getString("quote_id"));
                                        notification.setMsg(dataJSONObject.getString("msg"));
                                        notification.setNoOfDays(dataJSONObject.getString("date"));
                                        notification.setName(dataJSONObject.getString("user_name"));
                                        notificationListDTOS.add(notification);
                                    }
                                    binding.recyclerView.setAdapter(notificationAdapter);
                                } else {
                                    JSONObject object = obj.getJSONObject("data");
                                    JSONArray data = object.getJSONArray("freelancer");
                                    for (int x = 0; x < data.length(); x++) {
                                        JSONObject dataJSONObject = data.getJSONObject(x);

                                        NotificationListDTO notification = new NotificationListDTO();
                                        notification.setOrderId(dataJSONObject.getString("quote_id"));
                                        notification.setMsg(dataJSONObject.getString("msg"));
                                        notification.setNoOfDays(dataJSONObject.getString("date"));
                                        notification.setName(dataJSONObject.getString("user_name"));
                                        notificationListDTOS.add(notification);
                                    }
                                    binding.recyclerView.setAdapter(notificationAdapter);

                                }


                                //Log.e("wait free size", hire_free_waitDTOS.size() + "");


                            } else {
                                binding.emptylist.setVisibility(View.VISIBLE);
                                binding.recyclerView.setVisibility(View.GONE);
                            }

                        } catch (Exception ex) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                      //  dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put(parameter, session.getuserId().get(SessionManager.KEY_USERID));
              //  Log.e("params", ob.toString() + " " + parameter);
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


}
