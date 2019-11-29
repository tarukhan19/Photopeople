package com.mobiletemple.photopeople.TimeLine;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiletemple.photopeople.adapter.TimelineAdapter;
import com.mobiletemple.photopeople.adapter.TrendingTimelineAdapter;
import com.mobiletemple.photopeople.model.TimelineDTO;
import com.mobiletemple.photopeople.model.TrendingTimelineDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimelineApi {
    public void loadList(RequestQueue requestQueue, final String offsetString, final SessionManager session, final ArrayList<TimelineDTO> list,
                         final TimelineAdapter adapter, final RecyclerView recyclerStory)
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.TIMELINEPOST_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //  dialog.cancel();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success"))
                            {

                                recyclerStory.setVisibility(View.VISIBLE);
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++)
                                {

                                    Log.e("arraylenght",data.length()+"");
                                    final JSONObject dataJSONObject = data.getJSONObject(x);
                                    TimelineDTO timelineDTO = new TimelineDTO();
                                    timelineDTO.setPostid(dataJSONObject.getString("post_id"));
                                    timelineDTO.setUserid(dataJSONObject.getString("user_id"));
                                    timelineDTO.setUsername(dataJSONObject.getString("username"));
                                    timelineDTO.setPosttype(dataJSONObject.getString("post_type"));
                                    timelineDTO.setPostimage(dataJSONObject.getString("post_image"));
                                    timelineDTO.setPostdate(dataJSONObject.getString("created_utc"));
                                    timelineDTO.setReportabusestatus(dataJSONObject.getString("report_abuse_status"));
                                    timelineDTO.setPostlikestatus(dataJSONObject.getString("post_like_status"));
                                    timelineDTO.setLikecount(dataJSONObject.getString("total_likes"));
                                    timelineDTO.setTrendingstatus(dataJSONObject.getString("trending_status"));
                                    timelineDTO.setUserimage(dataJSONObject.getString("profile_image"));
                                    timelineDTO.setVideolink(dataJSONObject.getString("post_link"));
                                    timelineDTO.setDistance(dataJSONObject.getString("distance"));
                                    timelineDTO.setUsertype(dataJSONObject.getString("user_type"));

                                    if(x%5==0)
                                    {
                                        list.add(null);
                                    }
                                    list.add(timelineDTO);


                                }
                                adapter.notifyDataSetChanged();

                            } else if (status == 0 && message.equalsIgnoreCase("failed")) {


                            } else if (status == 0 && message.equalsIgnoreCase("Record Not Found")) {
                            }

                        } catch (Exception ex) {

                            Log.e("exception",ex.getMessage());
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
                //user_id & offset  & buket_id
                params.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                params.put("offset", offsetString);
                params.put("latitude", session.getMyLatLong().get(SessionManager.KEY_MYLATITUDE));
                params.put("longitude", session.getMyLatLong().get(SessionManager.KEY_MYLONGITUDE));

                Log.e("timelineparams", params + "");

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }

    public void loadTrendingList(RequestQueue requestQueue, final String trendingoffsetString, final SessionManager session,
                                 final ArrayList<TrendingTimelineDTO> trendingTimelineDTOS, final TrendingTimelineAdapter trendingTimelineAdapter, final RecyclerView trendingpostRV, final LinearLayout sliderImgLL) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.TRENDING_TIMELINE,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TRENDING_TIMELINE", response + "");


                        try {

                            //  dialog.cancel();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success"))
                            {

                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++)
                                {
                                    sliderImgLL.setVisibility(View.VISIBLE);
                                    trendingpostRV.setVisibility(View.VISIBLE);

                                    Log.e("arraylenght",data.length()+"");
                                    final JSONObject dataJSONObject = data.getJSONObject(x);
                                    TrendingTimelineDTO timelineDTO = new TrendingTimelineDTO();
                                    timelineDTO.setPostid(dataJSONObject.getString("post_id"));
                                    timelineDTO.setUserid(dataJSONObject.getString("user_id"));
                                    timelineDTO.setUsername(dataJSONObject.getString("username"));
                                    timelineDTO.setPosttype(dataJSONObject.getString("post_type"));
                                    timelineDTO.setPostimage(dataJSONObject.getString("post_image"));
                                    timelineDTO.setPostdate(dataJSONObject.getString("created_utc"));
                                    timelineDTO.setReportabusestatus(dataJSONObject.getString("report_abuse_status"));
                                    timelineDTO.setPostlikestatus(dataJSONObject.getString("post_like_status"));
                                    timelineDTO.setLikecount(dataJSONObject.getString("total_likes"));
                                   // timelineDTO.setTrendingstatus(dataJSONObject.getString("trending_status"));
                                    timelineDTO.setUserimage(dataJSONObject.getString("profile_image"));
                                    timelineDTO.setVideolink(dataJSONObject.getString("post_link"));
                                    timelineDTO.setDistance(dataJSONObject.getString("distance"));
                                    timelineDTO.setUsertype(dataJSONObject.getString("user_type"));


                                    trendingTimelineDTOS.add(timelineDTO);


                                }
                                trendingTimelineAdapter.notifyDataSetChanged();

                            } else if (status == 0 && message.equalsIgnoreCase("failed")) {


                            } else if (status == 0 && message.equalsIgnoreCase("Record Not Found")) {
                            }

                        } catch (Exception ex) {

                            Log.e("exception",ex.getMessage());
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
                //user_id & offset  & buket_id
                params.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                params.put("offset", trendingoffsetString);
                params.put("latitude", session.getMyLatLong().get(SessionManager.KEY_MYLATITUDE));
                params.put("longitude", session.getMyLatLong().get(SessionManager.KEY_MYLONGITUDE));

                Log.e("params", params + "");

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);


    }
}
