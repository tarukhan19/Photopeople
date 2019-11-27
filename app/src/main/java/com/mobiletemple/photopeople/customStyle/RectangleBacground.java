package com.mobiletemple.photopeople.customStyle;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class RectangleBacground extends Drawable {

        private int[] themeColors;

        public RectangleBacground(int[] themeColors) {
            this.themeColors = themeColors;
        }

        @Override
        public void draw(Canvas canvas) {

            // get drawable dimensions
            Rect bounds = getBounds();

            int width = bounds.right - bounds.left;
            int height = bounds.bottom - bounds.top;

            // draw background gradient
            Paint backgroundPaint = new Paint();
            int barWidth = width / themeColors.length;
            int barWidthRemainder = width % themeColors.length;
            for (int i = 0; i < themeColors.length; i++) {
                backgroundPaint.setColor(themeColors[i]);
                canvas.drawRect(i * barWidth, 0, (i + 1) * barWidth, height, backgroundPaint);
            }

            // draw remainder, if exists
            if (barWidthRemainder > 0) {
                canvas.drawRect(themeColors.length * barWidth, 0, themeColors.length * barWidth + barWidthRemainder, height, backgroundPaint);
            }

        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }


}
