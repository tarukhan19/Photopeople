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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiletemple.photopeople.Chat.ChatActivity;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.model.Hire_free_procsDTO;
import com.mobiletemple.photopeople.model.Hire_free_waitDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ishan Puranik on 27/04/2018.
 */

public class HireProcessAdapter extends RecyclerView.Adapter<HireProcessAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Hire_free_procsDTO> hire_free_procsDTOS;
    private int mSelectedItemPosition=-1;
    String enddateS,startdateS;
    public HireProcessAdapter(Context mContext,ArrayList<Hire_free_procsDTO> hire_free_procsDTOS)
    {
        this.mContext = mContext;
        this.hire_free_procsDTOS = hire_free_procsDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hiredjob_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final Hire_free_procsDTO freelancer = hire_free_procsDTOS.get(position);

        holder.eventname.setText(freelancer.getName());
        holder.price.setText(freelancer.getStarting_price());
        parseDateToddMMyyyy(freelancer.getStartDate(),freelancer.getEndDate());
        holder.name.setText(freelancer.getName());
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
        holder.chat.setVisibility(View.VISIBLE);
        holder.ratingll.setVisibility(View.GONE);
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
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, ChatActivity.class);
                in.putExtra("studioID", freelancer.getStudioId());
                in.putExtra("freelancerId", freelancer.getId());
                in.putExtra("quoteId", freelancer.getQuoteId());
                in.putExtra("from",  "hire");

                in.putExtra("recivertype", "2" );
                in.putExtra("RECIVERid", freelancer.getId() );
                //Log.e("adapterpass",freelancer.getStudioId()+" "+freelancer.getId()+" "+freelancer.getQuoteId()+" "+Constants.STUDIO_TYPE);

                mContext.startActivity(in);
            }
        });


        holder.clickview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, FreelancerDetails.class);
                intent.putExtra("freelancerId",freelancer.getId());
                intent.putExtra("studioID", freelancer.getStudioId());

                intent.putExtra("quoteid",freelancer.getQuoteId());
                intent.putExtra("from","hire");

                //Log.e("getQuoteId",freelancer.getQuoteId());
                intent.putExtra("status",freelancer.getStatus());
                intent.putExtra("quoteAmount",freelancer.getAmount());
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
        return hire_free_procsDTOS != null ? hire_free_procsDTOS.size() :0;}


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
            cardView=view.findViewById(R.id.cardView);
            clickview = view;
            ratingll=  view.findViewById(R.id.ratingll);

        }
    }
}
