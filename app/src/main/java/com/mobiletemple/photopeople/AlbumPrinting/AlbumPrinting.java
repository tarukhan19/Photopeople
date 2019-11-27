package com.mobiletemple.photopeople.AlbumPrinting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.PayuMoneyIntegration.ProdPurchaseActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlbumPrinting extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ArrayList<String> printerIdarraylist, printerNamearraylist;
    ArrayAdapter<String> printerarrayadapter;
    AlertDialog.Builder printerbuilder;
    AlertDialog prrinterdialog;
    ListView printerLV, sizeLV;
    String printe_quota_id,promo_codeS;
    LinearLayout selectprinter, selectpaper, submit, selectsize, ll;
    EditText address, drivelink, sheetquantity;

    ArrayList<String> paperIdarraylist, paperNamearraylist, pricearraylist, sizearraylist, amountArraylist;
    ArrayAdapter<String> paperarrayadapter, sizearrayadapter;
    AlertDialog.Builder paperbuilder, sizebuilder;
    AlertDialog paperdialog, sizedialog;
    ListView paperLV;
    String papernameString = "", paperidString, amountString;
    String printernameString = "", printeridString, sizestring = "", promocode_status;
    String drivelinkString = " ", addressString = " ", sheetquantityString = " ", bindingamount = "2000";
    double totaldouble = 0.0, grandtotaldouble = 0.0, amountDouble = 0.0;
    TextView printername, papername, amountTV, sizeTV,applycoupon;
    boolean isConnected;

    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    SessionManager mSessionManager;

    TextView grandtotalTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_printing);

        requestQueue = Volley.newRequestQueue(AlbumPrinting.this);
        progressDialog = new ProgressDialog(this);
        mSessionManager = new SessionManager(getApplicationContext());
        ll = findViewById(R.id.ll);
        printername = findViewById(R.id.printername);
        papername = findViewById(R.id.papername);
        selectprinter = findViewById(R.id.selectprinter);
        selectpaper = findViewById(R.id.selectpaper);
        amountTV = findViewById(R.id.amountTV);
        address = findViewById(R.id.address);
        drivelink = findViewById(R.id.drivelink);
        submit = findViewById(R.id.submit);
        sizeTV = findViewById(R.id.size);
        sheetquantity = findViewById(R.id.sheetquantity);
        selectsize = findViewById(R.id.selectsize);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);

        mTitle.setText("Album Printing");
        ImageView filterpic = toolbar.findViewById(R.id.filterpic);
        filterpic.setImageResource(R.drawable.detailicons);


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumPrinting.this, HomePage.class);
                intent.putExtra("from", "profile");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumPrinting.this, AlbumprintOrderHistory.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        printerLV = new ListView(this);
        sizeLV = new ListView(this);
        paperLV = new ListView(this);


        printerbuilder = new AlertDialog.Builder(AlbumPrinting.this);
        printerIdarraylist = new ArrayList<>();
        printerNamearraylist = new ArrayList<>();
        printerbuilder.setView(printerLV);
        prrinterdialog = printerbuilder.create();
        printerarrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_cameraspinner, R.id.text, printerNamearraylist);
        printerLV.setAdapter(printerarrayadapter);


        paperbuilder = new AlertDialog.Builder(AlbumPrinting.this);
        paperIdarraylist = new ArrayList<>();
        amountArraylist = new ArrayList<>();
        paperNamearraylist = new ArrayList<>();
        pricearraylist = new ArrayList<>();
        sizearraylist = new ArrayList<>();
        paperbuilder.setView(printerLV);
        paperdialog = paperbuilder.create();
        paperarrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_cameraspinner, R.id.text, paperNamearraylist);
        paperLV.setAdapter(paperarrayadapter);

        sizebuilder = new AlertDialog.Builder(AlbumPrinting.this);
        sizebuilder.setView(sizeLV);
        sizedialog = sizebuilder.create();
        sizearrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_cameraspinner, R.id.text, sizearraylist);
        sizeLV.setAdapter(sizearrayadapter);

        printerLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                printername.setText(txt.getText().toString());
                printernameString = txt.getText().toString();
                printeridString = printerIdarraylist.get(position).toString();
                amountTV.setText(" ");
                papername.setText("");
                papername.setHint("Choose Paper");
                sizeTV.setText("");
                sizeTV.setHint("Choose Size");
                prrinterdialog.dismiss();
            }

        });

        selectprinter.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                prrinterdialog.setView(printerLV);
                prrinterdialog.show();

            }
        });


        paperLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                papername.setText(txt.getText().toString());
                papernameString = txt.getText().toString();

                paperidString = paperIdarraylist.get(position).toString();
                sizeTV.setText("");
                sizeTV.setHint("Choose Size");
                amountTV.setText(" ");

//                amountTV.setText("₹"+paperamountString +"/Sheet");
//                amountDouble=Double.parseDouble(paperamountString);
                loadPrice(paperidString);

                paperdialog.dismiss();

            }

        });

        selectpaper.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                paperdialog.setView(paperLV);
                paperdialog.show();
            }
        });


        sizeLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                sizeTV.setText(txt.getText().toString());
                sizestring = txt.getText().toString();

                amountString = amountArraylist.get(position).toString();
                amountTV.setText("₹" + amountString + "/Sheet");
                amountDouble = Double.parseDouble(amountString);
                sizedialog.dismiss();

            }

        });

        selectsize.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                sizedialog.setView(sizeLV);
                sizedialog.show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isvalidate = true;


                drivelinkString = drivelink.getText().toString().trim();
                addressString = address.getText().toString();
                sheetquantityString = sheetquantity.getText().toString().trim();

                if (printernameString.isEmpty()) {
                    Toast.makeText(AlbumPrinting.this, "Select Printer Type", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }

                if (sizestring.isEmpty()) {
                    Toast.makeText(AlbumPrinting.this, "Select size", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }

                if (papernameString.isEmpty()) {
                    Toast.makeText(AlbumPrinting.this, "Select Paper Type", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (drivelinkString.isEmpty()) {
                    Toast.makeText(AlbumPrinting.this, "Enter your Google Drive Link", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (sheetquantityString.isEmpty()) {
                    Toast.makeText(AlbumPrinting.this, "Enter No. of Sheets", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                } else {

                    totaldouble = Integer.parseInt(sheetquantityString) * amountDouble;
                    grandtotaldouble = totaldouble + Double.parseDouble(bindingamount);
                    isvalidate = true;
                }


                if (addressString.isEmpty()) {
                    Toast.makeText(AlbumPrinting.this, "Enter your address", Toast.LENGTH_SHORT).show();
                    isvalidate = false;
                }
                if (isvalidate)
                {
                    if (!isConnected)
                    {
                        showSnack(isConnected);
                    } else
                    {
                        submitJob();
                    }
                }
            }
        });
    }

    private void submitJob() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.ALBUMPRINTER_JOB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("Success"))
                            {
                                printe_quota_id = obj.getString("printe_quota_id");
                                promocode_status = obj.getString("promocode_status");
                                openDialog();
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
                        progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", mSessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                params.put("user_type", mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                params.put("printer_id", printeridString);
                params.put("peper_size", sizestring);
                params.put("amoun_par_sheet", amountString);
                params.put("quantity_sheet", sheetquantityString);
                params.put("bounding_amount", bindingamount);
                params.put("total", String.valueOf(totaldouble));
                params.put("grandtotle", String.valueOf(grandtotaldouble));
                params.put("photo_share_link", drivelinkString);
                params.put("paper_id", paperidString);


                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    private void openDialog() {
        final Dialog dialog = new Dialog(AlbumPrinting.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_printsuccessdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView cross = (ImageView) dialog.findViewById(R.id.cross);
        LinearLayout submit = (LinearLayout) dialog.findViewById(R.id.submit);
        final TextView noOffilemsgTV = dialog.findViewById(R.id.noOffilemsgTV);
        final TextView totalTV = dialog.findViewById(R.id.totalTV);
        final TextView bindingTV = dialog.findViewById(R.id.bindingTV);
        grandtotalTV = dialog.findViewById(R.id.grandtotalTV);
        applycoupon=dialog.findViewById(R.id.applycoupon);

        noOffilemsgTV.setText("You have uploaded " + sheetquantityString + " files.");
        totalTV.setText(String.valueOf(totaldouble));
        bindingTV.setText(String.valueOf(bindingamount));
        grandtotalTV.setText(String.valueOf(grandtotaldouble));

        if (promocode_status.equalsIgnoreCase("1"))
        {
            applycoupon.setVisibility(View.VISIBLE);

        }
        else
        {
            applycoupon.setVisibility(View.GONE);

        }

        applycoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCouponDialog();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AlbumPrinting.this, ProdPurchaseActivity.class);
                intent.putExtra("amount", String.valueOf(grandtotaldouble));
                intent.putExtra("quoteid", printe_quota_id);
                intent.putExtra("from", "albumprinting");
                intent.putExtra("prodname", "Album Print");
                startActivity(intent);
                dialog.dismiss();


            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    private void loadPrinter() {
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Endpoints.PRINTER_LIST, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    printerIdarraylist.clear();
                    printerNamearraylist.clear();
                    paperIdarraylist.clear();
                    paperNamearraylist.clear();

                    //Log.e("response",response);
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray printer_list = obj.getJSONArray("printer_list");
                        JSONArray printer_paper_list = obj.getJSONArray("printer_paper_list");

                        for (int i = 0; i < printer_list.length(); i++) {
                            JSONObject categoy_listobject = printer_list.getJSONObject(i);
                            String printer_ID = categoy_listobject.getString("id");
                            String printer_name = categoy_listobject.getString("printer_name");
                            printerIdarraylist.add(printer_ID);
                            printerNamearraylist.add(printer_name);
                        }
                        printerLV.setAdapter(printerarrayadapter);


                        for (int i = 0; i < printer_paper_list.length(); i++) {
                            JSONObject printer_paper_listobj = printer_paper_list.getJSONObject(i);
                            String paper_ID = printer_paper_listobj.getString("id");
                            String paper_name = printer_paper_listobj.getString("paper");
                            paperIdarraylist.add(paper_ID);
                            paperNamearraylist.add(paper_name);
                        }
                        paperLV.setAdapter(paperarrayadapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void openCouponDialog()
    {
        final Dialog dialog = new Dialog(AlbumPrinting.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_couponcode);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout crossImage = (LinearLayout) dialog.findViewById(R.id.crossImage) ;
        LinearLayout submit = (LinearLayout) dialog.findViewById(R.id.submit) ;
        final EditText couponcodeET=dialog.findViewById(R.id.couponcodeET);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponcodeET.getText().toString().isEmpty())
                {
                    Toast.makeText(AlbumPrinting.this, "Enter valid coupon.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    promo_codeS=couponcodeET.getText().toString().trim();
                    dialog.dismiss();
                    submitCouponCode();
                }
            }
        });


        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }


    private void submitCouponCode()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Endpoints.PROMOCODE_ALBUMPRINT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response);
                        try {
//                            grandtotaldouble = obj.getDouble("paid_price");

                            JSONObject obj = new JSONObject(response);
                            String status=obj.getString("status");
                            String message=obj.getString("message");

                            if (status.equalsIgnoreCase("1") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject dataObj=obj.getJSONObject("data");
                                grandtotaldouble=dataObj.getDouble("Total price");
                                grandtotalTV.setText(String.valueOf(grandtotaldouble));

                                applycoupon.setText("Coupon applied successfully.");

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
                ob.put("promo_code",promo_codeS );
                ob.put("user_type",mSessionManager.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                ob.put("user_id", mSessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("price", grandtotaldouble+"");
                ob.put("printer_id", printeridString+"");

                Log.e("params",ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }




    private void loadPrice(final String paperidString) {
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.PAPER_LIST, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    sizearraylist.clear();
                    amountArraylist.clear();
                    progressDialog.dismiss();
                    //Log.e("PAPER_LIST",response);
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    int status = obj.getInt("status");
                    String message = obj.getString("message");
                    if (status == 200 && message.equalsIgnoreCase("success")) {
                        JSONArray price = obj.getJSONArray("SubPrinterList");

                        for (int i = 0; i < price.length(); i++) {
                            JSONObject object = price.getJSONObject(i);
                            String Size = object.getString("Size");
                            String Amount = object.getString("Amount");

                            sizearraylist.add(Size);
                            amountArraylist.add(Amount);

                        }
                        sizeLV.setAdapter(sizearrayadapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }


        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("paper_id", paperidString);
                //Log.e("params", ob.toString());
                return ob;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(AlbumPrinting.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "profile");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

    }

    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        //Log.e("onStart",isConnected+"");
        if (!isConnected) {
            showSnack(isConnected);
        } else {
            loadPrinter();
        }
        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected)
    {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            loadPrinter();
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
        this.isConnected = isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");
        //Log.e("onNetworkConnectionconn","onNetworkConnectionconn");

        showSnack(isConnected);


    }
}
