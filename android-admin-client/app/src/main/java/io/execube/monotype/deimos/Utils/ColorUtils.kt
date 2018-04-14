package io.execube.monotype.deimos.Utils

import android.graphics.Color

fun generateDarkerColorShade(backgroundColor: Int, factor:Float = 0.5f): Int {

  val a = Color.alpha(backgroundColor)
  val r = Math.round(Color.red(backgroundColor) * factor)
  val g = Math.round(Color.green(backgroundColor) * factor)
  val b = Math.round(Color.blue(backgroundColor) * factor)
  val newColor = Color.argb(
      a,
      Math.min(r, 255),
      Math.min(g, 255),
      Math.min(b, 255))

  return newColor

}


