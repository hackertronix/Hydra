package io.execube.monotype.deimos.event_details

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.Slide
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.generateDarkerColorShade
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.add_event.AddEventActivity
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.model.Event
import kotlinx.android.synthetic.main.activity_event_details.details_reveal_view
import kotlinx.android.synthetic.main.activity_event_details.details_toolbar
import kotlinx.android.synthetic.main.activity_event_details.event_category
import kotlinx.android.synthetic.main.activity_event_details.event_description
import kotlinx.android.synthetic.main.activity_event_details.event_details_container
import kotlinx.android.synthetic.main.activity_event_details.event_location
import kotlinx.android.synthetic.main.activity_event_details.event_name
import kotlinx.android.synthetic.main.activity_event_details.event_time
import kotlinx.android.synthetic.main.activity_event_details.fab
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
    val backArrowDrawable = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)
    backArrowDrawable.setColorFilter(iconColor,PorterDuff.Mode.SRC_ATOP)
    supportActionBar?.setHomeAsUpIndicator(backArrowDrawable)

    location_icon.setColorFilter(iconColor)
    fab.setColorFilter(textColor)
    time_icon.setColorFilter(iconColor)

    animateCategory()
    animateFab()

    fab.setOnClickListener {

      doReveal()
    }

  }

  override fun onResume() {
    super.onResume()
    details_reveal_view.visibility = View.INVISIBLE
  }

  private fun doReveal() {
    details_reveal_view.visibility = View.VISIBLE
    val cx = details_reveal_view.width
    val cy = details_reveal_view.height

    val fabWidth: Int = fab.width / 2
    val startX: Int = (fabWidth + fab.x).toInt()
    val startY: Int = (fabWidth + fab.y).toInt()

    val finalRadius = Math.max(cx, cy) * 1.2f

    val revealAnimator = ViewAnimationUtils.createCircularReveal(
        details_reveal_view, startX, startY, fabWidth.toFloat(), finalRadius
    )

    revealAnimator.setDuration(450)
        .addListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            val intent = Intent(this@EventDetailsActivity, AddEventActivity::class.java)
            intent.putExtra("EVENT_EDIT",event)
            startActivity(intent)
          }
        })
    revealAnimator.start()
  }

  private fun initToolbar() {
    details_toolbar.title = " "
    setSupportActionBar(details_toolbar as Toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  private fun animateFab() {
    fab.visibility = View.VISIBLE
    fab.alpha = 0f
    fab.scaleX = 0f
    fab.scaleY = 0f
    fab.translationY = fab.getHeight() / 2f
    fab.animate()
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

      item?.itemId == R.id.delete_event ->{
        askForConfirmation()
        return true
      }
      else -> return super.onOptionsItemSelected(item)

    }

  }

  private fun askForConfirmation() {
    AlertDialog.Builder(this)
        .setTitle("Delete")
        .setCancelable(false)
        .setMessage("Deleting this event will permanently remove it for all users. Are you sure?")
        .setPositiveButton("YES"){
          dialog, _ ->
          deleteEvent()
          dialog.dismiss()
        }.setNegativeButton("NO"){
          dialog, _ ->
          dialog.dismiss()
        }.show()
  }

  private fun deleteEvent() {
    FirebaseFirestore.getInstance().collection("Events")
        .document(event.eventId)
        .delete()
        .addOnSuccessListener {
          Toast.makeText(this,"${event.eventName} was deleted successfully",Toast.LENGTH_SHORT).show()
          startActivity(Intent(this@EventDetailsActivity,HomeActivity::class.java))
          finish()
        }.addOnFailureListener {
          Toast.makeText(this,"Faile to delete ${event.eventName} ",Toast.LENGTH_SHORT).show()

        }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.details_menu, menu)
    val menuDrawable = menu?.getItem(0)?.icon
    menuDrawable?.mutate()
    val iconColor = generateDarkerColorShade(event.eventColor.toInt(), 0.4f)
    menuDrawable?.setColorFilter(iconColor,PorterDuff.Mode.SRC_ATOP)
    return true
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
