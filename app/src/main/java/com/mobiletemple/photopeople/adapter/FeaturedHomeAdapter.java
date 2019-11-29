package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.FilterDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FeaturedHomeAdapter  extends RecyclerView.Adapter<FeaturedHomeAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<FilterDTO> filterDTOS;
    private int mSelectedItemPosition=-1;

    public FeaturedHomeAdapter(Context mContext, ArrayList<FilterDTO> filterDTOS)
    {
        this.mContext = mContext;
        this.filterDTOS = filterDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featuredfreelancer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final FilterDTO freelancer = filterDTOS.get(position);
        holder.featurell.setVisibility(View.VISIBLE);
        holder.name.setText(freelancer.getName());
        holder.price.setText(freelancer.getStarting_price());
        holder.ratingBar.setRating((float)freelancer.getRating() );

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date dob = sdf.parse(freelancer.getDob());
            Date dt = new Date();

            int diff = getDiffYears(dob,dt);
            //Log.e("diff",diff+"");

            holder.age.setText(diff + " Year");

        }
        catch(Exception ex ){
            //Log.e("DOB Parse Err",ex.getMessage());
            holder.age.setText("");
        }

        switch(freelancer.getTravel_by())
        {
            case 1 : holder.travelby.setText("Own Car"); break;
            case 2 : holder.travelby.setText("Own Bike"); break;
            case 3 : holder.travelby.setText("Rent Car"); break;
            case 4 : holder.travelby.setText("Taxi"); break;
            case 5 : holder.travelby.setText("Public Transport"); break;
        }



        if (freelancer.getCameratype()!=null)
        {holder.cameratype.setText(freelancer.getCameratype());}
        else {holder.cameratype.setText("Not Mentioned");}

        holder.clickview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, FreelancerDetails.class);
                in.putExtra("freelancerId",freelancer.getId());
                in.putExtra("quoteAmount",freelancer.getStarting_price());

                in.putExtra("from","featurehome");

                mContext.startActivity(in);
            }
        });


        if (!freelancer.getProfile_image().isEmpty())
        {
            Glide.with(mContext).load("http://www.eventdesire.com/event/images/profile/"+freelancer.getProfile_image())
                    .into(holder.iv_pic);
        }

        else {  Glide.with(mContext).load(R.drawable.default_new_img)
                .into(holder.iv_pic);}


    }

    private int getDiffYears(Date dob, Date dt) {
        Calendar a = getCalendar(dob);
        Calendar b = getCalendar(dt);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;

    }

    public Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    @Override
    public int getItemCount() {
        return filterDTOS != null ? filterDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_pic;
        public TextView name,age,travelby,cameratype,price;
        View clickview;
        LinearLayout featurell;
        CardView cardView;
        FilterDTO mDataItem;
        RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            iv_pic=view.findViewById(R.id.iv_pic);
            name=(TextView)view.findViewById(R.id.name);
            age=(TextView)view.findViewById(R.id.age);
            travelby=(TextView)view.findViewById(R.id.travelby);
            cameratype=(TextView)view.findViewById(R.id.cameratype);
            price=(TextView)view.findViewById(R.id.price);
            featurell=view.findViewById(R.id.featurell);
            ratingBar=view.findViewById(R.id.ratingBar);
            cardView=(CardView)view.findViewById(R.id.cardView);
            clickview = view;

//
//            cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                     Toast.makeText(mContext,"You clicked item number "+MyViewHolder.this.getAdapterPosition(),Toast.LENGTH_LONG).show();
//                    int previousSelectState=mSelectedItemPosition;
//                    mSelectedItemPosition = getAdapterPosition();
//                    //notify previous selected item
//                    notifyItemChanged(previousSelectState);
//                    //notify new selected Item
//                    notifyItemChanged(mSelectedItemPosition);
//                }
//            });
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
