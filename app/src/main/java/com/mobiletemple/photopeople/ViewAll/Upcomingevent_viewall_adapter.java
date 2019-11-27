package com.mobiletemple.photopeople.ViewAll;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.UpcomingEventDetails;
import com.mobiletemple.photopeople.model.UpcomingEventDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Upcomingevent_viewall_adapter extends RecyclerView.Adapter<Upcomingevent_viewall_adapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<UpcomingEventDTO> my_job_processDTOS;
    private int mSelectedItemPosition=-1;
    String enddateS,startdateS;
    public Upcomingevent_viewall_adapter(Context mContext, ArrayList<UpcomingEventDTO> my_job_processDTOS)
    {
        this.mContext = mContext;
        this.my_job_processDTOS = my_job_processDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcomingevent, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final UpcomingEventDTO freelancer = my_job_processDTOS.get(position);
        holder.price.setText(freelancer.getAmount());
        holder.eventdate.setText(freelancer.getStartDate()+" to "+freelancer.getEndDate());

        holder.city.setText(freelancer.getLocation());

//        if (!freelancer.getProfile_image().isEmpty())
//        { Picasso.with(mContext).load(freelancer.getProfile_image())
//                .into(holder.iv_pic);}
//        else {holder.iv_pic.setImageResource(R.mipmap.register_profile_default);}


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
        setAnimation(holder.cardView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, UpcomingEventDetails.class);
                intent.putExtra("eventtype",freelancer.getEventType());
                intent.putExtra("price",freelancer.getAmount());
                intent.putExtra("startdate",freelancer.getStartDate());
                intent.putExtra("enddate",freelancer.getEndDate());
                intent.putExtra("location",freelancer.getLocation());
                intent.putExtra("description",freelancer.getDesc());

                intent.putExtra("username",freelancer.getUser_name());
                intent.putExtra("userphone",freelancer.getUser_phone());

                mContext.startActivity(intent);

            }
        });

    }

//    public void parseDateToddMMyyyy(String startdate,String enddate) {
//
//        String inputPattern = "dd-MMM-yyyy";
//        String outputPattern = "ddMMMyy";
//        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
//        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
//
//        Date date = null;
//
//        try {
//            date = inputFormat.parse(startdate);
//            startdateS = outputFormat.format(date);
//
//            date = inputFormat.parse(enddate);
//            enddateS = outputFormat.format(date);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        //return startdate,enddate;
//    }
    private void setAnimation(CardView viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_right); //change this with your desidered (or custom) animation
        animation.setInterpolator(mContext, android.R.anim.decelerate_interpolator);
        viewToAnimate.startAnimation(animation);
    }
    @Override
    public int getItemCount() {
        return my_job_processDTOS != null ? my_job_processDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        public TextView  eventname, eventdate, city,  price;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            iv_pic = view.findViewById(R.id.iv_pic);
            eventname = (TextView) view.findViewById(R.id.eventname);
            cardView=view.findViewById(R.id.cardView);
            eventdate = (TextView) view.findViewById(R.id.eventdate);
            city = (TextView) view.findViewById(R.id.city);
            price = (TextView) view.findViewById(R.id.price);

        }
    }
}


