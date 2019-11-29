package com.mobiletemple.photopeople;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class PhototipWebView extends AppCompatActivity {
    String postUrl;
    Intent intent;
    private WebView mWebView;
    private ImageView progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phototip_web_view);
        intent=getIntent();
        progressBar = (ImageView) findViewById(R.id.ProgressBar);
        Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        progressBar.setAnimation(aniRotateClk);
        progressBar.setVisibility(View.VISIBLE);
        postUrl=intent.getStringExtra("url");
        //Log.e("url",postUrl);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Use remote resource
         mWebView.loadUrl(postUrl);
        progressBar.setVisibility(View.INVISIBLE);



        // Stop local links and redirects from opening in browser instead of WebView
        // mWebView.setWebViewClient(new MyAppWebViewClient());

        // Use local resource
        // mWebView.loadUrl("file:///android_asset/www/index.html");
    }


}
