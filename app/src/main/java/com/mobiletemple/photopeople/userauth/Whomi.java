package com.mobiletemple.photopeople.userauth;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.constant.Constants;

public class Whomi extends AppCompatActivity {
LinearLayout studio,freelancer;
    private String userType;
    Dialog responseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whomi);
        studio=findViewById(R.id.studio);
        freelancer=findViewById(R.id.freelancer);
        studio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = Constants.STUDIO_TYPE;
                openDialog(userType);
//                userType = Constants.STUDIO_TYPE;
//
//                Intent in = new Intent(WhoAmIActivity.this, SignUpActivity.class);
//                in.putExtra("userType", userType);
//                startActivity(in);
            }
        });


        freelancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userType = Constants.FREELANCER_TYPE;
                openDialog(userType);

//
//                Intent in = new Intent(WhoAmIActivity.this, SignUpActivity.class);
//                in.putExtra("userType", userType);
//                startActivity(in);
            }
        });

    }

    private void openDialog(final String userType)
    {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_wmi, null);

        responseDialog = new Dialog(this,R.style.CustomDialog);
        responseDialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        responseDialog.setContentView(dialogView);
        responseDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        LinearLayout proceed=responseDialog.findViewById(R.id.proceed);

        responseDialog.show();

        ImageView crossIV =  responseDialog.findViewById(R.id.crossIV);
        TextView details=responseDialog.findViewById(R.id.details);

        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseDialog.cancel();
            }
        });

        if (userType.equalsIgnoreCase("1"))
        {
            details.setText(R.string.studiomsg);
        }
        else
        {
            details.setText(R.string.freelancermsg);
        }

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseDialog.dismiss();
                Intent in = new Intent(Whomi.this, RegistrationActivity.class);
                in.putExtra("userType", userType);

                //Log.e("userType",userType);
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

    }
}
