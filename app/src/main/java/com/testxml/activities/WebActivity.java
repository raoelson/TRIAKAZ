package com.testxml.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;


import com.testxml.R;

import java.util.Date;

/**
 * Created by Raoelson on 19/09/2017.
 */

public class WebActivity extends AppCompatActivity {

    WebView webView;
    String url_ = "";
    ProgressBar pB;
    FrameLayout frameLayout;
    Boolean testLoad = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        url_ = getIntent().getStringExtra("url");
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        pB = (ProgressBar) findViewById(R.id.progress);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setDefaultFontSize((int) getResources()
                .getDimension(R.dimen.textSize));
        webView.loadUrl(url_);
        LoadPage();
    }

    public void LoadPage() {
        pB.setMax(100);
        webView.setWebViewClient(new WebClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                frameLayout.setVisibility(View.VISIBLE);
                pB.setProgress(newProgress);
                setTitle("Loading ...");
                if (newProgress == 100) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                }
            }
        });
        pB.setProgress(0);
    }

    private class WebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("ckey")) {
                Uri uri = Uri.parse(url);
                Date date = new Date();
                long tm = date.getTime() + 1800000;
                SharedPreferences sharedpreferences = PreferenceManager.
                        getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if (sharedpreferences.getString("acces_id", null) != null) {
                    editor.remove("acces_id");
                    editor.apply();
                    editor.putString("acces_id", uri.getQueryParameter("ckey"));
                    editor.commit();
                } else {
                    editor.putString("acces_id", uri.getQueryParameter("ckey"));
                    editor.commit();
                }

                if (sharedpreferences.getString("dateAjoutUser", null) != null) {
                    editor.remove("dateAjoutUser");
                    editor.apply();
                    editor.putString("dateAjoutUser", "" + tm);
                    editor.commit();
                } else {
                    editor.putString("dateAjoutUser", "" + tm);
                    editor.commit();
                }
            }
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (getIntent().getStringExtra("panier") != null) {
                if(testLoad == false){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            webView.loadUrl(url_);
                            // handler.postDelayed(this, 2000);
                        }
                    }, 1000);
                    testLoad = true;
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
