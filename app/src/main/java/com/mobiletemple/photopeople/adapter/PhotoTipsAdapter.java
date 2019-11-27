package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mobiletemple.photopeople.PhototipWebView;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.PhotoTips;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Ishan Puranik on 23/04/2018.
 */

public class PhotoTipsAdapter extends RecyclerView.Adapter<PhotoTipsAdapter.MyViewHolder> {

    private Context mContext;
    private List<PhotoTips> photoTipsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        TextView tv1,tv2;
        CardView cardview;
        LinearLayout ll;

        public MyViewHolder(View view)
        {
            super(view);

            ll=view.findViewById(R.id.ll);
            tv1=view.findViewById(R.id.tv1);
            tv2=view.findViewById(R.id.tv2);
            cardview=view.findViewById(R.id.cardview);
        }
    }


    public PhotoTipsAdapter(Context mContext, List<PhotoTips> photoTipsList) {
        this.mContext = mContext;
        this.photoTipsList = photoTipsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_phototips, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final PhotoTips operators = photoTipsList.get(position);
        holder.tv1.setText(operators.getTitle());
        holder.tv2.setText(operators.getDescription());
        holder.ll.setBackgroundResource(operators.getBackground());

        //Log.e("photoTipsList",photoTipsList.size()+"");

        final String link=operators.getLink();

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,PhototipWebView.class);
                intent.putExtra("url",operators.getLink());
                //Log.e("operators.getLi",operators.getLink());
                mContext.startActivity(intent);

            }
        });
    }



    @Override
    public int getItemCount() {
        return photoTipsList.size();
    }


}