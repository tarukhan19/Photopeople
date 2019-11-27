package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.mobiletemple.photopeople.R;

import java.util.ArrayList;

/**
 * Created by gourav on 13/10/17.
 */

public class VideoURLAdapter extends BaseAdapter
{
    private Context ctx;
    private ArrayList<String> videoList;

    public VideoURLAdapter(Context ctx, ArrayList<String> videoList) {
        this.ctx = ctx;
        this.videoList = videoList;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        View itemView = LayoutInflater.from(ctx)
                .inflate(R.layout.item_video_url, null, false);

        TextView lbl = (TextView) itemView.findViewById(R.id.videoLBL);
        ImageView iv = (ImageView) itemView.findViewById(R.id.delVideoIV);

        String url = videoList.get(i);
        lbl.setText(url);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoList.remove(i);
                notifyDataSetChanged();
            }
        });

        return itemView;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }
}
