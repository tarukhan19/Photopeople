package com.mobiletemple.photopeople.Freelancer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FreelancerImageDialog extends DialogFragment {
    ImageView selectedImage,cross,delete;
    Dialog dialog;
    SessionManager sessionManager;
    int count;

    String image,picid,userid,usertype;
    ProgressDialog progressDialog;
    RequestQueue queue;
    Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        intent = getActivity().getIntent();
        image=intent.getStringExtra("image");
        picid=intent.getStringExtra("imageid");
        userid=intent.getStringExtra("usrid");
        usertype=intent.getStringExtra("usertype");
        sessionManager=new SessionManager(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());


        dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        dialog.setContentView(R.layout.activity_freelancer_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        selectedImage = (ImageView) dialog.findViewById(R.id.selectedImage); // init a ImageView
        cross=dialog.findViewById(R.id.cross);
        delete=dialog.findViewById(R.id.delete);
        Picasso.with(getActivity()).load(image).placeholder(R.color.gray).into(selectedImage);

        if (sessionManager.getLoginSession().get(SessionManager.KEY_USERID).equalsIgnoreCase(userid))
        {
            delete.setVisibility(View.VISIBLE);
        }
        else
        {
            delete.setVisibility(View.GONE);
        }


        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=Integer.parseInt(sessionManager.getLoginSession().get(SessionManager.KEY_IMAGECOUNT));
                Log.e("count",count+"");

                        deletePhoto();

            }
        });



        return dialog;
    }



    private void deletePhoto()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.DELETEPIC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
                            if (status == 200 && message.equalsIgnoreCase("success")) {
                                String object = obj.getString("Data");

                                count=count-1;
                                sessionManager.setLoginSession(sessionManager.getLoginSession().get(SessionManager.KEY_EMAIL),
                                        sessionManager.getLoginSession().get(SessionManager.KEY_USERID),
                                        sessionManager.getLoginSession().get(SessionManager.KEY_NAME),
                                        sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE),
                                        sessionManager.getLoginSession().get(SessionManager.KEY_MOBILE),
                                        sessionManager.getLoginSession().get(SessionManager.KEY_IMAGE),
                                        sessionManager.getLoginSession().get(SessionManager.KEY_FREELANCERTYPE),
                                        String.valueOf(count));
                                Log.e("count2222",count+"");


                                progressDialog.dismiss();
                                dialog.dismiss();
                                startActivity(intent);
                                getActivity().overridePendingTransition(0,0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userid);
                params.put("user_type", usertype);
                params.put("image_id", picid);

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


}
