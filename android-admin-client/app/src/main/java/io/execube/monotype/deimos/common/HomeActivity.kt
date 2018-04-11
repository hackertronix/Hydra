package io.execube.monotype.deimos.common

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.TextView
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.event_feed.FeedFragment
import io.execube.monotype.deimos.photos.PhotosFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        animateToolbar()
        swapFragment()

      navigation.setOnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        when (item.itemId) {

          R.id.navigation_feed -> selectedFragment = FeedFragment()

          R.id.navigation_photos -> selectedFragment = PhotosFragment()

        //TODO change them back to their respective fragments
          R.id.navigation_notification -> selectedFragment = PhotosFragment()
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, selectedFragment)
        fragmentTransaction.commit()
        true
      }

    }



    private fun swapFragment() {

        val fragment = FeedFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout,fragment)
        transaction.commit()
    }

    private fun animateToolbar() {
        val t = toolbar.getChildAt(0)
        if (t != null && t is TextView) {
            val title = t

            // fade in and space out the title.  Animating the letterSpacing performs horribly so
            // fake it by setting the desired letterSpacing then animating the scaleX
            title.alpha = 0f
            title.scaleX = 0.8f

            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(300)
                    .setDuration(900).interpolator = getLinearOutSlowInInterpolator(this)
        }
    }



}
