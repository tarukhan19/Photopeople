package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.session.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpcomingEventDetails extends AppCompatActivity {
    Intent intent;
    String eventtype,shoottype,price,startdate,enddate,location,description,username,userphone;
    ImageView iv_pic;
    TextView eventtypetv,priceTV,timeTV,locationtv,desctv,nameTV,moblenoTV;
    ProgressDialog dialog;
    RequestQueue queue;
    SessionManager mSessionManager;
    String enddateS,startdateS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_event_details);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Event Details");
        ImageView filterpic=toolbar.findViewById(R.id.filterpic);
        filterpic.setVisibility(View.GONE);
        dialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        eventtypetv=findViewById(R.id.eventtypeTV);
        priceTV=findViewById(R.id.priceTV);
        timeTV=findViewById(R.id.timeTV);
        locationtv=findViewById(R.id.locationtv);
        desctv=findViewById(R.id.desc);
        moblenoTV=findViewById(R.id.moblenoTV);
        nameTV=findViewById(R.id.nameTV);



        iv_pic=findViewById(R.id.iv_pic);
        mSessionManager = new SessionManager(getApplicationContext());
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpcomingEventDetails.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","other");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        intent=getIntent();
        eventtype=intent.getStringExtra("eventtype");
        price=intent.getStringExtra("price");
        shoottype=intent.getStringExtra("shoottype");
        startdate=intent.getStringExtra("startdate");
        enddate=intent.getStringExtra("enddate");
        username=intent.getStringExtra("username");
        userphone=intent.getStringExtra("userphone");
        location=intent.getStringExtra("location");
        description=intent.getStringExtra("description");

        nameTV.setText(username);
        moblenoTV.setText(userphone);
        switch(eventtype)
        {
            case "1": iv_pic.setImageResource(R.drawable.birthday_default);
                eventtypetv.setText("Birthday"); break;
            case "2":iv_pic.setImageResource(R.drawable.wedding);
                eventtypetv.setText("Wedding"); break;
            case "3":iv_pic.setImageResource(R.drawable.pre_wedding);
                eventtypetv.setText("Pre Wedding"); break;
            case "4": iv_pic.setImageResource(R.drawable.engagement);
                eventtypetv.setText("Engagement"); break;
            case "5": iv_pic.setImageResource(R.drawable.maternity);
                eventtypetv.setText("Maternity"); break;
            case "6":iv_pic.setImageResource(R.drawable.kids);
                eventtypetv.setText("Kids"); break;
        }
        parseDateToddMMyyyy(startdate,enddate);

        timeTV.setText(startdate+ " to "+enddate);
        priceTV.setText(price);
        locationtv.setText(location);
        desctv.setText(description);




    }

    public void parseDateToddMMyyyy(String startdate,String enddate) {

        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "ddMMMyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;

        try {
            date = inputFormat.parse(startdate);
            startdateS = outputFormat.format(date);

            date = inputFormat.parse(enddate);
            enddateS = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //return startdate,enddate;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(UpcomingEventDetails.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","other");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }
}
