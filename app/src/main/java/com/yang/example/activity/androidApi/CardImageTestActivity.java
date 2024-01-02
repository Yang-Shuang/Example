package com.yang.example.activity.androidApi;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;
import com.yang.example.view.RoundedImageView;
import com.yang.example.view.card.CardImageView;

public class CardImageTestActivity extends SimpleBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_image_test);

        CardImageView card_image1 = findViewById(R.id.card_image1);
        RoundedImageView card_image2 = findViewById(R.id.card_image2);

        card_image1.setLayoutParams(new LinearLayout.LayoutParams(600,600));
        card_image2.setLayoutParams(new LinearLayout.LayoutParams(600,600));
    }
}
