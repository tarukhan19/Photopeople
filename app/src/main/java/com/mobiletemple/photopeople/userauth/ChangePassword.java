package com.mobiletemple.photopeople.userauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.ProfileUpdate;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.UIValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePassword extends AppCompatActivity   implements ConnectivityReceiver.ConnectivityReceiverListener{
EditText oldpass,newpass,confnewpass;
LinearLayout done;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager session;
String oldpassS="", newpassS="", confnewpassS="";
boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        dialog = new ProgressDialog(this);
        session = new SessionManager(getApplicationContext());
        queue = Volley.newRequestQueue(ChangePassword.this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Change Password");
        filter.setVisibility(View.GONE);
        oldpass=findViewById(R.id.oldpass);
        newpass=findViewById(R.id.newpass);
        confnewpass=findViewById(R.id.confnewpass);
        done=findViewById(R.id.done);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChangePassword.this, ProfileUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isvalidate=true;
                oldpassS=oldpass.getText().toString();
                if (oldpassS.isEmpty())
                {
                    Toast.makeText(ChangePassword.this, "Enter old password", Toast.LENGTH_SHORT).show();
                    isvalidate=false;
                }


                newpassS = newpass.getText().toString();
                String passwordValidateMSG = UIValidation.passwordValidate(newpassS, true);
                //Log.e("Pass Val", passwordValidateMSG);
                if (!passwordValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    newpass.setError(passwordValidateMSG);
                    isvalidate = false;
                }

                confnewpassS = confnewpass.getText().toString();
                String cpasswordValidateMSG = UIValidation.passwordValidate(confnewpassS, true);
                //Log.e("CPass Val", cpasswordValidateMSG);
                if (!cpasswordValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    confnewpass.setError(cpasswordValidateMSG);
                    isvalidate = false;
                }

                if (!confnewpassS.equals(newpassS)) {
                    confnewpass.setError("Password Not Match !");
                    isvalidate = false;
                }

                if (isvalidate)
                {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else{changePassword();}
                        }
            }
        });
    }

    private void changePassword()

    {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200)
                            {

                                final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitleText("Password Changed!");
                                sweetAlertDialog.show();

                                Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(ChangePassword.this,R.color.colorPrimary));

                                sweetAlertDialog.setConfirmText("Ok");
                                btn.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        session.logoutUser();
                                        startActivity(new Intent(ChangePassword.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);

                                        finish();
                                    }
                                });
                                sweetAlertDialog.show();


                            } else
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                Map<String, String> ob = new HashMap<>();
                ob.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("old_password", oldpassS);
                ob.put("new_password", newpassS);
                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
                Intent intent=new Intent(ChangePassword.this,ProfileUpdate.class);
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
