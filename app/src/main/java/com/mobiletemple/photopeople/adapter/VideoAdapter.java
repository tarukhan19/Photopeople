package com.mobiletemple.photopeople.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.YoutubeVideoView;
import com.mobiletemple.photopeople.model.FreedetailvideoDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

/**
 * Created by shree on 5/7/2018.
 */

public class VideoAdapter  extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<FreedetailvideoDTO> albumList;
    Bitmap   pic=null;
    Bitmap bitmap;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    String videoId;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView videopic,delete;
        //TextView text;
        public MyViewHolder(View view) {
            super(view);
            // text=view.findViewById(R.id.text);
            videopic = (ImageView) view.findViewById(R.id.videopic);
            delete=view.findViewById(R.id.delete);
            sessionManager=new SessionManager(mContext);
            progressDialog = new ProgressDialog(mContext);
            requestQueue = Volley.newRequestQueue(mContext);
        }
    }


    public VideoAdapter(Context mContext, ArrayList<FreedetailvideoDTO> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final FreedetailvideoDTO album = albumList.get(position);
        final String videoUrl=album.getVideo();

        videoId = getYoutubeVideoIdFromUrl(videoUrl);

        Log.e("VideoId is->", "" + videoId);

        String img_url = "http://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg"; // this is link which will give u thumnail image of that video
        Picasso.with(mContext)
                .load(img_url)
                .placeholder(R.color.gray)
                .error(R.color.gray)
                .into(holder.videopic);



        holder.videopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String video=album.getVideo();
                Intent intent=new Intent(mContext,YoutubeVideoView.class);
                intent.putExtra("video",video);
                mContext.startActivity(intent);

            }
        });

//        Log.e("albumList.get(position).getUserid()",albumList.get(position).getUserid());


//            if (albumList.get(position).getUserid().equalsIgnoreCase(sessionManager.getLoginSession().get(SessionManager.KEY_USERID))) {
//                holder.delete.setVisibility(View.VISIBLE);
//            } else {
//                holder.delete.setVisibility(View.GONE);
//            }

        Log.e("sessionManager.getLoginSession().get(SessionManager.KEY_USERID)",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteVideo(position);
            }
        });
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

    private void deleteVideo(final int position)
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.DELETEVIDEO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
                            if (status == 200 && message.equalsIgnoreCase("success")) {
                                String object = obj.getString("Data");
                                progressDialog.dismiss();
                                albumList.remove(position);
                                notifyDataSetChanged();


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
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", albumList.get(position).getUserid());
                params.put("user_type", albumList.get(position).getUsertype());
                params.put("video_id", albumList.get(position).getVideoid());
//user_id, & user_type, & video_id,
                Log.e("params", params.toString());
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
        return albumList.size();
    }
}
