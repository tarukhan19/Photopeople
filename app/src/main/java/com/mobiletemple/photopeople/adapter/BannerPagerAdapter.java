package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mobiletemple.photopeople.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BannerPagerAdapter extends PagerAdapter {
    private Context mContext;

    ArrayList<String> sliderImgList;

    public BannerPagerAdapter(Context context, ArrayList<String> sliderImgList)
    {
        mContext = context;
        this.sliderImgList = sliderImgList;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_banner, collection, false);
        ImageView iv = (ImageView)layout.findViewById(R.id.sliderImg);
        final String image=sliderImgList.get(position);
        //Log.e("image",image);
      //  Glide.with(mContext).load(image).placeholder(R.drawable.default_album_list).into(iv);
        Picasso.with(mContext).load(image).placeholder(R.drawable.default_album_list)
                .into(iv);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return sliderImgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}
