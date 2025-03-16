package com.example.cq_mobile.ui.MoreInFragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.cq_mobile.R;

public class AboutActivity extends AppCompatActivity {
    CardView cardView, cardView5;
    private int clickCount = 0;
    private int buttonPressCount = 0;
    TextView  textView4;
    WebView webView;
    ImageButton btn;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        cardView = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        webView = findViewById(R.id.webView);
        textView4 = findViewById(R.id.textView4);
        btn = findViewById(R.id.btn);
        progressBar= findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        cardView5.setVisibility(View.GONE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.cq-business-management-software.com");


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });



        textView4.setOnClickListener(v -> {
            clickCount++;
            if (clickCount == 10) {
                textView4.setText(R.string.cqbms_textBody);
            } else if (clickCount == 15) {
                cardView5.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);
            }
        });


        btn.setOnClickListener(v -> {
            if (cardView5.getVisibility() == View.VISIBLE) {
                buttonPressCount++;
                if (buttonPressCount == 5) {
                    cardView5.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    buttonPressCount = 0; // Reset count
                    textView4.setText("About CQBMS");
                }
            }
        });
    }
}
