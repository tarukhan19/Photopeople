package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.api.Status;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.UIValidation;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateStudioprofileOne extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    SessionManager session;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText studionameET,emailET,startpriceET;
    TextView location,fullname,mobileno;
    LinearLayout nextButton,locationll;
    double lng,lat;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    Intent intent;
    private String userType;
    EditText expNumET;
    boolean isConnected;
    String locationstring,namestring,emailidstring,mobilestring,studionamestring,pricestring,expString;
    List<Place.Field> fields;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_studioprofile_one);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        locationll=findViewById(R.id.locationll);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Personal Profile");
        filter.setVisibility(View.GONE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpdateStudioprofileOne.this,ProfileUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        session = new SessionManager(this);


        dialog = new ProgressDialog(this);
        intent=getIntent();
        fullname=findViewById(R.id.fullname);
        mobileno=findViewById(R.id.mobileno);
        expNumET=findViewById(R.id.expNumET);
        requestQueue= Volley.newRequestQueue(UpdateStudioprofileOne.this);
        studionameET=((EditText)findViewById(R.id.studionameET));
        emailET=((EditText)findViewById(R.id.emailET));
//        addressET=((EditText)findViewById(R.id.addressET));
        startpriceET=((EditText)findViewById(R.id.startpriceET));
        nextButton=findViewById(R.id.nextButton);
        location=findViewById(R.id.location);
        sessionManager = new SessionManager(getApplicationContext());

        namestring=intent.getStringExtra("fname");
        mobilestring=intent.getStringExtra("mobile");
        userType = intent.getStringExtra("userType");

        fullname.setText(namestring);
        mobileno.setText(mobilestring);
        Places.initialize(getApplicationContext(), "AIzaSyATs_vOy7Qths4ErsfalVYNNjWAoeiiS50");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        locationll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(UpdateStudioprofileOne.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


            }
        });




        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidate = true;
                expString=expNumET.getText().toString();

                emailidstring=emailET.getText().toString();
                studionamestring=studionameET.getText().toString();
                pricestring=startpriceET.getText().toString();

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


                String expValidateMSG = UIValidation.numberValidate(expString, true);
                //Log.e("Price Val", priceValidateMSG);
                if (!expValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    expNumET.setError(expValidateMSG);
                    isValidate = false;
                }


                pricestring = startpriceET.getText().toString();
                String priceValidateMSG = UIValidation.numberValidate(pricestring, true);
                //Log.e("Price Val", priceValidateMSG);
                if (!priceValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    startpriceET.setError(priceValidateMSG);
                    isValidate = false;
                }



                if (location.getText().toString().isEmpty())
                {
                    Toast.makeText(UpdateStudioprofileOne.this, "Please select Location", Toast.LENGTH_SHORT).show();
                    isValidate=false;
                }


                if (isValidate)
                {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else {
                        profileTask();
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                locationstring=place.getName();
                location.setText(locationstring);
                getLatLong(locationstring);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
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

                                final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(UpdateStudioprofileOne.this, SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitleText("Profile Updated!");
                                sweetAlertDialog.show();

                                Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(UpdateStudioprofileOne.this,R.color.colorPrimary));

                                sweetAlertDialog.setConfirmText("Ok");
                                btn.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        sweetAlertDialog.dismissWithAnimation();

                                        startActivity(new Intent(UpdateStudioprofileOne.this, ProfileUpdate.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);

                                        finish();
                                    }
                                });
                                sweetAlertDialog.show();




                            }


                            else {
                                Toast.makeText(UpdateStudioprofileOne.this, "Profile Upload Failed; Try Again !", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(UpdateStudioprofileOne.this,ProfileUpdate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }


    class ProfileDataLoad extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(UpdateStudioprofileOne.this);
            pd.setMessage("Loading Profile...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            Endpoints comm = new Endpoints();
            try {

                JSONObject ob = new JSONObject();
                String url = "";



                    ob.put("studio_id",session.getLoginSession().get(SessionManager.KEY_USERID));
                    url = comm.LOAD_STUDIO_PROFILE;


                //Log.e("Profile para",ob.toString());
                String result = comm.getStringResponse(url,ob);
                Log.e("LOAD_STUDIO_PROFILE",result);
                return result;
            }catch(Exception ex) {
                //Log.e("GEO ERR",ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.cancel();
            //Log.e("updateprofile",s);
            try {
                if (s != null)
                {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if(status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONObject data = obj.getJSONObject("data");
                        fullname.setText(data.getString("first_name"));
                        mobileno.setText(data.getString("phone"));
                        location.setText(data.getString("location"));
                        emailET.setText(data.getString("email"));
                        startpriceET.setText(data.getString("starting_price"));
                        locationstring=data.getString("location");
                        lat=data.getDouble("latitude");
                        lng=data.getDouble("longitude");
                        expString=data.getString("experiance");


                        studionameET.setText(data.getString("studio_name"));
                        expNumET.setText(expString);
                    }
                }
            }
            catch(Exception ex){}
            super.onPostExecute(s);
        }
    }




    @Override
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
        {
            ProfileDataLoad task = new ProfileDataLoad();
            task.execute();
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
            ProfileDataLoad task = new ProfileDataLoad();
            task.execute();
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
