package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.recyclerview.widget.RecyclerView;

import com.mobiletemple.photopeople.R;

import java.util.ArrayList;


/**
 * Created by shree on 3/30/2018.
 */

public class BitmapImageAdapter extends RecyclerView.Adapter<BitmapImageAdapter.CustomViewHodler>
{

    private Context mContext;
    ArrayList<Bitmap> bmList;

    public BitmapImageAdapter(Context context, ArrayList<Bitmap> photoBMList)
    {
        this.mContext = context;
        this.bmList = photoBMList;
    }

    @Override
    public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new CustomViewHodler(itemView);    }

    @Override
    public void onBindViewHolder(CustomViewHodler holder, final int position) {
        final Bitmap bm = bmList.get(position);
        holder.imageView.setImageBitmap(bm);

        holder.crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bmList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bmList.size();
    }

    public class CustomViewHodler extends RecyclerView.ViewHolder {
        ImageView imageView,crossIV;
        public CustomViewHodler(View itemView) {
            super(itemView);
            crossIV=(ImageView)itemView.findViewById(R.id.crossIV);
            imageView=(ImageView)itemView.findViewById(R.id.picIV);
        }
    }
}
