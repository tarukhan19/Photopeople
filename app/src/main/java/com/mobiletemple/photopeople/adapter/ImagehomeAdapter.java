package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.Studio.StudioProfileDetail;
import com.mobiletemple.photopeople.model.ImageHome;

import java.util.List;

/**
 * Created by shree on 4/21/2018.
 */

public class ImagehomeAdapter extends RecyclerView.Adapter<ImagehomeAdapter.MyViewHolder>
{

    private Context mContext;
    private List<ImageHome> imageHomeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.picIV);
        }
    }


    public ImagehomeAdapter(Context mContext, List<ImageHome> imageHomeList) {
        this.mContext = mContext;
        this.imageHomeList = imageHomeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pichome, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ImageHome operators = imageHomeList.get(position);
        String id=operators.getId();
        final String userid=operators.getUserId();
        final String usertype=operators.getUserType();




         Glide.with(mContext).load(operators.getThumbnail()).centerCrop().placeholder(R.drawable.default_gallery_dash)
                    .error(R.drawable.default_gallery_dash)

                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image);






        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usertype.equalsIgnoreCase("2"))
                {
                    Intent intent=new Intent(mContext, FreelancerDetails.class);
                    intent.putExtra("freelancerId",userid);
                    intent.putExtra("usertype",usertype);
                    intent.putExtra("from","homepage");


                    mContext.startActivity(intent);
                }
                else
                    {
                    Intent intent=new Intent(mContext, StudioProfileDetail.class);
                    intent.putExtra("studioId",userid);
                    intent.putExtra("usertype",usertype);
                    intent.putExtra("from","homepage");
                    mContext.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return imageHomeList.size();
    }
}