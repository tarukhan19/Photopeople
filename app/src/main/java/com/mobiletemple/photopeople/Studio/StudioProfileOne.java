package com.mobiletemple.photopeople.Studio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.mobiletemple.photopeople.Freelancer.FreelancerProfileOne;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.ProfileExperienceActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.UIValidation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class StudioProfileOne extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    PlaceAutocompleteFragment places;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText studionameET,emailET,startpriceET,expNumET;
    TextView location,fullname,mobileno;
    LinearLayout nextButton,locationll;
    double lng,lat;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    Intent intent;
    private String userType;
    boolean isConnected;
    String locationstring,namestring,emailidstring,mobilestring,studionamestring,pricestring,expString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_profile_one);
        dialog = new ProgressDialog(this);
        intent=getIntent();
        fullname=findViewById(R.id.fullname);
        mobileno=findViewById(R.id.mobileno);
        requestQueue= Volley.newRequestQueue(StudioProfileOne.this);
        studionameET=((EditText)findViewById(R.id.studionameET));
        emailET=((EditText)findViewById(R.id.emailET));
        expNumET=findViewById(R.id.expNumET);
        startpriceET=((EditText)findViewById(R.id.startpriceET));
        nextButton=findViewById(R.id.nextButton);
        location=findViewById(R.id.location);
        locationll=findViewById(R.id.locationll);
        sessionManager = new SessionManager(getApplicationContext());
        places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete);
        ((View)findViewById(R.id.place_autocomplete_search_button)).setVisibility(View.GONE);
        ((View)findViewById(R.id.place_autocomplete_clear_button)).setVisibility(View.GONE);
        namestring=intent.getStringExtra("fname");
        mobilestring=intent.getStringExtra("mobile");
        userType = intent.getStringExtra("userType");

        fullname.setText(namestring);
        mobileno.setText(mobilestring);

        locationll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1=  ((View)findViewById(R.id.place_autocomplete_search_input));
                ((View)findViewById(R.id.place_autocomplete_search_input)).performClick();

               }
        });

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                locationstring = place.getAddress().toString();
                location.setText(locationstring);
                //Log.e("locationstring",locationstring);
                getLatLong(locationstring);

            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });


nextButton.setOnClickListener(new View.OnClickListener()
{
    @Override
    public void onClick(View view) {
        boolean isValidate = true;

        emailidstring=emailET.getText().toString();
        studionamestring=studionameET.getText().toString();
        expString=expNumET.getText().toString();

        String studioNameValidateMSG = UIValidation.nameValidate(studionamestring, true);
        //Log.e("Studio Name Val", studioNameValidateMSG);
        if (!studioNameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            studionameET.setError(studioNameValidateMSG);
            isValidate = false;
        } else {
            String snames[] = studionamestring.split(" ");
            String finalSName = "";
            for (String nm : snames) {
                String first = nm.substring(0, 1);
                String rem = nm.substring(1, nm.length());

                finalSName += first.toUpperCase() + rem.toLowerCase() + " ";
            }
            studionamestring = finalSName.trim();
        }


//        String emailValidateMSG = UIValidation.emailValidate(emailidstring, true);
//        //Log.e("Email Val", emailValidateMSG);
//        if (!emailValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
//            emailET.setError(emailValidateMSG);
//            isValidate = false;
//        }

        pricestring = startpriceET.getText().toString();
        String priceValidateMSG = UIValidation.numberValidate(pricestring, true);
        //Log.e("Price Val", priceValidateMSG);
        if (!priceValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            startpriceET.setError(priceValidateMSG);
            isValidate = false;
        }

        String expValidateMSG = UIValidation.numberValidate(expString, true);
        //Log.e("Price Val", priceValidateMSG);
        if (!expValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            expNumET.setError(expValidateMSG);
            isValidate = false;
        }

        if (location.getText().toString().isEmpty())
        {
            Toast.makeText(StudioProfileOne.this, "Please select Location", Toast.LENGTH_SHORT).show();
            isValidate=false;
        }


        if (isValidate)
        {
            if (!isConnected)
            {
                showSnack(isConnected);
            }
            else
            {
                profileTask();
            }

        }
    }
});
    }



    public void getLatLong(final String youraddress)
    {
 String uri = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyDPmwsBRxE-EfeyHMpKneTCB19nhsaZDmU&address=" +youraddress + "&sensor=true";
        uri = uri.replaceAll(" ","%20");

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("response", response);

                        try {
                            //Log.e("response",response);
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");

                            //Log.e("latitude",""+ lat);
                            //Log.e("longitude", ""+lng);
                        } catch (JSONException e) {
                            e.printStackTrace();
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

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    private void profileTask()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.STUDIO_PERSONAL_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);

                        try
                        {

                                JSONObject obj = new JSONObject(response);
                                int status = obj.getInt("status");
                                String message = obj.getString("message");

                                if (status == 200 && message.equals("success"))
                                {

//                                            Intent in = new Intent(StudioProfileOne.this, ProfileExperienceActivity.class);
//                                            in.putExtra("userType", Constants.STUDIO_TYPE);
//                                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                                            startActivity(in);
//
//                                    overridePendingTransition(R.anim.trans_left_in,
//                                            R.anim.trans_left_out);


                                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(StudioProfileOne.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sweetAlertDialog.setTitleText("Successfully Registered !")
                                            .setConfirmText("OK");
                                    sweetAlertDialog.show();


                                    Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                    btn.setBackgroundColor(ContextCompat.getColor(StudioProfileOne.this, R.color.colorPrimary));

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            if (userType.equalsIgnoreCase("1")) {
                                                Intent in = new Intent(StudioProfileOne.this, LoginActivity.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                in.putExtra("userType", userType);

                                                startActivity(in);
                                                overridePendingTransition(R.anim.trans_left_in,
                                                        R.anim.trans_left_out);
                                            }
                                            if (userType.equalsIgnoreCase("2")) {
                                                Intent in = new Intent(StudioProfileOne.this, LoginActivity.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                in.putExtra("userType", userType);

                                                startActivity(in);
                                                overridePendingTransition(R.anim.trans_left_in,
                                                        R.anim.trans_left_out);
                                            }
                                        }
                                    });
                                }


                                else {
                                    Toast.makeText(StudioProfileOne.this, "Profile Upload Failed; Try Again !", Toast.LENGTH_SHORT).show();
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
                ob.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                ob.put("user_type","1");
                ob.put("studio_name", studionamestring);
                ob.put("email", emailidstring);
                ob.put("start_price", pricestring);
                ob.put("work_area_km", "0");
                ob.put("location", locationstring);
                ob.put("latitude", String.valueOf(lat));
                ob.put("longitude", String.valueOf(lng));
                ob.put("experience",expString);

                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);    }





    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }


        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }

}
