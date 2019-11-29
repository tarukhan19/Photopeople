package com.mobiletemple.photopeople.ViewAll;

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
import com.mobiletemple.photopeople.BuySell.ProductDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.Hotdeals;
import com.mobiletemple.photopeople.util.DynamiclinkCreate;
import com.mobiletemple.photopeople.util.Endpoints;

import java.util.ArrayList;

public class Hotdeal_viewall_adapter extends RecyclerView.Adapter<Hotdeal_viewall_adapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Hotdeals> filterDTOS;
    private int mSelectedItemPosition=-1;

    public Hotdeal_viewall_adapter(Context mContext, ArrayList<Hotdeals> filterDTOS)
    {
        this.mContext = mContext;
        this.filterDTOS = filterDTOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_viewall_hotdeals, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.bindDataWithViewHolder(filterDTOS.get(position),position);

        final Hotdeals freelancer = filterDTOS.get(position);
        holder.studioname.setText(freelancer.getstudioname());
        holder.discountmsg.setText(freelancer.getdiscountmsg());
        holder.termscondition.setText(freelancer.gettermscondition());
        //Log.e("freelancer.getstudi",freelancer.getstudioname());



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent in = new Intent(mContext, ProductDetails.class);
                    in.putExtra("productId",freelancer.getprodId());
                    in.putExtra("flag","hotdeal");
                    in.putExtra("prodCondition",freelancer.getprodCondition());
                    in.putExtra("amount",freelancer.getPrice());


                    mContext.startActivity(in);


            }
        });


        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prodtype=null;
                String image=freelancer.getThumbnail();
                String msg=freelancer.getdiscountmsg();
                String productid=freelancer.getprodId();

                if (freelancer.getprodCondition().equalsIgnoreCase("0"))
                {
                    prodtype="admin";
                }
                else
                {
                    prodtype="user";

                }
                try {

                    DynamiclinkCreate.sharelink(image, mContext,"Have a look on "+
                            msg,Endpoints.PRODUCT_DETAILS+ "?"+"product_id="+ productid
                            +"&product_type="+prodtype,"https://photopeoplehotdeal.page.link/");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (!freelancer.getThumbnail().isEmpty())
        {
            Glide.with(mContext).load(freelancer.getThumbnail()).centerCrop().placeholder(R.drawable.default_new_img)
                    .error(R.drawable.default_new_img)
                    .into(holder.iv_pic);
        }
        else {holder.iv_pic.setImageResource(R.drawable.default_new_img);}
        holder.price.setText(freelancer.getPrice());

        setAnimation(holder.cardView);

    }

    private void setAnimation(CardView viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_right); //change this with your desidered (or custom) animation
        animation.setInterpolator(mContext, android.R.anim.decelerate_interpolator);
        viewToAnimate.startAnimation(animation);
    }


    @Override
    public int getItemCount() {
        return filterDTOS != null ? filterDTOS.size() :0;}


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_pic;
        public TextView studioname,discountmsg,termscondition,price;
        LinearLayout share;

        CardView cardView;
        Hotdeals mDataItem;

        public MyViewHolder(View view) {
            super(view);
            iv_pic=view.findViewById(R.id.iv_pic);
            studioname=(TextView)view.findViewById(R.id.studioname);
            discountmsg=(TextView)view.findViewById(R.id.discountmsg);
            termscondition=(TextView)view.findViewById(R.id.termscondition);
            share=(LinearLayout) view.findViewById(R.id.share);
            price=(TextView)view.findViewById(R.id.price);


            cardView=(CardView)view.findViewById(R.id.cardView);


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

