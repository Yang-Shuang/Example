package com.yang.example.view.card;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface CardImageViewDelegate {

    void setCardBackground(Drawable drawable);

    Drawable getCardBackground();

    View getCardView();
}
