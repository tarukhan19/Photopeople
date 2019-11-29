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

import com.mobiletemple.photopeople.PhototipWebView;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.PhotoTips;

import java.util.List;

public class Phototip_viewall_adapter extends RecyclerView.Adapter<Phototip_viewall_adapter.MyViewHolder> {

    private Context mContext;
    private List<PhotoTips> photoTipsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv1,tv2;
        CardView cardview;
        LinearLayout ll;

        public MyViewHolder(View view)
        {
            super(view);

            ll=view.findViewById(R.id.ll);
            tv1=view.findViewById(R.id.tv1);
            tv2=view.findViewById(R.id.tv2);
            cardview=view.findViewById(R.id.cardview);
        }
    }


    public Phototip_viewall_adapter(Context mContext, List<PhotoTips> photoTipsList) {
        this.mContext = mContext;
        this.photoTipsList = photoTipsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_viewall_phototips, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final PhotoTips operators = photoTipsList.get(position);
        holder.tv1.setText(operators.getTitle());
        holder.tv2.setText(operators.getDescription());
        holder.ll.setBackgroundResource(operators.getBackground());

        final String link=operators.getLink();

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,PhototipWebView.class);
                intent.putExtra("url",operators.getLink());
                //Log.e("operators.getLi",operators.getLink());
                mContext.startActivity(intent);

            }
        });
        setAnimation(holder.cardview);
    }

    private void setAnimation(CardView viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_right); //change this with your desidered (or custom) animation
        animation.setInterpolator(mContext, android.R.anim.decelerate_interpolator);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return photoTipsList.size();
    }


}