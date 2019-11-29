package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiletemple.photopeople.BuySell.ProductDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.Hotdeals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeHotDeailsAdapter extends RecyclerView.Adapter<HomeHotDeailsAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Hotdeals> filterDTOS;
    private int mSelectedItemPosition=-1;

    public HomeHotDeailsAdapter(Context mContext, ArrayList<Hotdeals> filterDTOS)
    {
        this.mContext = mContext;
        this.filterDTOS = filterDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotdeals, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final Hotdeals freelancer = filterDTOS.get(position);
        holder.studioname.setText(freelancer.getstudioname());
        holder.discountmsg.setText(freelancer.getdiscountmsg());
        holder.termscondition.setText(freelancer.gettermscondition());



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(mContext, ProductDetails.class);
                in.putExtra("productId",freelancer.getprodId());
                in.putExtra("flag","hotdeal");
                in.putExtra("prodCondition",freelancer.getprodCondition());
                in.putExtra("amount",freelancer.getPrice());


                mContext.startActivity(in);

            }
        });


        if (!freelancer.getThumbnail().isEmpty())
        {
            Glide.with(mContext).load(freelancer.getThumbnail()).centerCrop().placeholder(R.drawable.default_album_list)
                    .error(R.drawable.default_album_list)

                    .into(holder.iv_pic);
        }
        else {holder.iv_pic.setImageResource(R.drawable.default_album_list);}
        holder.price.setText(freelancer.getPrice());



    }



    @Override
    public int getItemCount() {
        return filterDTOS != null ? filterDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_pic;
        public TextView studioname,discountmsg,termscondition,price;

        CardView cardView;
        Hotdeals mDataItem;

        public MyViewHolder(View view) {
            super(view);
            iv_pic=view.findViewById(R.id.iv_pic);
            studioname=(TextView)view.findViewById(R.id.studioname);
            discountmsg=(TextView)view.findViewById(R.id.discountmsg);
            termscondition=(TextView)view.findViewById(R.id.termscondition);
            price=(TextView)view.findViewById(R.id.price);

            cardView=(CardView)view.findViewById(R.id.cardView);


        }

//        public void bindDataWithViewHolder(FilterDTO dataItem, int currentPosition){
//            this.mDataItem=dataItem;
//            //Handle selection  state in object View.
//            if(currentPosition == mSelectedItemPosition){
//
//            }
//
//        }
    }
}

