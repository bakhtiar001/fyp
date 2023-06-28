package com.example.appscandiycrafts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);

        // Retrieve the URL passed from the previous activity
        String url = getIntent().getStringExtra("url");

        // Load the URL in the WebView
        webView.loadUrl(url);
    }
}