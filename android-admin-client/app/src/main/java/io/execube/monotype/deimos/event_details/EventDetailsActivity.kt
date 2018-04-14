package io.execube.monotype.deimos.event_details

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.generateDarkerColorShade
import io.execube.monotype.deimos.model.Event
import kotlinx.android.synthetic.main.activity_event_details.details_toolbar
import kotlinx.android.synthetic.main.activity_event_details.details_toolbar_back_button
import kotlinx.android.synthetic.main.activity_event_details.event_category
import kotlinx.android.synthetic.main.activity_event_details.event_description
import kotlinx.android.synthetic.main.activity_event_details.event_details_container
import kotlinx.android.synthetic.main.activity_event_details.event_location
import kotlinx.android.synthetic.main.activity_event_details.event_name
import kotlinx.android.synthetic.main.activity_event_details.event_time
import kotlinx.android.synthetic.main.activity_event_details.location_icon
import kotlinx.android.synthetic.main.activity_event_details.time_icon

class EventDetailsActivity : AppCompatActivity() {

  lateinit var event: Event

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_event_details)

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

    val iconColor = generateDarkerColorShade(event.eventColor.toInt(),0.4f)
    location_icon.setColorFilter(iconColor)
    time_icon.setColorFilter(iconColor)
    details_toolbar_back_button.setColorFilter(iconColor)
  }
}
