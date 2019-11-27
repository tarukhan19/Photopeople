package com.mobiletemple.photopeople.BuySell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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
import com.mobiletemple.photopeople.adapter.MyProductAdapter;
import com.mobiletemple.photopeople.model.MyProductDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellProductActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    RecyclerView prodlistrecycle;
    MyProductAdapter productListAdapter;
    ArrayList<MyProductDTO> productListDTOS;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    ImageView emptylist;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_product);
        prodlistrecycle=findViewById(R.id.prodlist);
        emptylist=findViewById(R.id.emptylist);
        FloatingActionMenu fab = (FloatingActionMenu) findViewById(R.id.fab);
        fab.getMenuIconView().setImageResource(R.drawable.add_new_product);
        fab.getMenuIconView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SellProductActivity.this,Addproduct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("My Product List");
        filter.setVisibility(View.GONE);


        requestQueue = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);
        session = new SessionManager(this);

        productListDTOS = new ArrayList<>();
        productListAdapter = new MyProductAdapter(this, productListDTOS);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        prodlistrecycle.setLayoutManager(mLayoutManager);
        prodlistrecycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        prodlistrecycle.setItemAnimator(new DefaultItemAnimator());
        prodlistrecycle.setAdapter(productListAdapter);



        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SellProductActivity.this,BuySellActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

    }


    private void loadProductListCategoryWise()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.MY_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        productListDTOS.clear();

                        dialog.dismiss();
                        //Log.e("MY_PRODUCT", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success"))
                            {
                                prodlistrecycle.setVisibility(View.VISIBLE);
                                emptylist.setVisibility(View.GONE);

                                productListDTOS.clear();
                                JSONArray product_list = obj.getJSONArray("ProductList");
                                JSONArray categorylist=obj.getJSONArray("categorylist");
                                for (int x = 0; x < product_list.length(); x++)
                                {
                                    //Log.e("product_list",product_list+"");
                                    JSONObject dataJSONObject = product_list.getJSONObject(x);
                                    JSONArray prodImage=dataJSONObject.getJSONArray("product_image");

                                    MyProductDTO productListDTO = new MyProductDTO();
                                    productListDTO.setProdimage(String.valueOf(prodImage.get(0)));
                                    productListDTO.setProdName(dataJSONObject.getString("product_title"));
                                    productListDTO.setPrice(dataJSONObject.getString("product_price"));
                                    productListDTO.setProdcondition(dataJSONObject.getString("product_condition"));
                                    //productListDTO.setDayesago(dataJSONObject.getString("date"));
                                    productListDTO.setProdId(dataJSONObject.getString("product_id"));
                                    String categ_id=dataJSONObject.getString("product_category_id");
                                    //Log.e("categ_id",categ_id);

                                    for (int j=0;j<categorylist.length();j++)
                                    {
                                        JSONObject catobject = categorylist.getJSONObject(j);
                                        if (categ_id.equalsIgnoreCase(catobject.getString("id")))
                                        {
                                            productListDTO.setCategName(catobject.getString("productt_category"));
                                            //Log.e("product_category",catobject.getString("productt_category"));
                                        }
                                    }
                                    productListDTOS.add(productListDTO);
                                }
                                prodlistrecycle.setAdapter(productListAdapter);
                            }

                            else {
                                prodlistrecycle.setVisibility(View.GONE);
                                emptylist.setVisibility(View.VISIBLE);

                                productListDTOS.clear();
                                productListAdapter.notifyDataSetChanged();}

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
                params.put("user_id",session.getLoginSession().get(SessionManager.KEY_USERID) );
                params.put("user_type",session.getLoginSession().get(SessionManager.KEY_USER_TYPE) );

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
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent=new Intent(SellProductActivity.this,BuySellActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else { loadProductListCategoryWise();}

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

