package me.harshithgoka.socmed.Misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.net.URL;

/**
 * Created by akashtrehan on 10/10/17.
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                setMeasuredDimension(getMinimumWidth(), getMinimumHeight());
            } else {
                int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
                int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
                if (measuredHeight == 0 && measuredWidth == 0) { //Height and width set to wrap_content
                    setMeasuredDimension(measuredWidth, measuredHeight);
                } else if (measuredHeight == 0) { //Height set to wrap_content
                    int width = measuredWidth;
                    int height = width *  drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
                    setMeasuredDimension(width, height);
                } else if (measuredWidth == 0){ //Width set to wrap_content
                    int height = measuredHeight;
                    int width = height * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                    setMeasuredDimension(width, height);
                } else { //Width and height are explicitly set (either to match_parent or to exact value)
                    setMeasuredDimension(measuredWidth, measuredHeight);
                }
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

//    public void fitWidth (Bitmap source, boolean isHeightScale) {
//
//        float scale;
//        int newSize;
//        Bitmap scaleBitmap;
//        if (isHeightScale) {
//            scale = (float) mSize / source.getHeight();
//            newSize = Math.round(source.getWidth() * scale);
//            scaleBitmap = Bitmap.createScaledBitmap(source, newSize, mSize, true);
//        } else {
//            scale = (float) mSize / source.getWidth();
//            newSize = Math.round(source.getHeight() * scale);
//            scaleBitmap = Bitmap.createScaledBitmap(source, mSize, newSize, true);
//        }
//
//        if (scaleBitmap != source) {
//            source.recycle();
//        }
//
//        return scaledBitmap
//    }
}
