package com.mobiletemple.photopeople.Studio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mobiletemple.AGVRecyclerViewAdapter;
import com.mobiletemple.AsymmetricItem;
import com.mobiletemple.AsymmetricRecyclerView;
import com.mobiletemple.AsymmetricRecyclerViewAdapter;
import com.mobiletemple.photopeople.Freelancer.FreelancerImageDialog;
import com.mobiletemple.photopeople.Freelancer.FreelancerfullGallery;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.Utils;
import com.mobiletemple.photopeople.adapter.VideoAdapter;
import com.mobiletemple.photopeople.model.DemoItem;
import com.mobiletemple.photopeople.model.FreedetailvideoDTO;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Studio_Imagedetail_frag extends Fragment {
    Intent intent;
    String studioId,from;
    ProgressDialog dialog;
    RequestQueue queue;
    ImageView vidactive,vidinactive,picinactive,picactive;
    LinearLayout picgallery,vidgallery;
    ArrayList<DemoItem> demoItems;
    int currentOffset=0;
    TextView viewall;
    RecyclerView vidrecyclerView;
   // Free_imageDetail free_imageDetailadapter;

    VideoAdapter videoAdapter;
    ArrayList<FreedetailvideoDTO> freedetailvideoDTOS;
    AsymmetricRecyclerView picrecyclerView;
    RecyclerViewAdapter adapter;
    Uri  deepLink;
    public Studio_Imagedetail_frag() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_studio__imagedetail_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        intent=getActivity().getIntent();
        picgallery=view.findViewById(R.id.picgallery);
        vidgallery=view.findViewById(R.id.vidgallery);
        vidactive=view.findViewById(R.id.vidactive);
        vidinactive=view.findViewById(R.id.vidinactive);
        picinactive=view.findViewById(R.id.picinactive);
        picactive=view.findViewById(R.id.picactive);
        picrecyclerView = (AsymmetricRecyclerView) view.findViewById(R.id.picrecyclerView);
        vidrecyclerView= (RecyclerView) view.findViewById(R.id.vidrecyclerView);
        viewall=view.findViewById(R.id.viewall);
        from=intent.getStringExtra("from");

        freedetailvideoDTOS=new ArrayList<>();

            studioId=intent.getStringExtra("studioId");




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
                           studioId= url.replace("http://eventdesire.com/event/webservice/studio/single_studio?studio_id=", "");
                            loadStudio();

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

        videoAdapter=new VideoAdapter(getActivity(),freedetailvideoDTOS);

        demoItems=new ArrayList<>();
        adapter = new RecyclerViewAdapter(demoItems,getActivity());
        picrecyclerView.setRequestedColumnCount(3);
        picrecyclerView.setDebugging(true);
        picrecyclerView.setRequestedHorizontalSpacing(Utils.dpToPx(getActivity(),3));
        picrecyclerView.addItemDecoration(
                new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_padding)));

        picrecyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(getActivity(), picrecyclerView, adapter));



        // set a GridLayoutManager with 3 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        LinearLayoutManager  mLayoutManager = new LinearLayoutManager(getActivity());
        vidrecyclerView.setLayoutManager(mLayoutManager); // set LayoutManager to RecyclerView
        vidrecyclerView.setAdapter(videoAdapter);


        picgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picactive.setVisibility(View.VISIBLE);
                picinactive.setVisibility(View.GONE);
                vidactive.setVisibility(View.GONE);
                vidinactive.setVisibility(View.VISIBLE);
                picrecyclerView.setVisibility(View.VISIBLE);
                vidrecyclerView.setVisibility(View.GONE);

            }
        });


        vidgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                vidactive.setVisibility(View.VISIBLE);
                vidinactive.setVisibility(View.GONE);
                picactive.setVisibility(View.GONE);
                picinactive.setVisibility(View.VISIBLE);
                picrecyclerView.setVisibility(View.GONE);
                vidrecyclerView.setVisibility(View.VISIBLE);

            }
        });
        viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getActivity(), StudioFullGallery.class);
                intent.putExtra("studioId",studioId);
                intent.putExtra("from",from);
                startActivity(intent);

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
                        freedetailvideoDTOS.clear();
                        dialog.dismiss();
                        Log.e("loadStudio", response);
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
                                    picrecyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(getActivity(), picrecyclerView, adapter));


                                }


                                for (int i=0;i<exp_video.length();i++)
                                {
                                    JSONObject vidObj=exp_video.getJSONObject(i);
                                    String video=vidObj.getString("video_url");
                                    FreedetailvideoDTO dtos2 = new FreedetailvideoDTO();
                                    dtos2.setVideo(video);
                                    dtos2.setUserid(vidObj.getString("user_id"));
                                    dtos2.setUsertype(vidObj.getString("user_type"));
                                    dtos2.setVideoid(vidObj.getString("id"));

                                    freedetailvideoDTOS.add(dtos2);
                                    videoAdapter.notifyDataSetChanged();
                                    vidrecyclerView.setAdapter(videoAdapter);


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


                    Intent intent=((Activity)getContext()).getIntent();
                    intent.putExtra("image",items.get(position).getThumbnail());
                    freelancerImageDialog.show(((Activity)getContext()).getFragmentManager(), "");
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


                Picasso.with(getActivity())
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
    public void onStart() {
       loadStudio();

        super.onStart();
    }


}
