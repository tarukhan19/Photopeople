package com.mobiletemple.photopeople.adapter;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiletemple.photopeople.BuySell.ProductDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.BuyOldProdDTO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BuyOldprodAdapter extends RecyclerView.Adapter<BuyOldprodAdapter.CustomViewHodler> {

    private Context mContext;
    ArrayList<BuyOldProdDTO> productListDTOS;
    long diff;

    public BuyOldprodAdapter(Context context, ArrayList<BuyOldProdDTO> productListDTOS) {
        this.mContext = context;
        this.productListDTOS = productListDTOS;
    }

    @Override
    public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prod_list, parent, false);
        return new CustomViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHodler holder, final int position) {
        final BuyOldProdDTO productListDTO = productListDTOS.get(position);


        holder.buynow.setVisibility(View.GONE);
        holder.contactme.setVisibility(View.VISIBLE);
        holder.delete.setVisibility(View.GONE);
        //Log.e("productListDTO", productListDTO.getProdimage());

        if (!productListDTO.getProdimage().isEmpty())


        {
            Picasso.with(mContext).load(productListDTO.getProdimage()).placeholder(R.drawable.default_album_list)
                    .into(holder.prodImage);
        } else {
            holder.prodImage.setImageResource(R.drawable.default_album_list);
        }

        holder.prodName.setText(productListDTO.getProdName());
        holder.prodprice.setText("₹" + productListDTO.getPrice());
        holder.prodCategory.setText(productListDTO.getCatgName());
        final String prodId = productListDTO.getProdId();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, ProductDetails.class);
                in.putExtra("productId",productListDTO.getProdId());
                in.putExtra("flag","buysell");
                in.putExtra("prodCondition",productListDTO.getProdcondition());
                in.putExtra("amount",productListDTO.getPrice());
                mContext.startActivity(in);
            }
        });
//        holder.contactme.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent callIntent = new Intent(Intent.ACTION_CALL);
////                callIntent.setData(Uri.parse("tel:123456789"));
////                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
////
////                    return;
////                }
////                mContext.startActivity(callIntent);
//            }
//        });

       // setAnimation(holder.cardView);
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
        TextView buynow,delete,contactme;
        CardView cardView;
        TextView prodName,prodCategory,prodprice;
        public CustomViewHodler(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardView);
            prodImage=(ImageView)itemView.findViewById(R.id.prodpic);
            prodName=itemView.findViewById(R.id.prodname);
            prodCategory=itemView.findViewById(R.id.prodcat);
            prodprice=itemView.findViewById(R.id.prodprice);
            buynow=itemView.findViewById(R.id.buynow);
            delete=itemView.findViewById(R.id.delete);
            contactme=itemView.findViewById(R.id.contactme);
        }
    }
}


