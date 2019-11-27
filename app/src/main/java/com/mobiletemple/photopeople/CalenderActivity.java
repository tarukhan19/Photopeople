package com.mobiletemple.photopeople;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.customStyle.TextColorDecor;
import com.mobiletemple.photopeople.model.CalendarData;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;
import com.mobiletemple.photopeople.util.Endpoints;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CalenderActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    MaterialCalendarView calendarVW;
    SessionManager sessionManager;
    private HashMap<String,CalendarData> calendarDataList;
    String from;
    Intent intent;
    boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        getSupportActionBar().hide();
        intent=getIntent();
        from=intent.getStringExtra("from");

        sessionManager = new SessionManager(CalenderActivity.this);
        calendarDataList = new HashMap<>();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Calender");
        filter.setVisibility(View.GONE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (from.equalsIgnoreCase("profile"))
                {
                    Intent intent=new Intent(CalenderActivity.this,HomePage.class);
                    intent.putExtra("from","profile");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
                else
                {
                    Intent intent=new Intent(CalenderActivity.this,HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from","other");

                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
                }
            }
        });

        if(sessionManager.getLoginSession().get(SessionManager.KEY_USERID).isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Session Expired !")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            Intent in = new Intent(CalenderActivity.this, LoginActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                        }
                    }).show();
        }


        calendarVW = (MaterialCalendarView) findViewById(R.id.calendarVW);

        calendarVW.setOnDateChangedListener(new OnDateSelectedListener()
        {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay cdate, boolean selected)
            {
                calendarVW.setSelectionColor(getResources().getColor(R.color.gray ));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date date = cdate.getDate();
                //Log.e("Select Date",date.toString());

                final String dateString = sdf.format(date);

                boolean isDateExist = calendarDataList.containsKey(dateString);

                final Dialog dialog = new Dialog(CalenderActivity.this, R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_calendar);
                dialog.setCanceledOnTouchOutside(false);

                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV) ;
                crossIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                TextView dateLBL = (TextView) dialog.findViewById(R.id.dateLBL) ;

                dateLBL.setText(dateString);
                LinearLayout occupyLBL = (LinearLayout) dialog.findViewById(R.id.occupyLBL) ;
                LinearLayout cancelLBL = (LinearLayout) dialog.findViewById(R.id.cancelLBL) ;

                occupyLBL.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        SetCalendarData task = new SetCalendarData();
                        task.execute(dateString,"2","0");
                    }
                });



                TextView budgetLBL = (TextView) dialog.findViewById(R.id.budgetLBL) ;

                budgetLBL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        showBudgetBox(dateString);
                    }
                });

                if(isDateExist){
                    cancelLBL.setVisibility(View.VISIBLE);
                    occupyLBL.setVisibility(View.GONE);
                    cancelLBL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                            CancelCalendarData task = new CancelCalendarData();
                            task.execute(dateString);
                        }
                    });
                }else{
                    cancelLBL.setVisibility(View.GONE);
                    occupyLBL.setVisibility(View.VISIBLE);

                }

                dialog.show();
            }
        });



    }


    private void showBudgetBox(final String dateString)
    {
        CalendarData cd = calendarDataList.get(dateString);
        String oldAmount = "";
        if(cd!=null){
            oldAmount = cd.getAmount();
        }

        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_budget);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView dateLBL = (TextView) dialog.findViewById(R.id.dateLBL) ;
        dateLBL.setText(dateString);

        ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV) ;
        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final TextView budgetET = (TextView) dialog.findViewById(R.id.budgetET) ;
        budgetET.setText(oldAmount);

        Button okBT = (Button) dialog.findViewById(R.id.okBT) ;

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budget = budgetET.getText().toString();
                if(budget.isEmpty()){
                    budgetET.setError("Please fill budget.");
                }else{
                    dialog.dismiss();
                    SetCalendarData task = new SetCalendarData();
                    task.execute(dateString,"3",budget);
                }
            }
        });
        dialog.show();
    }

    private void  setCalendarData()
    {
        calendarVW.removeDecorators();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        ArrayList<CalendarDay> occupyByAppList = new ArrayList<>();
        ArrayList<CalendarDay> occupyList = new ArrayList<>();
        ArrayList<CalendarDay> budgetList = new ArrayList<>();

        Set<String> keys = calendarDataList.keySet();
        for(String key : keys)
        {
            CalendarData cd = calendarDataList.get(key);

            try {

                switch (cd.getStatus()) {
                    case "1":
                        occupyByAppList.add(CalendarDay.from(sdf.parse(cd.getDate())));
                        break;
                    case "2":
                        occupyList.add(CalendarDay.from(sdf.parse(cd.getDate())));
                        break;
                    case "3":
                        budgetList.add(CalendarDay.from(sdf.parse(cd.getDate())));
                        break;
                }
            }catch(ParseException ex){
                //Log.e("Parese ERR",ex.getMessage());
            }
        }
        calendarVW.addDecorator(new TextColorDecor(1,occupyByAppList,CalenderActivity.this));
        calendarVW.addDecorator(new TextColorDecor(2,occupyList,CalenderActivity.this));
        calendarVW.addDecorator(new TextColorDecor(3,budgetList,CalenderActivity.this));
    }

    class SetCalendarData extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        String date,cstatus,amount;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CalenderActivity.this);
            pd.setMessage("Add Entry...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            // ?=1
            Endpoints comm = new Endpoints();
            date = strings[0];
            cstatus = strings[1];
            amount = strings[2];
            try {
                JSONObject ob = new JSONObject();
                ob.put("freelancer_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("date", strings[0]);
                ob.put("calenadar_status", strings[1]);
                ob.put("amount", strings[2]);
                Log.e("ob",ob+"");

                String result = comm.getStringResponse(Endpoints.CALENDAR_SET,ob);
                Log.e("calender add",result);
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.cancel();

            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equalsIgnoreCase("success"))
                    {
                        CalendarData cd = new CalendarData();
                        cd.setStatus(cstatus);
                        cd.setDate(date);
                        cd.setAmount(amount);

                        calendarDataList.put(date,cd);
                    }
                }
            }catch (Exception ex)
            {
                if(ex!=null) {
                    new SweetAlertDialog(CalenderActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(ex.getMessage())
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            }).show();
                }
            }finally{
                setCalendarData();
            }

            super.onPostExecute(s);
        }
    }

    class CancelCalendarData extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        String cancelDate;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CalenderActivity.this);
            pd.setMessage("Cancel Entry...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            // ?=1
            Endpoints comm = new Endpoints();
            cancelDate = strings[0];
            try {
                JSONObject ob = new JSONObject();
                ob.put("freelancer_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("date", strings[0]);
                // ?freelancer_id=1&date=14-Nov-2017

                String result = comm.getStringResponse(Endpoints.CALENDAR_CANCEL,ob);
                //Log.e("calender can",result);
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.cancel();

            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equalsIgnoreCase("success"))
                    {
                        // CalendarData cd = calendarDataList.get(cancelDate);
                        calendarDataList.remove(cancelDate);

                    }
                }
            }catch (Exception ex)
            {
                if(ex!=null) {
                    new SweetAlertDialog(CalenderActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(ex.getMessage())
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            }).show();
                }
            }finally{
                setCalendarData();
            }

            super.onPostExecute(s);
        }
    }


    class LoadCalendarData extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CalenderActivity.this);
            pd.setMessage("Loading Data...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            // ?=1
            Endpoints comm = new Endpoints();

            try {
                JSONObject ob = new JSONObject();
                ob.put("freelancer_id", sessionManager.getLoginSession().get(SessionManager.KEY_USERID));

                String result = comm.getStringResponse(Endpoints.CALENDAR_LOAD,ob);
                Log.e("calender response",result);
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.cancel();

            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("message");

                    if (status == 200 && message.equalsIgnoreCase("success"))
                    {
                        JSONArray arr = obj.getJSONArray("data");
                        for(int x=0; x<arr.length();x++){
                            JSONObject dataObj = arr.getJSONObject(x);

                            CalendarData cd = new CalendarData();
                            cd.setId(dataObj.getString("id"));
                            cd.setStatus(dataObj.getString("calenadar_status"));
                            cd.setDate(dataObj.getString("dates"));
                            cd.setAmount(dataObj.getString("amount"));

                            calendarDataList.put(dataObj.getString("dates"),cd);
                        }
                    }
                }
            }catch (Exception ex)
            {
                if(ex!=null) {
                    new SweetAlertDialog(CalenderActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(ex.getMessage())
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            }).show();
                }
            }finally{
                setCalendarData();
            }

            super.onPostExecute(s);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (from.equalsIgnoreCase("profile"))
        {
            Intent intent=new Intent(CalenderActivity.this,HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","profile");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
        else
        {
            Intent intent=new Intent(CalenderActivity.this,HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from","other");

            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
        }
    }



    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
        {
            LoadCalendarData task = new LoadCalendarData();
            task.execute();
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
            LoadCalendarData task = new LoadCalendarData();
            task.execute();
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
