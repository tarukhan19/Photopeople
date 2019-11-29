package com.mobiletemple.photopeople;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.ChangePassword;

public class ProfileUpdate extends AppCompatActivity implements View.OnClickListener{
    LinearLayout personalLL,experienceLL,socialLL,changeLL,bankLL,equipmentll;
    String user_id;
    Intent intent;
    SessionManager session;
    CardView equipmentcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage = toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);
        mTitle.setText("Profile Update");
        filter.setVisibility(View.GONE);
        session = new SessionManager(this);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileUpdate.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);

            }
        });



        personalLL = (LinearLayout) findViewById(R.id.personalLL);
        experienceLL = (LinearLayout) findViewById(R.id.experienceLL);
        socialLL = (LinearLayout) findViewById(R.id.socialLL);
        changeLL = (LinearLayout) findViewById(R.id.changeLL);
        bankLL = (LinearLayout) findViewById(R.id.bankLL);
        equipmentll= (LinearLayout) findViewById(R.id.equipmentll);
        equipmentcv=findViewById(R.id.equipmentcv);
        personalLL.setOnClickListener(this);
        experienceLL.setOnClickListener(this);
        socialLL.setOnClickListener(this);
        changeLL.setOnClickListener(this);
        bankLL.setOnClickListener(this);
        equipmentll.setOnClickListener(this);

        Log.e("freelancertyp",session.getLoginSession().get(SessionManager.KEY_FREELANCERTYPE));

        if(session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.STUDIO_TYPE)
                || session.getLoginSession().get(SessionManager.KEY_FREELANCERTYPE).equalsIgnoreCase("DESIGNER")
                || session.getLoginSession().get(SessionManager.KEY_FREELANCERTYPE).equalsIgnoreCase("VIDEO_EDITOR") )
        {
            equipmentcv.setVisibility(View.GONE);
        }
        else
        {
            equipmentcv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view)
    {
        Intent in = null;
        switch(view.getId())
        {
            case R.id.personalLL :

                if(session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.STUDIO_TYPE) )
                {
                    in = new Intent(ProfileUpdate.this, UpdateStudioprofileOne.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(in);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }

                if(session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase( Constants.FREELANCER_TYPE))
                {

                    in = new Intent(ProfileUpdate.this, UpdateFreelancerPersonalprofile.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(in);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }

                break;
            case R.id.experienceLL :
                in = new Intent(ProfileUpdate.this, UpdateProfileExperienceActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                in.putExtra("userType",session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                break;

            case R.id.equipmentll :
                in = new Intent(ProfileUpdate.this, UpdateEquipment.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                in.putExtra("userType",session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                break;
            case R.id.socialLL :
                in = new Intent(ProfileUpdate.this, ProfileSocialActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                in.putExtra("flag","update");

               startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                break;
            case R.id.changeLL :
                in = new Intent(ProfileUpdate.this, ChangePassword.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                break;
            case R.id.bankLL :
                in = new Intent(ProfileUpdate.this, PaymentDetailActivity.class);
                //  intent.putExtra("flag",flag);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(in);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ProfileUpdate.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","profile");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }
}
