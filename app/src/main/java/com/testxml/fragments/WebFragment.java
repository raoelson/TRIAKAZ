package com.testxml.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.testxml.R;
import com.testxml.activities.PrincipalActivity;

/**
 * Created by Raoelson on 26/09/2017.
 */

public class WebFragment extends Fragment {
    WebView webView;
    //String url = "https://triakaz.com/fr/contactez-nous";
    private static final String url = "url";
    ProgressBar pB;
    FrameLayout frameLayout;
    Context mContext;

    public WebFragment newInstance(String value,Context context) {
        this.mContext = context;
        WebFragment mFragment = new WebFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(url, value);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        pB = (ProgressBar) view.findViewById(R.id.progress);
        frameLayout = (FrameLayout) view.findViewById(R.id.framelayout);
        LoadPage();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PrincipalActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }
    public void  LoadPage(){
        pB.setMax(100);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDefaultFontSize((int)getResources()
                .getDimension(R.dimen.activity_horizontal_margin));
        webView.setWebViewClient(new WebClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                backView(view);
                frameLayout.setVisibility(View.VISIBLE);
                pB.setProgress(newProgress);
                //getActivity().setTitle("Loading ...");
                if (newProgress == 100) {
                    frameLayout.setVisibility(View.GONE);
                    //getActivity().setTitle(view.getTitle());
                }
            }
        });
        webView.loadUrl(getArguments().getString(url));
        pB.setProgress(0);
    }

    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("ckey")){
                Uri uri = Uri.parse(url);
                SharedPreferences sharedpreferences = PreferenceManager.
                        getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(sharedpreferences.getString("acces_id",null) != null){
                    editor.remove("acces_id");
                    editor.apply();
                    editor.putString("acces_id",uri.getQueryParameter("ckey"));
                    editor.commit();
                }else{
                    editor.putString("acces_id",uri.getQueryParameter("ckey"));
                    editor.commit();
                }
            }
            view.loadUrl(url);
            backView(view);
            return true;
        }
    }


    public void backView(View v){
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    startActivity(new Intent(getContext(), PrincipalActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        } );
    }
}
