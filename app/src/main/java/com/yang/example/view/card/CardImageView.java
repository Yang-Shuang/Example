package com.yang.example.view.card;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yang.example.R;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author yangshuang
 * @version V1.0
 * @date 2023-7-24
 * @Description 仿官方CarView实现 Imageview 圆角
 */
public class CardImageView extends AppCompatImageView {


    private static final int[] COLOR_BACKGROUND_ATTR = {android.R.attr.colorBackground};
    private static final CardImageViewImpl IMPL;

    static {
        IMPL = new CardImageViewApi21Impl();
    }

    public CardImageView(@NonNull Context context) {
        this(context, null);
    }

    public CardImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardImageView, defStyleAttr, R.style.CardView);
        ColorStateList backgroundColor;
        if (a.hasValue(R.styleable.CardImageView_cardBackgroundColor)) {
            backgroundColor = a.getColorStateList(R.styleable.CardImageView_cardBackgroundColor);
        } else {
            // There isn't one set, so we'll compute one based on the theme
            final TypedArray aa = getContext().obtainStyledAttributes(COLOR_BACKGROUND_ATTR);
            final int themeColorBackground = aa.getColor(0, 0);
            aa.recycle();

            // If the theme colorBackground is light, use our own light color, otherwise dark
            final float[] hsv = new float[3];
            Color.colorToHSV(themeColorBackground, hsv);
            backgroundColor = ColorStateList.valueOf(hsv[2] > 0.5f
                    ? getResources().getColor(R.color.cardview_light_background)
                    : getResources().getColor(R.color.cardview_dark_background));
        }
        float radius = a.getDimension(R.styleable.CardImageView_cardCornerRadius, 0);
        a.recycle();

        IMPL.initialize(mCardViewDelegate, context, backgroundColor, radius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!(IMPL instanceof CardImageViewApi21Impl)) {
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            switch (widthMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minWidth = (int) Math.ceil(IMPL.getMinWidth(mCardViewDelegate));
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minWidth,
                            MeasureSpec.getSize(widthMeasureSpec)), widthMode);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    // Do nothing
                    break;
            }

            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minHeight = (int) Math.ceil(IMPL.getMinHeight(mCardViewDelegate));
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minHeight,
                            MeasureSpec.getSize(heightMeasureSpec)), heightMode);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    // Do nothing
                    break;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setCardBackgroundColor(@ColorInt int color) {
        IMPL.setBackgroundColor(mCardViewDelegate, ColorStateList.valueOf(color));
    }

    public void setCardBackgroundColor(@Nullable ColorStateList color) {
        IMPL.setBackgroundColor(mCardViewDelegate, color);
    }

    @NonNull
    public ColorStateList getCardBackgroundColor() {
        return IMPL.getBackgroundColor(mCardViewDelegate);
    }

    public void setRadius(float radius) {
        IMPL.setRadius(mCardViewDelegate, radius);
    }

    public float getRadius() {
        return IMPL.getRadius(mCardViewDelegate);
    }

    private final CardImageViewDelegate mCardViewDelegate = new CardImageViewDelegate() {
        private Drawable mCardBackground;

        @Override
        public void setCardBackground(Drawable drawable) {
            mCardBackground = drawable;
            setBackgroundDrawable(drawable);
        }

        @Override
        public Drawable getCardBackground() {
            return mCardBackground;
        }

        @Override
        public View getCardView() {
            return CardImageView.this;
        }
    };
}
