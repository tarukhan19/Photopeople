package com.mobiletemple.photopeople.Studio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mobiletemple.AGVRecyclerViewAdapter;
import com.mobiletemple.AsymmetricItem;
import com.mobiletemple.AsymmetricRecyclerView;
import com.mobiletemple.AsymmetricRecyclerViewAdapter;
import com.mobiletemple.Utils;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Freelancer.FreelancerImageDialog;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.DemoItem;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudioFullGallery extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    ArrayList<DemoItem> demoItems;
    int currentOffset=0;
    AsymmetricRecyclerView picrecyclerView;
    ProgressDialog dialog;
    RequestQueue queue;
    RecyclerViewAdapter adapter;
    String studioId;
    Intent intent;
    SessionManager sessionManager;
    String from;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_full_gallery);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Gallery");
        filter.setVisibility(View.GONE);
        sessionManager = new SessionManager(getApplicationContext());
        picrecyclerView = (AsymmetricRecyclerView) findViewById(R.id.picrecyclerView);

        demoItems=new ArrayList<>();
        adapter = new RecyclerViewAdapter(demoItems,this);
        picrecyclerView.setRequestedColumnCount(3);
        picrecyclerView.setDebugging(true);
        picrecyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(this,3));
        picrecyclerView.addItemDecoration(
                new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_padding)));

        picrecyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, picrecyclerView, adapter));

        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        intent=getIntent();
        studioId=intent.getStringExtra("studioId");
        from=intent.getStringExtra("from");

        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (from.equalsIgnoreCase("profile"))
                {
                    Intent intent=new Intent(StudioFullGallery.this,HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","profile");

                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }

                else if (from.equalsIgnoreCase("homepage"))
                {
                    Intent intent=new Intent(StudioFullGallery.this,HomePage.class);
                    intent.putExtra("from","other");

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });


    }

    private void loadStudio()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.STUDIO_VIDIMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        demoItems.clear();
                        dialog.dismiss();
                        //Log.e("loadStudio", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("satatus");
                            String message = obj.getString("Message");
                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {

                                JSONArray exp_image=obj.getJSONArray("exp_image");
                                JSONArray exp_video=obj.getJSONArray("exp_video");

                                for (int i=0;i<exp_image.length();i++)
                                {
                                    int colSpan = Math.random() < 0.2f ? 2 : 1;
                                    int rowSpan = colSpan;
                                    String image=exp_image.getString(i);
                                    //Log.e("currentOffset",Math.random()+" "+" "+rowSpan+" "+colSpan);

                                    DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
                                    item.setThumbnail(image);
                                    demoItems.add(item);

                                    adapter.notifyDataSetChanged();
                                    picrecyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(StudioFullGallery.this, picrecyclerView, adapter));


                                }




                            } else{

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
                params.put("user_id", studioId);
                params.put("user_type", "1");

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }



    class RecyclerViewAdapter extends AGVRecyclerViewAdapter<RecyclerViewAdapter.ViewHolder>
    {
        private final ArrayList<DemoItem> items;
        Context context;
        FreelancerImageDialog freelancerImageDialog=new FreelancerImageDialog();
        RecyclerViewAdapter(ArrayList<DemoItem> items,Context context) {
            this.items = items;
            this.context=context;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Log.e("RecyclerViewActivity", "onCreateView");
            return new ViewHolder(parent);
        }

        @Override public void onBindViewHolder(ViewHolder holder, final int position) {
            //Log.e("RecyclerViewActivity", "onBindView position=" + position);
            holder.bind(items.get(position));

            holder.thumnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent=getIntent();
                    intent.putExtra("image",items.get(position).getThumbnail());
                    freelancerImageDialog.show(getFragmentManager(), "");
                }
            });
        }

        @Override public int getItemCount() {
            return items.size();
        }

        @Override public AsymmetricItem getItem(int position) {
            return items.get(position);
        }

        @Override public int getItemViewType(int position) {
            return position % 2 == 0 ? 1 : 0;
        }


        class ViewHolder extends RecyclerView.ViewHolder{
            private final ImageView thumnail;


            ViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_freedetailimage , parent, false));
                thumnail = (ImageView) itemView.findViewById(R.id.image);

            }

            void bind(final DemoItem item) {
                final String image=item.getThumbnail();
                //Log.e("image",image);


                Picasso.with(StudioFullGallery.this)
                        .load(item.getThumbnail()).placeholder(R.color.gray)
                        .noFade()
                        .into(thumnail);



            }


        }



    }




    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int padding;

        public SpacesItemDecoration(int padding) {
            this.padding = padding;
        }

        @Override public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = padding;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equalsIgnoreCase("profile"))
        {
            Intent intent=new Intent(StudioFullGallery.this,HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","profile");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        }

        else if (from.equalsIgnoreCase("homepage"))
        {
            Intent intent=new Intent(StudioFullGallery.this,HomePage.class);
            intent.putExtra("from","other");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else {loadStudio();}

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
            loadStudio();
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
