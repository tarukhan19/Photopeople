package com.mobiletemple.photopeople.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.hbb20.CountryCodePicker;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FreelancerPersonalDetal_Frag extends Fragment  {
TextView priceTV,cityTV,travelTV,emailTV,cameraTV,lensTV,lightningTV,supportningTV,fbTV,webTV,blogTV,aboutmeTV,freelancertypeTV;
    Intent intent;
    String freelancerId;
    ProgressDialog dialog;
    RequestQueue queue;
    String first_name;
    ArrayList<String> freelancertypeList;
    LinearLayout ll;
    String from,jobstatus;
    ArrayList<String> lenslist,supportninglist,lighninglist;
    SessionManager mSessionManager;
    String Mobile_no,email,price="";
    boolean isConnected;
    ImageView whatsapp,call;
    String countrycode;
    Uri deepLink;

    public FreelancerPersonalDetal_Frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freelancer_personal_detal_, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        intent=getActivity().getIntent();
        lenslist=new ArrayList<>();
        supportninglist=new ArrayList<>();
        freelancertypeList=new ArrayList<>();
        lighninglist=new ArrayList<>();
        priceTV=view.findViewById(R.id.priceTV);
        cityTV=view.findViewById(R.id.cityTV);
        travelTV=view.findViewById(R.id.travelTV);
        emailTV=view.findViewById(R.id.emailTV);
        cameraTV=view.findViewById(R.id.cameraTV);
        lensTV=view.findViewById(R.id.lensTV);
        ll=view.findViewById(R.id.ll);
        blogTV=view.findViewById(R.id.blogTV);
        aboutmeTV=view.findViewById(R.id.aboutmeTV);
        freelancertypeTV=view.findViewById(R.id.freelancertypeTV);
        lightningTV=view.findViewById(R.id.lightningTV);
        supportningTV=view.findViewById(R.id.supportningTV);
        fbTV=view.findViewById(R.id.fbTV);
        webTV=view.findViewById(R.id.webTV);
        dialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        mSessionManager = new SessionManager(getActivity().getApplicationContext());
        freelancerId=intent.getStringExtra("freelancerId");
        from=intent.getStringExtra("from");
        jobstatus=intent.getStringExtra("status");
        whatsapp=view.findViewById(R.id.whatsapp);
        call=view.findViewById(R.id.call);



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
                            freelancerId= url.replace("http://eventdesire.com/event/webservice/Freelancer_details/single_freelancer?user_type=2&freelancer_id=", "");
                            loadfreelancer();
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

        if (intent.hasExtra("from"))
        {
//            if (from.equalsIgnoreCase("profile"))
//            {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, 0, 0, 90);
//                ll.setLayoutParams(params);
//                ll.requestLayout();
//            }


            if (from.equalsIgnoreCase("filter"))
            {
                price=intent.getStringExtra("quoteAmount");
            }


//            if (from.equalsIgnoreCase("homepage"))
//            {
//
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, 0, 0, 10);
//                ll.setLayoutParams(params);
//                ll.requestLayout();
//
//            }


//            if (from.equalsIgnoreCase("hire"))
//            {
//                //  loadfreelancer();
//                if (jobstatus.equalsIgnoreCase("1"))
//                {
//                    FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams) ll.getLayoutParams();
//
//                    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 10); // left, top, right, bottom
//                    ll.setLayoutParams(params);
//                    ll.requestLayout();
//
//
//                }
//                else if (jobstatus.equalsIgnoreCase("2"))
//                {
//                    FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams) ll.getLayoutParams();
//
//                    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 90); // left, top, right, bottom
//                    ll.setLayoutParams(params);
//                    ll.requestLayout();
//
//
//                }
//                else if (jobstatus.equalsIgnoreCase("3"))
//                {
//                    FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams) ll.getLayoutParams();
//
//                    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 90); // left, top, right, bottom
//                    ll.setLayoutParams(params);
//                    ll.requestLayout();
//
//
//                }
//
//
//
//
//            }
//

        }

        else
        {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, 0, 0, 10);
//            ll.setLayoutParams(params);
//            ll.requestLayout();
        }



      whatsapp.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              openWhatsApp();

          }
      });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+countrycode+Mobile_no));
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(callIntent);
            }
        });

    }





    private void openWhatsApp()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_VIEW);
        String url = "https://api.whatsapp.com/send?phone="+countrycode+Mobile_no;
        sendIntent.setData(Uri.parse(url));
        startActivity(sendIntent);
    }

    private void loadfreelancer()
    {

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.FREELANCE_PERSONAL_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        freelancertypeList.clear();
                        lenslist.clear();
                        supportninglist.clear();
                        lighninglist.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("Message");
                            //Log.e("status",status+"");

                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                JSONObject object = obj.getJSONObject("Details");
                                //Log.e("object",object+"");
                                first_name=object.getString("first_name");

                                String address=object.getString("address");
                                String travel_type=object.getString("travel_type");
                                String camera_name=object.getString("camera_name");
                                String starting_price=object.getString("starting_price");
                                Mobile_no=object.getString("Mobile_no");
                                countrycode=object.getString("country_code");
                                String facebook_url=object.getString("facebook_url");
                                String web_url=object.getString("web_url");
                                email=object.getString("email");
                                String freelancertype=object.getString("feelancer_type");
                                emailTV.setText(email);

                                JSONArray lence_list=object.getJSONArray("lence_list");
                                JSONArray suport_list=object.getJSONArray("suport_list");
                                JSONArray lighting_list=object.getJSONArray("lighting_list");
                                priceTV.setText(starting_price);
                                if (web_url.isEmpty())
                                {
                                    webTV.setText("Not Mentioned");
                                }
                                else {
                                    webTV.setText(web_url);

                                }

                                if (facebook_url.isEmpty())
                                {
                                    fbTV.setText("Not Mentioned");
                                }
                                else {
                                    fbTV.setText(facebook_url);

                                }

                                if (object.getString("blog_url").isEmpty())
                                {
                                    blogTV.setText("Not Mentioned");
                                }
                                else {
                                    blogTV.setText(object.getString("blog_url"));

                                }

                                if (object.getString("about_me").isEmpty())
                                {
                                    aboutmeTV.setText("Not Mentioned");
                                }
                                else {
                                    aboutmeTV.setText(object.getString("about_me"));
                                }



                                emailTV.setText(email);
                                cityTV.setText(address);

                            //    aboutmeTV.setText(object.getString("about_me"));


                                String typeArr[] = freelancertype.split(",");
                                for (String ty : typeArr) {
                                    switch (ty) {
                                        case "1":

                                         // freelancertypeTV.setText("Candid Photographer");
                                            freelancertypeList.add("Candid Photographer");
                                            break;
                                        case "2":
                                            freelancertypeList.add("Traditional Photographer");

                                            // freelancertypeTV.setText("Traditional Photographer");
                                            break;
                                        case "3":
//                                            freelancertypeTV.setText("Candid Videographer");
                                            freelancertypeList.add("Candid Videographer");

                                            break;
                                        case "4":
                                          //  freelancertypeTV.setText("Traditional Videographer");
                                            freelancertypeList.add("Traditional Videographer");

                                            break;
                                        case "5":
                                           // freelancertypeTV.setText("Traditional Videographer");
                                            freelancertypeList.add("Traditional Videographer");

                                            break;
                                        case "6":
                                           // freelancertypeTV.setText("Designer");
                                            freelancertypeList.add("Designer");
                                            cameraTV.setText("Not Applicable");
                                            lensTV.setText("Not Applicable");
                                            supportningTV.setText("Not Applicable");
                                            lightningTV.setText("Not Applicable");
                                            break;
                                        case "7":
                                           // freelancertypeTV.setText("Video Editor");
                                            freelancertypeList.add("Video Editor");
                                            cameraTV.setText("Not Applicable");
                                            lensTV.setText("Not Applicable");
                                            supportningTV.setText("Not Applicable");
                                            lightningTV.setText("Not Applicable");
                                            break;

                                        case "8":
                                            freelancertypeList.add("Live Printing");

                                          //  freelancertypeTV.setText("Live Printing");

                                            break;
                                    }
                                }

                                String  freelancerT= freelancertypeList.toString().replace("[", "").replace("]", "");
                               freelancertypeTV.setText(freelancerT);

                                switch(travel_type)
                                {
                                    case "1" : travelTV.setText("Own Car"); break;
                                    case "2" : travelTV.setText("Own Bike"); break;
                                    case "3" : travelTV.setText("Rent Car"); break;
                                    case "4" : travelTV.setText("Taxi"); break;
                                    case "5" : travelTV.setText("Public Transport"); break;
                                }


                                if (camera_name.isEmpty())
                                {
                                    if (freelancertype.equalsIgnoreCase("6") || freelancertype.equalsIgnoreCase("7"))
                                    {cameraTV.setText("Not Applicable");}
                                    else
                                    {cameraTV.setText("Not Mentioned");}
                                }
                                else {
                                    cameraTV.setText(camera_name);
                                }

                                    for (int i=0;i<lence_list.length();i++)
                                    {
                                        String  jsonObject=lence_list.getString(i);
                                        if (lence_list.getString(0).equalsIgnoreCase("empty"))
                                        {
                                            Log.e("freelancertype",freelancertype);
                                            if (freelancertype.equalsIgnoreCase("6") || freelancertype.equalsIgnoreCase("7"))
                                            {lensTV.setText("Not Applicable");}
                                            else
                                            {lensTV.setText("Not Mentioned");}
                                        }
                                        else
                                        {
                                            String lens= jsonObject.replace("[","");
                                            lenslist.add(lens);

                                            String  text = lenslist.toString().replace("[", "").replace("]", "");

                                            lensTV.setText(text);
                                        }



                                    }




                                for (int i=0;i<suport_list.length();i++)
                                {
                                    String  jsonObject=suport_list.getString(i);
                                    if (suport_list.getString(0).equalsIgnoreCase("empty"))
                                    {
                                        if (freelancertype.equalsIgnoreCase("6") || freelancertype.equalsIgnoreCase("7"))
                                        {supportningTV.setText("Not Applicable");}
                                        else
                                        {supportningTV.setText("Not Mentioned");}                                    }
                                    else {
                                        String support = jsonObject.replace("[]", "");
                                        supportninglist.add(support);
                                        String text = supportninglist.toString().replace("[", "").replace("]", "");

                                        supportningTV.setText(text);
                                    }

                                }

                                for (int i=0;i<lighting_list.length();i++)
                                {
                                    String  jsonObject=lighting_list.getString(i);
                                    if (lighting_list.getString(0).equalsIgnoreCase("empty"))
                                    {
                                        if (freelancertype.equalsIgnoreCase("6") || freelancertype.equalsIgnoreCase("7"))
                                        {lightningTV.setText("Not Applicable");}
                                        else
                                        {lightningTV.setText("Not Mentioned");}                                    }
                                    else {
                                        String lightning = jsonObject.replace("[]", "");
                                        lighninglist.add(lightning);
                                        String text = lighninglist.toString().replace("[", "").replace("]", "");

                                        lightningTV.setText(text);
                                    }
                                }





//                                if (from.equalsIgnoreCase("filter")|| from.equalsIgnoreCase("featurehome") || from.equalsIgnoreCase("homepage"))
//                                {
////                                    String mN = Mobile_no;
////                                    String mNS = mN.substring(0, 4);
////                                    String mNS2 = mN.substring(5, 10);
////                                    mNS = mNS.replaceAll(mNS, "xxxxx");
////                                    mobileTV.setText(mNS + mNS2);
////
////                                    String eId = email;
////                                    String eID1 = eId.substring(eId.lastIndexOf("@") + 1);
////                                    int posA = eId.indexOf("@");
////                                    String eID2 = eId.substring(0, posA);
////                                    eID2 = eID2.replaceAll(eID2, "xxxxx");
////                                    emailTV.setText(eID2 + eID1);
//
//                                    emailTV.setText(email);
//                                }
//
//
//
//                                if (from.equalsIgnoreCase("hire"))
//                                {
//                                    //  loadfreelancer();
//                                    if (jobstatus.equalsIgnoreCase("1"))
//                                    {
////                                        String mN = Mobile_no;
////                                        String mNS = mN.substring(0, 4);
////                                        String mNS2 = mN.substring(5, 10);
////                                        mNS = mNS.replaceAll(mNS, "xxxxx");
////                                        mobileTV.setText(mNS + mNS2);
////
////                                        String eId = email;
////                                        String eID1 = eId.substring(eId.lastIndexOf("@") + 1);
////                                        int posA = eId.indexOf("@");
////                                        String eID2 = eId.substring(0, posA);
////                                        eID2 = eID2.replaceAll(eID2, "xxxxx");
////                                        emailTV.setText(eID2 + eID1);
//
//                                        emailTV.setText(email);
//
//                                    }
//                                    else if (jobstatus.equalsIgnoreCase("2"))
//                                    {
//                                        emailTV.setText(email);
//                                    }
//                                    else if (jobstatus.equalsIgnoreCase("3"))
//                                    {
//                                        emailTV.setText(email);
//
//                                    }
//
//
//
//
//                                }
//
//                                if (from.equalsIgnoreCase("profile"))
//                                {
//                                    emailTV.setText(email);
//
//                                }





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
                params.put("freelancer_id", freelancerId);
                params.put("user_type", "2");
                params.put("amount",price);

                //Log.e("params", params.toString());
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
         loadfreelancer();

        super.onStart();
    }




}
