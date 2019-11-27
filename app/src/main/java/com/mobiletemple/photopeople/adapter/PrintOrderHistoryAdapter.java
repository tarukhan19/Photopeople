package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.PrinterOrderHistoryDTO;

import java.util.ArrayList;

public class PrintOrderHistoryAdapter extends RecyclerView.Adapter<PrintOrderHistoryAdapter.CustomViewHodler> {

    private Context mContext;
    ArrayList<PrinterOrderHistoryDTO> productListDTOS;
    long diff;

    public PrintOrderHistoryAdapter(Context context, ArrayList<PrinterOrderHistoryDTO> productListDTOS) {
        this.mContext = context;
        this.productListDTOS = productListDTOS;
    }

    @Override
    public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_albumprint_order_history, parent, false);
        return new CustomViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHodler holder, final int position) {
        final PrinterOrderHistoryDTO productListDTO = productListDTOS.get(position);



       // //Log.e("productListDTO", productListDTO.getProdimage());

//        if (!productListDTO.getProdimage().isEmpty())
//
//
//        {
//            Picasso.with(mContext).load(productListDTO.getProdimage()).placeholder(R.drawable.default_album_list)
//                    .into(holder.prodImage);
//        } else {
//            holder.prodImage.setImageResource(R.drawable.default_album_list);
//        }

        holder.orderidTV.setText("#"+productListDTO.getOrderId());

//        //Log.e("getOrderId",productListDTO.getOrderId());
        holder.ratepersheet.setText("₹" + productListDTO.getRateperSheet()+"/Sheet");
        holder.printernameTV.setText(productListDTO.getPrinterName());
        holder.albumsizeTV.setText("Album Size: "+productListDTO.getAlbumSize()+" (inches)");
        holder.totalpriceTV.setText("₹"+productListDTO.gettotalprice());
        String workstatus=productListDTO.getWorkStatus();

        if (workstatus.equalsIgnoreCase("1"))
        {holder.workstatusTV.setText("Order Recieved");
        holder.workstatusTV.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        else if (workstatus.equalsIgnoreCase("2"))
        {
            holder.workstatusTV.setText("In Process");
            holder.workstatusTV.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        else if (workstatus.equalsIgnoreCase("3"))
        {
            holder.workstatusTV.setText("Dispatched");
            holder.workstatusTV.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        else if (workstatus.equalsIgnoreCase("4"))
        {
            holder.workstatusTV.setText("Delivered");
            holder.workstatusTV.setTextColor(mContext.getResources().getColor(R.color.green));
        }
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
        CardView cardView;
        TextView orderidTV,printernameTV,albumsizeTV,ratepersheet,totalpriceTV,workstatusTV;
        public CustomViewHodler(View itemView) {
            super(itemView);
            prodImage=(ImageView)itemView.findViewById(R.id.prodImage);
            orderidTV=itemView.findViewById(R.id.orderidTV);
            printernameTV=itemView.findViewById(R.id.printernameTV);
            albumsizeTV=itemView.findViewById(R.id.albumsizeTV);
            ratepersheet=itemView.findViewById(R.id.ratepersheet);
            totalpriceTV=itemView.findViewById(R.id.totalpriceTV);
            workstatusTV=itemView.findViewById(R.id.workstatusTV);
            cardView=itemView.findViewById(R.id.cardView);
        }
    }
}


