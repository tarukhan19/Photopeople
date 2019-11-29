package com.mobiletemple.photopeople.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.mobiletemple.photopeople.BuySell.ProductDetails;
import com.mobiletemple.photopeople.BuySell.SellProductActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.model.MyProductDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.CustomViewHodler>
{

    private Context mContext;
    ArrayList<MyProductDTO> productListDTOS;
    long diff ;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager sessionManager;

    public MyProductAdapter(Context context, ArrayList<MyProductDTO> productListDTOS)
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
        final MyProductDTO productListDTO = productListDTOS.get(position);

        dialog = new ProgressDialog(mContext);
        queue = Volley.newRequestQueue(mContext);
        sessionManager = new SessionManager(mContext);

        holder.buynow.setVisibility(View.GONE);
        holder.delete.setVisibility(View.VISIBLE);

        if (!productListDTO.getProdimage().isEmpty())


        { Picasso.with(mContext).load(productListDTO.getProdimage()).placeholder(R.drawable.default_album_list)
                .into(holder.prodImage);
        }
        else {holder.prodImage.setImageResource(R.drawable.default_album_list);}

        holder.prodName.setText(productListDTO.getProdName());
        holder.prodprice.setText("₹"+productListDTO.getPrice());
        holder.prodCategory.setText(productListDTO.getCatgName());
        final String prodId=productListDTO.getProdId();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, ProductDetails.class);
                intent.putExtra("flag","myproduct");
                intent.putExtra("productId",prodId);


                intent.putExtra("prodCondition",productListDTO.getProdcondition());
                intent.putExtra("amount",productListDTO.getPrice());
                mContext.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDial();
            }

            private void openConfirmDial()
            {

                final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("You want to delete it");
                sweetAlertDialog.show();



                Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                btn.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

                sweetAlertDialog.setConfirmText("Yes");
                btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        sweetAlertDialog.dismissWithAnimation();
                        deleteProd();

                    }
                });

                Button canbtn = (Button) sweetAlertDialog.findViewById(R.id.cancel_button);
                btn.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));

                sweetAlertDialog.setCancelText("No");
                canbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sweetAlertDialog.dismissWithAnimation();

                    }
                });
                sweetAlertDialog.show();

            }

            private void deleteProd()
            {
                dialog.setMessage("Please Wait..");
                dialog.setCancelable(true);
                dialog.show();

                StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PRODUCT_DELETE,
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

                                    if (status == 200 && message.equals("success")) {

                                        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Successfully")
                                                .setContentText("Product Deleted!")
                                                .setConfirmText("Ok")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        Intent in = new Intent(mContext, SellProductActivity.class);
                                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                        mContext.startActivity(in);

                                                        sDialog.dismissWithAnimation();
                                                    }
                                                })
                                                .show();
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
                        params.put("product_id", prodId);
                        params.put("user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                        params.put("user_type",sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));

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
        TextView buynow,delete;
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

        }
    }
}

