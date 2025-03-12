package com.example.cq_mobile.ui.MoreInFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.cq_mobile.R;

public class AboutActivity extends AppCompatActivity {
    CardView cardView, cardView5;
    private int clickCount = 0;
    TextView text_about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        cardView = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        text_about = findViewById(R.id.text_about);
        cardView5.setVisibility(View.GONE);

        cardView.setOnClickListener(v -> {
            clickCount++;
            if (clickCount == 30) {
                cardView5.setVisibility(View.VISIBLE);
            }
        });
    }
}
