package com.example.ecohouse.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class PixelImageView extends AppCompatImageView {

    public PixelImageView(Context context) {
        super(context);
    }

    public PixelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            ((BitmapDrawable) drawable).setFilterBitmap(false);
            ((BitmapDrawable) drawable).setAntiAlias(false);
        }

        Drawable bg = getBackground();
        if (bg instanceof BitmapDrawable) {
            ((BitmapDrawable) bg).setFilterBitmap(false);
            ((BitmapDrawable) bg).setAntiAlias(false);
        }

        super.onDraw(canvas);
    }
}