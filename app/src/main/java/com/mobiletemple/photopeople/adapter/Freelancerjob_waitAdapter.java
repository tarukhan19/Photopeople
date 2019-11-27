package com.mobiletemple.photopeople.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobiletemple.photopeople.Chat.ChatActivity;
import com.mobiletemple.photopeople.EventDetails;
import com.mobiletemple.photopeople.MyJobActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.fragment.MyJob_WaitingFragment;
import com.mobiletemple.photopeople.model.Myjob_waitDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.Users;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Ishan Puranik on 03/05/2018.
 */

public class Freelancerjob_waitAdapter  extends RecyclerView.Adapter<Freelancerjob_waitAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Myjob_waitDTO> myjob_waitDTOS;
    private int mSelectedItemPosition=-1;
    String enddateS,startdateS;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager mSessionManager;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public Freelancerjob_waitAdapter(Context mContext, ArrayList<Myjob_waitDTO> myjob_waitDTOS)
    {
        this.mContext = mContext;
        this.myjob_waitDTOS = myjob_waitDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myjob, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final Myjob_waitDTO freelancer = myjob_waitDTOS.get(position);

        holder.price.setText(freelancer.getAmount());
        parseDateToddMMyyyy(freelancer.getStartDate(),freelancer.getEndDate());
        holder.studioname.setText(freelancer.getStudioName());
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        holder.city.setText(freelancer.getLocation());
        dialog = new ProgressDialog(mContext);
        queue = Volley.newRequestQueue(mContext);
        mSessionManager = new SessionManager(mContext.getApplicationContext());

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
        holder.eventdate.setText(startdateS+ " to "+enddateS);

       if (freelancer.getStatus().equalsIgnoreCase("1"))
       {holder.accept.setVisibility(View.VISIBLE);}
       else if (freelancer.getStatus().equalsIgnoreCase("2"))
       {holder.accept.setVisibility(View.GONE);}

        holder.chat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, ChatActivity.class);
                in.putExtra("studioID",freelancer.getStudioId());
                in.putExtra("freelancerId",freelancer.getId());
                in.putExtra("quoteId",freelancer.getQuoteId());
                in.putExtra("from",  "freejob");

                in.putExtra("RECIVERid",freelancer.getId());
                in.putExtra("recivertype",freelancer.getType());

                //Log.e("adapterpass",freelancer.getStudioId()+" "+freelancer.getId()+" "+freelancer.getQuoteId()+" "+Constants.FREELANCER_TYPE);
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

        holder.accept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                acceptTask();
            }
            private void acceptTask()
            {
                dialog.setMessage("Please Wait..");
                dialog.setCancelable(true);
                dialog.show();

                StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.ACCEPT_FREELANCER_QUOTE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                dialog.dismiss();
                              //  Log.e("acceptresponse", response);
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    final int status = obj.getInt("status");
                                    String message = obj.getString("message");

                                    if (status == 200 && message.equalsIgnoreCase("Success"))
                                    {
                                        freelancer.setJobType(Constants.F_ACCEPT_BY_FREELANCER);

                                        holder.accept.setVisibility(View.GONE);


                                        databaseReference.orderByChild("mobileno").equalTo(mSessionManager.getLoginSession().get(SessionManager.KEY_MOBILE)).addValueEventListener(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot){
                                                if(dataSnapshot.exists()) {
                                                    switchToOtherActivity();
                                                }

                                                else
                                                {
                                                    Users user = new Users(mSessionManager.getLoginSession().get(SessionManager.KEY_USERID),
                                                            mSessionManager.getLoginSession().get(SessionManager.KEY_MOBILE),
                                                            mSessionManager.getLoginSession().get(SessionManager.KEY_NAME),
                                                            mSessionManager.getLoginSession().get(SessionManager.KEY_IMAGE)   );

                                                    databaseReference.push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {




                                                            switchToOtherActivity();
                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    switchToOtherActivity();
                                                                }
                                                            });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }});





                                    } else{}
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                dialog.dismiss();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("freelancerid", freelancer.getFreeId());
                        params.put("quote_id", freelancer.getQuoteId());
                        params.put("user_type", mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));

                        //Log.e("params", params.toString());
                        return params;
                    }
                };
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                postRequest.setRetryPolicy(policy);
                queue.add(postRequest);
            }

        });

        setAnimation(holder.cardView);
    }

    private void switchToOtherActivity()
    {

        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Job Accepted!");
        sweetAlertDialog.show();



        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

        sweetAlertDialog.setConfirmText("Ok");
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                sweetAlertDialog.dismissWithAnimation();
                Intent intent=new Intent(mContext, MyJobActivity.class);
                intent.putExtra("from","job");

                mContext.startActivity(intent);
            }
        });
        sweetAlertDialog.show();

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
        return myjob_waitDTOS.size();}


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_pic;
        public TextView studioname, eventname, eventdate, city, price;
        View clickview;
        LinearLayout accept;
        LinearLayout chat;
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
            chat=view.findViewById(R.id.chat);
            clickview = view;
            cardView=view.findViewById(R.id.cardView);

        }
    }}