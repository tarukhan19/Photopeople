package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.FilterDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;

/**
 * Created by Ishan Puranik on 26/04/2018.
 */

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<FilterDTO> filterDTOS;
    private int lastPosition = -1;
    private static final int DEFAULT_VIEW_TYPE = 1;
    private static final int NATIVE_AD_VIEW_TYPE = 2;

    public FilterAdapter(Context mContext, ArrayList<FilterDTO> filterDTOS) {
        this.mContext = mContext;
        this.filterDTOS = filterDTOS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        switch (viewType) {
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_featuredfreelancer, parent, false);
                return new MyViewHolder(itemView);


            case NATIVE_AD_VIEW_TYPE:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_admob, parent, false);



                return new NativeAdViewHolder(view);


        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, int position)
    {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);
        if (!(h instanceof MyViewHolder)) {
            return;
        }
        final MyViewHolder holder = (MyViewHolder) h;
        final FilterDTO freelancer = filterDTOS.get(position);

        holder.name.setText(freelancer.getName());
        holder.price.setText(freelancer.getStarting_price());
        holder.ratingBar.setRating((float) freelancer.getRating());

        //   (float) data.getDouble("rating")
        if (freelancer.getfeature().equalsIgnoreCase("0")) {
            holder.featurell.setVisibility(View.GONE);
        } else {
            holder.featurell.setVisibility(View.VISIBLE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date dob = sdf.parse(freelancer.getDob());
            Date dt = new Date();
            int diff = getDiffYears(dob, dt);
            holder.age.setText(diff + " Year");
        } catch (Exception ex) {
            holder.age.setText("");
        }

        switch (freelancer.getTravel_by()) {
            case 1:
                holder.travelby.setText("Own Car");
                break;
            case 2:
                holder.travelby.setText("Own Bike");
                break;
            case 3:
                holder.travelby.setText("Rent Car");
                break;
            case 4:
                holder.travelby.setText("Taxi");
                break;
            case 5:
                holder.travelby.setText("Public Transport");
                break;
        }


        if (freelancer.getCameratype() != null) {
            holder.cameratype.setText(freelancer.getCameratype());
        } else {
            holder.cameratype.setText("Not Mentioned");
        }

        holder.clickview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, FreelancerDetails.class);
                in.putExtra("freelancerId", freelancer.getId());
                in.putExtra("quoteAmount", freelancer.getStarting_price());
                Log.e("filtervalue", freelancer.getStarting_price());
                in.putExtra("from", "filter");
                mContext.startActivity(in);
            }
        });


        if (!freelancer.getProfile_image().isEmpty()) {
            Glide.with(mContext).load(freelancer.getProfile_image()).centerCrop().placeholder(R.drawable.default_new_img)
                    .error(R.drawable.default_new_img)

                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.iv_pic);
        } else {
            Glide.with(mContext).load(R.drawable.default_new_img).centerCrop().placeholder(R.drawable.default_new_img)
                    .error(R.drawable.default_new_img)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.iv_pic);
        }
        setAnimation(holder.cardView);
    }


    private void setAnimation(CardView viewToAnimate) {


        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_right); //change this with your desidered (or custom) animation
        animation.setInterpolator(mContext, android.R.anim.decelerate_interpolator);
        viewToAnimate.startAnimation(animation);
    }

    private int getDiffYears(Date dob, Date dt)
    {
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
        return filterDTOS != null ? filterDTOS.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (filterDTOS.get(position) == null) {
            return NATIVE_AD_VIEW_TYPE;
        }
        return DEFAULT_VIEW_TYPE;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_pic;
        public TextView name, age, travelby, cameratype, price;
        View clickview;
        LinearLayout featurell;
        CardView cardView;
        FilterDTO mDataItem;
        RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            iv_pic = view.findViewById(R.id.iv_pic);
            name = (TextView) view.findViewById(R.id.name);
            age = (TextView) view.findViewById(R.id.age);
            travelby = (TextView) view.findViewById(R.id.travelby);
            cameratype = (TextView) view.findViewById(R.id.cameratype);
            price = (TextView) view.findViewById(R.id.price);
            featurell = view.findViewById(R.id.featurell);
            ratingBar = view.findViewById(R.id.ratingBar);
            cardView = (CardView) view.findViewById(R.id.cardView);
            clickview = view;

        }
    }


    public class NativeAdViewHolder extends RecyclerView.ViewHolder
    {
        private AdView mAdView;
        public NativeAdViewHolder(View view) {

            super(view);


            MobileAds.initialize(mContext, "ca-app-pub-1234961524965105~5671037383");



            mAdView = itemView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();

            mAdView.loadAd(adRequest);


            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.


                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.

                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.

                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.

                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.

                }
            });
        }
    }


}
