package com.mobiletemple.photopeople.userauth;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.hbb20.CountryCodePicker;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.ProfilePic.PickerBuilder;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.BitmapUtil;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.MarshMallowPermission;
import com.mobiletemple.photopeople.util.UIValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    ImageView imageIV;
    String fullname,mobileno,password,confpassword,countrycode;
    LinearLayout register;
    TextView tandc;
    TextView login;
    EditText fullnameET,mobilenoET,passwordET,confpasswordET;
    private String userType;
    private SessionManager mSessionManager;
    ProgressDialog pd;
    boolean isConnected;
    byte[] profilePicbyte = null;

    CountryCodePicker codePicker;

    private int CAMERA_REQUEST = 11, GALLERY_REQUEST = 12;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private Bitmap profilePic;

    float maxHeight = 1000.1f;
    float maxWidth = 1000.1f;
    float minHeight = 150.1f;
    float minWidth = 100.1f;
    float imgRatio,maxRatio ;
    int actualWidth,actualHeight;
    Bitmap  imageUpload;
    MarshMallowPermission marshMallowPermission;

    Intent in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mSessionManager = new SessionManager(getApplicationContext());
        marshMallowPermission = new MarshMallowPermission(RegistrationActivity.this);
        pd = new ProgressDialog(RegistrationActivity.this);
        tandc=findViewById(R.id.tandc);
        codePicker=findViewById(R.id.ccp);


//        if (checkAndRequestPermissions()) {
//            // carry on the normal flow, as the case of  permissions  granted.
//        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imageIV=findViewById(R.id.iv_pic);
        fullnameET=findViewById(R.id.fullname);
        mobilenoET=findViewById(R.id.mobileno);
        passwordET=findViewById(R.id.password);
        confpasswordET=findViewById(R.id.confpassword);
        register=findViewById(R.id.register);
        login=findViewById(R.id.login);
        in = getIntent();
        userType = in.getStringExtra("userType");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname=fullnameET.getText().toString();
                mobileno=mobilenoET.getText().toString();
                password=passwordET.getText().toString();
                confpassword=confpasswordET.getText().toString();
                countrycode=  codePicker.getSelectedCountryCodeWithPlus();
                Log.e("country code",countrycode);

                boolean isValidate = true;

                fullname = fullnameET.getText().toString();
                String fnameValidateMSG = UIValidation.nameValidate(fullname, true);
                //Log.e("FName Val", fnameValidateMSG);
                if (!fnameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    fullnameET.setError(fnameValidateMSG);
                    isValidate = false;
                }
                mobileno = mobilenoET.getText().toString();
                String mobileValidateMSG = UIValidation.mobileValidate(mobileno, true);
                //Log.e("MOB Val", mobileValidateMSG);
                if (!mobileValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    mobilenoET.setError(mobileValidateMSG);
                    isValidate = false;
                }


                //Log.e("MOB Val", mobileValidateMSG);
                if (countrycode.isEmpty())  {
                    Toast.makeText(RegistrationActivity.this, "Country code required", Toast.LENGTH_SHORT).show();
                    isValidate = false;
                }

                password = passwordET.getText().toString();
                String passwordValidateMSG = UIValidation.passwordValidate(password, true);
                //Log.e("Pass Val", passwordValidateMSG);
                if (!passwordValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    passwordET.setError(passwordValidateMSG);
                    isValidate = false;
                }

                confpassword = confpasswordET.getText().toString();
                String cpasswordValidateMSG = UIValidation.passwordValidate(confpassword, true);
                //Log.e("CPass Val", cpasswordValidateMSG);
                if (!cpasswordValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
                    confpasswordET.setError(cpasswordValidateMSG);
                    isValidate = false;
                }

                if (!confpassword.equals(password)) {
                    confpasswordET.setError("Password Not Match !");
                    isValidate = false;
                }



                if (isValidate)
                {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else
                    {

                        OtpTask task = new OtpTask();
                        task.execute(mobileno);
                    }
                }

            }
        });

        tandc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegistrationActivity.this, WebViewActivity.class);
                startActivity(in);
            }
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        imageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(RegistrationActivity.this, R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_dlg_image);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                TextView cameraLBL = (TextView) dialog.findViewById(R.id.cameraLBL);
                TextView gallLBL = (TextView) dialog.findViewById(R.id.gallLBL);
                ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV);

                cameraLBL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new PickerBuilder(RegistrationActivity.this, PickerBuilder.SELECT_FROM_CAMERA)
                                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                                    @Override
                                    public void onImageReceived(Uri imageUri) {
                                        dialog.dismiss();

                                        Bitmap bitmap = null;
                                        try {
                                            bitmap = MediaStore.Images.Media.getBitmap(RegistrationActivity.this.getContentResolver(),imageUri);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        CompressResizeImage(bitmap);

                                        imageIV.setImageURI(imageUri);
                                    }
                                })
                                .setImageName("testImage")
                                .setImageFolderName("testFolder")
                                .withTimeStamp(true)
                                .setCropScreenColor(Color.MAGENTA)
                                .start();
                    }
                });

                gallLBL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new PickerBuilder(RegistrationActivity.this, PickerBuilder.SELECT_FROM_GALLERY)
                                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                                    @Override
                                    public void onImageReceived(Uri imageUri) {
                                        dialog.dismiss();
                                        imageIV.setImageURI(imageUri);


                                        try {
                                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                            CompressResizeImage(selectedImage);

                                        } catch (Exception ex) {
                                            Toast.makeText(RegistrationActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .setImageName("test")
                                .setImageFolderName("testFolder")
                                .setCropScreenColor(Color.MAGENTA)
                                .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                                    @Override
                                    public void onPermissionRefused() {

                                    }
                                })
                                .start();
                    }
                });
                crossIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }


    public Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        //Log.e("orientation", orientation + "");
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void CompressResizeImage(Bitmap bm)
    {

        actualHeight=bm.getHeight();
        actualWidth=bm.getWidth();
        //Log.e("actualHeight",">>"+actualHeight);
        //Log.e("actualWidth",">>"+actualWidth);

        imgRatio = actualWidth / actualHeight;
        maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth)
        {
            if (imgRatio < maxRatio)
            {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
                imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);
                imageIV.setImageBitmap(imageUpload);
                profilePic = imageUpload;



            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
                imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);
                imageIV.setImageBitmap(imageUpload);
                profilePic = imageUpload;


            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
                imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);
                imageIV.setImageBitmap(imageUpload);
                profilePic = imageUpload;


            }
        }

        else if ((actualHeight<minHeight || actualWidth<minWidth)  )
        {
            Toast.makeText(RegistrationActivity.this, "Please choose an image that's at least 300 pixels wide and at least 300 pixels tall.", Toast.LENGTH_SHORT).show();
        }

        else
        {
            imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);
            imageIV.setImageBitmap(imageUpload);
            profilePic = imageUpload;

        }

    }
    // Send OTP
    class OtpTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {

            pd.setMessage("OTP Sending ...");
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
                Log.e("OtpTask1", " "+ob.toString());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            //  return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("OtpTask2", s);
            pd.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equals("New user")) {
                        String str = "";
                        if (profilePic != null)
                        {
                            str = BitmapUtil.getStringFromBitmap(profilePic);
                            SharedPreferences sp = getSharedPreferences("photo", MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("pic", str);
                            edit.apply();
                            edit.commit();
                        }

                        Intent in = new Intent(RegistrationActivity.this, OtpActivity.class);
                        in.putExtra("userType", userType);
                        in.putExtra("fname", fullname);
                        in.putExtra("lname", " ");
                        in.putExtra("mobile", mobileno);
                        in.putExtra("password", password);
                        in.putExtra("country_code", countrycode);


//                        mSessionManager.setLoginSession( id, first_name, "", gender, "", profile_pic);
//                        mSessionManager.setImageSession(profile_pic);

                        startActivity(in);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                //Log.e("Parse Error", ex.getMessage());
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





    public static String getStringFromBitmap(Bitmap bm)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
    }

}
