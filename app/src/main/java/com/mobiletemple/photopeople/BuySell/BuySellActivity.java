package com.mobiletemple.photopeople.BuySell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.R;

public class BuySellActivity extends AppCompatActivity {
LinearLayout sell,buy;
ImageView back;
Intent intent;
String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_sell);
        sell=findViewById(R.id.sell);
        buy=findViewById(R.id.buy);
        back=findViewById(R.id.back);
        intent=getIntent();
        from=intent.getStringExtra("from");
        sell.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BuySellActivity.this,SellProductActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BuySellActivity.this,BuyProductsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BuySellActivity.this,HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent=new Intent(BuySellActivity.this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from","profile");
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }
}
