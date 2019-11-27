package com.mobiletemple.photopeople;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.session.SessionManager;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class FilteringActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    TextView location, startdate, enddate;
    Intent intent;
    double lng, lat;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    String freelancertype, locationstring = "", start_date = "", end_date = "";
    PlaceAutocompleteFragment places;
    DatePickerDialog datePickerDialog;
    LinearLayout nextButton, linearlayout;
    private long startTime;
    boolean isConnected;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);
        sessionManager = new SessionManager(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        MobileAds.initialize(this, "ca-app-pub-1234961524965105~5671037383");


        mTitle.setText("Filter");
        filter.setVisibility(View.INVISIBLE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilteringActivity.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "other");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        intent = getIntent();
        freelancertype = intent.getStringExtra("freelancertype");
        location = findViewById(R.id.location);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        requestQueue = Volley.newRequestQueue(FilteringActivity.this);
        nextButton = findViewById(R.id.nextButton);

        linearlayout = findViewById(R.id.linearlayout);
/// App ID: ca-app-pub-1234961524965105~5671037383


        if (freelancertype.equalsIgnoreCase("camerarental")) {
            linearlayout.setVisibility(View.GONE);
        } else {
            linearlayout.setVisibility(View.VISIBLE);

        }

        MobileAds.initialize(this,
                "ca-app-pub-1234961524965105~5671037383");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        places = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isConnected) {
                    showSnack(isConnected);
                } else {
                    ((View) findViewById(R.id.place_autocomplete_search_input)).performClick();
                }
            }
        });

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                locationstring = place.getAddress().toString();
                location.setText(locationstring);
                getLatLong(locationstring);

            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dlg = new DatePickerDialog(FilteringActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        start_date = date + "-" + (month + 1) + "-" + year;

                        startdate.setText(start_date);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
                        startTime = calendar.getTimeInMillis();

                    }
                }, 26, 11, 2017);
                dlg.setTitle("Choose Start Date");
                dlg.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dlg.show();
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dlg = new DatePickerDialog(FilteringActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        end_date = date + "-" + (month + 1) + "-" + year;
                        enddate.setText(end_date);
                    }
                }, 26, 11, 2017);
                dlg.setTitle("Choose End Date");
                if (start_date.isEmpty()) {
                    dlg.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                } else {
                    dlg.getDatePicker().setMinDate(startTime);
                }
                dlg.show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validate = true;



                    if (freelancertype.equalsIgnoreCase("camerarental"))
                    {

                        if (locationstring.isEmpty()) {
                            Toast.makeText(FilteringActivity.this, "Select location", Toast.LENGTH_SHORT).show();
                            validate = false;

                        }
                        if (validate) {
                            Intent intent = new Intent(FilteringActivity.this, CameraRentalListActivity.class);
                            intent.putExtra("lat", String.valueOf(lat));
                            intent.putExtra("lng", String.valueOf(lng));
                            intent.putExtra("from", "home");

                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in,
                                    R.anim.trans_left_out);
                        }
                    } else {


                        if (start_date.isEmpty()) {
                            Toast.makeText(FilteringActivity.this, "Select start date", Toast.LENGTH_SHORT).show();
                            validate = false;
                        }

                        if (end_date.isEmpty()) {
                            Toast.makeText(FilteringActivity.this, "Select end date", Toast.LENGTH_SHORT).show();
                            validate = false;

                        }

                        if (locationstring.isEmpty()) {
                            Toast.makeText(FilteringActivity.this, "Select location", Toast.LENGTH_SHORT).show();
                            validate = false;

                        }
                        if (validate) {
                            Intent intent = new Intent(FilteringActivity.this, FilterActivity.class);
                            sessionManager.setFilter(start_date, end_date, locationstring, String.valueOf(lat), String.valueOf(lng), freelancertype);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in,
                                    R.anim.trans_left_out);
                        }
                    }


            }
        });


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.

            }
        });

    }


    public void getLatLong(final String youraddress) {
        String uri = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyDPmwsBRxE-EfeyHMpKneTCB19nhsaZDmU&address=" + youraddress + "&sensor=true";
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FilteringActivity.this, HomePage.class);
        intent.putExtra("from", "other");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
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
        this.isConnected = isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);


    }
}