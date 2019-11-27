package com.mobiletemple.photopeople;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.adapter.BitmapImageAdapter;
import com.mobiletemple.photopeople.adapter.VideoURLAdapter;
import com.mobiletemple.photopeople.constant.Constants;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileExperienceActivity extends AppCompatActivity   implements ConnectivityReceiver.ConnectivityReceiverListener{
    LinearLayout nextButton;
    LinearLayout photoUpload;

    RecyclerView photoView;
    private ArrayList<Bitmap> photoBMList;
    private ArrayList<String> list;
    private BitmapImageAdapter photoADP;
    SessionManager session;
    String post;
    ImageView imageprofile;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Bitmap scaledBitmap = null;
    private String userType;

    private int STORAGE_REQUEST = 99, SELECT_FILE = 1;
    ImageLoadingUtils utils;
    Uri imageUri;
    Intent intent;

    private EditText videoET;
    private ImageView addVideoIV;
    private ExpandableHeightListView videoLV;
    private ArrayList<String> videoList;
    private VideoURLAdapter videoAdp;
    boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_experience);
        session = new SessionManager(this);
        nextButton = findViewById(R.id.nextButton);
        photoUpload = findViewById(R.id.photoUpload);
        photoView = findViewById(R.id.photoView);
        photoView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photoBMList = new ArrayList<>();
        list=new ArrayList<>();
        utils = new ImageLoadingUtils(this);
        //photoADP = new BitmapImageAdapter(this, photoBMList);
        photoView.setAdapter(photoADP);
        intent = getIntent();
        userType = intent.getStringExtra("userType");

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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // exp_no = expNumET.getText().toString();

                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else
                {
                    ProfileTask task = new ProfileTask();
                    task.execute();
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


    private void customCamGall() {


        final Dialog dlg = new Dialog(ProfileExperienceActivity.this, R.style.CustomDialog);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.item_dlg_image);
        dlg.setCanceledOnTouchOutside(false);
        dlg.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cameraLBL = (TextView) dlg.findViewById(R.id.cameraLBL);
        TextView gallLBL = (TextView) dlg.findViewById(R.id.gallLBL);
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

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
                dlg.cancel();

            }

        });
        dlg.show();

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FROM_GALLERY:
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                    imagesEncodedList = new ArrayList<String>();
                    if(data.getData()!=null){

                        Uri mImageUri=data.getData();

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                         String imageEncoded  = cursor.getString(columnIndex);
                         list.add(imageEncoded);
                        cursor.close();
                        new ImageCompressionAsyncTask().execute(imageEncoded);

//                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                        mArrayUri.add(mImageUri);
//                        galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
//                        gvGallery.setAdapter(galleryAdapter);
//                        gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
//                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
//                                .getLayoutParams();
//                        mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                    } else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String imageEncoded  = cursor.getString(columnIndex);
                                list.add(imageEncoded);

                                cursor.close();
                                new ImageCompressionAsyncTask().execute(imageEncoded);


                            }
                            Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }

                    break;
                case REQUEST_CODE_CLICK_IMAGE:
                    try {

                        String imageurl = getRealPathFromURI(imageUri);
                        list.add(imageurl);

                        new ImageCompressionAsyncTask().execute(imageurl);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
//        private boolean fromGallery;
//
//        public ImageCompressionAsyncTask(boolean fromGallery) {
//            this.fromGallery = fromGallery;
//        }

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

            if (list.size()>4)
            {
                Toast.makeText(ProfileExperienceActivity.this, "You can't select more than 5 images.", Toast.LENGTH_SHORT).show();
            }
            else {
                photoBMList.add(scaledBitmap);
                photoADP.notifyDataSetChanged();
            }


        }

    }


    // Profile Upload
    class ProfileTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(ProfileExperienceActivity.this);
            pd.setMessage("Profile Uploading ...");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {
                //Log.e("check", "1");


                //Log.e("check", "3");
                byte[][] photoArray = null;
                if (photoBMList.size() > 0) {
                    //Log.e("Array2", "???" + photoBMList.toString());
                    photoArray = getBitmapArray(photoBMList);

                }
                //Log.e("check", "4");

                JSONObject ob = new JSONObject();
                ob.put("user_id", session.getuserId().get(SessionManager.KEY_USERID));
                ob.put("user_type", userType);



                    ob.put("exp_year", "0");


              //  ob.put("photocount", photoBMList.size());

//                if (video.length() != 0) {
//                    ob.put("video", video.toString());
//                } else {
//                    ob.put("video", "");
//                }

                //Log.e("JSON", ob.toString());

                // String result = comm.getStringResponse(Endpoints.SIGNUP_URL,ob);
                String result = comm.forExperience(Endpoints.EXPERIENCE_PROFILE_URL, ob,videoList, photoArray);

                //Log.e("videolist",videoList+"");
                //Log.e("photoArray",photoArray+"");

                //Log.e("Registration response", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            //Log.e("Upload Response ", s);
            pd.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    Intent in = new Intent(ProfileExperienceActivity.this, ProfileSocialActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    in.putExtra("flag","new");
                    in.putExtra("userType", userType);

                    //  in.putExtra("fname",fname);
                    startActivity(in);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                    // }
                }
            } catch (Exception ex) {
            }
        }
           }

        private byte[][] getBitmapArray(ArrayList<Bitmap> photoBMList) {
            byte data[][] = new byte[photoBMList.size()][];
            int index = 0;
            for (Bitmap bm : photoBMList) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                data[index] = byteArray;
                index++;
            }
            return data;
        }



    @Override
    public void onStart()
    {
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
                .make(findViewById(R.id.rl), message, Snackbar.LENGTH_LONG);

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

