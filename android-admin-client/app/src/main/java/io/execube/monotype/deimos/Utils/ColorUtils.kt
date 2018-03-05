package io.execube.monotype.deimos.Utils

import android.graphics.Color

fun generateTextColor(backgroundColor: Int): Int{

    val factor = 3.2f
    val a = Color.alpha(backgroundColor)
    val r = Math.round(Color.red(backgroundColor) * factor)
    val g = Math.round(Color.green(backgroundColor) * factor)
    val b = Math.round(Color.blue(backgroundColor) * factor)
    return Color.argb(a,
            Math.min(r, 255),
            Math.min(g, 255),
            Math.min(b, 255))


}

