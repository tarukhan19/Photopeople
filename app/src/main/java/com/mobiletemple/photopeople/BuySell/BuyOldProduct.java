package com.mobiletemple.photopeople.BuySell;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.BuyOldprodAdapter;
import com.mobiletemple.photopeople.model.BuyOldProdDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyOldProduct extends Fragment {
    RecyclerView prodlistrecycle;
    BuyOldprodAdapter productListAdapter;
    ArrayList<BuyOldProdDTO> productListDTOS;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    ImageView emptylist;
    public BuyOldProduct() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buy_old_product, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        prodlistrecycle=view.findViewById(R.id.prodlist);
        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        session = new SessionManager(getActivity());
        emptylist=view.findViewById(R.id.emptylist);

        productListDTOS = new ArrayList<>();
        productListAdapter = new BuyOldprodAdapter(getActivity(), productListDTOS);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        prodlistrecycle.setLayoutManager(mLayoutManager);
        prodlistrecycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        prodlistrecycle.setItemAnimator(new DefaultItemAnimator());
        prodlistrecycle.setAdapter(productListAdapter);



    }
    private void loadProductListCategoryWise()
    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.OLD_PRODUCT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        productListDTOS.clear();

                        dialog.dismiss();
                        //Log.e("PRODUCT_LIST", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success"))
                            {

                                productListDTOS.clear();
                                emptylist.setVisibility(View.GONE);
                                prodlistrecycle.setVisibility(View.VISIBLE);
                                JSONArray product_list = obj.getJSONArray("ProductList");
                                JSONArray categorylist=obj.getJSONArray("categorylist");
                                for (int x = 0; x < product_list.length(); x++)
                                {
                                    JSONObject dataJSONObject = product_list.getJSONObject(x);
                                    JSONArray prodImage=dataJSONObject.getJSONArray("product_image");


                                    BuyOldProdDTO productListDTO = new BuyOldProdDTO();
                                    productListDTO.setProdimage(String.valueOf(prodImage.get(0)));
                                    productListDTO.setProdName(dataJSONObject.getString("product_title"));
                                    productListDTO.setPrice(dataJSONObject.getString("product_price"));
                                    productListDTO.setProdcondition(dataJSONObject.getString("product_condition"));
                                    productListDTO.setProdId(dataJSONObject.getString("product_id"));
                                    String categ_id=dataJSONObject.getString("product_category_id");
                                    for (int j=0;j<categorylist.length();j++)
                                    {
                                        JSONObject catobject = categorylist.getJSONObject(j);
                                        if (categ_id.equalsIgnoreCase(catobject.getString("id")))
                                        {
                                            productListDTO.setCategName(catobject.getString("productt_category"));
                                        }
                                    }
                                    productListDTOS.add(productListDTO);
                                }
                                prodlistrecycle.setAdapter(productListAdapter);
                            }

                            else if (status==0 && message.equalsIgnoreCase("Record Not Found")){
                                emptylist.setVisibility(View.VISIBLE);
                                prodlistrecycle.setVisibility(View.GONE);
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
    public void onStart()
    {

        super.onStart();
        loadProductListCategoryWise();
    }



}
