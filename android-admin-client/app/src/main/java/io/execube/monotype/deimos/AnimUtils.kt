package io.execube.monotype.deimos

import android.content.Context
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator


fun getLinearOutSlowInInterpolator(context: Context): Interpolator {

        val linearOutSlowIn = AnimationUtils.loadInterpolator(context,
                android.R.interpolator.linear_out_slow_in)

    return linearOutSlowIn
}