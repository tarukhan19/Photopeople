package com.mobiletemple.photopeople.customStyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 17-04-2018.
 */

@SuppressLint("AppCompatCustomView")
public class TextViewLailasemibold extends TextView {

    public TextViewLailasemibold(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public TextViewLailasemibold(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public TextViewLailasemibold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/Laila-Medium.ttf");
        setTypeface(customFont);
    }
}
