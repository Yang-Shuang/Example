package com.yang.example.view.card;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;

import androidx.annotation.Nullable;

public class CardImageViewApi21Impl implements CardImageViewImpl {

    @Override
    public void initialize(CardImageViewDelegate cardView, Context context, ColorStateList backgroundColor, float radius) {
        final CardDrawable background = new CardDrawable(backgroundColor, radius);
        cardView.setCardBackground(background);
        View view = cardView.getCardView();
        if (Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
    }

    @Override
    public void setRadius(CardImageViewDelegate cardView, float radius) {
        getCardBackground(cardView).setRadius(radius);
    }

    @Override
    public float getMinWidth(CardImageViewDelegate cardView) {
        return getRadius(cardView) * 2;
    }

    @Override
    public float getMinHeight(CardImageViewDelegate cardView) {
        return getRadius(cardView) * 2;
    }

    @Override
    public float getRadius(CardImageViewDelegate cardView) {
        return getCardBackground(cardView).getRadius();
    }

    @Override
    public void setBackgroundColor(CardImageViewDelegate cardView, @Nullable ColorStateList color) {
        getCardBackground(cardView).setColor(color);
    }

    @Override
    public ColorStateList getBackgroundColor(CardImageViewDelegate cardView) {
        return getCardBackground(cardView).getColor();
    }

    private CardDrawable getCardBackground(CardImageViewDelegate cardView) {
        return ((CardDrawable) cardView.getCardBackground());
    }
}
