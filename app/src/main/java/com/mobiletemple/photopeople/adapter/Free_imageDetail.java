package com.mobiletemple.photopeople.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobiletemple.photopeople.Freelancer.FreelancerImageDialog;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.FreedetailimageDTO;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ishan Puranik on 04/05/2018.
 */

public class Free_imageDetail extends RecyclerView.Adapter<Free_imageDetail.MyViewHolder> {

    private Context mContext;
    private ArrayList<FreedetailimageDTO> albumList;
    Bitmap   pic=null;
    FreelancerImageDialog freelancerImageDialog=new FreelancerImageDialog();
    Bitmap bitmap;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        //TextView text;
        public MyViewHolder(View view) {
            super(view);
           // text=view.findViewById(R.id.text);
            thumbnail = (ImageView) view.findViewById(R.id.image);
        }
    }


    public Free_imageDetail(Context mContext, ArrayList<FreedetailimageDTO> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_freedetailimage, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FreedetailimageDTO album = albumList.get(position);
        final String image=album.getThumbnail();

        Glide.with(mContext).load(image).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String image=album.getThumbnail();


                Intent intent=((Activity)mContext).getIntent();
                intent.putExtra("image",image);

                freelancerImageDialog.show(((Activity)mContext).getFragmentManager(), "1");
            }
        });

    }



    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
