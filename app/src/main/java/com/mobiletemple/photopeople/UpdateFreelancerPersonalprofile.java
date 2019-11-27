package com.mobiletemple.photopeople;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateFreelancerPersonalprofile extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    private static final int AUTOCOMPLETE_REQUEST_CODE = 0;
    private String userType;
    Intent intent;
    Integer ageInt = 0;
    EditText emailET, priceET;
    TextView locationTV, fullnameTV, mobilenoTV, dobTV;
    LinearLayout malelayout, femalelayout, ph_ll, ph_can_ll, ph_trad_ll, vid_ll, vid_can_ll, vid_trad_ll, vid_heli_ll, des_ll, videdit_ll,
            liveprint_ll, owncar_ll, ownbike_ll, rentcar_ll, taxi_ll, publictransport_ll, nextButton, pricell;
    ImageView maleactiveradio, maleinactiveradio, femaleactiveradio, femaleinactiveradio, ph_active, ph_inactive, ph_can_active, ph_can_inactive, ph_trad_active, ph_trad_inactive, vid_active, vid_inactive, vid_can_active, vid_can_inactive, vid_trad_active, vid_trad_inactive, vid_heli_active, vid_heli_inactive, des_active, des_inactive, videdi_active, videdi_inactive, liveprint_active, liveprint_inactive,
            owncar_active, owncar_inactive, ownbike_active, ownbike_inactive, rentcar_active, rentcar_inactive, taxi_active, taxi_inactive, publictransport_active, publictransport_inactive;
    double lng, lat;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    DatePickerDialog datePickerDialog;
    String expString, namestring,genderString, mobilestring, locationstring, dobString="",travelBy, ageS = "0", emailidstring,  pricestring, cameranamestring, freelancerType,lensidstring;
    RequestQueue queue;
    ProgressDialog dialog;
    boolean isConnected;
    String ph_clickable = "0",freelancertypearaylist;
    String vid_clickable = "0";
    EditText expNumET;
    List<Place.Field> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_freelancer_personalprofile);

        queue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(getApplicationContext());
        expNumET=findViewById(R.id.expNumET);

        emailET = findViewById(R.id.emailET);
        priceET = findViewById(R.id.price);
        locationTV = findViewById(R.id.location);
        fullnameTV = findViewById(R.id.fullname);
        mobilenoTV = findViewById(R.id.mobileno);
        dobTV = findViewById(R.id.dob);
        dialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(UpdateFreelancerPersonalprofile.this);
        malelayout = findViewById(R.id.malelayout);
        femalelayout = findViewById(R.id.femalelayout);
        ph_ll = findViewById(R.id.ph_ll);
        ph_can_ll = findViewById(R.id.ph_can_ll);
        ph_trad_ll = findViewById(R.id.ph_trad_ll);
        vid_ll = findViewById(R.id.vid_ll);
        vid_can_ll = findViewById(R.id.vid_can_ll);
        vid_trad_ll = findViewById(R.id.vid_trad_ll);
        vid_heli_ll = findViewById(R.id.vid_heli_ll);
        des_ll = findViewById(R.id.des_ll);
        videdit_ll = findViewById(R.id.videdit_ll);
        liveprint_ll = findViewById(R.id.liveprint_ll);
        owncar_ll = findViewById(R.id.owncar_ll);
        ownbike_ll = findViewById(R.id.ownbike_ll);
        rentcar_ll = findViewById(R.id.rentcar_ll);
        taxi_ll = findViewById(R.id.taxi_ll);
        publictransport_ll = findViewById(R.id.publictransport_ll);
        nextButton = findViewById(R.id.nextButton);
        maleactiveradio = findViewById(R.id.maleactiveradio);
        maleinactiveradio = findViewById(R.id.maleinactiveradio);
        femaleactiveradio = findViewById(R.id.femaleactiveradio);
        femaleinactiveradio = findViewById(R.id.femaleinactiveradio);
        ph_active = findViewById(R.id.ph_active);
        ph_inactive = findViewById(R.id.ph_inactive);
        ph_can_active = findViewById(R.id.ph_can_active);
        ph_can_inactive = findViewById(R.id.ph_can_inactive);
        ph_trad_active = findViewById(R.id.ph_trad_active);
        ph_trad_inactive = findViewById(R.id.ph_trad_inactive);
        vid_active = findViewById(R.id.vid_active);
        vid_inactive = findViewById(R.id.vid_inactive);
        vid_can_active = findViewById(R.id.vid_can_active);
        vid_can_inactive = findViewById(R.id.vid_can_inactive);
        vid_trad_active = findViewById(R.id.vid_trad_active);
        vid_trad_inactive = findViewById(R.id.vid_trad_inactive);
        vid_heli_active = findViewById(R.id.vid_heli_active);
        vid_heli_inactive = findViewById(R.id.vid_heli_inactive);
        des_active = findViewById(R.id.des_active);
        des_inactive = findViewById(R.id.des_inactive);
        videdi_active = findViewById(R.id.videdi_active);
        videdi_inactive = findViewById(R.id.videdi_inactive);
        liveprint_active = findViewById(R.id.liveprint_active);
        liveprint_inactive = findViewById(R.id.liveprint_inactive);
        owncar_active = findViewById(R.id.owncar_active);
        owncar_inactive = findViewById(R.id.owncar_inactive);
        ownbike_active = findViewById(R.id.ownbike_active);
        ownbike_inactive = findViewById(R.id.ownbike_inactive);
        rentcar_active = findViewById(R.id.rentcar_active);
        rentcar_inactive = findViewById(R.id.rentcar_inactive);
        taxi_active = findViewById(R.id.taxi_active);
        taxi_inactive = findViewById(R.id.taxi_inactive);
        publictransport_active = findViewById(R.id.publictransport_active);
        publictransport_inactive = findViewById(R.id.publictransport_inactive);
        pricell = findViewById(R.id.pricell);


        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyATs_vOy7Qths4ErsfalVYNNjWAoeiiS50");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
//        autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete);

        // Specify the types of place data to return.
       // autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        locationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(UpdateFreelancerPersonalprofile.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                locationTV.setText(place.getName()+","+place.getId());
//                locationstring=place.getName()+","+place.getId();
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//            }
//        });
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        intent = getIntent();


        mTitle.setText("Update Personal Profile");
        filter.setVisibility(View.INVISIBLE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpdateFreelancerPersonalprofile.this,ProfileUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

//        locationTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                ((View) findViewById(R.id.place)).performClick();
//            }
//        });
//


        dobTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(dobTV);

            }
        });
        fullnameTV.setText(namestring);
        mobilenoTV.setText(mobilestring);

        malelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (maleactiveradio.getVisibility() == View.VISIBLE) {
                    maleinactiveradio.setVisibility(View.VISIBLE);
                    maleactiveradio.setVisibility(View.GONE);
                } else if (maleinactiveradio.getVisibility() == View.VISIBLE) {
                    maleactiveradio.setVisibility(View.VISIBLE);
                    maleinactiveradio.setVisibility(View.GONE);
                    femaleactiveradio.setVisibility(View.GONE);
                    femaleinactiveradio.setVisibility(View.VISIBLE);
                }

            }
        });

        femalelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (femaleactiveradio.getVisibility() == View.VISIBLE) {
                    femaleinactiveradio.setVisibility(View.VISIBLE);
                    femaleactiveradio.setVisibility(View.GONE);
                } else if (femaleinactiveradio.getVisibility() == View.VISIBLE) {
                    femaleactiveradio.setVisibility(View.VISIBLE);
                    femaleinactiveradio.setVisibility(View.GONE);
                    maleactiveradio.setVisibility(View.GONE);
                    maleinactiveradio.setVisibility(View.VISIBLE);
                }
            }
        });

        ph_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceET.setHint("Price");

                ph_active.setVisibility(View.VISIBLE);
                ph_inactive.setVisibility(View.GONE);
                vid_active.setVisibility(View.GONE);
                vid_inactive.setVisibility(View.VISIBLE);

                des_active.setVisibility(View.GONE);
                des_inactive.setVisibility(View.VISIBLE);
                videdi_active.setVisibility(View.GONE);
                videdi_inactive.setVisibility(View.VISIBLE);
                liveprint_active.setVisibility(View.GONE);
                liveprint_inactive.setVisibility(View.VISIBLE);

                ph_clickable = "1";
                vid_clickable = "0";
                vid_heli_active.setVisibility(View.GONE);
                vid_trad_active.setVisibility(View.GONE);
                vid_can_active.setVisibility(View.GONE);

                vid_heli_inactive.setVisibility(View.VISIBLE);
                vid_trad_inactive.setVisibility(View.VISIBLE);
                vid_can_inactive.setVisibility(View.VISIBLE);



            }
        });

        ph_can_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ph_clickable.equalsIgnoreCase("1")) {
                    if (ph_can_active.getVisibility() == View.VISIBLE) {
                        ph_can_inactive.setVisibility(View.VISIBLE);
                        ph_can_active.setVisibility(View.GONE);
                    } else if (ph_can_inactive.getVisibility() == View.VISIBLE) {
                        ph_can_inactive.setVisibility(View.GONE);
                        ph_can_active.setVisibility(View.VISIBLE);
                    }
                } else {
                }

            }
        });

        ph_trad_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ph_clickable.equalsIgnoreCase("1")) {
                    if (ph_trad_active.getVisibility() == View.VISIBLE) {
                        ph_trad_inactive.setVisibility(View.VISIBLE);
                        ph_trad_active.setVisibility(View.GONE);
                    } else if (ph_trad_inactive.getVisibility() == View.VISIBLE) {
                        ph_trad_inactive.setVisibility(View.GONE);
                        ph_trad_active.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        vid_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceET.setHint("Price");

                vid_active.setVisibility(View.VISIBLE);
                vid_inactive.setVisibility(View.GONE);
                ph_active.setVisibility(View.GONE);
                ph_inactive.setVisibility(View.VISIBLE);

                des_active.setVisibility(View.GONE);
                des_inactive.setVisibility(View.VISIBLE);
                videdi_active.setVisibility(View.GONE);
                videdi_inactive.setVisibility(View.VISIBLE);
                liveprint_active.setVisibility(View.GONE);
                liveprint_inactive.setVisibility(View.VISIBLE);
                ph_clickable = "0";
                vid_clickable = "1";

                ph_can_active.setVisibility(View.GONE);
                ph_trad_active.setVisibility(View.GONE);
                ph_can_inactive.setVisibility(View.VISIBLE);
                ph_trad_inactive.setVisibility(View.VISIBLE);



            }
        });


        vid_can_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vid_clickable.equalsIgnoreCase("1")) {
                    if (vid_can_active.getVisibility() == View.VISIBLE) {
                        vid_can_inactive.setVisibility(View.VISIBLE);
                        vid_can_active.setVisibility(View.GONE);
                    } else if (vid_can_inactive.getVisibility() == View.VISIBLE) {
                        vid_can_inactive.setVisibility(View.GONE);
                        vid_can_active.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        vid_trad_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vid_clickable.equalsIgnoreCase("1")) {
                    if (vid_trad_active.getVisibility() == View.VISIBLE) {
                        vid_trad_inactive.setVisibility(View.VISIBLE);
                        vid_trad_active.setVisibility(View.GONE);
                    } else if (vid_trad_inactive.getVisibility() == View.VISIBLE) {
                        vid_trad_inactive.setVisibility(View.GONE);
                        vid_trad_active.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        vid_heli_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vid_clickable.equalsIgnoreCase("1")) {
                    if (vid_heli_active.getVisibility() == View.VISIBLE) {
                        vid_heli_active.setVisibility(View.GONE);
                        vid_heli_inactive.setVisibility(View.VISIBLE);
                    } else if (vid_heli_inactive.getVisibility() == View.VISIBLE) {
                        vid_heli_inactive.setVisibility(View.GONE);
                        vid_heli_active.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        des_ll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                priceET.setHint("Price");

                des_active.setVisibility(View.VISIBLE);
                des_inactive.setVisibility(View.GONE);
                ph_active.setVisibility(View.GONE);
                ph_inactive.setVisibility(View.VISIBLE);

                vid_active.setVisibility(View.GONE);
                vid_inactive.setVisibility(View.VISIBLE);
                videdi_active.setVisibility(View.GONE);
                videdi_inactive.setVisibility(View.VISIBLE);
                liveprint_active.setVisibility(View.GONE);
                liveprint_inactive.setVisibility(View.VISIBLE);

                ph_clickable = "0";
                vid_clickable = "0";

                ph_can_active.setVisibility(View.GONE);
                ph_trad_active.setVisibility(View.GONE);
                vid_heli_active.setVisibility(View.GONE);
                vid_trad_active.setVisibility(View.GONE);
                vid_can_active.setVisibility(View.GONE);
                ph_can_inactive.setVisibility(View.VISIBLE);
                ph_trad_inactive.setVisibility(View.VISIBLE);

                vid_heli_inactive.setVisibility(View.VISIBLE);
                vid_trad_inactive.setVisibility(View.VISIBLE);
                vid_can_inactive.setVisibility(View.VISIBLE);



            }
        });

        videdit_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceET.setHint("Price");

                vid_active.setVisibility(View.GONE);
                vid_inactive.setVisibility(View.VISIBLE);
                ph_active.setVisibility(View.GONE);
                ph_inactive.setVisibility(View.VISIBLE);

                des_active.setVisibility(View.GONE);
                des_inactive.setVisibility(View.VISIBLE);
                videdi_active.setVisibility(View.VISIBLE);
                videdi_inactive.setVisibility(View.GONE);
                liveprint_active.setVisibility(View.GONE);
                liveprint_inactive.setVisibility(View.VISIBLE);

                ph_clickable = "0";
                vid_clickable = "0";

                ph_can_active.setVisibility(View.GONE);
                ph_trad_active.setVisibility(View.GONE);
                vid_heli_active.setVisibility(View.GONE);
                vid_trad_active.setVisibility(View.GONE);
                vid_can_active.setVisibility(View.GONE);
                ph_can_inactive.setVisibility(View.VISIBLE);
                ph_trad_inactive.setVisibility(View.VISIBLE);

                vid_heli_inactive.setVisibility(View.VISIBLE);
                vid_trad_inactive.setVisibility(View.VISIBLE);
                vid_can_inactive.setVisibility(View.VISIBLE);



            }
        });


        liveprint_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceET.setHint("Rate/Sheet");
                vid_active.setVisibility(View.GONE);
                vid_inactive.setVisibility(View.VISIBLE);
                ph_active.setVisibility(View.GONE);
                ph_inactive.setVisibility(View.VISIBLE);

                des_active.setVisibility(View.GONE);
                des_inactive.setVisibility(View.VISIBLE);
                videdi_active.setVisibility(View.GONE);
                videdi_inactive.setVisibility(View.VISIBLE);
                liveprint_active.setVisibility(View.VISIBLE);
                liveprint_inactive.setVisibility(View.GONE);
                ph_clickable = "0";
                vid_clickable = "0";


                ph_can_active.setVisibility(View.GONE);
                ph_trad_active.setVisibility(View.GONE);
                vid_heli_active.setVisibility(View.GONE);
                vid_trad_active.setVisibility(View.GONE);
                vid_can_active.setVisibility(View.GONE);
                ph_can_inactive.setVisibility(View.VISIBLE);
                ph_trad_inactive.setVisibility(View.VISIBLE);

                vid_heli_inactive.setVisibility(View.VISIBLE);
                vid_trad_inactive.setVisibility(View.VISIBLE);
                vid_can_inactive.setVisibility(View.VISIBLE);

            }
        });
        owncar_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (owncar_inactive.getVisibility() == View.VISIBLE) {
                    owncar_inactive.setVisibility(View.GONE);
                    owncar_active.setVisibility(View.VISIBLE);
                    rentcar_active.setVisibility(View.GONE);
                    rentcar_inactive.setVisibility(View.VISIBLE);
                    ownbike_active.setVisibility(View.GONE);
                    ownbike_inactive.setVisibility(View.VISIBLE);
                    taxi_active.setVisibility(View.GONE);
                    taxi_inactive.setVisibility(View.VISIBLE);
                    publictransport_active.setVisibility(View.GONE);
                    publictransport_inactive.setVisibility(View.VISIBLE);

                }
            }
        });


        ownbike_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ownbike_inactive.getVisibility() == View.VISIBLE) {
                    ownbike_inactive.setVisibility(View.GONE);
                    ownbike_active.setVisibility(View.VISIBLE);
                    rentcar_active.setVisibility(View.GONE);
                    rentcar_inactive.setVisibility(View.VISIBLE);
                    owncar_active.setVisibility(View.GONE);
                    owncar_inactive.setVisibility(View.VISIBLE);
                    taxi_active.setVisibility(View.GONE);
                    taxi_inactive.setVisibility(View.VISIBLE);
                    publictransport_active.setVisibility(View.GONE);
                    publictransport_inactive.setVisibility(View.VISIBLE);
                }
            }
        });

        rentcar_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rentcar_inactive.getVisibility() == View.VISIBLE) {
                    rentcar_inactive.setVisibility(View.GONE);
                    rentcar_active.setVisibility(View.VISIBLE);

                    owncar_active.setVisibility(View.GONE);
                    owncar_inactive.setVisibility(View.VISIBLE);
                    ownbike_active.setVisibility(View.GONE);
                    ownbike_inactive.setVisibility(View.VISIBLE);
                    taxi_active.setVisibility(View.GONE);
                    taxi_inactive.setVisibility(View.VISIBLE);
                    publictransport_active.setVisibility(View.GONE);
                    publictransport_inactive.setVisibility(View.VISIBLE);
                }
            }
        });

        taxi_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taxi_inactive.getVisibility() == View.VISIBLE) {
                    taxi_inactive.setVisibility(View.GONE);
                    taxi_active.setVisibility(View.VISIBLE);
                    rentcar_active.setVisibility(View.GONE);
                    rentcar_inactive.setVisibility(View.VISIBLE);
                    ownbike_active.setVisibility(View.GONE);
                    ownbike_inactive.setVisibility(View.VISIBLE);
                    owncar_active.setVisibility(View.GONE);
                    owncar_inactive.setVisibility(View.VISIBLE);
                    publictransport_active.setVisibility(View.GONE);
                    publictransport_inactive.setVisibility(View.VISIBLE);
                }
            }
        });

        publictransport_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (publictransport_inactive.getVisibility() == View.VISIBLE) {
                    publictransport_inactive.setVisibility(View.GONE);
                    publictransport_active.setVisibility(View.VISIBLE);
                    rentcar_active.setVisibility(View.GONE);
                    rentcar_inactive.setVisibility(View.VISIBLE);
                    ownbike_active.setVisibility(View.GONE);
                    ownbike_inactive.setVisibility(View.VISIBLE);
                    taxi_active.setVisibility(View.GONE);
                    taxi_inactive.setVisibility(View.VISIBLE);
                    owncar_active.setVisibility(View.GONE);
                    owncar_inactive.setVisibility(View.VISIBLE);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {




                emailidstring = emailET.getText().toString();
                pricestring = priceET.getText().toString();
                dobString=dobTV.getText().toString();
                expString=expNumET.getText().toString();

                //Log.e("dobString",dobString);

                if (maleactiveradio.getVisibility() == View.VISIBLE) {
                    genderString = "Male";
                } else {
                    genderString = "Female";
                }

                if (owncar_active.getVisibility() == View.VISIBLE) {
                    travelBy = Constants.OWN_CAR;
                } else if (ownbike_active.getVisibility() == View.VISIBLE) {
                    travelBy = Constants.OWN_BIKE;
                } else if (rentcar_active.getVisibility() == View.VISIBLE) {
                    travelBy = Constants.RENT_CAR;
                } else if (taxi_active.getVisibility() == View.VISIBLE) {
                    travelBy = Constants.TAXI;
                } else if (publictransport_active.getVisibility() == View.VISIBLE) {
                    travelBy = Constants.PUBLIC_TRANSPORT;
                }


                List<Integer> str =new ArrayList<>();
                if (ph_can_active.getVisibility() == View.VISIBLE) {

                    str.add(Constants.CANDID_PHOTOGRAPHER);
                    freelancerType="";
                    // str += Constants.CANDID_PHOTOGRAPHER + ",";
                }
                if (ph_trad_active.getVisibility() == View.VISIBLE) {
                    // str += Constants.TRADITIONAL_PHOTOGRAPHER + ",";
                    str.add(Constants.TRADITIONAL_PHOTOGRAPHER);
                    freelancerType="";

                }
                if (vid_trad_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.TRADITIONAL_VIDEOGRAPHER);
                    freelancerType="";

                    //str += Constants.TRADITIONAL_VIDEOGRAPHER + ",";

                }
                if (vid_can_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.CANDID_VIDEOGRAPHER);
                    freelancerType="";

                    //str += Constants.CANDID_VIDEOGRAPHER + ",";
                }
                if (vid_heli_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.HELICAM);
                    freelancerType="";

                }
                if (liveprint_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.LIVE_PRINT);
                    freelancerType="";

                }
                if (des_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.DESIGNER);
                    freelancerType="DESIGNER";

                }
                if (videdi_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.VIDEO_EDITOR);
                    freelancerType="VIDEO_EDITOR";

                }
                ///String csAppendedCode = phoneNumber.length() > csCountryCode.length() ? phoneNumber.substring(0, csCountryCode.length()) : "";



                StringBuilder sb = new StringBuilder();
                for (Integer item : str) {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(item);
                }
                freelancertypearaylist = sb.toString();



                String priceValidateMSG = UIValidation.numberValidate(pricestring, true);
                boolean isvalidate = true;

              if (owncar_active.getVisibility() == View.GONE && ownbike_active.getVisibility() == View.GONE && rentcar_active.getVisibility() == View.GONE
                        && taxi_active.getVisibility() == View.GONE && publictransport_active.getVisibility() == View.GONE) {
                    Toast.makeText(UpdateFreelancerPersonalprofile.this, "Please select travel type", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }  if (maleactiveradio.getVisibility() == View.GONE && femaleactiveradio.getVisibility() == View.GONE) {
                    Toast.makeText(UpdateFreelancerPersonalprofile.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
               if (dobString.isEmpty()) {
                    Toast.makeText(UpdateFreelancerPersonalprofile.this, "Select Date Of Birth", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (!priceValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    priceET.setError(priceValidateMSG);
                    isvalidate = false;
                }  if (ph_active.getVisibility() == View.GONE && vid_active.getVisibility() == View.GONE && des_active.getVisibility() == View.GONE
                        && videdi_active.getVisibility() == View.GONE && liveprint_active.getVisibility() == View.GONE)

                {
                    Toast.makeText(UpdateFreelancerPersonalprofile.this, "Please Choose type", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }  if (locationTV.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateFreelancerPersonalprofile.this, "Please select Location", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }  if (ph_active.getVisibility() == View.VISIBLE) {
                    if (ph_can_active.getVisibility() == View.GONE && ph_trad_active.getVisibility() == View.GONE) {
                        Toast.makeText(UpdateFreelancerPersonalprofile.this, "Please select subtype", Toast.LENGTH_SHORT).show();
                        isvalidate = false;

                    }
                }  if (vid_active.getVisibility() == View.VISIBLE) {
                    if (vid_can_active.getVisibility() == View.GONE && vid_trad_active.getVisibility() == View.GONE && vid_heli_active.getVisibility() == View.GONE) {
                        Toast.makeText(UpdateFreelancerPersonalprofile.this, "Please select subtype", Toast.LENGTH_SHORT).show();
                        isvalidate = false;

                    }
                }


                if(isvalidate)
                {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else
                    {
                        getLatLong(locationstring);
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
                locationTV.setText(locationstring);
                getLatLong(locationstring);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    // Profile Upload
    class ProfileTask extends AsyncTask<String, Void, String>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(UpdateFreelancerPersonalprofile.this);
            pd.setMessage("Profile Uploading ...");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {


                JSONObject ob = new JSONObject();
                ob.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                ob.put("address",locationstring);
                ob.put("email",emailidstring);
                ob.put("start_price",pricestring);
                ob.put("travel_by",travelBy);
                ob.put("gendar",genderString);
                ob.put("dob",dobString);
                ob.put("equipments","a");
                ob.put("freelancer_type",freelancertypearaylist);
                ob.put("latitude", String.valueOf(lat));
                ob.put("longitude", String.valueOf(lng));
                ob.put("experience",expString);


                //Log.e("JSON", ob.toString());

                // String result = comm.getStringResponse(Endpoints.SIGNUP_URL,ob);
                String result = comm.forFreelancerPersonal(Endpoints.UPDATE_FREELANCER_PERSONAL_PROFILE, ob);


                //Log.e("Registration response", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s)
        {
            //Log.e("Upload Response ", s);
            pd.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                  //  {"status":200,"message":"success","user_id":"1142"}
                    if (status == 200 && message.equals("success")) {


                        sessionManager.setLoginSession(sessionManager.getLoginSession().get(SessionManager.KEY_EMAIL),
                                sessionManager.getLoginSession().get(SessionManager.KEY_USERID),
                                sessionManager.getLoginSession().get(SessionManager.KEY_NAME),
                                sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE),
                                sessionManager.getLoginSession().get(SessionManager.KEY_MOBILE),
                                sessionManager.getLoginSession().get(SessionManager.KEY_IMAGE),
                                freelancerType,
                                sessionManager.getLoginSession().get(SessionManager.KEY_IMAGECOUNT));

                        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(UpdateFreelancerPersonalprofile.this, SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Profile Updated!");
                        sweetAlertDialog.show();

                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(UpdateFreelancerPersonalprofile.this,R.color.colorPrimary));

                        sweetAlertDialog.setConfirmText("Ok");
                        btn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();

                                Intent in = new Intent(UpdateFreelancerPersonalprofile.this, ProfileUpdate.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(in);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);



                            }
                        });
                        sweetAlertDialog.show();





                    }


                    else {
                        Toast.makeText(UpdateFreelancerPersonalprofile.this, "Profile Upload Failed; Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
            }
        }
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
                        //Log.e("PERSONAL_DETAIL", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("Message");

                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                JSONObject object = obj.getJSONObject("Details");
//                                quoteId=object.getString("quoteId");
                                namestring=object.getString("first_name") + object.getString("last_name");
                                emailidstring=object.getString("email");
                                mobilestring=object.getString("Mobile_no");
                                dobString=object.getString("dob");
                                locationstring=object.getString("address");
                                pricestring=object.getString("starting_price");
                                cameranamestring=object.getString("camera_name");
                                travelBy=object.getString("travel_type");
                                genderString=object.getString("gender");
                                freelancertypearaylist=object.getString("feelancer_type");
                                expString=object.getString("exp_year");



                                String typeArr[] = freelancertypearaylist.split(",");
                                for (String ty : typeArr) {
                                    switch (ty) {
                                        case "1":
                                            ph_active.setVisibility(View.VISIBLE);
                                            ph_inactive.setVisibility(View.GONE);
                                            ph_can_active.setVisibility(View.VISIBLE);
                                            ph_can_inactive.setVisibility(View.GONE);
                                            break;
                                        case "2":
                                            ph_active.setVisibility(View.VISIBLE);
                                            ph_inactive.setVisibility(View.GONE);
                                            ph_trad_active.setVisibility(View.VISIBLE);
                                            ph_trad_inactive.setVisibility(View.GONE);
                                            break;
                                        case "3":
                                            vid_active.setVisibility(View.VISIBLE);
                                            vid_inactive.setVisibility(View.GONE);
                                            vid_can_active.setVisibility(View.VISIBLE);
                                            vid_can_inactive.setVisibility(View.GONE);
                                            break;
                                        case "4":
                                            vid_active.setVisibility(View.VISIBLE);
                                            vid_inactive.setVisibility(View.GONE);
                                            vid_trad_active.setVisibility(View.VISIBLE);
                                            vid_trad_inactive.setVisibility(View.GONE);
                                            break;
                                        case "5":
                                            vid_active.setVisibility(View.VISIBLE);
                                            vid_inactive.setVisibility(View.GONE);
                                            vid_heli_active.setVisibility(View.VISIBLE);
                                            vid_heli_inactive.setVisibility(View.GONE);
                                            break;
                                        case "6":
                                            des_active.setVisibility(View.VISIBLE);
                                            des_inactive.setVisibility(View.GONE);
                                            break;
                                        case "7":
                                            videdi_active.setVisibility(View.VISIBLE);
                                            videdi_inactive.setVisibility(View.GONE);
                                            break;

                                        case "8":
                                            liveprint_active.setVisibility(View.VISIBLE);
                                            liveprint_inactive.setVisibility(View.GONE);
                                            break;
                                    }
                                }


                                fullnameTV.setText(namestring);
                                emailET.setText(emailidstring);
                                mobilenoTV.setText(mobilestring);
                                dobTV.setText(dobString);
                                locationTV.setText(locationstring);
                                priceET.setText(pricestring);
                                expNumET.setText(expString);


                                if (genderString.equalsIgnoreCase("Female"))
                                {
                                    femaleactiveradio.setVisibility(View.VISIBLE);
                                    femaleinactiveradio.setVisibility(View.GONE);
                                }

                                else  if (genderString.equalsIgnoreCase("Male"))
                                {
                                    maleactiveradio.setVisibility(View.VISIBLE);
                                    maleinactiveradio.setVisibility(View.GONE);
                                }


                                switch(travelBy)
                                {
                                    case "1" :
                                        owncar_active.setVisibility(View.VISIBLE);
                                        owncar_inactive.setVisibility(View.GONE);
                                        break;
                                    case "2" :
                                        owncar_active.setVisibility(View.VISIBLE);
                                        owncar_inactive.setVisibility(View.GONE);
                                        break;
                                    case "3" :
                                        rentcar_active.setVisibility(View.VISIBLE);
                                        rentcar_inactive.setVisibility(View.GONE);
                                        break;
                                    case "4" :taxi_active.setVisibility(View.VISIBLE);
                                        taxi_inactive.setVisibility(View.GONE);
                                        break;
                                    case "5" :
                                        publictransport_active.setVisibility(View.VISIBLE);
                                        publictransport_inactive.setVisibility(View.GONE);
                                        break;
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
                params.put("freelancer_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type", "2");

                //    //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }
    public void getLatLong(final String youraddress) {
        String uri = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyDPmwsBRxE-EfeyHMpKneTCB19nhsaZDmU&address=" + youraddress + "&sensor=true";
        uri = uri.replaceAll(" ", "%20");

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        //Log.e("response", response);

                        try {
                            //Log.e("response", response);
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");

                            ProfileTask task = new ProfileTask();
                            task.execute();


                            //Log.e("latitude", "" + lat);
                            //Log.e("longitude", "" + lng);
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

    private void showDateTimePicker(final TextView dobTV)
    {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(UpdateFreelancerPersonalprofile.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dobString = dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year;
                        //Log.e("dobString", dobString);
                        // set day of month , month and year value in the edit text
                        dobTV.setText(dobString);

                        parseDateToddMMyyyy(dobString);
                        //getAge(year, monthOfYear, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        // TODO Hide Future Date Here
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // TODO Hide Past Date Here
        //  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    public String parseDateToddMMyyyy(String time)
    {
        //Log.e("time",time);
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;

        try {
            date = inputFormat.parse(time);
            //Log.e("date",date+" ");
            dobString = outputFormat.format(date);
            //Log.e("outputFormat.format",dobString+" ");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dobString;
    }



    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent=new Intent(UpdateFreelancerPersonalprofile.this,ProfileUpdate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
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
            loadfreelancer();
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
            loadfreelancer();
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
