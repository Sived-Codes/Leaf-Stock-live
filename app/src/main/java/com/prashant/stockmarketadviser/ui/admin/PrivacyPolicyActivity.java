package com.prashant.stockmarketadviser.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.prashant.stockmarketadviser.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends AppCompatActivity {

    ActivityPrivacyPolicyBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        WebSettings webSettings = bind.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        bind.back.setOnClickListener(view -> finish());

        bind.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                bind.progressBar.show.setVisibility(android.view.View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                bind.progressBar.show.setVisibility(android.view.View.GONE);
            }
        });

        bind.webView.loadUrl("https://trade-master-edbae.web.app/privacy.html");
    }

    @Override
    public void onBackPressed() {
        if (bind.webView.canGoBack()) {
            bind.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}