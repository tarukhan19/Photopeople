package com.mobiletemple.photopeople.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.PayuMoneyIntegration.ProdPurchaseActivity;
import com.mobiletemple.photopeople.BuySell.ProductDetails;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.BuyNewProdDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyNewprodAdapter extends RecyclerView.Adapter<BuyNewprodAdapter.CustomViewHodler>
{

    private Context mContext;
    ArrayList<BuyNewProdDTO> productListDTOS;
    long diff ;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager sessionManager;

    public BuyNewprodAdapter(Context context, ArrayList<BuyNewProdDTO> productListDTOS)
    {

        this.mContext = context;
        this.productListDTOS = productListDTOS;
    }

    @Override
    public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prod_list, parent, false);
        return new CustomViewHodler(itemView);    }

    @Override
    public void onBindViewHolder(CustomViewHodler holder, final int position)
    {
        final BuyNewProdDTO productListDTO = productListDTOS.get(position);

        dialog = new ProgressDialog(mContext);
        queue = Volley.newRequestQueue(mContext);
        sessionManager = new SessionManager(mContext);
        holder.buynow.setVisibility(View.VISIBLE);
        holder.contactme.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);
        //Log.e("productListDTO",productListDTO.getProdimage());
        if (!productListDTO.getProdimage().isEmpty())
        {
            Picasso.with(mContext).load(productListDTO.getProdimage()).placeholder(R.drawable.default_album_list)
                .into(holder.prodImage);
        }
        else {
            holder.prodImage.setImageResource(R.drawable.default_album_list);
        }

        holder.prodName.setText(productListDTO.getProdName());
        holder.prodprice.setText("₹"+productListDTO.getPrice());
        holder.prodCategory.setText(productListDTO.getCatgName());
        final String prodId=productListDTO.getProdId();
        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
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

        holder.buynow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if (sessionManager.getMyLatLong().get(SessionManager.KEY_MYCOUNTRY).
                        equalsIgnoreCase(productListDTO.getCountryname())) {


                    String prdId = productListDTO.getProdId();
                    String amnt = productListDTO.getPrice();
                    generateQuoteId(prdId, amnt,productListDTO.getProdName());

                }

                else
                {
                    Toast.makeText(mContext, "You are not able to purchase this product because it is from different country.", Toast.LENGTH_SHORT).show();
                }
            }
        });

     //   setAnimation(holder.cardView);

    }



    private void generateQuoteId(final String prdId, final String amnt, final String prodName)
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.GENERATE_PRODUCID,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                String quoteId=obj.getString("printe_quota_id");
                                Intent intent=new Intent(mContext, ProdPurchaseActivity.class);
                                intent.putExtra("quoteid",quoteId);
                                intent.putExtra("amount",amnt);
                                intent.putExtra("from","buysell");
                                intent.putExtra("prodname",prodName);
                                mContext.startActivity(intent);

                            } else
                                Toast.makeText(mContext.getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("product_id", prdId);
                params.put("user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type",sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                params.put("product_quentity","1");
                params.put("amount",amnt);

                //Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
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


