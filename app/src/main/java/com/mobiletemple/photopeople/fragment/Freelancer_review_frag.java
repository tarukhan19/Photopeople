package com.mobiletemple.photopeople.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.RatingDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Freelancer_review_frag extends Fragment  {
RecyclerView recyclerView;
ArrayList<RatingDTO> ratingDTOArrayList;
    ProgressDialog dialog;
    RequestQueue queue;
    Intent intent;
    SessionManager sessionManager;
    ReviewAdapter reviewAdapter;
    String freelancerId,from;
    boolean isConnected;
    Uri deepLink;
    public Freelancer_review_frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freelancer_review_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        intent = getActivity().getIntent();

        recyclerView=view.findViewById(R.id.recyclerView);
        ratingDTOArrayList=new ArrayList<>();
        reviewAdapter = new ReviewAdapter(getActivity(), ratingDTOArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(reviewAdapter);

        freelancerId=intent.getStringExtra("freelancerId");
        from=intent.getStringExtra("from");
        sessionManager = new SessionManager(getActivity());


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
                            loadRating();
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


    }




    private void loadRating()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.RATING_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ratingDTOArrayList.clear();
                        dialog.dismiss();
                        //Log.e("RATING_LIST", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                            JSONArray jsonArray=obj.getJSONArray("data");

                            for (int i=0;i<jsonArray.length();i++)
                            {
                                RatingDTO ratingDTO=new RatingDTO();
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                ratingDTO.setName(jsonObject.getString("first_name"));
                                ratingDTO.setPic(jsonObject.getString("sen_user_profile"));
                                ratingDTO.setRating((float)jsonObject.getDouble("rating"));
                                ratingDTO.setReview(jsonObject.getString("review"));
                                ratingDTOArrayList.add(ratingDTO);

                            }
                            reviewAdapter.notifyDataSetChanged();



                            }
                            else
                            {

                            }
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
                params.put("user_id",freelancerId );

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }




    public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
        private Context mContext;
        private ArrayList<RatingDTO> hired_free_declineDTOS;
        private int mSelectedItemPosition=-1;
        String enddateS,startdateS;
        public ReviewAdapter(Context mContext, ArrayList<RatingDTO> hired_free_declineDTOS)
        {
            this.mContext = mContext;
            this.hired_free_declineDTOS = hired_free_declineDTOS;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rating, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

            final RatingDTO freelancer = hired_free_declineDTOS.get(position);

            holder.name.setText(freelancer.getName());
            holder.review.setText(freelancer.getReview());
            holder.ratingBar.setRating((float)freelancer.getRating());
            if (!freelancer.getPic().isEmpty())
            {
                Glide.with(mContext).load(freelancer.getPic()).centerCrop().placeholder(R.drawable.default_new_img)
                        .error(R.drawable.default_new_img)

                        .into(holder.iv_pic);
            }
            else {  Glide.with(mContext).load(R.drawable.default_new_img).centerCrop().placeholder(R.drawable.default_new_img)
                    .error(R.drawable.default_new_img)

                    .into(holder.iv_pic);}

        }


        @Override
        public int getItemCount() {
            return hired_free_declineDTOS != null ? hired_free_declineDTOS.size() :0;}


        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_pic;
            public TextView name, review;
            RatingBar ratingBar;


            public MyViewHolder(View view) {
                super(view);
                iv_pic = view.findViewById(R.id.iv_pic);
                name = (TextView) view.findViewById(R.id.name);
                review = (TextView) view.findViewById(R.id.review);
                ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

            }
        }
    }

    @Override
    public void onStart() {
        loadRating();
        super.onStart();
    }



}
