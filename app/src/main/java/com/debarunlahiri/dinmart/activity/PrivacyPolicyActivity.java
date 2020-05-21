package com.debarunlahiri.dinmart.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.debarunlahiri.dinmart.next.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private Toolbar privacypolicytoolbar;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        privacypolicytoolbar = findViewById(R.id.privacypolicytoolbar);
        privacypolicytoolbar.setTitleTextColor(Color.WHITE);
        privacypolicytoolbar.setTitle("Privacy Policy");
        setSupportActionBar(privacypolicytoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        privacypolicytoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        privacypolicytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        webView = findViewById(R.id.webView);
        String s = "https://lahiriproductions.com/dinmart/privacy.html";

        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(s);
    }
}
