package com.example.neighbormaterebuild.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.neighbormaterebuild.R;

public class CampaignViewActivity extends AppCompatActivity {

    private WebView webview;
    private TextView toolbar_text_fullname;
    private Toolbar toolbarWebview;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        toolbarWebview = findViewById(R.id.toolbarWebview);
        toolbar_text_fullname = findViewById(R.id.toolbar_text_fullname);
        webview = findViewById(R.id.webview);
        progressLoading = findViewById(R.id.progressLoading);

        toolbarWebview.setLogo(R.drawable.ic_back_svg);
        View view = toolbarWebview.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String url = getIntent().getExtras().getString("url");

        toolbar_text_fullname.setText(getIntent().getExtras().getString("title"));

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                progressLoading.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressLoading.setVisibility(View.GONE);
            }
        });

        webview.loadUrl(url);
    }
}
