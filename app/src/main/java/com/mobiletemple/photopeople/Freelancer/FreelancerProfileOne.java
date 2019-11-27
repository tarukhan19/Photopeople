package com.mobiletemple.photopeople.Freelancer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.mobiletemple.photopeople.ProfileExperienceActivity;
import com.mobiletemple.photopeople.ProfileSocialActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.model.LensDTO;
import com.mobiletemple.photopeople.model.LightningDTO;
import com.mobiletemple.photopeople.model.SupportDto;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;
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
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
public class FreelancerProfileOne extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private String userType;
    Intent intent;
    Integer ageInt = 0;
    EditText emailET, priceET, expNumET;
    TextView locationTV, fullnameTV, mobilenoTV, dobTV, selectequipment;
    LinearLayout malelayout, femalelayout, ph_ll, ph_can_ll, ph_trad_ll, vid_ll, vid_can_ll, vid_trad_ll, vid_heli_ll, des_ll, videdit_ll,
            liveprint_ll, owncar_ll, ownbike_ll, rentcar_ll, taxi_ll, publictransport_ll, nextButton, pricell;
    ImageView maleactiveradio, maleinactiveradio, femaleactiveradio, femaleinactiveradio, ph_active, ph_inactive, ph_can_active, ph_can_inactive, ph_trad_active, ph_trad_inactive, vid_active, vid_inactive, vid_can_active, vid_can_inactive, vid_trad_active, vid_trad_inactive, vid_heli_active, vid_heli_inactive, des_active, des_inactive, videdi_active, videdi_inactive, liveprint_active, liveprint_inactive,
            owncar_active, owncar_inactive, ownbike_active, ownbike_inactive, rentcar_active, rentcar_inactive, taxi_active, taxi_inactive, publictransport_active, publictransport_inactive;
    double lng, lat;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    DatePickerDialog datePickerDialog;
    String experienceString, namestring, freelancertypearaylist = "", type, genderString, mobilestring, locationstring, dobString, travelBy, ageS = "0", emailidstring = "", adressstring, pricestring, cameranamestring, cameraIdtring, lensidstring;
    RecyclerView lensrecycle, lightningrecycle, supportrecycle;
    TextView cameraspinner, lensspinner, lightningspinner, supportingspinner;

    RecyclerView lensrv, lightrv, supportrv;
    Dialog lensDialog, lightdialog, supportdialog;
    AlertDialog.Builder lensalertdialog, lightalertdialog, supportalertdialog;
    View lensView, supportview, lightview;

    ArrayList<LensDTO> lensDTOArrayList;
    ArrayList<SupportDto> supportDtoArrayList;
    ArrayList<LightningDTO> lightningDTOArrayList;


    ArrayList<String> lenslistsentrray, lenslistnamerray, cameraIdarraylist, cameraNamearraylist, lightningnamearraylist, lightningsentarraylist, supportsentaraylist, supportnamearraylist;
    LensAdapter lensAdapter;
    LensAdp lensAdp;

    SupportAdapter supportAdapter;
    SupportAdp supportAdp;
    LightningAdapter lightningAdapter;
    LightningAdp lightningAdp;

    ListView camLV;
    LinearLayout cameraLL, supportningLL, lIghtningLL, lensLL;
    Dialog camdialog;
    ArrayAdapter<String> cameraarrayadapter;

    boolean isConnected;

    String ph_clickable = "0";
    String vid_clickable = "0";
    List<Place.Field> fields;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_profile_one);
        intent = getIntent();
        userType = intent.getStringExtra("userType");
        namestring = intent.getStringExtra("fname");
        mobilestring = intent.getStringExtra("mobile");

        emailET = findViewById(R.id.emailET);
        priceET = findViewById(R.id.price);
        locationTV = findViewById(R.id.location);
        fullnameTV = findViewById(R.id.fullname);
        expNumET = findViewById(R.id.expNumET);
        mobilenoTV = findViewById(R.id.mobileno);
        dobTV = findViewById(R.id.dob);
        dialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(FreelancerProfileOne.this);
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
        selectequipment = findViewById(R.id.selectequipment);
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
        sessionManager = new SessionManager(getApplicationContext());
        Places.initialize(getApplicationContext(), "AIzaSyATs_vOy7Qths4ErsfalVYNNjWAoeiiS50");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        cameraLL = findViewById(R.id.cameraLL);
        supportningLL = findViewById(R.id.supportningLL);
        lIghtningLL = findViewById(R.id.lIghtningLL);
        lensLL = findViewById(R.id.lensLL);

        cameraspinner = findViewById(R.id.cameraspinner);
        lensspinner = findViewById(R.id.lensspinner);
        lightningspinner = findViewById(R.id.lightningspinner);
        supportingspinner = findViewById(R.id.supportingspinner);


        lensrecycle = findViewById(R.id.lensrecycle);
        supportrecycle = findViewById(R.id.supportrecycle);
        lightningrecycle = findViewById(R.id.lightningrecycle);

        supportsentaraylist = new ArrayList<>();
        supportnamearraylist = new ArrayList<>();

        camLV = new ListView(this);
        camdialog = new Dialog(FreelancerProfileOne.this);
        cameraNamearraylist = new ArrayList<>();
        cameraIdarraylist = new ArrayList<>();
        cameraarrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_cameraspinner, R.id.text, cameraNamearraylist);

        lensDTOArrayList = new ArrayList<>();
        lenslistsentrray = new ArrayList<>();
        lenslistnamerray = new ArrayList<>();
        lensAdapter = new LensAdapter(this, lensDTOArrayList);

        lightningDTOArrayList = new ArrayList<>();
        lightningnamearraylist = new ArrayList<>();
        lightningsentarraylist = new ArrayList<>();
        lightningAdapter = new LightningAdapter(this, lightningDTOArrayList);

        supportDtoArrayList = new ArrayList<>();
        supportnamearraylist = new ArrayList<>();
        supportsentaraylist = new ArrayList<>();
        supportAdapter = new SupportAdapter(this, supportDtoArrayList);


        nextButton = findViewById(R.id.nextButton);

        lensalertdialog = new AlertDialog.Builder(this);
        lensalertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        lensView = LayoutInflater.from(this).inflate(R.layout.item_equipment_rv, null);
        lensalertdialog.setView(lensView);
        lensDialog = lensalertdialog.create();
        lensrv = (RecyclerView) lensView.findViewById(R.id.rv);
        lensrv.setLayoutManager(new LinearLayoutManager(this));
        lensrv.setHasFixedSize(true);
        lensrv.setAdapter(lensAdapter);


        supportalertdialog = new AlertDialog.Builder(this);
        supportalertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        supportview = LayoutInflater.from(this).inflate(R.layout.item_equipment_support, null);
        supportalertdialog.setView(supportview);
        supportdialog = supportalertdialog.create();
        supportrv = (RecyclerView) supportview.findViewById(R.id.rv);
        supportrv.setLayoutManager(new LinearLayoutManager(this));
        supportrv.setHasFixedSize(true);
        supportrv.setAdapter(supportAdapter);


        lightalertdialog = new AlertDialog.Builder(this);
        lightalertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        lightview = LayoutInflater.from(this).inflate(R.layout.item_equipment_light, null);
        lightalertdialog.setView(lightview);
        lightdialog = lightalertdialog.create();
        lightrv = (RecyclerView) lightview.findViewById(R.id.rv);
        lightrv.setLayoutManager(new LinearLayoutManager(this));
        lightrv.setHasFixedSize(true);
        lightrv.setAdapter(lightningAdapter);


        lensspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lensalertdialog.setView(lensrv);
                lensDialog.show();
            }
        });

        supportingspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportalertdialog.setView(supportrv);
                supportdialog.show();
            }
        });


        lightningspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lightalertdialog.setView(lightrv);
                lightdialog.show();
            }
        });


        camLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                cameraspinner.setText(txt.getText().toString());
                cameranamestring = txt.getText().toString();
                cameraIdtring = cameraIdarraylist.get(position).toString();
                camdialog.dismiss();
            }
        });

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camdialog.setContentView(camLV);
                camdialog.show();

            }
        });


        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        locationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(FreelancerProfileOne.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


            }
        });




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

                cameraLL.setVisibility(View.VISIBLE);
                lIghtningLL.setVisibility(View.VISIBLE);
                supportningLL.setVisibility(View.VISIBLE);
                lensLL.setVisibility(View.VISIBLE);
                selectequipment.setVisibility(View.VISIBLE);

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


                cameraLL.setVisibility(View.VISIBLE);
                lIghtningLL.setVisibility(View.VISIBLE);
                supportningLL.setVisibility(View.VISIBLE);
                lensLL.setVisibility(View.VISIBLE);
                selectequipment.setVisibility(View.VISIBLE);


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


        des_ll.setOnClickListener(new View.OnClickListener() {
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


                cameraLL.setVisibility(View.GONE);
                lIghtningLL.setVisibility(View.GONE);
                supportningLL.setVisibility(View.GONE);
                lensLL.setVisibility(View.GONE);
                selectequipment.setVisibility(View.GONE);


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

                cameraLL.setVisibility(View.GONE);
                lIghtningLL.setVisibility(View.GONE);
                supportningLL.setVisibility(View.GONE);
                lensLL.setVisibility(View.GONE);
                selectequipment.setVisibility(View.GONE);


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


                cameraLL.setVisibility(View.VISIBLE);
                lIghtningLL.setVisibility(View.VISIBLE);
                supportningLL.setVisibility(View.VISIBLE);
                lensLL.setVisibility(View.VISIBLE);
                selectequipment.setVisibility(View.VISIBLE);


            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailidstring = emailET.getText().toString();
                //   adressstring = addressET.getText().toString();
                pricestring = priceET.getText().toString();
                experienceString = expNumET.getText().toString();

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
                   // str += Constants.CANDID_PHOTOGRAPHER + ",";
                }
                if (ph_trad_active.getVisibility() == View.VISIBLE) {
                   // str += Constants.TRADITIONAL_PHOTOGRAPHER + ",";
                    str.add(Constants.TRADITIONAL_PHOTOGRAPHER);

                }
                if (vid_trad_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.TRADITIONAL_VIDEOGRAPHER);

                    //str += Constants.TRADITIONAL_VIDEOGRAPHER + ",";

                }
                if (vid_can_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.CANDID_VIDEOGRAPHER);

                    //str += Constants.CANDID_VIDEOGRAPHER + ",";
                }
                if (vid_heli_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.HELICAM);
                }
                if (liveprint_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.LIVE_PRINT);
                }
                if (des_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.DESIGNER);
                }
                if (videdi_active.getVisibility() == View.VISIBLE) {
                    str.add(Constants.VIDEO_EDITOR);
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
                Log.e("freelancertypearaylist", freelancertypearaylist + "");



                //   Log.e("freelancertypearaylist", freelancertypearaylist);


                String priceValidateMSG = UIValidation.numberValidate(pricestring, true);
                String expValidatemsg = UIValidation.numberValidate(experienceString, true);
                boolean isvalidate = true;

                if (owncar_active.getVisibility() == View.GONE && ownbike_active.getVisibility() == View.GONE && rentcar_active.getVisibility() == View.GONE
                        && taxi_active.getVisibility() == View.GONE && publictransport_active.getVisibility() == View.GONE) {
                    Toast.makeText(FreelancerProfileOne.this, "Please select travel type", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (maleactiveradio.getVisibility() == View.GONE && femaleactiveradio.getVisibility() == View.GONE) {
                    Toast.makeText(FreelancerProfileOne.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (ageInt < 12) {
                    Toast.makeText(FreelancerProfileOne.this, "Invalid Date Of Birth", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (dobTV.getText().toString().equalsIgnoreCase(null)) {
                    Toast.makeText(FreelancerProfileOne.this, "Select Date Of Birth", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (!priceValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    priceET.setError(priceValidateMSG);
                    isvalidate = false;
                }
                if (!expValidatemsg.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    expNumET.setError(expValidatemsg);
                    isvalidate = false;
                }
                if (ph_active.getVisibility() == View.GONE && vid_active.getVisibility() == View.GONE && des_active.getVisibility() == View.GONE
                        && videdi_active.getVisibility() == View.GONE && liveprint_active.getVisibility() == View.GONE)

                {
                    Toast.makeText(FreelancerProfileOne.this, "Please Choose type", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (locationTV.getText().toString().isEmpty()) {
                    Toast.makeText(FreelancerProfileOne.this, "Please select Location", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (ph_active.getVisibility() == View.VISIBLE) {
                    if (ph_can_active.getVisibility() == View.GONE && ph_trad_active.getVisibility() == View.GONE) {
                        Toast.makeText(FreelancerProfileOne.this, "Please select subtype", Toast.LENGTH_SHORT).show();
                        isvalidate = false;

                    }
                }
                if (vid_active.getVisibility() == View.VISIBLE) {
                    if (vid_can_active.getVisibility() == View.GONE && vid_trad_active.getVisibility() == View.GONE && vid_heli_active.getVisibility() == View.GONE) {
                        Toast.makeText(FreelancerProfileOne.this, "Please select subtype", Toast.LENGTH_SHORT).show();
                        isvalidate = false;

                    }
                }


                if (isvalidate) {
                    if (!isConnected) {
                        showSnack(isConnected);
                    } else {
                        ProfileTask task = new ProfileTask();
                        task.execute();
                    }

                }

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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                locationstring=place.getName();
                locationTV.setText(locationstring);
                getLatLong(locationstring);
                Log.e( "Place: " , place.getAddress() );
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void showDateTimePicker(final TextView dobTV) {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(FreelancerProfileOne.this,
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
                        getAge(year, monthOfYear, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        // TODO Hide Future Date Here
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // TODO Hide Past Date Here
        //  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public String parseDateToddMMyyyy(String time) {

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


    // Profile Upload
    class ProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            dialog.setMessage("Profile Uploading ...");
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {


                JSONObject ob = new JSONObject();
                ob.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                ob.put("user_type", userType);
                ob.put("address", locationstring);
                ob.put("location", adressstring);
                ob.put("email", emailidstring);
                ob.put("start_price", pricestring);
                ob.put("travel_by", travelBy);
                ob.put("gendar", genderString);
                ob.put("dob", dobString);
                ob.put("equipments", "a");
                ob.put("freelancer_type", freelancertypearaylist);
                ob.put("latitude", String.valueOf(lat));
                ob.put("longitude", String.valueOf(lng));
                ob.put("camera_id", cameraIdtring);
                ob.put("experience",experienceString);

                Log.e("params", ob.toString());

                String result = comm.forFreelancerStepOne(Endpoints.FREELANCER_PERSONAL_PROFILE_URL, ob, lenslistsentrray, lightningsentarraylist, supportsentaraylist);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            dialog.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equals("data successfully Submit")) {

//                                Intent in = new Intent(FreelancerProfileOne.this, ProfileExperienceActivity.class);
//                                in.putExtra("userType", Constants.FREELANCER_TYPE);
//                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                                startActivity(in);
//                                overridePendingTransition(R.anim.trans_left_in,
//                                        R.anim.trans_left_out);


                        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(FreelancerProfileOne.this, SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Successfully Registered !")
                                .setConfirmText("OK");
                        sweetAlertDialog.show();


                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(FreelancerProfileOne.this, R.color.colorPrimary));

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();
                                if (userType.equalsIgnoreCase("1")) {
                                    Intent in = new Intent(FreelancerProfileOne.this, LoginActivity.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    in.putExtra("userType", userType);

                                    startActivity(in);
                                    overridePendingTransition(R.anim.trans_left_in,
                                            R.anim.trans_left_out);
                                }
                                if (userType.equalsIgnoreCase("2")) {
                                    Intent in = new Intent(FreelancerProfileOne.this, LoginActivity.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    in.putExtra("userType", userType);

                                    startActivity(in);
                                    overridePendingTransition(R.anim.trans_left_in,
                                            R.anim.trans_left_out);
                                }
                            }
                        });


                    } else {
                        Toast.makeText(FreelancerProfileOne.this, "Profile Upload Failed; Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
            }
        }
    }


    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        ageInt = new Integer(age);
        ageS = ageInt.toString();
        //Log.e("age", ageS);

        return ageS;
    }

    public void getLatLong(final String youraddress)
    {
        String uri = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyDPmwsBRxE-EfeyHMpKneTCB19nhsaZDmU&address=" + youraddress + "&sensor=true";
        uri = uri.replaceAll(" ", "%20");

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
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


    private void loadSpinnerData()
    {
        dialog.setMessage("Loading Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.CAMERA_LISTING, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    JSONArray camera_listing = obj.getJSONArray("camera_listing");
                    JSONArray suporting_listng = obj.getJSONArray("suporting_listng");
                    JSONArray lighting_listing = obj.getJSONArray("lighting_listing");
                    JSONArray lence_listing = obj.getJSONArray("lence_listing");

                    for (int i = 0; i < camera_listing.length(); i++) {
                        JSONObject camera_listingobject = camera_listing.getJSONObject(i);
                        String camera_id = camera_listingobject.getString("camera_id");
                        String camera_name = camera_listingobject.getString("camera_name");
                        cameraIdarraylist.add(camera_id);
                        cameraNamearraylist.add(camera_name);
                    }
                    camLV.setAdapter(cameraarrayadapter);


                    for (int i = 0; i < lence_listing.length(); i++) {

                        LensDTO lensDTO = new LensDTO();
                        JSONObject lence_listingobject = lence_listing.getJSONObject(i);
                        String lence_id = lence_listingobject.getString("lence_id");
                        String lence_name = lence_listingobject.getString("lence_name");

                        lensDTO.setName(lence_name);
                        lensDTO.setId(lence_id);
                        lensDTOArrayList.add(lensDTO);
                    }
                    lensrv.setAdapter(lensAdapter);


                    for (int i = 0; i < lighting_listing.length(); i++) {

                        LightningDTO lightningDTO = new LightningDTO();
                        JSONObject lighting_listingobject = lighting_listing.getJSONObject(i);
                        String lighting_id = lighting_listingobject.getString("lighting_id");
                        String lighting_name = lighting_listingobject.getString("lighting_name");
                        lightningDTO.setName(lighting_name);
                        lightningDTO.setId(lighting_id);
                        lightningDTOArrayList.add(lightningDTO);

                    }
                    lightrv.setAdapter(lightningAdapter);


                    for (int i = 0; i < suporting_listng.length(); i++) {
                        SupportDto supportDto = new SupportDto();
                        JSONObject suporting_listngobject = suporting_listng.getJSONObject(i);
                        String suporting_id = suporting_listngobject.getString("suporting_id");
                        String suporting_name = suporting_listngobject.getString("suporting_name");
                        supportDto.setName(suporting_name);
                        supportDto.setId(suporting_id);
                        supportDtoArrayList.add(supportDto);

                    }
                    supportrv.setAdapter(supportAdapter);
                    //lensspinner.setAdapter(lensAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    private class LensAdapter extends RecyclerView.Adapter<LensAdapter.CustomViewHodler> {

        private Context mContext;
        ArrayList<LensDTO> lensDTOS;

        public LensAdapter(Context context, ArrayList<LensDTO> lensDTOS) {
            this.mContext = context;
            this.lensDTOS = lensDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final LensDTO lensDTO = lensDTOS.get(position);
            holder.text.setText(lensDTO.getName());
            holder.ll.setBackgroundColor(lensDTO.isSelected() ? Color.LTGRAY : Color.WHITE);

            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    lensDTO.setSelected(!lensDTO.isSelected());
                    holder.ll.setBackgroundColor(lensDTO.isSelected() ? Color.LTGRAY : Color.WHITE);


                    if (lensDTO.isSelected() == true) {
                        lensrecycle.setVisibility(View.VISIBLE);
                        lenslistnamerray.add(lensDTO.getName());
                        lenslistsentrray.add(lensDTO.getId());
                        //Log.e("lenslistsentrrayadd",lenslistsentrray+" "+lenslistnamerray);

                    } else {
                        lenslistnamerray.remove(lensDTO.getName());
                        lenslistsentrray.remove(lensDTO.getId());
                        //Log.e("lenslistsentrrayrem",lenslistsentrray+" "+lenslistnamerray);

                    }
                    lensrecycle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    lensAdp = new LensAdp(mContext, holder.ll, lenslistsentrray, lenslistnamerray, lensDTOArrayList);
                    lensrecycle.setAdapter(lensAdp);
                    notifyDataSetChanged();
                    //   lensAdp.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return lensDTOS == null ? 0 : lensDTOS.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
            }
        }
    }


    private class LensAdp extends RecyclerView.Adapter<LensAdp.CustomViewHodler> {
        private Context mContext;
        ArrayList<String> lenslistsent;
        ArrayList<String> lenslistname;
        ArrayList<LensDTO> lensDTOS;
        LinearLayout ll;
        int click = 0;

        public LensAdp(Context context, LinearLayout ll, ArrayList<String> lenslistsent, ArrayList<String> lenslistname, ArrayList<LensDTO> lensDTOS) {
            this.mContext = context;
            this.lenslistname = lenslistname;
            this.lenslistsent = lenslistsent;
            this.lensDTOS = lensDTOS;
            this.ll = ll;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameralisting, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            String name = lenslistname.get(position);
            final LensDTO lensDTO = lensDTOS.get(position);

            holder.text.setText(name);
            holder.imageView.setVisibility(View.GONE);


        }

        @Override
        public int getItemCount() {
            return lenslistsent == null ? 0 : lenslistsent.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            ImageView imageView;
            TextView text;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                imageView = (ImageView) itemView.findViewById(R.id.cross);
            }
        }
    }


    private class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.CustomViewHodler> {

        private Context mContext;
        ArrayList<SupportDto> supportDtos;

        public SupportAdapter(Context context, ArrayList<SupportDto> supportDtos) {
            this.mContext = context;
            this.supportDtos = supportDtos;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final SupportDto supportDto = supportDtos.get(position);
            holder.text.setText(supportDto.getName());
            holder.ll.setBackgroundColor(supportDto.isSelected() ? Color.LTGRAY : Color.WHITE);

            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    supportDto.setSelected(!supportDto.isSelected());
                    holder.ll.setBackgroundColor(supportDto.isSelected() ? Color.LTGRAY : Color.WHITE);


                    if (supportDto.isSelected() == true) {
                        supportrecycle.setVisibility(View.VISIBLE);
                        supportnamearraylist.add(supportDto.getName());
                        supportsentaraylist.add(supportDto.getId());
                        //Log.e("lenslistsentrrayadd",supportnamearraylist+" "+supportsentaraylist);

                    } else {
                        supportnamearraylist.remove(supportDto.getName());
                        supportsentaraylist.remove(supportDto.getId());
                        //Log.e("lenslistsentrrayrem",supportnamearraylist+" "+supportsentaraylist);

                    }
                    supportrecycle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    supportAdp = new SupportAdp(mContext, supportsentaraylist, supportnamearraylist, supportDtos);
                    supportrecycle.setAdapter(supportAdp);
                    notifyDataSetChanged();
                    //   lensAdp.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return supportDtos == null ? 0 : supportDtos.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
            }
        }
    }


    private class SupportAdp extends RecyclerView.Adapter<SupportAdp.CustomViewHodler> {
        private Context mContext;
        ArrayList<String> supportlistsent;
        ArrayList<String> supportlistname;
        ArrayList<SupportDto> supportDTOS;
        int click = 0;

        public SupportAdp(Context context, ArrayList<String> supportlistsent, ArrayList<String> supportlistname, ArrayList<SupportDto> supportDTOS) {
            this.mContext = context;
            this.supportlistname = supportlistname;
            this.supportlistsent = supportlistsent;
            this.supportDTOS = supportDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameralisting, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            String name = supportlistname.get(position);
            final SupportDto supportDto = supportDTOS.get(position);

            holder.text.setText(name);
            holder.imageView.setVisibility(View.GONE);


        }

        @Override
        public int getItemCount() {
            return supportlistsent == null ? 0 : supportlistsent.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            ImageView imageView;
            TextView text;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                imageView = (ImageView) itemView.findViewById(R.id.cross);
            }
        }
    }


    private class LightningAdapter extends RecyclerView.Adapter<LightningAdapter.CustomViewHodler> {

        private Context mContext;
        ArrayList<LightningDTO> lightningDTOS;

        public LightningAdapter(Context context, ArrayList<LightningDTO> lightningDTOS) {
            this.mContext = context;
            this.lightningDTOS = lightningDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final LightningDTO lightningDTO = lightningDTOS.get(position);
            holder.text.setText(lightningDTO.getName());
            holder.ll.setBackgroundColor(lightningDTO.isSelected() ? Color.LTGRAY : Color.WHITE);

            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    lightningDTO.setSelected(!lightningDTO.isSelected());
                    holder.ll.setBackgroundColor(lightningDTO.isSelected() ? Color.LTGRAY : Color.WHITE);


                    if (lightningDTO.isSelected() == true) {
                        lightningrecycle.setVisibility(View.VISIBLE);
                        lightningnamearraylist.add(lightningDTO.getName());
                        lightningsentarraylist.add(lightningDTO.getId());
                        //Log.e("lenslistsentrrayadd",lightningnamearraylist+" "+lightningsentarraylist);

                    } else {
                        lightningnamearraylist.remove(lightningDTO.getName());
                        lightningsentarraylist.remove(lightningDTO.getId());
                        //Log.e("lenslistsentrrayrem",lightningnamearraylist+" "+lightningsentarraylist);

                    }
                    lightningrecycle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    lightningAdp = new LightningAdp(mContext, lightningsentarraylist, lightningnamearraylist, lightningDTOS);
                    lightningrecycle.setAdapter(lightningAdp);
                    notifyDataSetChanged();
                    //   lensAdp.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return lightningDTOS == null ? 0 : lightningDTOS.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
            }
        }
    }


    private class LightningAdp extends RecyclerView.Adapter<LightningAdp.CustomViewHodler> {
        private Context mContext;
        ArrayList<String> lightlistsent;
        ArrayList<String> lightlistname;
        ArrayList<LightningDTO> lightningDTOS;
        int click = 0;

        public LightningAdp(Context context, ArrayList<String> lightlistsent, ArrayList<String> lightlistname, ArrayList<LightningDTO> lightningDTOS) {
            this.mContext = context;
            this.lightlistsent = lightlistsent;
            this.lightlistname = lightlistname;
            this.lightningDTOS = lightningDTOS;
        }


        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameralisting, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            String name = lightlistname.get(position);
            final LightningDTO lightningDTO = lightningDTOS.get(position);

            holder.text.setText(name);
            holder.imageView.setVisibility(View.GONE);


        }

        @Override
        public int getItemCount() {
            return lightlistsent == null ? 0 : lightlistsent.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            ImageView imageView;
            TextView text;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                imageView = (ImageView) itemView.findViewById(R.id.cross);
            }
        }
    }

    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            showSnack(isConnected);
        } else {
            loadSpinnerData();
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
            loadSpinnerData();
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
