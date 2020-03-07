package com.mobiletemple.photopeople.userauth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mobiletemple.photopeople.Freelancer.FreelancerProfileOne;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.OneTimeSignupCheck;
import com.mobiletemple.photopeople.PayuMoneyIntegration.ProdPurchaseActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.Studio.StudioProfileOne;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.fcm.Config;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtpActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    static EditText ET;
    LinearLayout verify;
    TextView resend;
    private LinearLayout backIV;
    private String userType;
    private String fname, lname, mobile, password, pic,country_code;
    private byte[] profilePic = null;
    private boolean forChangePassword;
    SessionManager sessionManager;
    ProgressDialog pd;
    boolean isConnected;
    private String verificationId;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String regId;
    RequestQueue requestQueue;
    OneTimeSignupCheck oneTimeSignupCheck=new OneTimeSignupCheck();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        sessionManager = new SessionManager(getApplicationContext());
        backIV=findViewById(R.id.backIV);
        pd = new ProgressDialog(OtpActivity.this);
        requestQueue = Volley.newRequestQueue(OtpActivity.this);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        SharedPreferences sp = getSharedPreferences("photo", MODE_PRIVATE);
        ET=findViewById(R.id.ET);

        verify=findViewById(R.id.verify);
        resend=findViewById(R.id.resend);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(OtpActivity.this,RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( OtpActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        regId = instanceIdResult.getToken();
                        Log.e("Token",regId);
                    }
                });
        Intent in = getIntent();
        forChangePassword = in.getBooleanExtra("forChangePassword", false);
        mobile = in.getStringExtra("mobile");
        country_code=in.getStringExtra("country_code");


        if (!forChangePassword)
        {
            userType = in.getStringExtra("userType");
            fname = in.getStringExtra("fname");
            lname = in.getStringExtra("lname");
            password = in.getStringExtra("password");

            pic = sp.getString("pic", "");
            if (!pic.isEmpty()) {
                profilePic = Base64.decode(pic, Base64.NO_WRAP);
            }
        }

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else
                {
                    Toast.makeText(OtpActivity.this, "OTP resend successfully ", Toast.LENGTH_SHORT).show();
                    sendVerificationCode(country_code+mobile);

                }

            }
        });



        verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
            if (ET.getText().toString().isEmpty())
            {
                Toast.makeText(OtpActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            }
               else {

                if (!isConnected) {
                    showSnack(isConnected);
                } else {
                    String otp = ET.getText().toString();
                    verifyCode(otp);
                }
            }

            }
        });


       sendVerificationCode(country_code+mobile);


    }

    private void sendVerificationCode(String number)
    {
        Log.e("number",number);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                120,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Log.e("code",code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("e.getMessage()",e+"");
        }
    };



    private void verifyCode(String code)
    {
        pd.setMessage("OTP Verifying ...");
        pd.setCancelable(true);
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }



    private void signInWithCredential(PhoneAuthCredential credential)
    {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            pd.dismiss();
                            if (!forChangePassword)
                            {
                                SignUpTask task1 = new SignUpTask();
                                task1.execute();
                            } else {
                                Intent in = new Intent(OtpActivity.this, ResetPassword.class);
                                in.putExtra("mobile", mobile);
                                startActivity(in);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);
                            }

                        } else
                         {
                             pd.dismiss();
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    class SignUpTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {

            pd.setMessage("Registration In Process ...");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Endpoints comm = new Endpoints();
            //Log.e("profile", pic);
            try {
                JSONObject ob = new JSONObject();
                ob.put("login_type", Constants.MANAUAL_LOGIN_TYPE); // 3 for Manual
                ob.put("fname", fname);
                ob.put("lname", lname);
                ob.put("contact_no", mobile);
                ob.put("password", password);
                ob.put("user_type", userType);
                ob.put("country_code",country_code);
                ob.put("device_type", 1); // 1 for Android
                ob.put("device_id", regId);
//                Toast.makeText(OTPActivity.this, ">>"+ displayFirebaseRegId(), Toast.LENGTH_SHORT).show();
                ob.put("tnc", 1);
                Log.e("SignUP", ob.toString());
                //Log.e("",""+profilePic);
                // String result = comm.getStringResponse(Endpoints.SIGNUP_URL,ob);
                String result = comm.forSignUp(Endpoints.SIGNUP_URL, ob, profilePic);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            //  return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.e("SignUpTask", s);
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    final String user_id = obj.getString("user_id");
                    sessionManager.setuserid(user_id);

                    if (status == 200 && message.equalsIgnoreCase("success"))
                    {
                        Users user = new Users(user_id, mobile,fname,"");
                        databaseReference.push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                pd.dismiss();
                                checkUserPaymentStatus(user_id,userType);

                               // oneTimeSignupCheck.checkUserPaymentStatus(requestQueue,pd,user_id,userType);
                           //
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        checkUserPaymentStatus(user_id,userType);
                                    }
                                });



                    } else {
                        Toast.makeText(OtpActivity.this, "SignUp Failed; Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    private void switchToNextScreen()
    {
        pd.dismiss();
        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Successfully Registered !");
        sweetAlertDialog.show();
        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(OtpActivity.this,R.color.colorPrimary));
        sweetAlertDialog.setConfirmText("Ok");
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                if (userType .equalsIgnoreCase("1") ) {
                    Intent in = new Intent(OtpActivity.this, StudioProfileOne.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    in.putExtra("fname", fname);
                    in.putExtra("lname", lname);
                    in.putExtra("mobile", mobile);
                    in.putExtra("userType", userType);
                    startActivity(in);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out); }
                if (userType .equalsIgnoreCase("2")) {
                    Intent in = new Intent(OtpActivity.this, FreelancerProfileOne.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    in.putExtra("fname", fname);
                    in.putExtra("lname", lname);
                    in.putExtra("mobile", mobile);
                    in.putExtra("userType", userType);
                    startActivity(in);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
            }
        });
        sweetAlertDialog.show();

    }


    // Send OTP
    class OtpTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(OtpActivity.this);
            pd.setMessage("OTP Re-Sending ...");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Endpoints comm = new Endpoints();

            try {
                JSONObject ob = new JSONObject();
                ob.put("contact_no", strings[0]);

                String result = comm.getStringResponse(Endpoints.OTP_URL, ob);
                //Log.e("OtpTask", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            //  return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.e("OtpTask ", s);
            pd.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equals("success")) {
                        Toast.makeText(OtpActivity.this, "OTP Send Successfully  !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OtpActivity.this, "OTP Sending Failed; Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);

    }



    public void checkUserPaymentStatus( final String user_id,
                                       final String userType)
    {
        pd.setMessage("Please Wait..");
        pd.setCancelable(true);
        pd.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.ONE_TIME_FEE_ACTIVATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("msg");
                            if (status == 1)
                            {
                                if (message.equalsIgnoreCase("payment option is off for studio!")  ||

                                    message.equalsIgnoreCase("payment option is off for freelancer!"))
                                   {switchToNextScreen();}

                                else if (message.equalsIgnoreCase("Please complete your payment Process!"))
                                {
                                    JSONObject object=obj.getJSONObject("data");
                                    String amount=object.getString("amount");

                                    openOneTimePayDialog(amount,user_id);
                                }
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
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();

                ob.put("user_type",userType );
                ob.put("user_id", user_id);
                //Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private void openOneTimePayDialog(final String amount, final String user_id)
    {
        final Dialog dialog = new Dialog(OtpActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_onetimesignup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                OtpActivity.this.finish();
                finishAffinity();
                finish();

            }
        });
        TextView amountTV = (TextView) dialog.findViewById(R.id.amount) ;
        amountTV.setText(amount);
        LinearLayout okBT = (LinearLayout) dialog.findViewById(R.id.submit) ;

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(OtpActivity.this, ProdPurchaseActivity.class);
                in.putExtra("amount", amount);
                in.putExtra("usertype", userType);
                in.putExtra("userid", user_id);
                in.putExtra("mobile", mobile);
                in.putExtra("name", fname);
                in.putExtra("from","otp");
                in.putExtra("prodname","onetimesignup");


                startActivity(in);
                finish();
            }
        });
        dialog.show();
    }

}
