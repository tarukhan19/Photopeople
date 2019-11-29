package com.mobiletemple.photopeople.adapter;

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

import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.WalletDTO;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder>
{
    private Context mContext;
    private ArrayList<WalletDTO> myjob_completeDTOS;
    private int mSelectedItemPosition=-1;
    String enddateS,startdateS;
    public WalletAdapter(Context mContext, ArrayList<WalletDTO> myjob_completeDTOS)
    {
        this.mContext = mContext;
        this.myjob_completeDTOS = myjob_completeDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        final WalletDTO freelancer = myjob_completeDTOS.get(position);

        holder.name.setText(freelancer.getName());
        holder.stage1amnt.setText("₹"+freelancer.getStage1amnt());
        holder.stage2amnt.setText("₹"+freelancer.getStage2amnt());
        parseDateToddMMyyyy(freelancer.getStartDate(),freelancer.getEndDate());

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
        holder.place.setText(freelancer.getLocation().trim());
        setAnimation(holder.cardView);
    }
    private void setAnimation(CardView viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_right); //change this with your desidered (or custom) animation
        animation.setInterpolator(mContext, android.R.anim.decelerate_interpolator);
        viewToAnimate.startAnimation(animation);
    }
    private void parseDateToddMMyyyy(String startDate, String endDate) {
        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "ddMMMyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;

        try {
            date = inputFormat.parse(startDate);
            startdateS = outputFormat.format(date);

            date = inputFormat.parse(endDate);
            enddateS = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return myjob_completeDTOS != null ? myjob_completeDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        public TextView name,eventname,stage1amnt,stage2amnt,eventdate,place;
        View clickview;
        CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            iv_pic = view.findViewById(R.id.event_pic);
            name = (TextView) view.findViewById(R.id.name);
            eventname=view.findViewById(R.id.eventname);
            eventdate=view.findViewById(R.id.eventdate);
            place=view.findViewById(R.id.place);
            cardView=view.findViewById(R.id.cardView);
            stage1amnt = (TextView) view.findViewById(R.id.stage1amnt);
            stage2amnt = (TextView) view.findViewById(R.id.stage2amnt);


        }
    }}