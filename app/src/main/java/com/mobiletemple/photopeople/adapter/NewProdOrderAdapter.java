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
import android.widget.TextView;

import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.NewProdOrderDTO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewProdOrderAdapter extends RecyclerView.Adapter<NewProdOrderAdapter.CustomViewHodler> {

    private Context mContext;
    ArrayList<NewProdOrderDTO> productListDTOS;
    long diff;

    public NewProdOrderAdapter(Context context, ArrayList<NewProdOrderDTO> productListDTOS) {
        this.mContext = context;
        this.productListDTOS = productListDTOS;
    }

    @Override
    public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_productorderhistory, parent, false);
        return new CustomViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHodler holder, final int position) {
        final NewProdOrderDTO productListDTO = productListDTOS.get(position);



        // //Log.e("productListDTO", productListDTO.getProdimage());

        if (!productListDTO.getProdimage().isEmpty())


        {
            Picasso.with(mContext).load(productListDTO.getProdimage()).placeholder(R.drawable.default_album_list)
                    .into(holder.prodImage);
        } else {
            holder.prodImage.setImageResource(R.drawable.default_album_list);
        }

        holder.orderidTV.setText("OrderId: "+"#"+productListDTO.getOrderId());

        holder.productnameTV.setText(productListDTO.getPoductName());
        holder.totalpriceTV.setText("₹"+productListDTO.gettotalprice());
        String workstatus=productListDTO.getWorkStatus();

        if (workstatus.equalsIgnoreCase("1"))
        {holder.workstatusTV.setText("Order Recieved");
            holder.workstatusTV.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        else if (workstatus.equalsIgnoreCase("2"))
        {
            holder.workstatusTV.setText("Dispatched");
            holder.workstatusTV.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        else if (workstatus.equalsIgnoreCase("3"))
        {
            holder.workstatusTV.setText("Delivered");
            holder.workstatusTV.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        holder.time.setText(productListDTO.getTime());
        setAnimation(holder.cardView);

    }
    private void setAnimation(CardView viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_right); //change this with your desidered (or custom) animation
        animation.setInterpolator(mContext, android.R.anim.decelerate_interpolator);
        viewToAnimate.startAnimation(animation);
    }
    @Override
    public int getItemCount() {
        return productListDTOS.size();
    }

    public class CustomViewHodler extends RecyclerView.ViewHolder {
        ImageView prodImage;
        TextView productnameTV,orderidTV,time,workstatusTV,totalpriceTV;
        CardView cardView;
        public CustomViewHodler(View itemView) {
            super(itemView);
            prodImage=(ImageView)itemView.findViewById(R.id.prodImage);
            productnameTV=itemView.findViewById(R.id.productnameTV);
            orderidTV=itemView.findViewById(R.id.orderidTV);
            time=itemView.findViewById(R.id.time);
            workstatusTV=itemView.findViewById(R.id.wrokstatus);
            totalpriceTV=itemView.findViewById(R.id.totalpriceTV);
            cardView=itemView.findViewById(R.id.cardView);
        }
    }
}


