package com.mobiletemple.photopeople.BuySell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.NewProdOrderAdapter;
import com.mobiletemple.photopeople.model.NewProdOrderDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewProductOrderHistory extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    NewProdOrderAdapter productListAdapter;
    ArrayList<NewProdOrderDTO> productListDTOS;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    ImageView emptyimage;
    SessionManager session;
    boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product_order_history);
        emptyimage=findViewById(R.id.emptyIMAGE);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Product History");
        filter.setVisibility(View.GONE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NewProductOrderHistory.this,BuyProductsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });
        dialog = new ProgressDialog(this);
        session = new SessionManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);

        recyclerView = (RecyclerView) findViewById(R.id.prodlist);
        productListDTOS = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        productListAdapter = new NewProdOrderAdapter(this, productListDTOS);
        recyclerView.setAdapter(productListAdapter);

    }

    private void loadProductListCategoryWise() {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.NEWPRODUCT_ORDER_HISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        productListDTOS.clear();

                        dialog.dismiss();
                        //Log.e("PRINTER_ORDER_HISTORY", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success")) {
                                emptyimage.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                productListDTOS.clear();
                                JSONArray product_list = obj.getJSONArray("product_list");
                                for (int x = 0; x < product_list.length(); x++) {
                                    JSONObject dataJSONObject = product_list.getJSONObject(x);
                                      JSONArray prodImage = dataJSONObject.getJSONArray("product_image");


                                    NewProdOrderDTO productListDTO = new NewProdOrderDTO();
                                    productListDTO.setProdimage(String.valueOf(prodImage.get(0)));
                                    productListDTO.setOrderId(dataJSONObject.getString("order_id"));
                                    //Log.e("orderid",dataJSONObject.getString("order_id"));
                                    productListDTO.setPoductName(dataJSONObject.getString("product_title"));
                                    productListDTO.settotalprice(dataJSONObject.getString("amount"));
                                    productListDTO.setWorkStatus(dataJSONObject.getString("delivered_status"));
                                    productListDTO.setTime(dataJSONObject.getString("order_date"));
                                    productListDTOS.add(productListDTO);
                                }
                                recyclerView.setAdapter(productListAdapter);
                            } else if (status == 0 && message.equalsIgnoreCase("Record Not Found")) {
                                emptyimage.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                productListDTOS.clear();
                                productListAdapter.notifyDataSetChanged();
                            }

                        } catch (Exception ex) {
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
                params.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                //  params.put("user_type",session.getLoginSession().get(SessionManager.KEY_USER_TYPE) );

                //Log.e("params", params.toString());
                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(NewProductOrderHistory.this,BuyProductsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }

    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else {    loadProductListCategoryWise();}

        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            loadProductListCategoryWise();
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }
}
