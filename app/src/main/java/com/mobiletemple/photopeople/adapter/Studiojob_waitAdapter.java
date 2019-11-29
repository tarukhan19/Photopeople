package com.mobiletemple.photopeople.adapter;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.Myjob_waitDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ishan Puranik on 03/05/2018.
 */

public class Studiojob_waitAdapter  extends RecyclerView.Adapter<Studiojob_waitAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Myjob_waitDTO> myjob_waitDTOS;
    private int mSelectedItemPosition=-1;
    String enddateS,startdateS;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager mSessionManager;

    public Studiojob_waitAdapter(Context mContext, ArrayList<Myjob_waitDTO> myjob_waitDTOS)
    {
        this.mContext = mContext;
        this.myjob_waitDTOS = myjob_waitDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myjob, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final Myjob_waitDTO freelancer = myjob_waitDTOS.get(position);

        holder.price.setText(freelancer.getStarting_price());
        parseDateToddMMyyyy(freelancer.getStartDate(),freelancer.getEndDate());
        holder.studioname.setText(freelancer.getStudioName());

        holder.city.setText(freelancer.getLocation());
        dialog = new ProgressDialog(mContext);
        queue = Volley.newRequestQueue(mContext);
        mSessionManager = new SessionManager(mContext.getApplicationContext());

        if (!freelancer.getProfile_image().isEmpty())
        { Picasso.with(mContext).load(freelancer.getProfile_image()).placeholder(R.drawable.default_new_img)
                .into(holder.iv_pic);}
        else {holder.iv_pic.setImageResource(R.drawable.default_new_img);}

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

        holder.accept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                acceptTask();
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
        return myjob_waitDTOS != null ? myjob_waitDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        public TextView studioname, eventname, eventdate, city, price;
        View clickview;
        LinearLayout accept;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            iv_pic = view.findViewById(R.id.iv_pic);
            eventname = (TextView) view.findViewById(R.id.eventname);
            studioname = view.findViewById(R.id.studioname);
            eventdate = (TextView) view.findViewById(R.id.eventdate);
            city = (TextView) view.findViewById(R.id.city);
            price = (TextView) view.findViewById(R.id.price);
            accept = (LinearLayout) view.findViewById(R.id.accept);
            cardView=view.findViewById(R.id.cardView);
            clickview = view;

        }
    }  }