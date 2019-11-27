package com.mobiletemple.photopeople;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.adapter.FilterAdapter;
import com.mobiletemple.photopeople.model.FilterDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private RecyclerView recyclerView;
    private ImageView nodataIV;
    private ImageView progressBar;
    private ImageView backIV, filterIV;
    long diff ;

    AlertDialog.Builder cambuilder;
    AlertDialog camdialog;
    ListView cameraLV;
    ArrayList<String> camIdarraylist, camNamearraylist;
    ArrayAdapter<String> camnamearrayadpater;
    String  camIdtring;
    ImageView emptylist;
    String camTypestring="";
    Bundle bundle;
    Intent in;
    SessionManager sessionManager;
    private ArrayList<FilterDTO> freelancerList;
    FilterAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    boolean isConnected;
  //  private AdView mAdView,adViewbottom;

    //   String freelancer_type,locationstring;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        emptylist=findViewById(R.id.emptylist);
        cambuilder= new AlertDialog.Builder(FilterActivity.this);
        cameraLV=new ListView(this);
        camIdarraylist= new ArrayList<>();
        camNamearraylist= new ArrayList<>();
        camnamearrayadpater =new ArrayAdapter<String>(this,
                R.layout.item_cameraspinner, R.id.text, camNamearraylist);
        cambuilder.setView(cameraLV);
        camdialog = cambuilder.create();

        in=getIntent();
        progressBar = (ImageView) findViewById(R.id.ProgressBar);
        freelancerList = new ArrayList<>();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Freelancer List");
        sessionManager = new SessionManager(getApplicationContext());
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FilterActivity.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","other");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new FilterAdapter(this, freelancerList);


        queue = Volley.newRequestQueue(this);


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                opendialog();

            }
        });

        MobileAds.initialize(this, "ca-app-pub-1234961524965105~5671037383");


        Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        progressBar.setAnimation(aniRotateClk);
    }


    private void runLayoutAnimation(final RecyclerView recyclerView)
    {



        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            View v = recyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .setStartDelay(i * 50)
                                    .start();
                        }

                        return true;
                    }
                });
    }
    private void opendialog()
    {
        final Dialog dialog = new Dialog(FilterActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_camerafilter_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView cross = (ImageView) dialog.findViewById(R.id.cross);
        LinearLayout submit = (LinearLayout) dialog.findViewById(R.id.submit);
        LinearLayout cameratypeLL=dialog.findViewById(R.id.cameratypeLL);
        final TextView cameraspinner=dialog.findViewById(R.id.cameraspinner);

        cameraLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                cameraspinner.setText(txt.getText().toString());
                 camTypestring = txt.getText().toString();
                //Log.e("prodTypestring",camTypestring);
                camIdtring = camIdarraylist.get(position).toString();
                camdialog.dismiss();
            }

        });

        cameratypeLL.setOnClickListener(new View.OnClickListener()
        {
            @Override

            public void onClick(View view) {
                camdialog.setView(cameraLV);
                camdialog.show();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (camTypestring.isEmpty())
                {
                    Toast.makeText(FilterActivity.this, "Select Camera", Toast.LENGTH_SHORT).show();
                }
               else
                {
                    cameraFilter();
                    dialog.dismiss();
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void cameraFilter()
    {
        Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        progressBar.setAnimation(aniRotateClk);
        progressBar.setVisibility(View.VISIBLE);

        freelancerList.clear();


        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.Freelancer_camera_Filter,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        freelancerList.clear();
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e("cam_Filter", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data = obj.getJSONObject("data");

                                final JSONArray freelancerjsonarray=data.getJSONArray("freelancer");
                                JSONArray camer_listing=obj.getJSONArray("camer_listing");
                                //Log.e("camer_listing",camer_listing+"");
                                for (int x = 0; x < freelancerjsonarray.length(); x++)
                                {
                                    emptylist.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    JSONObject freelancerObj = null;
                                    try {
                                        freelancerObj = freelancerjsonarray.getJSONObject(x);
                                        FilterDTO freelancer = new FilterDTO();

                                        freelancer.setId(freelancerObj.getString("id"));
                                        freelancer.setName(freelancerObj.getString("first_name")+" "+freelancerObj.getString("last_name"));
                                        freelancer.setStarting_price(freelancerObj.getString("starting_price"));
                                        if(! freelancerObj.getString("travel_by").isEmpty())
                                            freelancer.setTravel_by(Integer.parseInt(freelancerObj.getString("travel_by")));
                                        freelancer.setDob(freelancerObj.getString("dob"));
                                        freelancer.setProfile_image(freelancerObj.getString("profile_image"));
                                        freelancer.setRating(freelancerObj.getDouble("freelancer_rating"));
                                        freelancer.setfeature(freelancerObj.getString("feturde"));
                                        String cameraid=freelancerObj.getString("camera_type");
                                        for (int j=0;j<camer_listing.length();j++)
                                        {
                                            JSONObject jsonObject=camer_listing.getJSONObject(j);
                                            if (cameraid.equalsIgnoreCase(jsonObject.getString("id")))
                                            {
                                                freelancer.setCameratype(jsonObject.getString("camera_name"));
                                            }
                                        }

                                        if(x%5==0)
                                        {
                                            freelancerList.add(null);
                                        }
                                        freelancerList.add(freelancer);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        runLayoutAnimation(recyclerView);

                                    }catch(Exception ee){
                                        //Log.e("### "+ee.getMessage(),x + " " +freelancerObj.getString("first_name"));
                                    }
                                }
                            }
                            else if ( status==1 && message.equalsIgnoreCase("No record found")){
                                emptylist.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                freelancerList.clear();
                                adapter.notifyDataSetChanged();
                            }
                            else

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
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("lat",sessionManager.getFilter().get(SessionManager.KEY_LATITUDE));
                ob.put("lng", sessionManager.getFilter().get(SessionManager.KEY_LONGITUDE));
                ob.put("startDate", sessionManager.getFilter().get(SessionManager.KEY_STARTDATE));
                ob.put("endDate", sessionManager.getFilter().get(SessionManager.KEY_ENDDATE));
                ob.put("freelancer_type", sessionManager.getFilter().get(SessionManager.KEY_FREEE_TYPE));
                ob.put("camera_type", camIdtring);


                Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void freelancerFilter()
   {
       progressBar.setVisibility(View.VISIBLE);


       StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.Freelancer_Filter,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("filterresponse",response);
                        freelancerList.clear();

                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e("filter", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data = obj.getJSONObject("data");

                                final JSONArray freelancerjsonarray=data.getJSONArray("freelancer");
                                JSONArray camer_listing=obj.getJSONArray("camer_listing");
                                //Log.e("camer_listing",camer_listing+"");
                                for (int x = 0; x < freelancerjsonarray.length(); x++)
                                {
                                    emptylist.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    JSONObject freelancerObj = null;
                                    try {
                                        freelancerObj = freelancerjsonarray.getJSONObject(x);
                                        FilterDTO freelancer = new FilterDTO();

                                        freelancer.setId(freelancerObj.getString("id"));
                                        freelancer.setName(freelancerObj.getString("first_name")+" "+freelancerObj.getString("last_name"));
                                        freelancer.setStarting_price(freelancerObj.getString("starting_price"));
                                        if(! freelancerObj.getString("travel_by").isEmpty())
                                         freelancer.setTravel_by(Integer.parseInt(freelancerObj.getString("travel_by")));
                                        freelancer.setDob(freelancerObj.getString("dob"));
                                        freelancer.setProfile_image(freelancerObj.getString("profile_image"));
                                        freelancer.setRating(freelancerObj.getDouble("freelancer_rating"));
                                        String cameraid=freelancerObj.getString("camera_type");
                                        freelancer.setfeature(freelancerObj.getString("feturde"));

                                        for (int j=0;j<camer_listing.length();j++)
                                        {
                                            JSONObject jsonObject=camer_listing.getJSONObject(j);
                                            if (cameraid.equalsIgnoreCase(jsonObject.getString("id")))
                                            {
                                                freelancer.setCameratype(jsonObject.getString("camera_name"));
                                            }
                                        }

                                        if(x%5==0)
                                        {
                                            freelancerList.add(null);
                                        }
                                        freelancerList.add(freelancer);
                                        recyclerView.setAdapter(adapter);

                                        adapter.notifyDataSetChanged();


                                    }catch(Exception ee){
                                        //Log.e("### "+ee.getMessage(),x + " " +freelancerObj.getString("first_name"));
                                    }
                                }
                            } else{
                                emptylist.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("lat",sessionManager.getFilter().get(SessionManager.KEY_LATITUDE));
                ob.put("lng", sessionManager.getFilter().get(SessionManager.KEY_LONGITUDE));
                ob.put("startDate", sessionManager.getFilter().get(SessionManager.KEY_STARTDATE));
                ob.put("endDate", sessionManager.getFilter().get(SessionManager.KEY_ENDDATE));
                ob.put("freelancer_type", sessionManager.getFilter().get(SessionManager.KEY_FREEE_TYPE));
                Log.e("params", ob.toString());

                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(FilterActivity.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }


    private void loadSpinnerData()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.CAMERA_LISTING, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    camIdarraylist.clear();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    JSONArray camera_listing = obj.getJSONArray("camera_listing");
                    JSONArray suporting_listng = obj.getJSONArray("suporting_listng");
                    JSONArray lighting_listing = obj.getJSONArray("lighting_listing");
                    JSONArray lence_listing = obj.getJSONArray("lence_listing");

                    for (int i = 0; i < camera_listing.length(); i++) {
                        JSONObject camera_listingobject = camera_listing.getJSONObject(i);

                        String camera_id = camera_listingobject.getString("camera_id");
                        String camera_name = camera_listingobject.getString("camera_name");
                        camIdarraylist.add(camera_id);
                        camNamearraylist.add(camera_name);
                    }
                    cameraLV.setAdapter(camnamearrayadpater);


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
        queue.add(stringRequest);
    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
            {  freelancerFilter();
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
            freelancerFilter();
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
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }
}
