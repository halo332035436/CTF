package com.bullb.ctf.WebView;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by oscar on 5/6/15.
 */
public class MyWebClient extends WebViewClient
{
    private AVLoadingIndicatorView progressBar;
    private WebView mWebView;


    public MyWebClient(AVLoadingIndicatorView progressBar, WebView mWebView){
        this.progressBar = progressBar;
        this.mWebView = mWebView;

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //mWebView.setVisibility(progressBar.VISIBLE);
        if (progressBar != null)
            progressBar.setVisibility(progressBar.VISIBLE);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;

    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (progressBar != null)
            progressBar.setVisibility(progressBar.INVISIBLE);

    }

}