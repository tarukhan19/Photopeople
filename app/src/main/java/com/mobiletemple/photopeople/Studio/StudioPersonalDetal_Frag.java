package com.mobiletemple.photopeople.Studio;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudioPersonalDetal_Frag extends Fragment {
    TextView priceTV,cityTV,emailTV,mobileTV,fbTV,webTV,blogTV,aboutmeTV;
    Intent intent;
    String studioid;
    ProgressDialog dialog;
    RequestQueue queue;
    Uri deepLink;

    SessionManager mSessionManager;
    public StudioPersonalDetal_Frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_studio_personal_detal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        intent=getActivity().getIntent();

        priceTV=view.findViewById(R.id.priceTV);
        cityTV=view.findViewById(R.id.cityTV);
        blogTV=view.findViewById(R.id.blogTV);
        emailTV=view.findViewById(R.id.emailTV);
        mobileTV=view.findViewById(R.id.mobileTV);
        aboutmeTV=view.findViewById(R.id.aboutmeTV);

        fbTV=view.findViewById(R.id.fbTV);
        webTV=view.findViewById(R.id.webTV);
        dialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        mSessionManager = new SessionManager(getActivity().getApplicationContext());
        studioid=intent.getStringExtra("studioId");
//        //Log.e("freelancerIdfragment",freelancerId);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getActivity().getIntent())
                .addOnSuccessListener(getActivity(), new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData != null)
                        {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            String  url = deepLink.toString();
                            studioid= url.replace("http://eventdesire.com/event/webservice/studio/single_studio?studio_id=", "");
                            loadStudio();

                        } else {
                            Log.d("deeplink", "getDynamicLink: no link found");
                        }
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("deeplink", "getDynamicLink:onFailure", e);
                    }
                });



    }
    private void loadStudio()
    {

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LOAD_STUDIO_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    Log.e("response",response);
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                JSONObject jsonObject = obj.getJSONObject("data");

                                    priceTV.setText(jsonObject.getString("starting_price"));
                                    cityTV.setText(jsonObject.getString("location"));
                                    emailTV.setText(jsonObject.getString("email"));
                                    mobileTV.setText(jsonObject.getString("phone"));
//                                    fbTV.setText(jsonObject.getString("fb_page"));
//                                    webTV.setText(jsonObject.getString("web_url"));
//                                    blogTV.setText(jsonObject.getString("blog_url"));
//                                    aboutmeTV.setText(jsonObject.getString("about_me"));


                                    if (jsonObject.getString("web_url").isEmpty())
                                    {
                                        webTV.setText("Not Mentioned");
                                    }
                                    else {
                                        webTV.setText(jsonObject.getString("web_url"));

                                    }

                                    if (jsonObject.getString("fb_page").isEmpty())
                                    {
                                        fbTV.setText("Not Mentioned");
                                    }
                                    else {
                                        fbTV.setText(jsonObject.getString("fb_page"));

                                    }

                                    if (jsonObject.getString("blog_url").isEmpty())
                                    {
                                        blogTV.setText("Not Mentioned");
                                    }
                                    else {
                                        blogTV.setText(jsonObject.getString("blog_url"));

                                    }

                                    if (jsonObject.getString("about_me").isEmpty())
                                    {
                                        aboutmeTV.setText("Not Mentioned");
                                    }
                                    else {
                                        aboutmeTV.setText(jsonObject.getString("about_me"));
                                    }





                            } else{}
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Map<String, String> params = new HashMap<>();
                params.put("studio_id", studioid);
                //    //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    @Override
    public void onStart() {
       loadStudio();

        super.onStart();
    }


}
