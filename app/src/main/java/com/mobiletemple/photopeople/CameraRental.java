package com.mobiletemple.photopeople;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CameraRental extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_rental);
        progressDialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);

        PlaceAutocompleteFragment places = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete);

        ImageView cross = (ImageView) findViewById(R.id.cross);
        final LinearLayout submit = (LinearLayout) findViewById(R.id.submit);
        final EditText fullname = findViewById(R.id.fullname);
        final EditText mobileno = findViewById(R.id.mobileno);
        LinearLayout location = findViewById(R.id.location);
        final TextView locationTV = findViewById(R.id.locationTV);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ((View) findViewById(R.id.place_autocomplete_search_input)).performClick();

            }
        });

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                String locate = place.getAddress().toString();
                locationTV.setText(locate);
                getLatLong(locate);

            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullnameS = fullname.getText().toString();
                final String mobileNoS = mobileno.getText().toString();
                final String locationS = locationTV.getText().toString();

                if (fullnameS.isEmpty())
                {
                    Toast.makeText(CameraRental.this, "Enter Full Name", Toast.LENGTH_SHORT).show();
                }
                else if (mobileNoS.isEmpty() || mobileNoS.length()<8 || mobileNoS.length()>15)
                {
                    Toast.makeText(CameraRental.this, "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();

                }
                else if (locationS.isEmpty())
                {
                    Toast.makeText(CameraRental.this, "Enter location", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    submitCameraRentalInfo(fullnameS,mobileNoS,locationS);
                }


            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    private void submitCameraRentalInfo(final String fullnameS, final String mobileNoS, final String locationS)
    {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CAMERA_RENTALS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200)
                            {
                                final Dialog dialog = new Dialog(CameraRental.this, R.style.CustomDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_camerarentalsuccess);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                LinearLayout submit=dialog.findViewById(R.id.submit);
                                ImageView cross=dialog.findViewById(R.id.cross);
                                dialog.show();

                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                                cross.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                            } else
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("name",fullnameS );
                ob.put("mobile",mobileNoS);
                ob.put("location", locationS);
                ob.put("latitude", lat+"");
                ob.put("longitude", lng+"");

                Log.e("params",ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }



    private void getLatLong(String locate)
    {
        String uri = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyDPmwsBRxE-EfeyHMpKneTCB19nhsaZDmU&address=" +
                locate + "&sensor=true";
        uri = uri.replaceAll(" ", "%20");

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);


                        try {
                            //Log.e("response", response);
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
//                            Log.e("place", "" + youraddress);
//
//                            Log.e("latitude", "" + lat);
//                            Log.e("longitude", "" + lng);
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


}