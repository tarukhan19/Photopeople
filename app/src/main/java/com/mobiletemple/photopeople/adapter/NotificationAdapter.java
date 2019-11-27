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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiletemple.photopeople.InboxActivity;
import com.mobiletemple.photopeople.MyJobActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.NotificationListDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<NotificationListDTO> notificationListDTOS;
    private int mSelectedItemPosition=-1;
    String startdateS,enddateS;
    SessionManager sessionManager;
    Intent intent;
    public NotificationAdapter(Context mContext, ArrayList<NotificationListDTO> notificationListDTOS)
    {
        this.mContext = mContext;
        this.notificationListDTOS = notificationListDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        sessionManager = new SessionManager(mContext);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final NotificationListDTO freelancer = notificationListDTOS.get(position);
        holder.orderid.setText(freelancer.getName());
        holder.noOfDays.setText(freelancer.getNoOfDays());
        holder.msg.setText(freelancer.getMsg());

        holder.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (freelancer.getMsg().equalsIgnoreCase("New quote request send by studio"))
                {
                    if (!sessionManager.isLoggedIn())
                    {intent = new Intent(mContext, LoginActivity.class); }
                    else
                    {intent = new Intent(mContext, MyJobActivity.class).putExtra("tabIndex", 0);
                        intent.putExtra("from","notification");
                    }
                }
                else  if (freelancer.getMsg().equalsIgnoreCase("studio accept your quote,your work is now inprocess stage"))
                {
                    if (!sessionManager.isLoggedIn())
                    {intent = new Intent(mContext, LoginActivity.class); }
                    else
                    {intent = new Intent(mContext, MyJobActivity.class).putExtra("tabIndex", 0);
                        intent.putExtra("from","notification");}
                }
                else  if (freelancer.getMsg().equalsIgnoreCase("studio finel Payment your quote,your work is now compleate stage"))
                {
                    if (!sessionManager.isLoggedIn())
                    {intent = new Intent(mContext, LoginActivity.class); }
                    else
                    {intent = new Intent(mContext, MyJobActivity.class).putExtra("tabIndex", 0);
                        intent.putExtra("from","notification");}
                }
                else  if (freelancer.getMsg().equalsIgnoreCase("Freelancer accept your quote request"))
                {
                    if (!sessionManager.isLoggedIn())
                    {intent = new Intent(mContext, LoginActivity.class); }
                    else
                    {intent = new Intent(mContext, InboxActivity.class).putExtra("tabIndex", 2);
                        intent.putExtra("from","notification");}
                }
                else  if (freelancer.getMsg().equalsIgnoreCase("Freelancer cancel your quote request"))
                {
                    if (!sessionManager.isLoggedIn())
                    {intent = new Intent(mContext, LoginActivity.class); }
                    else
                    {intent = new Intent(mContext, InboxActivity.class).putExtra("tabIndex", 2);
                        intent.putExtra("from","notification");}
                }
                else
                {

                }

                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return notificationListDTOS != null ? notificationListDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView noOfDays, orderid,msg;
        View clickview;
        CardView linearlayout;
        public MyViewHolder(View view) {
            super(view);
            noOfDays = (TextView) view.findViewById(R.id.noOfDays);
            orderid = (TextView) view.findViewById(R.id.orderid);
            linearlayout = (CardView) view.findViewById(R.id.linearlayout);
            msg=view.findViewById(R.id.msg);
            clickview = view;

        }
    }
}

