package com.mobiletemple.photopeople.TimeLine;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.TimelineAdapter;
import com.mobiletemple.photopeople.adapter.TrendingTimelineAdapter;
import com.mobiletemple.photopeople.databinding.ActivityTimeLineDialogBinding;
import com.mobiletemple.photopeople.databinding.FragmentTimelineBinding;
import com.mobiletemple.photopeople.model.TimelineDTO;
import com.mobiletemple.photopeople.model.TrendingTimelineDTO;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.BitmapUtil;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.ImageLoadingUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TimelineFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    FragmentTimelineBinding binding;

    public ArrayList<TimelineDTO> list;
    public TimelineAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    String offsetString;
    int offset = 0;
    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalitemcount, previoustotal = 0;
    int view_threshold = 10;
    public ArrayList<TrendingTimelineDTO> trendingTimelineDTOS;
    public TrendingTimelineAdapter trendingTimelineAdapter;
    LinearLayoutManager trendinglinearLayoutManager;
    String trendingoffsetString;
    int trendingoffset = 0;
    private boolean trendingisLoading = true;
    int trendingpastVisibleItems, trendingvisibleItemCount, trendingtotalitemcount, trendingprevioustotal = 0;
    int trendingview_threshold = 10;

    TimelineApi timelineApi;

    RequestQueue requestQueue;
    public SessionManager session;
    ProgressDialog pd;

    boolean isConnected;
    Vibrator vibe;

    ActivityTimeLineDialogBinding dialogBinding;
    Dialog dialog;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Bitmap scaledBitmap = null;
    Uri imageUri;
    byte[] profilePicbyte = null;
    public static final int PERMISSION_REQUEST = 100;
    ImageLoadingUtils utils;
    String gmtTime;

    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        View view = binding.getRoot();


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        list = new ArrayList<TimelineDTO>();
        adapter = new TimelineAdapter(getActivity(), list);
        session = new SessionManager(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());
        timelineApi = new TimelineApi();
        isConnected = ConnectivityReceiver.isConnected();
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        trendingTimelineDTOS = new ArrayList<TrendingTimelineDTO>();
        trendingTimelineAdapter = new TrendingTimelineAdapter(getActivity(), trendingTimelineDTOS);

        list.clear();
        trendingTimelineDTOS.clear();

        pd = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ImageView filterpic = toolbar.findViewById(R.id.filterpic);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        backImage.setVisibility(View.GONE);
        filterpic.setImageResource(R.drawable.add_post);
        mTitle.setText("Timeline");
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });



            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.recyclerStory.setLayoutManager(linearLayoutManager);
            binding.recyclerStory.setAdapter(adapter);
            binding.recyclerStory.setHasFixedSize(true);
           // binding.recyclerStory.setNestedScrollingEnabled(false);
            offset = 0;
            offsetString = String.valueOf(offset);
            addDataToList();


            trendinglinearLayoutManager = new LinearLayoutManager(getActivity());
            trendinglinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            binding.trendingpostRV.setLayoutManager(trendinglinearLayoutManager);
            binding.trendingpostRV.setAdapter(trendingTimelineAdapter);
            binding.trendingpostRV.setHasFixedSize(true);
            trendingoffset = 0;
            trendingoffsetString = String.valueOf(trendingoffset);
            addtrendingDataToList();




        binding.recyclerStory.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {

                visibleItemCount = linearLayoutManager.getChildCount();
                totalitemcount = linearLayoutManager.getItemCount();
                pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (dy > 0) //check for scroll down
                {

                    if (isLoading)
                    {

                        if (totalitemcount > previoustotal) {
                            isLoading = false;
                            previoustotal = totalitemcount;

                        }


                    }

                    if (!isLoading && (totalitemcount - visibleItemCount) <= (pastVisibleItems + view_threshold))
                    {
                        offset = offset + 1;
                        offsetString = String.valueOf(offset);
                        Log.e("offset",offsetString);
                        addDataToList();
                        isLoading = true;
                    }

                }


            }
        });


    }

    private void showDialog() {
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.activity_time_line_dialog, null, false);
        dialog = new Dialog(getActivity());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        utils = new ImageLoadingUtils(getActivity());

        dialogBinding.uploadLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogBinding.etStatus.getText().toString().isEmpty())
                {

                    final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.item_dlg_image);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView cameraLBL = (TextView) dialog.findViewById(R.id.cameraLBL);
                    TextView gallLBL = (TextView) dialog.findViewById(R.id.gallLBL);
                    ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV);
                    cameraLBL.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override

                        public void onClick(View view) {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                            imageUri = getActivity().getContentResolver().insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
                            dialog.cancel();
                        }
                        //  }
                    });

                    gallLBL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, REQUEST_CODE_FROM_GALLERY);
                            dialog.cancel();
                        }
                    });


                    crossIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else {
                    Toast.makeText(getActivity(), "Either you can upload video or image.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBinding.tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date myDate = new Date();
                Log.e("myDate", myDate + "");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                calendar.setTime(myDate);
                Date time = calendar.getTime();
                Log.e("myTime", time + "");

                SimpleDateFormat outputFmt = new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy");
                gmtTime = outputFmt.format(time);

                PostPoll task = new PostPoll();
                task.execute();
            }
        });

        dialogBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void addDataToList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timelineApi.loadList(requestQueue, offsetString, session,
                        list, adapter, binding.recyclerStory);
            }
        }, 0);

    }


    private void addtrendingDataToList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timelineApi.loadTrendingList(requestQueue, trendingoffsetString, session,
                        trendingTimelineDTOS, trendingTimelineAdapter, binding.trendingpostRV, binding.sliderImgLL);
            }
        }, 0);

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }


    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FROM_GALLERY:
                    new ImageCompressionAsyncTask().execute(data.getDataString());
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


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

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
            Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
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
            dialogBinding.uploadedImage.setVisibility(View.VISIBLE);
            dialogBinding.uploadLL.setVisibility(View.GONE);
            dialogBinding.uploadedImage.setImageBitmap(scaledBitmap);

        }

    }

    class PostPoll extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            pd.setMessage("Loading..");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {
                if (scaledBitmap != null) {
                    String str = BitmapUtil.getStringFromBitmap(scaledBitmap);
                    profilePicbyte = android.util.Base64.decode(str, android.util.Base64.NO_WRAP);
                }


                JSONObject ob = new JSONObject();
                ob.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("user_type", session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("link", dialogBinding.etStatus.getText().toString().trim());
                ob.put("created_utc", gmtTime);

                //
                if (dialogBinding.etStatus.getText().toString().trim().isEmpty()) {
                    ob.put("post_type", "Image");
                } else {
                    ob.put("post_type", "Link");

                }

                String result = comm.forPost(Endpoints.COMPOSE_POST, ob, profilePicbyte);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Upload Response ", s);
            pd.cancel();
            try {
                if (s != null) {

                    //{"status":"200","message":"Post added successfully."}
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("Post added successfully.")) {

                        dialog.dismiss();
                        final SweetAlertDialog
                                sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Succesfully Posted!")
                                .setConfirmText("OK");
                        sweetAlertDialog.show();


                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();
                                list.clear();
                                offset = 0;
                                offsetString = String.valueOf(offset);
                                addDataToList();
                            }
                        });
                    }

                }
            } catch (Exception ex) {
            }
        }
    }
}
