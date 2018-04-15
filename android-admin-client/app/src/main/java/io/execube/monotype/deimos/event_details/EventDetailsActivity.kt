package io.execube.monotype.deimos.event_details

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.Slide
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.generateDarkerColorShade
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.model.Event
import kotlinx.android.synthetic.main.activity_event_details.details_toolbar
import kotlinx.android.synthetic.main.activity_event_details.event_category
import kotlinx.android.synthetic.main.activity_event_details.event_description
import kotlinx.android.synthetic.main.activity_event_details.event_details_container
import kotlinx.android.synthetic.main.activity_event_details.event_location
import kotlinx.android.synthetic.main.activity_event_details.event_name
import kotlinx.android.synthetic.main.activity_event_details.event_time
import kotlinx.android.synthetic.main.activity_event_details.favourite_button
import kotlinx.android.synthetic.main.activity_event_details.linearLayout
import kotlinx.android.synthetic.main.activity_event_details.location_icon
import kotlinx.android.synthetic.main.activity_event_details.time_icon

class EventDetailsActivity : AppCompatActivity() {

  lateinit var event: Event

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_event_details)

    initToolbar()
    val slide = Slide(Gravity.LEFT)
    slide.excludeTarget(android.R.id.statusBarBackground, true)
    slide.excludeTarget(android.R.id.navigationBarBackground, true)

    getWindow().setEnterTransition(slide)
    postponeEnterTransition()

    event = intent.extras.get("EVENT") as Event

    val textColor = generateDarkerColorShade(event.eventColor.toInt())
    details_toolbar.setBackgroundColor(event.eventColor.toInt())
    event_details_container.setBackgroundColor(event.eventColor.toInt())
    event_details_container.setStatusBarBackgroundColor(event.eventColor.toInt())

    event_name.text = event.eventName
    event_description.text = event.eventDescription
    event_category.text = event.eventCategory
    event_location.text = event.eventVenue
    event_time.text = "${event.eventDate}, ${event.eventTime}"

    event_name.setTextColor(textColor)
    event_description.setTextColor(textColor)
    event_time.setTextColor(textColor)
    event_category.setTextColor(textColor)
    event_location.setTextColor(textColor)

    val iconColor = generateDarkerColorShade(event.eventColor.toInt(), 0.4f)
    val drawable = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)
    drawable.setColorFilter(iconColor,PorterDuff.Mode.SRC_ATOP)
    supportActionBar?.setHomeAsUpIndicator(drawable)

    location_icon.setColorFilter(iconColor)
    favourite_button.setColorFilter(textColor)
    time_icon.setColorFilter(iconColor)

    animateCategory()
    animateFab()

  }

  private fun initToolbar() {
    details_toolbar.title = " "
    setSupportActionBar(details_toolbar as Toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  private fun animateFab() {
    favourite_button.visibility = View.VISIBLE
    favourite_button.alpha = 0f
    favourite_button.scaleX = 0f
    favourite_button.scaleY = 0f
    favourite_button.translationY = favourite_button.getHeight() / 2f
    favourite_button.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(this))
        .start()
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when {

      item?.itemId == android.R.id.home -> {

        finish()
        return true
      }

      else -> return super.onOptionsItemSelected(item)

    }

  }

  private fun animateCategory() {

    val container = linearLayout as LinearLayout

    // fade in and space out the title.  Animating the letterSpacing performs horribly so
    // fake it by setting the desired letterSpacing then animating the scaleX
    container.alpha = 0f
    container.translationY = 60f

    container.animate()
        .alpha(1f)
        .setStartDelay(200)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(this))

        .start()
  }

}
