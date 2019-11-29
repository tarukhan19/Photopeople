package com.mobiletemple.photopeople.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.CameraRentalDTO;
import com.mobiletemple.photopeople.session.SessionManager;


import java.util.ArrayList;


public class CameraRentalAdapter extends RecyclerView.Adapter<CameraRentalAdapter.CustomViewHodler>
{

    private Context mContext;
    ArrayList<CameraRentalDTO> productListDTOS;
    long diff ;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager sessionManager;

    public CameraRentalAdapter(Context context, ArrayList<CameraRentalDTO> productListDTOS)
    {

        this.mContext = context;
        this.productListDTOS = productListDTOS;
    }

    @Override
    public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_camerarental_list, parent, false);
        return new CustomViewHodler(itemView);    }

    @Override
    public void onBindViewHolder(CustomViewHodler holder, final int position)
    {
        final CameraRentalDTO productListDTO = productListDTOS.get(position);

        dialog = new ProgressDialog(mContext);
        queue = Volley.newRequestQueue(mContext);
        sessionManager = new SessionManager(mContext);
        holder.locationTV.setText(productListDTO.getLocation());
        holder.mobilenoTV.setText(productListDTO.getMobile());
        holder.nameTV.setText(productListDTO.getName());


    }





    @Override
    public int getItemCount() {
        return productListDTOS.size();
    }

    public class CustomViewHodler extends RecyclerView.ViewHolder {
        TextView locationTV,mobilenoTV,nameTV;

        public CustomViewHodler(View itemView) {
            super(itemView);
            locationTV=itemView.findViewById(R.id.locationTV);
            mobilenoTV=itemView.findViewById(R.id.mobilenoTV);
            nameTV=itemView.findViewById(R.id.nameTV);

        }
    }
}


