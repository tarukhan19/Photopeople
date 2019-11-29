package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiletemple.photopeople.Chat.ChatActivity;
import com.mobiletemple.photopeople.EventDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.model.My_Job_ProcessDTO;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ishan Puranik on 03/05/2018.
 */

public class FreelancerJob_ProcessAdapter extends RecyclerView.Adapter<FreelancerJob_ProcessAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<My_Job_ProcessDTO> my_job_processDTOS;
    private int mSelectedItemPosition=-1;
    String enddateS,startdateS;
    public FreelancerJob_ProcessAdapter(Context mContext, ArrayList<My_Job_ProcessDTO> my_job_processDTOS)
    {
        this.mContext = mContext;
        this.my_job_processDTOS = my_job_processDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myjob, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final My_Job_ProcessDTO freelancer = my_job_processDTOS.get(position);

        holder.price.setText(freelancer.getAmount());
        parseDateToddMMyyyy(freelancer.getStartDate(),freelancer.getEndDate());
        holder.studioname.setText(freelancer.getStudioName());
//        //Log.e("freelancer.getId",freelancer.getFreeId());

        holder.city.setText(freelancer.getLocation());

//        if (!freelancer.getProfile_image().isEmpty())
//        { Picasso.with(mContext).load(freelancer.getProfile_image())
//                .into(holder.iv_pic);}
//        else {holder.iv_pic.setImageResource(R.mipmap.register_profile_default);}
        holder.accept.setVisibility(View.GONE);

        switch(freelancer.getEventType())
        {
            case "1": holder.iv_pic.setImageResource(R.drawable.birthday_default);
                holder.eventname.setText("Birthday"); break;
            case "2":holder.iv_pic.setImageResource(R.drawable.wedding);
                holder.eventname.setText("Wedding"); break;
            case "3":holder.iv_pic.setImageResource(R.drawable.pre_wedding);
                holder.eventname.setText("Pre Wedding"); break;
            case "4": holder.iv_pic.setImageResource(R.drawable.engagement);
                holder.eventname.setText("Engagement"); break;
            case "5": holder.iv_pic.setImageResource(R.drawable.maternity);
                holder.eventname.setText("Maternity"); break;
            case "6":holder.iv_pic.setImageResource(R.drawable.kids);
                holder.eventname.setText("Kids"); break;
        }
        holder.eventdate.setText(startdateS+ " to "+enddateS);

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, ChatActivity.class);
                in.putExtra("studioID",freelancer.getStudioId());
                in.putExtra("freelancerId",freelancer.getId());
                in.putExtra("quoteId",freelancer.getQuoteId());
                in.putExtra("from",  "freejob");

                in.putExtra("RECIVERid",freelancer.getId());
                in.putExtra("recivertype",freelancer.getType());

                mContext.startActivity(in);
            }
        });
        holder.clickview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, EventDetails.class);
                intent.putExtra("pic",freelancer.getProfile_image());
                intent.putExtra("eventtype",freelancer.getEventType());
                intent.putExtra("shoottype",freelancer.getShootType());
                intent.putExtra("price",freelancer.getAmount());
                intent.putExtra("startdate",freelancer.getStartDate());
                intent.putExtra("enddate",freelancer.getEndDate());
                intent.putExtra("location",freelancer.getLocation());
                intent.putExtra("description",freelancer.getDesc());
                intent.putExtra("status",freelancer.getStatus());
                intent.putExtra("freelancerId",freelancer.getFreeId());
                intent.putExtra("quoteid",freelancer.getQuoteId());
                intent.putExtra("studioID", freelancer.getStudioId());
                intent.putExtra("from","job");
                intent.putExtra("RECIVERid",freelancer.getStudioId());
                intent.putExtra("recivertype",freelancer.getType());

                //Log.e("adapterpass",freelancer.getStudioId()+" "+freelancer.getId()+" "+freelancer.getType()+" "+Constants.FREELANCER_TYPE);

                mContext.startActivity(intent);

            }
        });
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
        return my_job_processDTOS != null ? my_job_processDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        public TextView studioname, eventname, eventdate, city,  price;
        View clickview;
        LinearLayout accept;
        LinearLayout chat;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            iv_pic = view.findViewById(R.id.iv_pic);
            eventname = (TextView) view.findViewById(R.id.eventname);
            studioname=view.findViewById(R.id.studioname);

            eventdate = (TextView) view.findViewById(R.id.eventdate);
            city = (TextView) view.findViewById(R.id.city);
            price = (TextView) view.findViewById(R.id.price);
            accept = (LinearLayout) view.findViewById(R.id.accept);
            chat=view.findViewById(R.id.chat);
            clickview = view;
            cardView=view.findViewById(R.id.cardView);

        }
    }
}


