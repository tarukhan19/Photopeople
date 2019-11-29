package com.mobiletemple.photopeople.customStyle;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.mobiletemple.photopeople.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by shree on 5/9/2018.
 */

public class TextColorDecor implements DayViewDecorator
{
    private int type;
    private final HashSet<CalendarDay> dates;

    private Drawable occupy_drawable,busy_drawable,budget_drawable;

    public TextColorDecor(int type, Collection<CalendarDay> dates, Context context) {
        this.dates = new HashSet<>(dates);
        this.type = type;
        occupy_drawable = ContextCompat.getDrawable(context,R.drawable.occupy_circle);
        busy_drawable = ContextCompat.getDrawable(context,R.drawable.busy_circle);
        budget_drawable = ContextCompat.getDrawable(context, R.drawable.budget_circle);

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view)
    {
//            view.addSpan(new ForegroundColorSpan(color));
        switch(type)
        {
            case 1 : view.setSelectionDrawable(occupy_drawable); break;
            case 2 : view.setSelectionDrawable(busy_drawable); break;
            case 3 : view.setSelectionDrawable(budget_drawable); break;
        }

    }
}
