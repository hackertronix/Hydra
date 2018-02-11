package io.execube.monotype.phobos.custom_views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.execube.monotype.phobos.R;

import static android.support.v4.content.ContextCompat.getColor;
import static android.support.v4.content.ContextCompat.getDrawable;

/**
 * Created by hackertronix on 11/02/18.
 */

public class Pv extends android.support.v7.widget.AppCompatTextView {


    public Pv(Context context) {
        super(context);
        configure();
    }

    public Pv(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public void configure() {

        GradientDrawable drawable = (GradientDrawable)getDrawable(getContext(),R.drawable.pill_button);
        drawable.mutate();
        drawable.setColor(ContextCompat.getColor(getContext(),R.color.white));

        this.setAllCaps(true);
        this.setBackground(drawable);
        this.setTypeface(Typeface.create("null",Typeface.BOLD));
        this.setTextColor(getColor(getContext(),R.color.black));
        this.setLetterSpacing(0.2f);

    }
}
