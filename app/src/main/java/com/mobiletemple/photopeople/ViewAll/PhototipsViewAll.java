package com.mobiletemple.photopeople.ViewAll;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.PhotoTips;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhototipsViewAll extends AppCompatActivity
{

   // ProgressDialog dialog;
    RequestQueue queue;
    // ViewPager viewPager_featuredfree;
    ImageView image,placeiv;
    int j=0,k=2;
    List<PhotoTips> photoTipsList;
    Phototip_viewall_adapter phototip_viewall_adapter;
    RecyclerView picrecyclerView;
    private LinearLayoutManager mLayoutManager;
    String offsetString;
    int offset=0;
    boolean isConnected;

    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;


    public static final int DISMISS_TIMEOUT = 5000;
  //  SwipyRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phototips_view_all);
        queue = Volley.newRequestQueue(this);
  //      dialog = new ProgressDialog(this);
        photoTipsList = new ArrayList<>();
        picrecyclerView = (RecyclerView) findViewById(R.id.picrecyclerView);
        phototip_viewall_adapter = new Phototip_viewall_adapter(this, photoTipsList);
        isConnected = ConnectivityReceiver.isConnected();

//        swipeRefreshLayout=(SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayout) ;
//        swipeRefreshLayout.setOnRefreshListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Photography Tips");
        filter.setVisibility(View.GONE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhototipsViewAll.this, HomePage.class);
                intent.putExtra("from","other");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });
        phototip_viewall_adapter = new Phototip_viewall_adapter(this, photoTipsList);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        picrecyclerView.setLayoutManager(mLayoutManager);
        picrecyclerView.setHasFixedSize(true);
        picrecyclerView.setAdapter(phototip_viewall_adapter);

        offset = 0;
        offsetString = String.valueOf(offset);
        photoTips();



            picrecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }


                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    visibleItemCount = mLayoutManager.getChildCount();
                    totalitemcount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (dy > 0) //check for scroll down
                    {



                        if (isLoading) {

                            if (totalitemcount > previoustotal) {
                                isLoading = false;
                                previoustotal = totalitemcount;

                            }


                        }

                        if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold))
                        {
                            offset = offset + 1;
                            offsetString = String.valueOf(offset);
                            photoTips();
                            isLoading = true;
                        }

                    }



                }
            });



    }



    private void photoTips()
    {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.HOMEPAGE_PHOTOTIPS, new Response.Listener<String>()
        {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                Log.e("VIEWALL_PHOTOTIPS",response);
                try {
                 //   dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status=obj.getInt("status");
                    String message=obj.getString("message");
                    if (status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONArray photo_tips_list=obj.getJSONArray("photo_tips_list");

                        for (int i=0;i<photo_tips_list.length();i++)
                        {
                            JSONObject photo_tips_listOBJ = photo_tips_list.getJSONObject(i);
                            String id=photo_tips_listOBJ.getString("id");
                            String title=photo_tips_listOBJ.getString("title");
                            String discription=photo_tips_listOBJ.getString("discription");
                            String link=photo_tips_listOBJ.getString("link");
                            PhotoTips photoTips=new PhotoTips();
                            photoTips.setTitle(title);
                            photoTips.setDescription(discription);
                            photoTips.setLink(link);

                            if (j==0)
                            {photoTips.setBackground(R.drawable.phototipsgrad1);}
                            if (j==1)
                            {photoTips.setBackground(R.drawable.phototipsgrad2);}
                            if (j==2)
                            {photoTips.setBackground(R.drawable.phototipsgrad3);}
                            j++;


                            if (k==i)
                            {
                                k=k+3;
                                j=0;

                            }

                            photoTipsList.add(photoTips);

                        }
                        phototip_viewall_adapter.notifyDataSetChanged();
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
        }


        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("offset",offsetString);

                Log.e("params", ob.toString());
                return ob;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(PhototipsViewAll.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }


//
//    @Override
//    public void onClick(View view) {
//        swipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
//
//    }
//
//    @Override
//    public void onRefresh(SwipyRefreshLayoutDirection direction) {
//        //Log.e("MainActivity", "Refresh triggered at "
//                + (direction == SwipyRefreshLayoutDirection.BOTTOM?  "bottom" :"top"));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Hide the refresh after 2sec
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                        offset=offset+1;
//                        offsetString=String.valueOf(offset);
//                        photoTips(offsetString);
//                    }
//                });
//            }
//        }, DISMISS_TIMEOUT);
//    }

}
