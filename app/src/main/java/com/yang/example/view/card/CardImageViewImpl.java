package com.yang.example.view.card;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.Nullable;

public interface CardImageViewImpl {

    void initialize(CardImageViewDelegate cardView, Context context, ColorStateList backgroundColor, float radius);

    void setRadius(CardImageViewDelegate cardView, float radius);

    float getRadius(CardImageViewDelegate cardView);

    float getMinWidth(CardImageViewDelegate cardView);

    float getMinHeight(CardImageViewDelegate cardView);

    void setBackgroundColor(CardImageViewDelegate cardView, @Nullable ColorStateList color);

    ColorStateList getBackgroundColor(CardImageViewDelegate cardView);
}
