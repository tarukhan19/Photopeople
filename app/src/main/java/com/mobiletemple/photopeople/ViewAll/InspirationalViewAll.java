package com.mobiletemple.photopeople.ViewAll;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.Studio.StudioProfileDetail;
import com.mobiletemple.Utils;
import com.mobiletemple.photopeople.model.DemoItem;

import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class InspirationalViewAll extends AppCompatActivity {
    ProgressDialog dialog;
    RequestQueue queue;

    AsymmetricRecyclerView picrecyclerView;
    int currentOffset=0;
    ArrayList<DemoItem> demoItems;
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspirational_view_all);

        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Inspirational Photographs");
        filter.setVisibility(View.GONE);
        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(InspirationalViewAll.this,HomePage.class);
                intent.putExtra("from","other");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

            }
        });
        picrecyclerView = (AsymmetricRecyclerView) findViewById(R.id.picrecyclerView);

        demoItems=new ArrayList<>();
        adapter = new RecyclerViewAdapter(demoItems,this);
        picrecyclerView.setRequestedColumnCount(3);
        picrecyclerView.setDebugging(true);
        picrecyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(this,3));
        picrecyclerView.addItemDecoration(
                new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_padding)));


        loadInspirationPic();
    }


    private void loadInspirationPic()
    {
        dialog.setMessage("Loading Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET,Endpoints.VIEWALL_INPIRATIONAL_PHOTO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e("HOMEPAGE_INPIRATIONAL",response);
                try {
                    demoItems.clear();
                    dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status=obj.getInt("status");
                    String message=obj.getString("message");
                    if (status==200 && message.equalsIgnoreCase("success"))
                    {
                        JSONArray inspiretion_photograph_list=obj.getJSONArray("inspiretion_photograph_list");

                        for (int i=0;i<inspiretion_photograph_list.length();i++)
                        {
                            JSONObject inspiretion_photographobj = inspiretion_photograph_list.getJSONObject(i);
                            String id=inspiretion_photographobj.getString("id");
                            String user_id=inspiretion_photographobj.getString("user_id");
                            String image=inspiretion_photographobj.getString("image");
                            String user_type=inspiretion_photographobj.getString("user_type");
                            //Log.e("usertype",user_type);




                            int colSpan = Math.random() < 0.2f ? 2 : 1;
                            int rowSpan = colSpan;

                            DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
                            item.setThumbnail(image);
                            item.setUserId(user_id);
                            item.setUserType(user_type);
                            item.setId(id);
                            demoItems.add(item);

                            adapter.notifyDataSetChanged();


                        }
                        picrecyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(InspirationalViewAll.this, picrecyclerView, adapter));

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

    class RecyclerViewAdapter extends AGVRecyclerViewAdapter<RecyclerViewAdapter.ViewHolder>
    {
        private final ArrayList<DemoItem> items;
        Context context;
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
            final String usertype=items.get(position).getUserType();
            final String userid=items.get(position).getUserId();
            holder.thumnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (usertype.equalsIgnoreCase("2"))
                    {
                        Intent intent=new Intent(context, FreelancerDetails.class);
                        intent.putExtra("freelancerId",userid);
                        intent.putExtra("usertype",usertype);
                        intent.putExtra("from","homepage");


                        context.startActivity(intent);
                    }
                    else
                    {
                        Intent intent=new Intent(context, StudioProfileDetail.class);
                        intent.putExtra("studioId",userid);
                        intent.putExtra("usertype",usertype);
                        intent.putExtra("from","homepage");
                        context.startActivity(intent);
                    }
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


                Picasso.with(context)
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
        Intent intent=new Intent(InspirationalViewAll.this,HomePage.class);
        intent.putExtra("from","other");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }
}
