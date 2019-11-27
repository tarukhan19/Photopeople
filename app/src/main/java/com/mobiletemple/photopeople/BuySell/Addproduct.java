package com.mobiletemple.photopeople.BuySell;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.adapter.BitmapImageAdapter;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;
import com.mobiletemple.photopeople.util.ImageLoadingUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Addproduct extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener {
EditText prodnameET,prodpriceET,desc;
LinearLayout selectprod,submit,photoUpload;
TextView prodcat;
String prodNameString="",prodPriceString="",descString="",prodcatString="",prodIdString="";


    private ArrayList<Bitmap> photoBMList;
    private BitmapImageAdp photoADP;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Bitmap scaledBitmap = null;
    private int STORAGE_REQUEST = 99, SELECT_FILE = 1;
    ImageLoadingUtils utils;
    Uri imageUri;
    RecyclerView photoView;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    Intent intent;

    ArrayList<String> productIdarraylist, productNamearraylist;
    ArrayAdapter<String> productarrayadapter;
    AlertDialog.Builder prodbuilder;
    AlertDialog proddialog;
    ListView productLV;
    boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);

        intent=getIntent();
        prodnameET=findViewById(R.id.prodnameET);
        prodpriceET=findViewById(R.id.prodpriceET);
        desc=findViewById(R.id.desc);
        selectprod=findViewById(R.id.selectprod);
        sessionManager = new SessionManager(this);
        submit=findViewById(R.id.submit);
        prodcat=findViewById(R.id.prodcat);
        photoUpload=findViewById(R.id.photoUpload);
        photoView=findViewById(R.id.photoView);
        requestQueue = Volley.newRequestQueue(Addproduct.this);
        dialog = new ProgressDialog(this);
        isConnected = ConnectivityReceiver.isConnected();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Add Product");
        filter.setVisibility(View.GONE);

        photoView.setLayoutManager(new LinearLayoutManager(Addproduct.this, LinearLayoutManager.HORIZONTAL, false));
        photoBMList = new ArrayList<>();
        utils = new ImageLoadingUtils(Addproduct.this);
        photoADP = new BitmapImageAdp(Addproduct.this, photoBMList);
        photoView.setAdapter(photoADP);

        prodbuilder = new AlertDialog.Builder(Addproduct.this);
        productLV= new ListView(this);
        productIdarraylist= new ArrayList<>();
        productNamearraylist= new ArrayList<>();
        prodbuilder.setView(productLV);
        proddialog = prodbuilder.create();
        productarrayadapter =new ArrayAdapter<String>(this,
                R.layout.item_cameraspinner, R.id.text, productNamearraylist);
        productLV.setAdapter(productarrayadapter);



        productLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                prodcat.setText(txt.getText().toString());
                prodcatString = txt.getText().toString();
                //Log.e("prodTypestring",prodNameString);
                prodIdString = productIdarraylist.get(position).toString();
           //     Toast.makeText(Addproduct.this, prodNameString+prodIdString, Toast.LENGTH_LONG).show();
                proddialog.dismiss();
            }

        });

        selectprod.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                proddialog.setView(productLV);
                proddialog.show();

            }
        });




        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Addproduct.this,SellProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });


        photoUpload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                String intentType = "image/*";

                customCamGall(intentType);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prodNameString=prodnameET.getText().toString();
                prodPriceString=prodpriceET.getText().toString();
                descString=desc.getText().toString();
                boolean isvalidate=true;
                if (prodNameString.isEmpty())
                {
                    Toast.makeText(Addproduct.this, "Enter Product Name", Toast.LENGTH_SHORT).show();
                    isvalidate=false;
                }
                if (prodPriceString.isEmpty())
                {
                    Toast.makeText(Addproduct.this, "Enter Product Price", Toast.LENGTH_SHORT).show();
                    isvalidate=false;
                }
                if (prodcatString.isEmpty())
                {
                    Toast.makeText(Addproduct.this, "Select Product Category", Toast.LENGTH_SHORT).show();
                    isvalidate=false;
                }
                if (isvalidate)
                {

                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    }
                    else {  createImageReq task = new createImageReq();
                        task.execute();}

                }

            }
        });
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else {  loadSpinnerData();}


    }


    private class createImageReq extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(Addproduct.this);
            pd.setMessage("Adding Product ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            Endpoints comm = new Endpoints();

            try {

                JSONObject params = new JSONObject();
                params.put("product_category_id", prodIdString);
                params.put("user_type",sessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE) );
                params.put("product_price",prodPriceString );
                params.put("product_title", prodNameString);
                params.put("product_discription",descString);
                params.put("product_condition","0");

                params.put("user_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));

                //Log.e("params",params+"");
                byte[][] photoArray = null;
                if (photoBMList.size() > 0) {
                    //Log.e("Array2", "???" + photoBMList.toString());
                    photoArray = getBitmapArray(photoBMList);
                }
                String result = comm.forProductPost(Endpoints.ADD_PRODUCT, params, photoArray);
                //Log.e("CREATE_POST response", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            //  return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.e("Upload Response ", s);
            pd.cancel();
            pd.dismiss();
            try {
                if (s != null) {
                    photoBMList.clear();


                    JSONObject jsonObject = new JSONObject(s);
                    String flag = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (flag.equalsIgnoreCase("200") && message.equalsIgnoreCase("Data Successfully Submit"))
                    {
                        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(Addproduct.this, SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Product Added!");
                        sweetAlertDialog.show();

                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(Addproduct.this,R.color.colorPrimary));

                        sweetAlertDialog.setConfirmText("Ok");
                        btn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();

                                Intent in = new Intent(Addproduct.this, SellProductActivity.class);

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


    private void customCamGall(String intentType)
    {
        final Dialog dialog = new Dialog(Addproduct.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dlg_image);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cameraLBL = (TextView) dialog.findViewById(R.id.cameraLBL);
        TextView gallLBL = (TextView) dialog.findViewById(R.id.gallLBL);
        ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV);

        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
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
                dialog.cancel();
            }
        });

        gallLBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_CODE_FROM_GALLERY);
                dialog.cancel();

            }

        });
        dialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FROM_GALLERY:
                    //Log.e("data",""+data);
                    new ImageCompressionAsyncTask().execute(data.getDataString());
                    //Log.e("data.getDataString()",""+data.getDataString());

                    break;
                case REQUEST_CODE_CLICK_IMAGE:
                    try {

                        String  imageurl = getRealPathFromURI(imageUri);
                        new ImageCompressionAsyncTask().execute(imageurl);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor =  getContentResolver().query(contentUri, proj, null, null, null);
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


            if (photoBMList.size()==3)
            {
                Toast.makeText(Addproduct.this, "You can't upload more than 3 images", Toast.LENGTH_SHORT).show();
            }
            else
            {
                photoBMList.add(scaledBitmap);
                photoADP.notifyDataSetChanged();
            }

        }

    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent=new Intent(Addproduct.this,SellProductActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }

    class BitmapImageAdp extends RecyclerView.Adapter<BitmapImageAdp.CustomViewHodler>
    {

        private Context mContext;
        ArrayList<Bitmap> bmList;

        public BitmapImageAdp(Context context, ArrayList<Bitmap> photoBMList)
        {
            this.mContext = context;
            this.bmList = photoBMList;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_prodimage, parent, false);
            return new CustomViewHodler(itemView);    }

        @Override
        public void onBindViewHolder(CustomViewHodler holder, final int position) {
            final Bitmap bm = bmList.get(position);
            holder.imageView.setImageBitmap(bm);

            holder.crossIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bmList.remove(position);
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return bmList.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            ImageView imageView,crossIV;
            public CustomViewHodler(View itemView) {
                super(itemView);
                crossIV=(ImageView)itemView.findViewById(R.id.crossIV);
                imageView=(ImageView)itemView.findViewById(R.id.photoUpload);
            }
        }
    }

    private byte[][] getBitmapArray(ArrayList<Bitmap> photoBMList)
    {
        byte data[][] = new byte[photoBMList.size()][];
        int index = 0;
        for (Bitmap bm : photoBMList) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();
            data[index] = byteArray;
            index++;
        }
        return data;
    }


    private void loadSpinnerData()
    {
        dialog.setMessage("Loading Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.CATEGORY_LIST, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    productIdarraylist.clear();
                    productNamearraylist.clear();
                    dialog.dismiss();
                    //Log.e("response",response);
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status=obj.getInt("status");
                    String message=obj.getString("message");
                    if (status==200 && message.equalsIgnoreCase("success")){
                        JSONArray categoy_list = obj.getJSONArray("data");

                        for (int i = 0; i < categoy_list.length(); i++) {
                            JSONObject categoy_listobject = categoy_list.getJSONObject(i);

                            String categoy_ID = categoy_listobject.getString("id");
                            String categoy_NAME = categoy_listobject.getString("productt_category");
                            productIdarraylist.add(categoy_ID);
                            productNamearraylist.add(categoy_NAME);
                        }
                        productLV.setAdapter(productarrayadapter);
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onStart() {


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
            loadSpinnerData();
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
