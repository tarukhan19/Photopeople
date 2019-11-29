package com.mobiletemple.photopeople.BottomNavigation;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.mobiletemple.photopeople.CalenderActivity;
import com.mobiletemple.photopeople.CameraRental;
import com.mobiletemple.photopeople.EventDetails;
import com.mobiletemple.photopeople.FilteringActivity;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.PhototipWebView;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.UpcomingEventDetails;
import com.mobiletemple.photopeople.ViewAll.FeaturedViewAll;
import com.mobiletemple.photopeople.ViewAll.FreelancerUpcomingEvent;
import com.mobiletemple.photopeople.ViewAll.HotDealsViewall;
import com.mobiletemple.photopeople.ViewAll.InspirationalViewAll;
import com.mobiletemple.photopeople.ViewAll.PhototipsViewAll;
import com.mobiletemple.photopeople.ViewAll.UpcomingEventViewall;
import com.mobiletemple.photopeople.YoutubeVideoView;

import com.mobiletemple.photopeople.adapter.FeaturedHomeAdapter;
import com.mobiletemple.photopeople.adapter.HomeHotDeailsAdapter;
import com.mobiletemple.photopeople.adapter.ImagehomeAdapter;
import com.mobiletemple.photopeople.adapter.PhotoTipsAdapter;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.databinding.FragmentHomeBinding;
import com.mobiletemple.photopeople.model.FilterDTO;
import com.mobiletemple.photopeople.model.Hotdeals;
import com.mobiletemple.photopeople.model.ImageHome;
import com.mobiletemple.photopeople.model.PhotoTips;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class HomeFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    boolean isConnected;


    LocationManager manager;
    String address;
    Geocoder geocoder;
    double latitude;
    double longitude;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private Location mylocation;
    ImagehomeAdapter imagehomeAdapter;
    PhotoTipsAdapter photoTipsAdapter;
    FeaturedHomeAdapter adapter;
    HomeHotDeailsAdapter hotDeailsAdapter;
    RequestQueue requestQueue;
    double lat,lng;

    Intent intent;
    List<ImageHome> imageHomeList;
    List<PhotoTips> photoTipsList;
    ArrayList<Hotdeals> hotdealsList;
    private ArrayList<FilterDTO> freelancerList;
    SessionManager sessionManager;
    String im1, im2, im3, im4, im5, im6, link1, link2, link3, link4, link5, link6, adtype1, adtype2, adtype3, adtype4, adtype5, adtype6, status1, status2, status3, status4, status5, status6;
    String event_type = null, start_date = null, end_date = null, location = null, id = null, shoottype = null, price = null, evntstatus = null, desc;
    String quoteId, freeId, studioId, eventType, shootType, startDate, endDate, quoteAmount, venue, quoteStatus, profile, firstName, lastName, startPrice, studio_name;
    String country;
//    ProgressDialog progressDialog;
    RequestQueue queue;
    int j = 0, k = 2;
    final Random rnd = new Random();

    FragmentHomeBinding binding;
    //CardView ad6LL, ad5LL, ad4LL, ad3LL, ad2LL, ad1LL;
//    ImageView ad6IV, ad5IV, ad4IV, ad3IV, ad2IV, ad1IV, play1, play2, play3, play4, play5, play6;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpGClient();

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        queue = Volley.newRequestQueue(getActivity());
        //progressDialog = new ProgressDialog(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());

        freelancerList = new ArrayList<>();
        imageHomeList = new ArrayList<>();
        photoTipsList = new ArrayList<>();
        hotdealsList = new ArrayList<>();


        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        isConnected = ConnectivityReceiver.isConnected();

        photoTipsAdapter = new PhotoTipsAdapter(getActivity(), photoTipsList);
        imagehomeAdapter = new ImagehomeAdapter(getActivity(), imageHomeList);
        adapter = new FeaturedHomeAdapter(getActivity(), freelancerList);
        hotDeailsAdapter = new HomeHotDeailsAdapter(getActivity(), hotdealsList);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setHasFixedSize(true);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerView);
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.hotdealRecyclerView.setLayoutManager(layoutManager);
        binding.hotdealRecyclerView.setHasFixedSize(true);
        SnapHelper sh = new LinearSnapHelper();
        sh.attachToRecyclerView(binding.hotdealRecyclerView);


        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.picRecyclerView.setLayoutManager(layoutManager1);
        binding.picRecyclerView.setAdapter(imagehomeAdapter);


        LinearLayoutManager lm
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.tipsRecyclerView.setLayoutManager(lm);
        binding.tipsRecyclerView.setAdapter(photoTipsAdapter);

        binding.inspPicviewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InspirationalViewAll.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        binding.featureviewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FeaturedViewAll.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });

        binding.photoTipsviewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhototipsViewAll.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        binding.hotDealsviewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HotDealsViewall.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        binding.upcomeventviewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase("1")) {
                    Intent intent = new Intent(getActivity(), UpcomingEventViewall.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                } else {
                    Intent intent = new Intent(getActivity(), FreelancerUpcomingEvent.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            }
        });


        binding.canPh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.CANDID_PHOTOGRAPHER));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.canVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.CANDID_VIDEOGRAPHER));

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.livePrinting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.LIVE_PRINT));

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.tradPh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.TRADITIONAL_PHOTOGRAPHER));

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.tradVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.TRADITIONAL_VIDEOGRAPHER));

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.helicam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.HELICAM));

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.designer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.DESIGNER));

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.vidEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("freelancertype", String.valueOf(Constants.VIDEO_EDITOR));

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });
        if (sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase("1")) {
            binding.calendar.setVisibility(View.GONE);
        } else {
            binding.calendar.setVisibility(View.VISIBLE);
        }

        binding.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalenderActivity.class);
                intent.putExtra("from", "home");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.camerarentalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openCameraRentalDialog();
                Intent intent=new Intent(getActivity(), CameraRental.class);
                startActivity(intent);
            }
        });

        binding.camerarental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FilteringActivity.class);
                intent.putExtra("from", "home");
                intent.putExtra("freelancertype", "camerarental");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        String imgName = "hotdeal_" + rnd.nextInt(4);
        int id = getResources().getIdentifier(imgName, "drawable", getActivity().getPackageName());
//        Glide.with(getActivity()).load(id).placeholder(R.drawable.default_gallery_dash)
//                .error(R.drawable.default_gallery_dash)
//
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(binding.image);

        Picasso.with(getActivity()).load(id).fit().centerCrop().placeholder(R.color.black).error(R.color.black).into(binding.image, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "onSuccess");
            }

            @Override
            public void onError() {
                Toast.makeText(getActivity().getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        if (isConnected) {
            binding.linearlayout.setVisibility(View.VISIBLE);
            inpirationalPhotographs();
            photoTips();
            hotDeals();
            loadAds();


            if (sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase("1")) {
                loadStudioEvent();
            } else {
                waitingJobFreelancer();
            }
        }


        binding.seemorell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.horizontalsc.fullScroll(ScrollView.FOCUS_RIGHT);

            }
        });

        binding.ad6LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link6.isEmpty()) {

                    if (link6.startsWith("https://www.youtube.com") || link6.startsWith("http://www.youtube.com")) {
                        Intent intent = new Intent(getActivity(), YoutubeVideoView.class);
                        intent.putExtra("video", link6);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), PhototipWebView.class);
                        intent.putExtra("url", link6);
                        startActivity(intent);
                    }

                }
            }
        });

        binding.ad5LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!link5.isEmpty()) {
                    if (link5.startsWith("https://www.youtube.com") || link5.startsWith("http://www.youtube.com")) {

                        Intent intent = new Intent(getActivity(), YoutubeVideoView.class);
                        intent.putExtra("video", link5);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), PhototipWebView.class);
                        intent.putExtra("url", link5);
                        startActivity(intent);
                    }
                }
            }
        });

        binding.ad4LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!link4.isEmpty()) {
                    if (link4.startsWith("https://www.youtube.com") || link4.startsWith("http://www.youtube.com")) {
                        Intent intent = new Intent(getActivity(), YoutubeVideoView.class);
                        intent.putExtra("video", link4);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), PhototipWebView.class);
                        intent.putExtra("url", link4);
                        startActivity(intent);
                    }

                }
            }
        });

        binding.ad3LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link3.isEmpty()) {
                    if (link3.startsWith("https://www.youtube.com") || link3.startsWith("http://www.youtube.com")) {

                        Intent intent = new Intent(getActivity(), YoutubeVideoView.class);
                        intent.putExtra("video", link3);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), PhototipWebView.class);
                        intent.putExtra("url", link3);
                        startActivity(intent);
                    }

                }
            }
        });

        binding.ad2LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link2.isEmpty()) {
                    if (link2.startsWith("https://www.youtube.com") || link2.startsWith("http://www.youtube.com")) {

                        Intent intent = new Intent(getActivity(), YoutubeVideoView.class);
                        intent.putExtra("video", link2);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), PhototipWebView.class);
                        intent.putExtra("url", link2);
                        startActivity(intent);
                    }
                }
            }
        });

        binding.ad1LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link1.isEmpty()) {
                    if (link1.startsWith("https://www.youtube.com") || link1.startsWith("http://www.youtube.com")) {

                        Intent intent = new Intent(getActivity(), YoutubeVideoView.class);
                        intent.putExtra("video", link1);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), PhototipWebView.class);
                        intent.putExtra("url", link1);
                        startActivity(intent);
                    }
                }
            }
        });

        animate(binding.ll);


        return view;
    }

    public void animate(View view) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        animation.setDuration(1000);
        view.setAnimation(animation);
        view.animate();
        animation.start();
    }


    @Override
    public void onStart() {

        googleApiClient.connect();
        getMyLocation();


        super.onStart();
    }


    private void inpirationalPhotographs() {
//        progressDialog.setMessage("Loading Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.HOMEPAGE_INPIRATIONAL_PHOTO, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                Log.e("inpirationalPhotographs", response);
                try {
                    imageHomeList.clear();
                    //progressDialog.dismiss();

                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray inspiretion_photograph_list = obj.getJSONArray("inspiretion_photograph_list");

                        for (int i = 0; i < inspiretion_photograph_list.length(); i++) {
                            JSONObject inspiretion_photographobj = inspiretion_photograph_list.getJSONObject(i);
                            String id = inspiretion_photographobj.getString("id");
                            String user_id = inspiretion_photographobj.getString("user_id");
                            String image = inspiretion_photographobj.getString("image");
                            String user_type = inspiretion_photographobj.getString("user_type");
                            //Log.e("usertype",user_type);


                            ImageHome imageHome = new ImageHome();
                            imageHome.setId(id);
                            imageHome.setThumbnail(image);
                            imageHome.setUserId(user_id);
                            imageHome.setUserType(user_type);

                            imageHomeList.add(imageHome);


                        }
                        binding.picRecyclerView.setAdapter(imagehomeAdapter);
                    }


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


    private void photoTips() {
//        progressDialog.setMessage("Loading Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.HOMEPAGE_PHOTOTIPS, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                //Log.e("HOMEPAGE_PHOTOTIPS",response);
                try {
                    photoTipsList.clear();
                    //progressDialog.dismiss();

                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray photo_tips_list = obj.getJSONArray("photo_tips_list");

                        for (int i = 0; i < photo_tips_list.length(); i++) {
                            JSONObject photo_tips_listOBJ = photo_tips_list.getJSONObject(i);
                            String id = photo_tips_listOBJ.getString("id");
                            String title = photo_tips_listOBJ.getString("title");
                            String discription = photo_tips_listOBJ.getString("discription");
                            String link = photo_tips_listOBJ.getString("link");
                            PhotoTips photoTips = new PhotoTips();
                            photoTips.setTitle(title);
                            photoTips.setDescription(discription);
                            photoTips.setLink(link);


                            if (j == 0) {
                                photoTips.setBackground(R.drawable.phototipsgrad1);
                            }
                            if (j == 1) {
                                photoTips.setBackground(R.drawable.phototipsgrad2);
                            }
                            if (j == 2) {
                                photoTips.setBackground(R.drawable.phototipsgrad3);
                            }
                            j++;


                            if (k == i) {
                                k = k + 3;
                                j = 0;

                            }

                            photoTipsList.add(photoTips);


                        }
                        binding.tipsRecyclerView.setAdapter(photoTipsAdapter);
                    }


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

    public void featureFreelancer() {
//        progressDialog.setMessage("Loading Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.HOMEPAGE_FEATUREFREE, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                Log.e("HOMEPAGE_PHOTOTIPS", response);
                try {
                    freelancerList.clear();
                   // progressDialog.dismiss();

                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray feturde_freelancer_list = obj.getJSONArray("feturde_freelancer_list");

                        for (int i = 0; i < feturde_freelancer_list.length(); i++) {
                            JSONObject freelancerObj = feturde_freelancer_list.getJSONObject(i);
                            JSONArray camer_listing = obj.getJSONArray("camera");

                            FilterDTO freelancer = new FilterDTO();

                            freelancer.setId(freelancerObj.getString("id"));
                            freelancer.setName(freelancerObj.getString("first_name") + " " + freelancerObj.getString("last_name"));
                            freelancer.setStarting_price(freelancerObj.getString("starting_price"));
                            if (!freelancerObj.getString("travel_by").isEmpty())
                                freelancer.setTravel_by(Integer.parseInt(freelancerObj.getString("travel_by")));
                            freelancer.setDob(freelancerObj.getString("dob"));
                            freelancer.setProfile_image(freelancerObj.getString("profile_image"));
                            freelancer.setRating(freelancerObj.getDouble("freelancer_rating"));
                            String cameraid = freelancerObj.getString("camera_type");
                            for (int j = 0; j < camer_listing.length(); j++) {
                                JSONObject jsonObject = camer_listing.getJSONObject(j);
                                if (cameraid.equalsIgnoreCase(jsonObject.getString("id"))) {
                                    freelancer.setCameratype(jsonObject.getString("camera_name"));
                                }
                            }

                            freelancerList.add(freelancer);

                        }
                        binding.recyclerView.setAdapter(adapter);

                    }


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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("lat", sessionManager.getMyLatLong().get(SessionManager.KEY_MYLATITUDE));
                ob.put("lng", sessionManager.getMyLatLong().get(SessionManager.KEY_MYLONGITUDE));

                Log.e("params", ob.toString());
                return ob;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }


    private void loadAds() {
//        progressDialog.setMessage("Loading Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.HOMEPAGE_LOADADS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("HOMEPAGE_HOTDEALS", response);
                try {
                  //  progressDialog.dismiss();

                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray advertisement_list = obj.getJSONArray("advertisement_list");


                        JSONObject ProductListobj = advertisement_list.getJSONObject(0);
                        im1 = ProductListobj.getString("image");
                        link1 = ProductListobj.getString("link");
                        adtype1 = ProductListobj.getString("type");
                        status1 = ProductListobj.getString("status");

                        Picasso.with(getActivity()).load(im1).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(binding.ad1IV, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "onSuccess");
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getActivity().getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });


                        JSONObject ProductListobj1 = advertisement_list.getJSONObject(1);
                        im2 = ProductListobj1.getString("image");
                        link2 = ProductListobj1.getString("link");
                        adtype2 = ProductListobj1.getString("type");
                        status2 = ProductListobj1.getString("status");


                        Picasso.with(getActivity()).load(im2).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(binding.ad2IV, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "onSuccess");
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getActivity().getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

                        JSONObject ProductListobj2 = advertisement_list.getJSONObject(2);
                        im3 = ProductListobj2.getString("image");
                        link3 = ProductListobj2.getString("link");
                        adtype3 = ProductListobj2.getString("type");
                        status3 = ProductListobj2.getString("status");


                        Picasso.with(getActivity()).load(im3).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(binding.ad3IV, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "onSuccess");
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getActivity().getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                        JSONObject ProductListobj3 = advertisement_list.getJSONObject(3);
                        im4 = ProductListobj3.getString("image");
                        link4 = ProductListobj3.getString("link");
                        adtype4 = ProductListobj3.getString("type");
                        status4 = ProductListobj3.getString("status");


                        Picasso.with(getActivity()).load(im4).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(binding.ad4IV, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "onSuccess");
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getActivity().getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

                        JSONObject ProductListobj4 = advertisement_list.getJSONObject(4);
                        im5 = ProductListobj4.getString("image");
                        link5 = ProductListobj4.getString("link");
                        adtype5 = ProductListobj4.getString("type");
                        status5 = ProductListobj4.getString("status");

                        Picasso.with(getActivity()).load(im5).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(binding.ad5IV, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "onSuccess");
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getActivity().getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });


                        JSONObject ProductListobj5 = advertisement_list.getJSONObject(5);
                        im6 = ProductListobj5.getString("image");
                        link6 = ProductListobj5.getString("link");
                        adtype6 = ProductListobj5.getString("type");
                        status6 = ProductListobj5.getString("status");

                        Picasso.with(getActivity()).load(im6).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(binding.ad6IV, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "onSuccess");
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getActivity().getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });


                        if (adtype1.equalsIgnoreCase("Link")) {
                            if (link1.startsWith("https://www.youtube.com") || link1.startsWith("http://www.youtube.com")) {

                                binding.play1.setVisibility(View.VISIBLE);
                            } else {
                                binding.play1.setVisibility(View.GONE);

                            }

                        } else {
                            binding.play1.setVisibility(View.GONE);

                        }


                        if (adtype2.equalsIgnoreCase("Link")) {
                            if (link2.startsWith("https://www.youtube.com") || link2.startsWith("http://www.youtube.com")) {

                                binding.play2.setVisibility(View.VISIBLE);
                            } else {
                                binding.play2.setVisibility(View.GONE);

                            }
                        } else {
                            binding.play2.setVisibility(View.GONE);

                        }

                        if (adtype3.equalsIgnoreCase("Link")) {
                            if (link3.startsWith("https://www.youtube.com") || link3.startsWith("http://www.youtube.com")) {

                                binding.play3.setVisibility(View.VISIBLE);
                            } else {
                                binding.play3.setVisibility(View.GONE);

                            }
                        } else {
                            binding.play3.setVisibility(View.GONE);

                        }
                        if (adtype4.equalsIgnoreCase("Link")) {
                            if (link4.startsWith("https://www.youtube.com") || link4.startsWith("http://www.youtube.com")) {

                                binding.play4.setVisibility(View.VISIBLE);
                            } else {
                                binding.play4.setVisibility(View.GONE);

                            }
                        } else {
                            binding.play4.setVisibility(View.GONE);

                        }

                        if (adtype5.equalsIgnoreCase("Link")) {
                            if (link5.startsWith("https://www.youtube.com") || link5.startsWith("http://www.youtube.com")) {

                                binding.play5.setVisibility(View.VISIBLE);
                            } else {
                                binding.play5.setVisibility(View.GONE);

                            }
                        } else {
                            binding.play5.setVisibility(View.GONE);

                        }

                        if (adtype6.equalsIgnoreCase("Link")) {
                            if (link6.startsWith("https://www.youtube.com") || link6.startsWith("http://www.youtube.com")) {

                                binding.play6.setVisibility(View.VISIBLE);
                            } else {
                                binding.play6.setVisibility(View.GONE);

                            }
                        } else {
                            binding.play6.setVisibility(View.GONE);

                        }


                        if (status1.equalsIgnoreCase("0")) {
                            binding.ad1LL.setVisibility(View.GONE);
                        } else {
                            binding.ad1LL.setVisibility(View.VISIBLE);

                        }

                        if (status2.equalsIgnoreCase("0")) {
                            binding.ad2LL.setVisibility(View.GONE);
                        } else {
                            binding.ad2LL.setVisibility(View.VISIBLE);

                        }
                        if (status3.equalsIgnoreCase("0")) {
                            binding.ad3LL.setVisibility(View.GONE);
                        } else {
                            binding.ad3LL.setVisibility(View.VISIBLE);

                        }
                        if (status4.equalsIgnoreCase("0")) {
                            binding.ad4LL.setVisibility(View.GONE);
                        } else {
                            binding.ad4LL.setVisibility(View.VISIBLE);

                        }

                        if (status5.equalsIgnoreCase("0")) {
                            binding.ad5LL.setVisibility(View.GONE);
                        } else {
                            binding.ad5LL.setVisibility(View.VISIBLE);

                        }

                        if (status6.equalsIgnoreCase("0")) {
                            binding.ad6LL.setVisibility(View.GONE);
                        } else {
                            binding.ad6LL.setVisibility(View.VISIBLE);

                        }

                    }


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

    private void hotDeals() {
//        progressDialog.setMessage("Loading Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.HOMEPAGE_HOTDEALS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.e("HOMEPAGE_HOTDEALS",response);
                try {
                    hotdealsList.clear();
                    //progressDialog.dismiss();

                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray ProductList = obj.getJSONArray("ProductList");

                        for (int i = 0; i < ProductList.length(); i++) {
                            JSONObject ProductListobj = ProductList.getJSONObject(i);
                            String product_id = ProductListobj.getString("product_id");
                            String product_condition = ProductListobj.getString("product_condition");
                            String product_title = ProductListobj.getString("product_title");
                            String product_discription = ProductListobj.getString("product_discription");
                            String product_image = ProductListobj.getString("product_image");
                            String username = ProductListobj.getString("username");
                            String user_type = ProductListobj.getString("user_type");
                            String price = ProductListobj.getString("product_price");
                            Hotdeals hotdeals = new Hotdeals();
                            hotdeals.setdiscountmsg(product_title);
                            hotdeals.settermscondition(product_discription);
                            hotdeals.setstudioname(username);
                            hotdeals.setThumbnail(product_image);
                            hotdeals.setProdCondition(product_condition);
                            hotdeals.setprodId(product_id);
                            hotdeals.setPrice(price);


                            hotdealsList.add(hotdeals);
                            hotDeailsAdapter.notifyDataSetChanged();

                        }
                        binding.hotdealRecyclerView.setAdapter(hotDeailsAdapter);
                    }


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






    private void loadStudioEvent()

    {
//        dialog.setMessage("Loading Please Wait...");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.VIEWALL_UPCOMINGEVENT,
                new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                Log.e("VIEWALL_UPCOMINGEVENT",response);
                try {
                    // dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status=obj.getInt("status");
                    String message=obj.getString("message");
                    if (status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONArray ProductList=obj.getJSONArray("data");

                        for (int i=0;i<ProductList.length();i++)
                        {
                            JSONObject ProductListobj = ProductList.getJSONObject(0);
                            final String event_type=ProductListobj.getString("event_name");
                            final String location=ProductListobj.getString("location");
                            final String start_date=ProductListobj.getString("start_date");
                            final String end_date=ProductListobj.getString("end_date");
                            //String shoottype=ProductListobj.getString("type");
                            final String price=ProductListobj.getString("budget");
                            final String desc=ProductListobj.getString("description");
                            final String user_name=ProductListobj.getString("user_name");
                            final String mobile_no=ProductListobj.getString("mobile_no");


                            binding.cardview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), UpcomingEventDetails.class);
                                    intent.putExtra("eventtype", event_type);
                                    intent.putExtra("price", price);
                                    intent.putExtra("startdate", start_date);
                                    intent.putExtra("enddate", end_date);
                                    intent.putExtra("location", location);
                                    intent.putExtra("description", desc);
                                    intent.putExtra("username",user_name);
                                    intent.putExtra("userphone",mobile_no);

                                    startActivity(intent);

                                }
                            });
                            switch (event_type) {
                                case "1":
                                    binding.eventimage.setImageResource(R.drawable.birthday_default);
                                    binding.eventtype.setText("Birthday");
                                    break;
                                case "2":
                                    binding.eventimage.setImageResource(R.drawable.wedding);
                                    binding.eventtype.setText("Wedding");
                                    break;
                                case "3":
                                    binding.eventimage.setImageResource(R.drawable.pre_wedding);
                                    binding.eventtype.setText("Pre Wedding");
                                    break;
                                case "4":
                                    binding.eventimage.setImageResource(R.drawable.engagement);
                                    binding.eventtype.setText("Engagement");
                                    break;
                                case "5":
                                    binding.eventimage.setImageResource(R.drawable.maternity);
                                    binding.eventtype.setText("Maternity");
                                    break;
                                case "6":
                                    binding.eventimage.setImageResource(R.drawable.kids);
                                    binding.eventtype.setText("Kids");
                                    break;
                            }

                            binding.date.setText(start_date + " to " + end_date);
                            binding.place.setText(location);

                        }

                    }
                    else {
                        binding.eventtype.setText("Hello," + " " + sessionManager.getLoginSession().get(SessionManager.KEY_NAME));
                        binding.date.setText("Welcome to Photo People");
                        binding.placeiv.setVisibility(View.GONE);
                    }


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
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("offset","0");



                Log.e("params", ob.toString());
                return ob;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }





    private void waitingJobFreelancer() {
//        progressDialog.setMessage("Please Wait..");
//        progressDialog.setCancelable(true);
//        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PROCESS_FREELANCER_JOB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //
                        //
                        // progressDialog.dismiss();

                        //Log.e("PROCESS_FREELANCER_JOB", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            final int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success")) {
                                binding.placeiv.setVisibility(View.VISIBLE);
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++) {
                                    JSONObject dataJSONObject = data.getJSONObject(0);
                                    quoteId = dataJSONObject.getString("id");
                                    freeId = dataJSONObject.getString("freelancer_id");
                                    studioId = dataJSONObject.getString("studio_id");
                                    eventType = dataJSONObject.getString("event_type");
                                    shootType = dataJSONObject.getString("type");

                                    startDate = dataJSONObject.getString("start_date");
                                    endDate = dataJSONObject.getString("end_date");

                                    quoteAmount = dataJSONObject.getString("amount");

                                    venue = dataJSONObject.getString("venue");
                                    location = dataJSONObject.getString("location");

                                    quoteStatus = dataJSONObject.getString("status");
                                    profile = dataJSONObject.getString("profile_image");
                                    firstName = dataJSONObject.getString("first_name");
                                    lastName = dataJSONObject.getString("last_name");

                                    startPrice = dataJSONObject.getString("starting_price");
                                    studio_name = dataJSONObject.getString("studio_name");
                                    desc = dataJSONObject.getString("description");


                                }
                                switch (eventType) {
                                    case "1":
                                        binding.eventimage.setImageResource(R.drawable.birthday_default);
                                        binding.eventtype.setText("Birthday");
                                        break;
                                    case "2":
                                        binding.eventimage.setImageResource(R.drawable.wedding);
                                        binding.eventtype.setText("Wedding");
                                        break;
                                    case "3":
                                        binding.eventimage.setImageResource(R.drawable.pre_wedding);
                                        binding.eventtype.setText("Pre Wedding");
                                        break;
                                    case "4":
                                        binding.eventimage.setImageResource(R.drawable.engagement);
                                        binding.eventtype.setText("Engagement");
                                        break;
                                    case "5":
                                        binding.eventimage.setImageResource(R.drawable.maternity);
                                        binding.eventtype.setText("Maternity");
                                        break;
                                    case "6":
                                        binding.eventimage.setImageResource(R.drawable.kids);
                                        binding.eventtype.setText("Kids");
                                        break;
                                }

                                binding.date.setText(startDate + " to " + endDate);
                                binding.place.setText(location);

                                binding.cardview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), EventDetails.class);
                                        intent.putExtra("eventtype", eventType);
                                        intent.putExtra("shoottype", shootType);
                                        intent.putExtra("price", quoteAmount);
                                        intent.putExtra("startdate", startDate);
                                        intent.putExtra("enddate", endDate);
                                        intent.putExtra("location", location);
                                        intent.putExtra("description", desc);
                                        intent.putExtra("status", quoteStatus);
                                        intent.putExtra("freelancerId", freeId);
                                        intent.putExtra("quoteid", quoteId);
                                        intent.putExtra("studioID", studioId);
                                        intent.putExtra("from", "home");

                                        startActivity(intent);

                                    }
                                });

                            } else {
                                binding.eventtype.setText("Hello," + " " + sessionManager.getLoginSession().get(SessionManager.KEY_NAME));
                                binding.date.setText("Welcome to Photo People");

                                binding.placeiv.setVisibility(View.GONE);
                            }

                        } catch (Exception ex) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                       // progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("freelancerid", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }


    private synchronized void setUpGClient() {

        googleApiClient = new GoogleApiClient.Builder(getActivity().getBaseContext())
                .enableAutoManage(getActivity(), 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            longitude = mylocation.getLongitude();


            featureFreelancer();

            Log.e("latlng", latitude + " " + +longitude);

            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 4); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                address = addresses.get(0).getCountryName(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                sessionManager.setMyLatLong(String.valueOf(latitude), String.valueOf(longitude), String.valueOf(address));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void getMyLocation() {
        if (googleApiClient != null) {
            Log.e("connect", googleApiClient.isConnected() + "");
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(getActivity(),
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);


                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;

                                case ConnectionResult.SERVICE_MISSING:
                                    Toast.makeText(getActivity(), "Google Play Services Missing", Toast.LENGTH_SHORT).show();
                                    break;
                                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                                    //The user has to update play services
                                    Toast.makeText(getActivity(), "Update Google Play Services", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.with(getActivity().getApplicationContext()).pauseRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }
}
