package com.mobiletemple.photopeople.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.Freelancer.FreelancerImageDialog;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.Studio.StudioProfileDetail;
import com.mobiletemple.photopeople.TimeLine.TimelineChatActivity;
import com.mobiletemple.photopeople.YoutubeVideoView;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.databinding.ItemTimelineBinding;
import com.mobiletemple.photopeople.model.TimelineDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.DynamiclinkCreate;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.TimeConversion;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<TimelineDTO> timelineDTOS;
    private int mSelectedItemPosition = -1;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    ItemTimelineBinding viewDataBinding;
    String videoId;
    private TimeConversion timeConversion;
    FreelancerImageDialog freelancerImageDialog = new FreelancerImageDialog();

    private static final int DEFAULT_VIEW_TYPE = 1;
    private static final int NATIVE_AD_VIEW_TYPE = 2;

    public TimelineAdapter(Context mContext, ArrayList<TimelineDTO> timelineDTOS) {
        this.mContext = mContext;
        this.timelineDTOS = timelineDTOS;

        sessionManager = new SessionManager(mContext.getApplicationContext());
        requestQueue = Volley.newRequestQueue(mContext);
        progressDialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        timeConversion = new TimeConversion();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        switch (viewType) {
            default:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_timeline, parent, false);
                return new MyViewHolder(viewDataBinding);


            case NATIVE_AD_VIEW_TYPE:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_admob, parent, false);

                return new NativeAdViewHolder(view);


        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {

        if (!(h instanceof MyViewHolder)) {
            return;
        }
        final MyViewHolder holder = (MyViewHolder) h;
        final TimelineDTO timelineDTO = timelineDTOS.get(position);
        holder.binding.name.setText(timelineDTO.getUsername());
        holder.binding.distance.setText("(" + timelineDTO.getDistance() + "Km)");

        if (timelineDTO.getUserid().equalsIgnoreCase(sessionManager.getLoginSession().get(SessionManager.KEY_USERID)))
        {
            holder.binding.chatLL.setVisibility(View.GONE);

        }

        else
        {
            holder.binding.chatLL.setVisibility(View.VISIBLE);

        }

        Picasso.with(mContext).load(timelineDTO.getUserimage()).fit().centerCrop().placeholder(R.mipmap.register_profile_default).
                error(R.mipmap.register_profile_default).into(holder.binding.ivPic);

        holder.binding.likecount.setText("(" + timelineDTO.getLikecount() + ")");

        holder.binding.shareLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timelineDTO.getUsertype().equalsIgnoreCase(Constants.STUDIO_TYPE))
                {
                    DynamiclinkCreate.sharelink("", mContext, "Have a look on " +
                                    timelineDTO.getUsername() + " (Studio) profile.",
                            Endpoints.LOAD_STUDIO_PROFILE + "?studio_id=" + timelineDTO.getUserid(),
                            "https://photopeoplestudiodetail.page.link/");

                }
                else
                {
                    DynamiclinkCreate.sharelink("", mContext, "Have a look on " +
                            timelineDTO.getUsername() + " (Freelancer) profile.",
                            Endpoints.FREELANCE_PERSONAL_DETAIL + "?user_type=2" + "&freelancer_id="
                            + timelineDTO.getUserid(), "https://photopeoplefreelancerdetails.page.link/");
                }


            }
        });


        if (!timelineDTO.getPostdate().isEmpty()) {
            String dateStr = timelineDTO.getPostdate();
            SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = null, sendDate = null;
            String d2 = "";
            try {
                date = (Date) sdf.parse(dateStr);
                //Log.e("date",date+"");

            } catch (ParseException e) {
                e.printStackTrace();
            }


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getDefault());


            try {
                d2 = simpleDateFormat.format(date);
                sendDate = simpleDateFormat.parse(d2);
                //Log.e("sendDate",sendDate+" "+sendDate.toLocaleString());
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            timeConversion.getTimeAgo(position, holder.binding.time, mContext, sendDate);
        }


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

        holder.binding.chatLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, TimelineChatActivity.class);
                intent.putExtra("userid",timelineDTOS.get(position).getUserid());
                intent.putExtra("from","timeline");

                mContext.startActivity(intent);
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

        holder.binding.menubar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopUpMenu(holder.getAdapterPosition(),
                        holder.binding.menubar,
                        timelineDTO.getPostid(),
                        timelineDTO.getReportabusestatus(),
                        timelineDTO.getUserid());

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

        holder.binding.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timelineDTO.getUsertype().equalsIgnoreCase(Constants.FREELANCER_TYPE)) {
                    Intent intent = new Intent(mContext, FreelancerDetails.class);
                    intent.putExtra("freelancerId", timelineDTO.getUserid());
                    intent.putExtra("from", "timeline");

                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, StudioProfileDetail.class);
                    intent.putExtra("studioId", timelineDTO.getUserid());
                    intent.putExtra("from", "timeline");

                    mContext.startActivity(intent);
                }
            }
        });

        holder.binding.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timelineDTO.getUsertype().equalsIgnoreCase(Constants.FREELANCER_TYPE)) {
                    Intent intent = new Intent(mContext, FreelancerDetails.class);
                    intent.putExtra("freelancerId", timelineDTO.getUserid());
                    intent.putExtra("from", "timeline");

                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, StudioProfileDetail.class);
                    intent.putExtra("studioId", timelineDTO.getUserid());
                    intent.putExtra("from", "timeline");

                    mContext.startActivity(intent);
                }
            }
        });


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
        if (timelineDTOS.get(position) == null) {
            return NATIVE_AD_VIEW_TYPE;
        }
        return DEFAULT_VIEW_TYPE;
    }


    public class NativeAdViewHolder extends RecyclerView.ViewHolder {
        private AdView mAdView;

        public NativeAdViewHolder(View view) {

            super(view);


            MobileAds.initialize(mContext, "ca-app-pub-1234961524965105~5671037383");


            mAdView = itemView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();

            mAdView.loadAd(adRequest);


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
    }

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


    private void openPopUpMenu(final int adapterPosition, LinearLayout moreoption, final String postid, final String reportabusestatus, String userId) {
        if (userId.equalsIgnoreCase(sessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {

            PopupMenu popup = new PopupMenu(mContext, moreoption);
            //inflating menu from xml resource
            popup.inflate(R.menu.menudel);
            //adding click listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            showDeleteDialog(postid, adapterPosition);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();


        } else {
            PopupMenu popup = new PopupMenu(mContext, moreoption);
            popup.inflate(R.menu.menu_reportabuse);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.reportabuse:
                            if (reportabusestatus.equalsIgnoreCase("0")) {
                                openReportDialog(postid, adapterPosition);
                            } else {
                                openDialog("You have already given your feedback for this post!");
                            }
                            return true;

                        default:
                            return false;
                    }
                }
            });
            //displaying the popup
            popup.show();
        }

    }


    private void openReportDialog(final String postId, final int adapterPosition) {

        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_reportabuse);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();


        LinearLayout ok = dialog.findViewById(R.id.ok);
        LinearLayout cancel = dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final EditText descripationET = dialog.findViewById(R.id.descripationET);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descriptionS = descripationET.getText().toString();
                if (descriptionS.isEmpty()) {
                    openDialog("Please enter the reason for report abuse");
                } else {
                    reportAbuse(descriptionS, postId, adapterPosition);
                    dialog.dismiss();
                }


            }
        });
    }


    private void openDialog(String s) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msg = dialog.findViewById(R.id.msgTV);

        msg.setText(s);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }


    private void showDeleteDialog(final String postId, final int adapterPosition) {

        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_delete);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout ok = (LinearLayout) dialog.findViewById(R.id.ok);
        LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
        TextView msgTV = (TextView) dialog.findViewById(R.id.msgTV);
        msgTV.setText("You want to delete this post!");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deletePost(postId, adapterPosition);
                dialog.dismiss();


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void reportAbuse(final String descriptionS, final String postId, final int adapterPosition) {
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.TIMELINEPOST_REPORTABUSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        progressDialog.dismiss();
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
//{"status":1,"message":"Thank you for reporting , we will look into this and take neccessary action"}

                            if (status == 1) {
                                timelineDTOS.get(adapterPosition).setReportabusestatus("1");
                                // reportAbuseStatus = "1";
                                notifyDataSetChanged();
                                openDialog("Thank you for reporting us, we will look into this and take necessary action.");

                            } else if (status == 0 && message.equals("Failed")) {
                                notifyDataSetChanged();
                                openDialog(obj.getString("Data"));

                            }


                        } catch (Exception ex) {
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
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //user_id & post_id & message
                params.put("post_id", postId);
                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type", sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                params.put("message", descriptionS);

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private void deletePost(final String postId, final int adapterPosition) {

        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(true);
        progressDialog.show();
        //Log.e("DetailDialPos", position + "");

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.TIMELINEPOST_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 1) {
                                final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_message);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                dialog.show();

                                LinearLayout ok = dialog.findViewById(R.id.ok);
                                TextView msgTV = dialog.findViewById(R.id.msgTV);

                                msgTV.setText("Post Deleted Successfully");
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        timelineDTOS.remove(adapterPosition);
                                        notifyDataSetChanged();
                                        notifyItemChanged(adapterPosition);


                                    }
                                });
                            }


                        } catch (Exception ex) {
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
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", postId);

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
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
        ItemTimelineBinding binding;

        public MyViewHolder(ItemTimelineBinding view) {
            super(view.getRoot());
            binding = view;

        }


    }
}

