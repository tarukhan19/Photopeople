package com.mobiletemple.photopeople;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.material.snackbar.Snackbar;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.SelectMultiImage.CustomGallery_Activity;
import com.mobiletemple.photopeople.adapter.BitmapImageAdapter;
import com.mobiletemple.photopeople.adapter.VideoURLAdapter;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.ImageLoadingUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateProfileExperienceActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    LinearLayout nextButton;
    LinearLayout photoUpload;
    List<String> selectedImages;
    RecyclerView photoView;
    private ArrayList<Bitmap> photoBMList;
    private BitmapImageAdapter photoADP;
    SessionManager session;
    String post;
    private final int REQUEST_CODE_FROM_GALLERY = 1;
    private final int REQUEST_CODE_CLICK_IMAGE = 2;
    Bitmap scaledBitmap = null;
    boolean isConnected;
    ImageLoadingUtils utils;
    Uri imageUri;
    Intent intent;
    RequestQueue queue;
    private EditText videoET;
    private ImageView addVideoIV;
    private ExpandableHeightListView videoLV;
    private ArrayList<String> videoList;
    private VideoURLAdapter videoAdp;
    ProgressDialog dialog;
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_experience);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Profile Experience");
        intent=getIntent();
        isConnected = ConnectivityReceiver.isConnected();

        filter.setVisibility(View.GONE);


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpdateProfileExperienceActivity.this,ProfileUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        session = new SessionManager(this);
        nextButton = findViewById(R.id.nextButton);
        photoUpload = findViewById(R.id.photoUpload);
        photoView = findViewById(R.id.photoView);
        photoView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photoBMList = new ArrayList<>();

        photoADP = new BitmapImageAdapter(this, photoBMList);
        photoView.setAdapter(photoADP);

        utils = new ImageLoadingUtils(this);

        intent = getIntent();
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);

        photoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customCamGall();
            }
        });

        videoET = (EditText) findViewById(R.id.videoET);
        addVideoIV = (ImageView) findViewById(R.id.addVideoIV);
        videoLV = (ExpandableHeightListView) findViewById(R.id.videoLV);
        videoList = new ArrayList<>();
        videoAdp = new VideoURLAdapter(this, videoList);
        videoLV.setAdapter(videoAdp);
        videoLV.setExpanded(true);

        String imagecount=session.getLoginSession().get(SessionManager.KEY_IMAGECOUNT);
        count=Integer.parseInt(imagecount);
        Log.e("photosize111",session.getLoginSession().get(SessionManager.KEY_IMAGECOUNT));


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else {

                   if (count>=20)
                   {

                               getPicCount();
                   }
                   else
                   {
                       ProfileTask task = new ProfileTask();
                       task.execute();
                   }
                }
            }
        });

        addVideoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoUrl = videoET.getText().toString();
                if (videoUrl == null || videoUrl.isEmpty()) {
                    videoET.setError("Please Fill The URL !");
                } else {
                    videoList.add(videoUrl);
                    //Log.e("videoarray",videoList+"");
                    videoAdp.notifyDataSetChanged();
                    videoET.setText("");
                }
            }
        });





    }


    public  void getPicCount()
    {


        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Warning!")
                .setMessage("You can't have more than 20 images in your gallery.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();



//        final SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(UpdateProfileExperienceActivity.this, SweetAlertDialog.ERROR_TYPE);
//        sweetAlertDialog.setTitleText("You can't have more than 20 images in your gallery.")
//                .setConfirmText("OK");
//        sweetAlertDialog.show();
//
//
//
//        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
//        btn.setBackgroundColor(ContextCompat.getColor(UpdateProfileExperienceActivity.this,R.color.colorPrimary));
//        btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                sweetAlertDialog.dismissWithAnimation();
//            }
//        });
    }
    private void customCamGall()
    {

        final Dialog dlg = new Dialog(UpdateProfileExperienceActivity.this, R.style.CustomDialog);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.item_dlg_image);
        dlg.setCanceledOnTouchOutside(false);
        dlg.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cameraLBL = (TextView) dlg.findViewById(R.id.cameraLBL);
        TextView gallLBL = (TextView) dlg.findViewById(R.id.gallLBL);
        LinearLayout cameLL=dlg.findViewById(R.id.cameLL);
        //cameLL.setVisibility(View.GONE);
        ImageView crossIV = (ImageView) dlg.findViewById(R.id.crossIV);


        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.cancel();
            }
        });

        cameraLBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
                dlg.cancel();
            }
        });

        gallLBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(UpdateProfileExperienceActivity.this, CustomGallery_Activity.class), REQUEST_CODE_FROM_GALLERY);

                dlg.cancel();


            }

        });
        dlg.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode) {

                case REQUEST_CODE_FROM_GALLERY:
                        String imagesArray = data.getStringExtra(CustomGalleryIntentKey);//get Intent data
                         selectedImages = Arrays.asList(imagesArray.substring(1, imagesArray.length() - 1).split(", "));
                         for (int i=0;i<selectedImages.size();i++)
                         {
                             String image=selectedImages.get(i);
                             new ImageCompressionAsyncTask().execute(image);
                         }


                  //  loadGridView(new ArrayList<String>(selectedImages));//call load gridview method by passing converted list into arrayList

                    break;

                case REQUEST_CODE_CLICK_IMAGE:
                    try {

                        String imageurl = getRealPathFromURI(imageUri);
                        new ImageCompressionAsyncTask().execute(imageurl);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

//    private void loadGridView(ArrayList<String> imagesArray) {
//        photoADP = new BitmapImageAdapter(this, imagesArray);
//        photoView.setAdapter(photoADP);
//    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            String filePath = compressImage(params[0]);
            return filePath;
        }

        public String compressImage(String imageUri) {

            String filePath = getRealPathFromURI(imageUri);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            photoView.setVisibility(View.VISIBLE);

            if (photoBMList.size()<5)
            {
                photoBMList.add(scaledBitmap);
                photoADP.notifyDataSetChanged();

//                if (photoBMList.size()>5)
//                {
//                    Toast.makeText(UpdateProfileExperienceActivity.this, "You can't upload more than 5 images", Toast.LENGTH_SHORT).show();
//
//                }

            }

//            else
//            {
//                Toast.makeText(UpdateProfileExperienceActivity.this, "You can't upload more than 5 images", Toast.LENGTH_SHORT).show();
//            }

        }

    }


    // Profile Upload
    class ProfileTask extends AsyncTask<String, Void, String>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(UpdateProfileExperienceActivity.this);
            pd.setMessage("Profile Uploading ...");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {

                byte[][] photoArray = null;
                if (photoBMList.size() > 0) {
                    Log.e("Array2", "???" + photoBMList.toString());
                    photoArray = getBitmapArray(photoBMList);

                }
                //Log.e("check", "4");

                JSONObject ob = new JSONObject();
                ob.put("user_id", session.getuserId().get(SessionManager.KEY_USERID));
                ob.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("exp_year", "");




                //Log.e("JSON", ob.toString());
                String result = comm.forExperience(Endpoints.EXPERIENCE_PROFILE_URL, ob,videoList, photoArray);

                Log.e("EXPERIENCE_PROFILE_URL", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s)
        {
            //Log.e("Upload Response ", s);
            pd.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status==200 && message.equalsIgnoreCase("success"))
                    {

                        session.setLoginSession(session.getLoginSession().get(SessionManager.KEY_EMAIL),
                                session.getLoginSession().get(SessionManager.KEY_USERID),
                                session.getLoginSession().get(SessionManager.KEY_NAME),
                                session.getLoginSession().get(SessionManager.KEY_USER_TYPE),
                                session.getLoginSession().get(SessionManager.KEY_MOBILE),
                                session.getLoginSession().get(SessionManager.KEY_IMAGE),
                                session.getLoginSession().get(SessionManager.KEY_FREELANCERTYPE),
                                        String.valueOf(count+photoBMList.size()));

                        Log.e("photosize",session.getLoginSession().get(SessionManager.KEY_IMAGECOUNT));

                        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(UpdateProfileExperienceActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Profile Updated!");
                        sweetAlertDialog.show();

                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(UpdateProfileExperienceActivity.this,R.color.colorPrimary));

                        sweetAlertDialog.setConfirmText("Ok");
                        btn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();

                                Intent in = new Intent(UpdateProfileExperienceActivity.this, ProfileUpdate.class);

                                startActivity(in);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);

                                finish();
                            }
                        });
                        sweetAlertDialog.show();
                    }







                }
            } catch (Exception ex) {
            }
        }
    }

    private byte[][] getBitmapArray(List<Bitmap> photoBMList)
    {
        byte data[][] = new byte[photoBMList.size()][];
        int index = 0;
        for (Bitmap bitmap : photoBMList) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            data[index] = byteArray;
            index++;
        }
        return data;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(UpdateProfileExperienceActivity.this,ProfileUpdate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (count>=20)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPicCount();
                }
            }, 2000);
        }

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
                .make(findViewById(R.id.rl), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();

        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        showSnack(isConnected);



    }


}
