package io.execube.monotype.deimos.photo_feed

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import kotlinx.android.synthetic.main.fragment_photos.add_photo

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PhotosFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_photos,container,false)
    return view
  }

  override fun onPause() {
    super.onPause()
  }

  override fun onResume() {
    super.onResume()
    animateFab()
  }

  private fun animateFab() {

    add_photo.visibility = View.VISIBLE
    add_photo.alpha = 0f
    add_photo.scaleX = 0f
    add_photo.scaleY = 0f
    add_photo.translationY = add_photo.getHeight() / 2f;
    add_photo.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(context!!))
        .start()
  }
}
