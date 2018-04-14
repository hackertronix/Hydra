package io.execube.monotype.deimos.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.add_event.AddEventActivity
import io.execube.monotype.deimos.event_feed.FeedFragment
import io.execube.monotype.deimos.photos.PhotosFragment
import io.execube.monotype.deimos.sign_in.SignInActivity
import kotlinx.android.synthetic.main.activity_home.home_reveal_view
import kotlinx.android.synthetic.main.activity_home.navigation
import kotlinx.android.synthetic.main.activity_home.toolbar

class HomeActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    setSupportActionBar(toolbar)
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

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    return true
  }

  public fun doReveal(
    fabWidth: Float,
    fabX: Float,
    fabY: Float
  ) {
    home_reveal_view.visibility = View.VISIBLE
    val cx = home_reveal_view.width
    val cy = home_reveal_view.height

    val startX: Int = (fabWidth + fabX).toInt()
    val startY: Int = (fabWidth + fabY).toInt()

    val finalRadius = Math.max(cx, cy) * 1.2f

    val revealAnimator = ViewAnimationUtils.createCircularReveal(
        home_reveal_view, startX, startY, fabWidth, finalRadius
    )

    revealAnimator.setDuration(450)
        .addListener(object : AnimatorListenerAdapter() {
           override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            startActivity(Intent(this@HomeActivity, AddEventActivity::class.java))
          }
        })
    revealAnimator.start()

  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {

      R.id.logout -> {
        FirebaseAuth.getInstance()
            .signOut()
        startActivityForResult(Intent(this@HomeActivity, SignInActivity::class.java), 900)
        finish()
      }

    }

    return super.onOptionsItemSelected(item)
  }

  override fun onResume() {
    super.onResume()
    home_reveal_view.visibility = View.INVISIBLE
  }

  private fun swapFragment() {

    val fragment = FeedFragment()
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.frame_layout, fragment)
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
          .setDuration(900)
          .interpolator = getLinearOutSlowInInterpolator(this)
    }
  }

}
