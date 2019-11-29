package com.mobiletemple.photopeople.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.Freelancer.FreelancerImageDialog;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.YoutubeVideoView;
import com.mobiletemple.photopeople.databinding.ItemTrendingtimelineBinding;
import com.mobiletemple.photopeople.model.TrendingTimelineDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.TimeConversion;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrendingTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<TrendingTimelineDTO> timelineDTOS;
    private int mSelectedItemPosition = -1;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    ItemTrendingtimelineBinding viewDataBinding;
    String videoId;
    private TimeConversion timeConversion;
    FreelancerImageDialog freelancerImageDialog = new FreelancerImageDialog();
//    private static final int DEFAULT_VIEW_TYPE = 1;
//    private static final int NATIVE_AD_VIEW_TYPE = 2;

    public TrendingTimelineAdapter(Context mContext, ArrayList<TrendingTimelineDTO> timelineDTOS) {
        this.mContext = mContext;
        this.timelineDTOS = timelineDTOS;
        sessionManager = new SessionManager(mContext.getApplicationContext());
        requestQueue = Volley.newRequestQueue(mContext);
        progressDialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        timeConversion = new TimeConversion();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      //  switch (viewType) {
//            default:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_trendingtimeline, parent, false);
                return new MyViewHolder(viewDataBinding);


//            case NATIVE_AD_VIEW_TYPE:
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.item_admob, parent, false);
//
//                return new NativeAdViewHolder(view);


   //     }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {

//        if (!(h instanceof MyViewHolder)) {
//            return;
//        }
        final MyViewHolder holder = (MyViewHolder) h;
        final TrendingTimelineDTO timelineDTO = timelineDTOS.get(position);

        holder.binding.likecount.setText("(" + timelineDTO.getLikecount() + ")");


        if (timelineDTO.getPostlikestatus().equalsIgnoreCase("0")) {
            timelineDTO.setLikeSelected(false);
            holder.binding.likeImage.setImageResource(R.mipmap.likeemptywhite);
        } else {
            timelineDTO.setLikeSelected(true);
            holder.binding.likeImage.setImageResource(R.drawable.likebutton);
        }

        holder.binding.likeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timelineDTO.getPostlikestatus().equalsIgnoreCase("0")) {
                    timelineDTO.setPostlikestatus("1");
                    int c = Integer.parseInt(timelineDTO.getLikecount()) + 1;
                    holder.binding.likeImage.setImageResource(R.drawable.likebutton);
                    timelineDTO.setLikecount("" + c);
                    notifyDataSetChanged();
                    likeAdd(timelineDTO.getPostid(), mContext);
                }

            }
        });


        holder.binding.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (timelineDTO.getPosttype().equalsIgnoreCase("Image")) {
                    openImage(timelineDTO.getPostimage());

                } else {

                    Intent intent = new Intent(mContext, YoutubeVideoView.class);
                    intent.putExtra("video", timelineDTO.getVideolink());
                    mContext.startActivity(intent);

                }

            }
        });




        if (timelineDTO.getPosttype().equalsIgnoreCase("Image")) {
            Picasso.with(mContext).load(timelineDTO.getPostimage()).fit().centerCrop().placeholder(R.color.black).
                    error(R.color.black).into(holder.binding.postimage);
            holder.binding.playbutton.setVisibility(View.GONE);
        } else {
            String videoUrl = timelineDTO.getVideolink().toString();
            holder.binding.playbutton.setVisibility(View.VISIBLE);


            //   try {
            videoId = getYoutubeVideoIdFromUrl(videoUrl);

            Log.e("VideoId is->", "" + videoId);

            String img_url = "http://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg"; // this is link which will give u thumnail image of that video
            Picasso.with(mContext)
                    .load(img_url)
                    .placeholder(R.color.gray)
                    .error(R.color.gray)
                    .into(holder.binding.postimage);


        }





    }

    private void openImage(String postimage) {

        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        dialog.setContentView(R.layout.activity_freelancer_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        ImageView selectedImage = (ImageView) dialog.findViewById(R.id.selectedImage); // init a ImageView
        ImageView cross = dialog.findViewById(R.id.cross);
        ImageView delete = dialog.findViewById(R.id.delete);
        delete.setVisibility(View.GONE);
        Picasso.with(mContext).load(postimage).placeholder(R.color.gray).into(selectedImage);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }


//    public class NativeAdViewHolder extends RecyclerView.ViewHolder {
//        private AdView mAdView;
//
//        public NativeAdViewHolder(View view) {
//
//            super(view);
//
//
//            MobileAds.initialize(mContext, "ca-app-pub-1234961524965105~5671037383");
//
//
//            mAdView = itemView.findViewById(R.id.adView);
//            AdRequest adRequest = new AdRequest.Builder().build();
//
//            mAdView.loadAd(adRequest);
//
//
//            mAdView.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    // Code to be executed when an ad finishes loading.
//
//
//                }
//
//                @Override
//                public void onAdFailedToLoad(int errorCode) {
//                    // Code to be executed when an ad request fails.
//
//                }
//
//                @Override
//                public void onAdOpened() {
//                    // Code to be executed when an ad opens an overlay that
//                    // covers the screen.
//
//                }
//
//                @Override
//                public void onAdLeftApplication() {
//                    // Code to be executed when the user has left the app.
//
//                }
//
//                @Override
//                public void onAdClosed() {
//                    // Code to be executed when when the user is about to return
//                    // to the app after tapping on an ad.
//
//                }
//            });
//        }
//    }

    public static String getYoutubeVideoIdFromUrl(String inUrl) {
        inUrl = inUrl.replace("&feature=youtu.be", "");
        if (inUrl.toLowerCase().contains("youtu.be")) {
            return inUrl.substring(inUrl.lastIndexOf("/") + 1);
        }
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(inUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }






    private void likeAdd(final String postid, Context mContext) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.TIMELINEPOST_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("response", response);

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String msg = obj.getString("Message");
                            String data = obj.getString("Data");
                            if (status == 200 && msg.equalsIgnoreCase("success")) {


                            }

                        } catch (Exception ex) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //  dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("post_id", postid);
                params.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));
                params.put("user_type", sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));

                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    @Override
    public int getItemCount() {

        return timelineDTOS != null ? timelineDTOS.size() : 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemTrendingtimelineBinding binding;

        public MyViewHolder(ItemTrendingtimelineBinding view) {
            super(view.getRoot());
            binding = view;

        }


    }
}