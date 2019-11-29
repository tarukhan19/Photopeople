package com.mobiletemple.photopeople.userauth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.ChatNotification.Token;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.PayuMoneyIntegration.ProdPurchaseActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.fcm.Config;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.UIValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    TextView register, forPass;
    LinearLayout login;
    EditText mobilenoET, passwordET;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager mSessionManager;
    private String mobile = "", password = "", email = "";
    boolean isConnected;
    String feelancer_type = "";
    private FirebaseAuth mAuth;
    JSONObject obj;
    String id, first_name, user_type, profile_image, phone, photoscount;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener authListener;


    private String verificationId;

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(LoginActivity.this);
        mSessionManager = new SessionManager(getApplicationContext());
        mobilenoET = findViewById(R.id.mobileno);
        passwordET = findViewById(R.id.password);
        login = findViewById(R.id.login);
        forPass = findViewById(R.id.forPass);
        register = findViewById(R.id.register);
        mayRequestPermissions();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    Log.e("userid", userId);
                }
            }
        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Whomi.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });

        forPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                boolean validate = true;
                mobile = mobilenoET.getText().toString();
                String mobileValidateMSG = UIValidation.mobileValidate(mobile, true);
                //Log.e("MOB Val", mobileValidateMSG);
                if (!mobileValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    mobilenoET.setError(mobileValidateMSG);
                    validate = false;
                }

                password = passwordET.getText().toString();
                String passwordValidateMSG = UIValidation.passwordValidate(password, true);
                //Log.e("Pass Val", passwordValidateMSG);
                if (!passwordValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    passwordET.setError(passwordValidateMSG);
                    validate = false;
                }

                if (validate) {
//                    if (!isConnected) {
//                        showSnack(isConnected);
//                    } else {
                    loginReq();

                    // }

                }
            }
        });


    }

    private void loginReq() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);

                        try {
                            obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success")) {
                                JSONObject object = null;
                                try {
                                    object = obj.getJSONObject("data");
                                    id = object.getString("id");
                                    email = object.getString("email");
                                    first_name = object.getString("first_name");
                                    user_type = object.getString("user_type");
                                    profile_image = object.getString("profile_image");
                                    phone = object.getString("phone");
                                    photoscount = object.getString("photoscount");

                                    if (object.has("feelancer_type")) {
                                        feelancer_type = object.getString("feelancer_type");
                                    }
                                    insertInDatabase();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                sendVerificationCode("+91"+mobile);


                            } else
                                progressDialog.dismiss();
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
                params.put("login_type", "3");
                params.put("device_type", "1");
                params.put("device_id", displayFirebaseRegId());
                params.put("contact_no", mobile);
                params.put("password", password);
                params.put("email", email);
                params.put("facebook_id", "");
                params.put("google_id", "");
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void insertInDatabase() {
        databaseReference.orderByChild("mobileno").equalTo(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // switchToOtherActivity();
                    checkUserPaymentStatus();
                } else {
                    Users user = new Users(id, mobile,first_name,profile_image);

                    databaseReference.push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkUserPaymentStatus();
                            //switchToOtherActivity();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    /// switchToOtherActivity();
                                    checkUserPaymentStatus();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void switchToOtherActivity() {


        progressDialog.dismiss();
        mSessionManager.setLoginSession(email, id, first_name, user_type, phone, profile_image, feelancer_type, photoscount);
        mSessionManager.setImageSession(profile_image);
        mSessionManager.setuserid(id);
        startActivity(new Intent(LoginActivity.this, HomePage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("from", "other"));
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);


    }

    private String displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        try {
            //Log.e("regId", "" + regId);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return regId;
    }

    private boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) + checkSelfPermission(READ_EXTERNAL_STORAGE) + checkSelfPermission(CALL_PHONE) +
                checkSelfPermission(WRITE_EXTERNAL_STORAGE) + checkSelfPermission(CALL_PHONE) +
                checkSelfPermission(ACCESS_FINE_LOCATION) + checkSelfPermission(ACCESS_COARSE_LOCATION) +
                checkSelfPermission(READ_SMS) + checkSelfPermission(SEND_SMS) +
                checkSelfPermission(RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.permission_rationale);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage("Please confirm access to files & folders");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                                    WRITE_EXTERNAL_STORAGE, CAMERA, CALL_PHONE,
                                    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                            Constants.PERMISSION_REQUEST);
                }
            });
            builder.show();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE, CAMERA, CALL_PHONE,
                            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_REQUEST);
        }
        return false;
    }

    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
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
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
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
        this.isConnected = isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);


    }

    public void checkUserPaymentStatus() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.ONE_TIME_FEE_ACTIVATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("msg");
                            if (status == 1)
                            {
                                if (message.equalsIgnoreCase("payment option is off for studio!") ||

                                        message.equalsIgnoreCase("payment option is off for freelancer!")) {
                                    switchToOtherActivity();
                                } else if (message.equalsIgnoreCase("Please complete your payment Process!")) {
                                    JSONObject object = obj.getJSONObject("data");
                                    String amount = object.getString("amount");

                                    openOneTimePayDialog(amount);
                                }
                            }

                            else if (status==0 && message.equalsIgnoreCase("success"))
                            {
                                switchToOtherActivity();
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
                Map<String, String> ob = new HashMap<>();

                ob.put("user_type", user_type);
                ob.put("user_id", id);
                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void openOneTimePayDialog(final String amount) {
        final Dialog dialog = new Dialog(LoginActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_onetimesignup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView amountTV = (TextView) dialog.findViewById(R.id.amount);
        amountTV.setText(amount);
        LinearLayout okBT = (LinearLayout) dialog.findViewById(R.id.submit);

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, ProdPurchaseActivity.class);
                in.putExtra("amount", amount);
                in.putExtra("usertype", user_type);
                in.putExtra("userid", id);
                in.putExtra("mobile", phone);
                in.putExtra("name", first_name);
                in.putExtra("from", "login");
                in.putExtra("prodname", "onetimesignup");
                in.putExtra("profile_image", profile_image);
                in.putExtra("feelancer_type", feelancer_type);
                in.putExtra("photoscount", photoscount);


                startActivity(in);
                finish();
            }
        });
        dialog.show();
    }


}
