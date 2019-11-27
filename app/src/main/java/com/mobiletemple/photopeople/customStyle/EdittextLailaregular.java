package com.mobiletemple.photopeople.customStyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by admin on 17-04-2018.
 */

@SuppressLint("AppCompatCustomView")
public class EdittextLailaregular extends EditText {

    public EdittextLailaregular(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public EdittextLailaregular(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public EdittextLailaregular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/Laila-Regular_0.ttf");
        setTypeface(customFont);
    }
}
