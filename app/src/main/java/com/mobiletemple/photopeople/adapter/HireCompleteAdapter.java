package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.ReviewRating;
import com.mobiletemple.photopeople.model.Hired_free_complteDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ishan Puranik on 27/04/2018.
 */

public class HireCompleteAdapter extends RecyclerView.Adapter<HireCompleteAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Hired_free_complteDTO> hired_free_complteDTOS;
    private int mSelectedItemPosition=-1;
    String enddateS,startdateS;
    public HireCompleteAdapter(Context mContext, ArrayList<Hired_free_complteDTO> hired_free_complteDTOS)
    {
        this.mContext = mContext;
        this.hired_free_complteDTOS = hired_free_complteDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hiredjob_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final Hired_free_complteDTO freelancer = hired_free_complteDTOS.get(position);
        holder.chat.setVisibility(View.GONE);

        holder.eventname.setText(freelancer.getName());
        holder.price.setText(freelancer.getStarting_price());
        parseDateToddMMyyyy(freelancer.getStartDate(),freelancer.getEndDate());
        holder.name.setText(freelancer.getName());

        if (freelancer.getRatingdone().equalsIgnoreCase("0"))
        {
            holder.chat.setVisibility(View.GONE);
            holder.ratingll.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.chat.setVisibility(View.GONE);

            holder.ratingll.setVisibility(View.GONE);
        }

        holder.ratingll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent=new Intent(mContext, ReviewRating.class);
                    intent.putExtra("jobid",freelancer.getQuoteId());
                    intent.putExtra("id",freelancer.getId());

                    mContext.startActivity(intent);


            }
        });
        if (!freelancer.getProfile_image().isEmpty())
        {
            Glide.with(mContext).load(freelancer.getProfile_image()).centerCrop().placeholder(R.drawable.default_new_img)
                    .error(R.drawable.default_new_img)

                    .into(holder.event_pic);
        }
        else {  Glide.with(mContext).load(R.drawable.default_new_img).centerCrop().placeholder(R.drawable.default_new_img)
                .error(R.drawable.default_new_img)

                .into(holder.event_pic);}
        holder.city.setText(freelancer.getLocation());

        switch(freelancer.getEventType())
        {
            case "1":// holder.freelancerIV.setImageResource(R.mipmap.birthday);
                holder.eventname.setText("Birthday"); break;
            case "2":// holder.freelancerIV.setImageResource(R.mipmap.wedding);
                holder.eventname.setText("Wedding"); break;
            case "3":// holder.freelancerIV.setImageResource(R.mipmap.prewedding);
                holder.eventname.setText("Pre Wedding"); break;
            case "4": //holder.freelancerIV.setImageResource(R.mipmap.engagement);
                holder.eventname.setText("Engagement"); break;
            case "5": //holder.freelancerIV.setImageResource(R.mipmap.maternity);
                holder.eventname.setText("Maternity"); break;
            case "6":// holder.freelancerIV.setImageResource(R.mipmap.kids);
                holder.eventname.setText("Kids"); break;
        }
        holder.eventdate.setText(startdateS+ " to "+enddateS);
        setAnimation(holder.cardView);
    }
    private void setAnimation(CardView viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_right); //change this with your desidered (or custom) animation
        animation.setInterpolator(mContext, android.R.anim.decelerate_interpolator);
        viewToAnimate.startAnimation(animation);
    }
    public void parseDateToddMMyyyy(String startdate,String enddate) {

        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "ddMMMyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;

        try {
            date = inputFormat.parse(startdate);
            startdateS = outputFormat.format(date);

            date = inputFormat.parse(enddate);
            enddateS = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //return startdate,enddate;
    }

    @Override
    public int getItemCount() {
        return hired_free_complteDTOS != null ? hired_free_complteDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView event_pic;
        public TextView eventname, eventdate, city,  price,name;
        View clickview;
        LinearLayout chat,ratingll;
        CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            event_pic = view.findViewById(R.id.event_pic);
            eventname = (TextView) view.findViewById(R.id.eventname);
            eventdate = (TextView) view.findViewById(R.id.eventdate);
            city = (TextView) view.findViewById(R.id.city);
            price = (TextView) view.findViewById(R.id.price);
            chat = (LinearLayout) view.findViewById(R.id.chat);
            name= (TextView) view.findViewById(R.id.name);
            ratingll=  view.findViewById(R.id.ratingll);
            clickview = view;
            cardView=view.findViewById(R.id.cardView);

        }
    }
}

