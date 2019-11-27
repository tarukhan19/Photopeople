package com.mobiletemple.photopeople;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.Users;
import com.mobiletemple.photopeople.util.Endpoints;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SendQuote extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    LinearLayout eventtype;
    TextView startdate, enddate, location, eventTV, price;
    EditText venue, msg;
    String eventtypestring = "", locationstring = "", start_date = "", end_date = "", priceS = "", venueS = "", msgS = "", shootType,
            freelancerId, selection, from;
    DatePickerDialog datePickerDialog;
    LinearLayout nextButton;
    private long startTime;
    Intent intent;
    RequestQueue requestQueue;
    double lng, lat;
    LinearLayout sendquote;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager mSessionManager;
    AlertDialog.Builder builder;
    AlertDialog eventdialog;
    String[] event = {"Birthday", "Wedding", "Pre-Wedding", "Engagement", "Maternity", "Kids"};
    SessionManager session;
    String starting_price;
    ArrayList<String> eventArraylist;
    ArrayAdapter<String> eventArrayadapter;
    ListView eventLV;
    String eventtypesend;
    boolean isConnected;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    List<Place.Field> fields;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 0;

    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_quote);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(SendQuote.this);
        mSessionManager = new SessionManager(getApplicationContext());
        eventtype = findViewById(R.id.eventtype);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        location = findViewById(R.id.location);
        msg = findViewById(R.id.msg);
        venue = findViewById(R.id.venue);
        price = findViewById(R.id.price);
        sendquote = findViewById(R.id.sendquote);
        eventLV = new ListView(this);
        eventTV = findViewById(R.id.eventTV);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        session = new SessionManager(SendQuote.this);

        builder = new AlertDialog.Builder(SendQuote.this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setView(eventLV);
        eventdialog = builder.create();
        eventArraylist = new ArrayList<>();
        eventArrayadapter = new ArrayAdapter<String>(this,
                R.layout.eventtype, R.id.spinnertext, eventArraylist);
        eventArraylist.addAll(Arrays.asList(event));
        eventLV.setAdapter(eventArrayadapter);

        intent = getIntent();
        freelancerId = intent.getStringExtra("freelancerId");
        selection = intent.getStringExtra("selection");
        from = intent.getStringExtra("from");
        priceS = intent.getStringExtra("starting_price");

        location = findViewById(R.id.location);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        requestQueue = Volley.newRequestQueue(SendQuote.this);
        nextButton = findViewById(R.id.nextButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);

        if (selection.equalsIgnoreCase("2")) {
            enddate.setText(mSessionManager.getFilter().get(SessionManager.KEY_ENDDATE));
            end_date = mSessionManager.getFilter().get(SessionManager.KEY_ENDDATE);

            startdate.setText(mSessionManager.getFilter().get(SessionManager.KEY_STARTDATE));
            start_date = mSessionManager.getFilter().get(SessionManager.KEY_STARTDATE);

            location.setText(mSessionManager.getFilter().get(SessionManager.KEY_LOCATION));
            locationstring = mSessionManager.getFilter().get(SessionManager.KEY_LOCATION);
            click = 1;
        } else {
            click = 0;
        }
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendQuote.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (from.equalsIgnoreCase("timeline"))
                {
                    intent.putExtra("from", "timeline");

                }
                else
                {
                    intent.putExtra("from", "other");

                }

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        eventLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView lensspinnertext = (TextView) view.findViewById(R.id.spinnertext);
                eventtypestring = lensspinnertext.getText().toString();
                eventTV.setText(eventtypestring);
                eventArrayadapter.notifyDataSetChanged();
                eventdialog.dismiss();


            }

        });

        eventtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eventdialog.setView(eventLV);
                eventdialog.show();
            }
        });
        mTitle.setText("Send Quote");
        filter.setVisibility(View.INVISIBLE);


        //Log.e("priceS",priceS);
        price.setText(priceS);

        Places.initialize(getApplicationContext(), "AIzaSyATs_vOy7Qths4ErsfalVYNNjWAoeiiS50");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(SendQuote.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


            }
        });


        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click == 0) {
                    DatePickerDialog dlg = new DatePickerDialog(SendQuote.this, new DatePickerDialog.OnDateSetListener() {
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
                } else {
                    Toast.makeText(SendQuote.this, "Already Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (click == 0) {
                    DatePickerDialog dlg = new DatePickerDialog(SendQuote.this, new DatePickerDialog.OnDateSetListener() {
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
                } else {
                    Toast.makeText(SendQuote.this, "Already Selected", Toast.LENGTH_SHORT).show();
                    end_date = enddate.getText().toString();
                }
            }
        });

        sendquote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceS = price.getText().toString();
                msgS = msg.getText().toString();
                venueS = venue.getText().toString();
                switch (eventtypestring) {
                    case "Birthday":// holder.freelancerIV.setImageResource(R.mipmap.birthday);
                        eventtypesend = "1";
                        break;
                    case "Wedding":// holder.freelancerIV.setImageResource(R.mipmap.wedding);
                        eventtypesend = "2";
                        break;
                    case "Pre-Wedding":// holder.freelancerIV.setImageResource(R.mipmap.prewedding);
                        eventtypesend = "3";
                        break;
                    case "Engagement": //holder.freelancerIV.setImageResource(R.mipmap.engagement);
                        eventtypesend = "4";
                        break;
                    case "Maternity": //holder.freelancerIV.setImageResource(R.mipmap.maternity);
                        eventtypesend = "5";
                        break;
                    case "Kids":// holder.freelancerIV.setImageResource(R.mipmap.kids);
                        eventtypesend = "6";
                        break;
                }



                boolean isvalidate = true;

//                 if (maleactiveradio.getVisibility() == View.GONE && femaleactiveradio.getVisibility() == View.GONE)
//                 {
//                    Toast.makeText(SendQuote.this, "Please select Shoot Type", Toast.LENGTH_SHORT).show();
//                    isvalidate = false;}

                if (venueS.isEmpty()) {
                    venue.setError("Please enter venue !");
                    isvalidate = false;
                }
                if (priceS.isEmpty()) {
                    price.setError("Please enter price !");
                    isvalidate = false;
                }

                if (msgS.isEmpty()) {
                    msg.setError("Please enter description !");
                    isvalidate = false;
                }

                if (eventtypestring.isEmpty()) {
                    Toast.makeText(SendQuote.this, "Select event type", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (start_date.isEmpty()) {
                    Toast.makeText(SendQuote.this, "Select start date", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }

                if (end_date.isEmpty()) {
                    Toast.makeText(SendQuote.this, "Select end date", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }

                if (locationstring.isEmpty()) {
                    Toast.makeText(SendQuote.this, "Select location", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }

                if (isvalidate)

                {
                    if (!isConnected) {
                        showSnack(isConnected);
                    } else {
                        sendQuote();

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
    private void sendQuote() {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.SEND_FREELANCER_QUOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
                            if (status == 200 && message.equalsIgnoreCase("Success")) {


                                databaseReference.orderByChild("mobileno").equalTo(session.getLoginSession().get(SessionManager.KEY_MOBILE)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            switchToOtherActivity();
                                        } else {
                                            Users user = new
                                                    Users(session.getLoginSession().get(SessionManager.KEY_USERID)
                                                    , session.getLoginSession().get(SessionManager.KEY_MOBILE),
                                                    session.getLoginSession().get(SessionManager.KEY_NAME)
                                            ,mSessionManager.getLoginSession().get(SessionManager.KEY_IMAGE));

                                            databaseReference.push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                    switchToOtherActivity();
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            switchToOtherActivity();
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("studio_id", session.getuserId().get(SessionManager.KEY_USERID));
                ob.put("freelancer_id", freelancerId);
                ob.put("event_type", eventtypesend);
                ob.put("location", locationstring);
                ob.put("venue", venueS);
                ob.put("start_date", start_date);
                ob.put("end_date", end_date);
                ob.put("type", "0");
                ob.put("price", priceS);
                ob.put("description", msgS);
                ob.put("reqiest_user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void switchToOtherActivity() {


        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(SendQuote.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Quote Send !");
        sweetAlertDialog.show();

        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(SendQuote.this, R.color.colorPrimary));

        sweetAlertDialog.setConfirmText("Ok");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();

                Intent in = new Intent(SendQuote.this, HomePage.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in.putExtra("from", "other");

                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);

            }
        });
        sweetAlertDialog.show();
    }


    public void getLatLong(final String youraddress) {
        String uri = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyDPmwsBRxE-EfeyHMpKneTCB19nhsaZDmU&address=" + youraddress + "&sensor=true";
        uri = uri.replaceAll(" ", "%20");

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SendQuote.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (from.equalsIgnoreCase("timeline"))
        {
            intent.putExtra("from", "timeline");

        }
        else
        {
            intent.putExtra("from", "other");

        }

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
                .make(findViewById(R.id.rl), message, Snackbar.LENGTH_LONG);

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
