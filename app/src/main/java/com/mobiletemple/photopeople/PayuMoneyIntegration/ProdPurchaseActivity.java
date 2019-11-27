package com.mobiletemple.photopeople.PayuMoneyIntegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mobiletemple.photopeople.AlbumPrinting.PrinterWelcomepage;
import com.mobiletemple.photopeople.ProductWelcomepage;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.WelcomePageActivity;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.OneTimeSignupActivation;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ProdPurchaseActivity extends AppCompatActivity {
    private boolean isDisableExitConfirmation = false;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    Intent intent;
    String amount, quoteid, mobileno, name, prodname, emailid, paymentid, transactionid, from,
            freelancerId, user_id, usertype,  profile_image, feelancer_type, photoscount;
    int stage;
    LinearLayout linearlayout, submit;
    EditText emailidET;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_purchase);
        linearlayout = findViewById(R.id.linearlayout);
        emailidET = findViewById(R.id.emailidET);
        submit = findViewById(R.id.submit);
        sessionManager = new SessionManager(this);
        intent = getIntent();

        if (intent.hasExtra("from")) {
            linearlayout.setVisibility(View.VISIBLE);
            amount = intent.getStringExtra("amount");
            from = intent.getStringExtra("from");
            prodname = intent.getStringExtra("prodname");

            if (from.equalsIgnoreCase("otp"))
            {
                user_id = intent.getStringExtra("userid");
                usertype = intent.getStringExtra("usertype");
                mobileno = intent.getStringExtra("mobile");
                name = intent.getStringExtra("name");

            }
            else if (from.equalsIgnoreCase("login")) {
                profile_image = intent.getStringExtra("profile_image");
                feelancer_type = intent.getStringExtra("feelancer_type");
                photoscount = intent.getStringExtra("photoscount");
                user_id = intent.getStringExtra("userid");
                usertype = intent.getStringExtra("usertype");
                mobileno = intent.getStringExtra("mobile");
                name = intent.getStringExtra("name");

            }
            else {
                mobileno = sessionManager.getLoginSession().get(SessionManager.KEY_MOBILE);
                name = sessionManager.getLoginSession().get(SessionManager.KEY_NAME);
                quoteid = intent.getStringExtra("quoteid");

                if (from.equalsIgnoreCase("booking")) {
                    freelancerId = intent.getStringExtra("freelancerId");
                    stage = intent.getIntExtra("stage", 0);
                }
            }
        } else {
            linearlayout.setVisibility(View.GONE);
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailid = emailidET.getText().toString();
                if (emailid.isEmpty()) {
                    Toast.makeText(ProdPurchaseActivity.this, "Please enter your email Id", Toast.LENGTH_SHORT).show();
                } else {
                    launchPayUMoneyFlow();
                }
            }
        });


    }

    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();


        transactionid = System.currentTimeMillis() + "";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";
        builder.setAmount(amount)
                .setTxnId(transactionid)
                .setPhone(mobileno)
                .setProductName(prodname)
                .setFirstName(name)
                .setEmail(emailid)
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(false)
//                .setKey("7PZe86")
//                .setMerchantId("5070302");
                .setKey("7PZe86")
                .setMerchantId("5070302");
        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);


            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, this, R.style.PayumoneyAppTheme, false);


        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

//        stringBuilder.append("pxIoqvgD");
        stringBuilder.append("pxIoqvgD");

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);
        return paymentParam;
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.e("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    String payuResponse = transactionResponse.getPayuResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(payuResponse);
                        JSONObject resultObj = jsonObject.getJSONObject("result");
                        paymentid = resultObj.getString("paymentId");

                        if (from.equalsIgnoreCase("buysell")) {
                            Intent in = new Intent(ProdPurchaseActivity.this, ProductWelcomepage.class);
                            in.putExtra("amount", amount);
                            in.putExtra("first_name", name);
                            in.putExtra("Mobile_no", mobileno);
                            in.putExtra("email", emailid);
                            in.putExtra("randomString", transactionid);
                            in.putExtra("quoteid", quoteid);
                            in.putExtra("paymentid", paymentid);
                            startActivity(in);
                            finish();
                        } else if (from.equalsIgnoreCase("albumprinting")) {
                            Intent in = new Intent(ProdPurchaseActivity.this, PrinterWelcomepage.class);
                            in.putExtra("amount", amount);
                            in.putExtra("first_name", name);
                            in.putExtra("Mobile_no", mobileno);
                            in.putExtra("email", emailid);
                            in.putExtra("randomString", transactionid);
                            in.putExtra("quoteid", quoteid);
                            in.putExtra("paymentid", paymentid);
                            startActivity(in);
                            finish();
                        } else if (from.equalsIgnoreCase("booking")) {
                            Intent in = new Intent(ProdPurchaseActivity.this, WelcomePageActivity.class);
                            in.putExtra("amount", amount);
                            in.putExtra("stage", stage);
                            in.putExtra("first_name", name);
                            in.putExtra("freelancerId", freelancerId);
                            in.putExtra("Mobile_no", mobileno);
                            in.putExtra("email", emailid);
                            in.putExtra("randomString", transactionid);
                            in.putExtra("quoteid", quoteid);
                            in.putExtra("paymentid", paymentid);


                            startActivity(in);
                            finish();
                        } else if (from.equalsIgnoreCase("otp")) {
                            Intent in = new Intent(ProdPurchaseActivity.this, OneTimeSignupActivation.class);
                            in.putExtra("amount", amount);
                            in.putExtra("userid", user_id);
                            in.putExtra("usertype", usertype);
                            in.putExtra("randomString", transactionid);
                            in.putExtra("paymentid", paymentid);
                            in.putExtra("email", emailid);
                            in.putExtra("name", name);

                            in.putExtra("from", from);

                            startActivity(in);
                            finish();
                        }
                        else if (from.equalsIgnoreCase("login")) {
                            Intent in = new Intent(ProdPurchaseActivity.this, OneTimeSignupActivation.class);
                            in.putExtra("amount", amount);
                            in.putExtra("userid", user_id);
                            in.putExtra("usertype", usertype);
                            in.putExtra("randomString", transactionid);
                            in.putExtra("paymentid", paymentid);
                            in.putExtra("from", from);
                            in.putExtra("email", emailid);
                            in.putExtra("mobileno",mobileno);
                            in.putExtra("profile_image", profile_image);
                            in.putExtra("feelancer_type", feelancer_type);
                            in.putExtra("photoscount", photoscount);
                            in.putExtra("name", name);


                            startActivity(in);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Log.e("payuresponse", payuResponse);
                } else {
                    //Failure Transaction
                }


            } else if (resultModel != null && resultModel.getError() != null) {
                Log.e("Error response : ", resultModel.getError().getTransactionResponse().toString());
            } else {
                Log.e("TAG", "Both objects are null!");
            }
        }
    }


}
