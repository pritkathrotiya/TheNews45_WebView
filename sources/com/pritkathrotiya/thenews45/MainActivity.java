package com.pritkathrotiya.thenews45;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public WebView myWebView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0272R.layout.activity_main);
        getSupportActionBar().hide();
        this.myWebView = (WebView) findViewById(C0272R.C0274id.webView);
        this.myWebView.getSettings().setJavaScriptEnabled(true);
        this.myWebView.loadUrl("https://thenews45.blogspot.com");
        this.myWebView.setWebViewClient(new WebViewClient());
    }

    public void onBackPressed() {
        if (this.myWebView.canGoBack()) {
            this.myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
