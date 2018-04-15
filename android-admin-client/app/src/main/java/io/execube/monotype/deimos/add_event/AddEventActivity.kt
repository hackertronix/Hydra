package io.execube.monotype.deimos.add_event

import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.model.Event
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function6
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_add_event.category_spinner
import kotlinx.android.synthetic.main.activity_add_event.done_fab
import kotlinx.android.synthetic.main.activity_add_event.event_day_spinner
import kotlinx.android.synthetic.main.activity_add_event.event_description
import kotlinx.android.synthetic.main.activity_add_event.event_name
import kotlinx.android.synthetic.main.activity_add_event.select_time
import kotlinx.android.synthetic.main.activity_add_event.venue_spinner
import java.text.SimpleDateFormat
import java.util.Calendar

class AddEventActivity : AppCompatActivity() {

  internal val EVENTS = "Events"
  lateinit var combinedObservable: Observable<Boolean>
  lateinit var fieldObserver: Disposable
  var collectionReference = FirebaseFirestore.getInstance()
      .collection(EVENTS)
  var documentReference = FirebaseFirestore.getInstance()
      .collection(EVENTS)
      .document()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_event)
    checkIfDataWasPassed()
    combinedObservable = combineLatest(
        RxAdapterView.itemSelections(category_spinner).skipInitialValue().distinctUntilChanged(),
        RxAdapterView.itemSelections(venue_spinner).skipInitialValue().distinctUntilChanged(),
        RxAdapterView.itemSelections(event_day_spinner).skipInitialValue().distinctUntilChanged(),
        RxTextView.textChanges(select_time),
        RxTextView.textChanges(event_name.editText as TextView),
        RxTextView.textChanges(event_description.editText as TextView),

        Function6 { eventCategory, eventVenue, eventDay, eventTime, eventName, eventDescription ->

          when {

            eventName.trim().length in 1..3 || eventName.isEmpty() -> {
              event_name.error =
                  "Event Name must be at least 4 characters long. You are ${4 - eventName.length} characters short"

            }

            else -> event_name.isErrorEnabled = false
          }
          when {

            eventDescription.trim().length in 1..20 || eventDescription.isEmpty() -> {
              event_description.error =
                  "Event Description must be at least 20 characters long. You are ${22 - eventDescription.length} characters short"

            }

            else -> event_description.isErrorEnabled = false
          }

          if (eventTime.contains("TAP TO SELECT TIME", true)) {

            select_time.setTextColor(Color.parseColor("#FF0000"))
            select_time.startAnimation(
                AnimationUtils.loadAnimation(applicationContext, R.anim.abc_popup_enter)
            )
          }

          if (eventCategory == 0) {
            val textView = category_spinner.selectedView as? TextView
            textView?.error = ""
            textView?.text = "Event Category not selected"
          }

          if (eventVenue == 0) {

            val textView = venue_spinner.selectedView as? TextView
            textView?.error = ""
            textView?.text = "Event Venue not selected"
          }

          if (eventDay == 0) {

            val textView = event_day_spinner.selectedView as? TextView
            textView?.error = ""
            textView?.text = "Event Day not selected"
          }
          if (!eventName.isEmpty() &&
              eventName.length > 4 &&
              !eventDescription.isEmpty() &&
              eventDescription.length > 20 &&
              eventCategory != 0 &&
              eventVenue != 0 &&
              eventDay != 0 &&
              !eventTime.contains("TAP TO SELECT TIME", true)
          ) {
            return@Function6 true
          } else {
            return@Function6 false
          }

        })


    fieldObserver = object : DisposableObserver<Boolean>() {

      override fun onComplete() {

      }

      override fun onNext(value: Boolean?) {

        toggleButton(value)

      }

      override fun onError(e: Throwable?) {
      }

    }


    combinedObservable.subscribe(fieldObserver as DisposableObserver<Boolean>)




    select_time.setOnClickListener {

      // Get Current Time
      val c = Calendar.getInstance()
      val mHour = c.get(Calendar.HOUR_OF_DAY)
      val mMinute = c.get(Calendar.MINUTE)

      // Launch Time Picker Dialog
      val timePickerDialog = TimePickerDialog(
          this,
          TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
            c.set(Calendar.MINUTE, minute)
            select_time.text = simpleDateFormat.format(c.time)
          }, mHour, mMinute, false
      )
      timePickerDialog.show()
    }


    done_fab.setOnClickListener {
      val eventExists = intent.hasExtra("EVENT_EDIT")
      saveEventToFirebase(eventExists)
    }

  }

  private fun checkIfDataWasPassed() {
    if (this.intent.hasExtra("EVENT_EDIT")) {
      setupEventData()
    }
  }

  private fun setupEventData() {

    toggleButton(true)
    val event = intent.extras.get("EVENT_EDIT") as Event
    event_name.editText?.setText(event.eventName, EDITABLE)
    event_description.editText?.setText(event.eventDescription, EDITABLE)
    select_time.text = event.eventTime

    when {
      event.eventCategory.equals("MAIN") -> {
        category_spinner.setSelection(1)

      }
      event.eventCategory.equals("GENERAL") -> {
        category_spinner.setSelection(2)
      }
      event.eventCategory.equals("FLAGSHIP") -> {
        category_spinner.setSelection(3)
      }
      event.eventCategory.equals("TEACHER") -> {
        category_spinner.setSelection(4)
      }
    }

    when {
      event.eventVenue.equals("Main Stage") -> {
        venue_spinner.setSelection(1)

      }
      event.eventVenue.equals("Quadrangle") -> {
        venue_spinner.setSelection(2)

      }
      event.eventVenue.equals("CSE Lab") -> {
        venue_spinner.setSelection(3)

      }
      event.eventVenue.equals("EC Lab") -> {
        venue_spinner.setSelection(4)
      }
      event.eventVenue.equals("Edusat Hall") -> {
        venue_spinner.setSelection(5)
      }
      event.eventVenue.equals("PU Ground") -> {
        venue_spinner.setSelection(6)
      }

      event.eventVenue.equals("Maggie Station") -> {
        venue_spinner.setSelection(7)
      }
      event.eventVenue.equals("CSE Ground") -> {
        venue_spinner.setSelection(8)

      }
      event.eventVenue.equals("EEE Class Room") -> {
        venue_spinner.setSelection(9)
      }
      event.eventVenue.equals("Admin Seminar Hall") -> {
        venue_spinner.setSelection(10)
      }
      event.eventVenue.equals("School Ground") -> {
        venue_spinner.setSelection(11)
      }
      event.eventVenue.equals("Basketball Court") -> {
        venue_spinner.setSelection(12)
      }
      event.eventVenue.equals("ISE Lab") -> {
        venue_spinner.setSelection(13)
      }
    }

    when {
      event.eventDate.equals("April 20") -> {
        event_day_spinner.setSelection(1)

      }
      event.eventDate.equals("April 21") -> {
        event_day_spinner.setSelection(2)
      }

    }
  }

  private fun toggleFields(value: Boolean) {
    event_name.isActivated = value
    event_description.isActivated = value
    event_day_spinner.isActivated = value
    category_spinner.isActivated = value
    venue_spinner.isActivated = value

  }

  override fun onBackPressed() {
    finish()
  }

  private fun saveEventToFirebase(eventExistsOnCloud: Boolean) {

    if (eventExistsOnCloud) {
      done_fab.isActivated = false
      val uploadingAVD =
        getDrawable(R.drawable.avd_uploading) as AnimatedVectorDrawable
      if (uploadingAVD != null) {
        done_fab.setImageDrawable(uploadingAVD)
        uploadingAVD.start()
      }
      val eventColor = getEventColor(category_spinner.selectedItem.toString())
      val documendId = (intent.extras.get("EVENT_EDIT") as Event).eventId
      val documentReference = collectionReference.document(documendId)

      val event = Event(
          eventId = documentReference.id,
          eventName = event_name.editText?.text.toString().trim(),
          eventDescription = event_description.editText?.text.toString().trim(),
          eventCategory = category_spinner.selectedItem.toString(),
          eventColor = eventColor,
          eventVenue = venue_spinner.selectedItem.toString(),
          eventDate = event_day_spinner.selectedItem.toString(),
          eventTime = select_time.text.toString().trim()
      )

      FirebaseFirestore.getInstance()
          .runTransaction(object : Transaction.Function<Void> {

            override fun apply(transaction: Transaction): Void? {

              val snapShot = transaction.get(documentReference)

              transaction.update(documentReference, "eventName", event.eventName)
              transaction.update(documentReference, "eventDescription", event.eventDescription)
              transaction.update(documentReference, "eventCategory", event.eventCategory)
              transaction.update(documentReference, "eventColor", event.eventColor)
              transaction.update(documentReference, "eventVenue", event.eventVenue)
              transaction.update(documentReference, "eventDate", event.eventDate)
              transaction.update(documentReference, "eventTime", event.eventTime)

              return null

            }

          })
          .addOnSuccessListener {
            val complete =
              getDrawable(R.drawable.avd_upload_complete) as AnimatedVectorDrawable?
            if (complete != null) {
              done_fab.setImageDrawable(complete)
              complete.start()
              done_fab.postDelayed(
                  {
                    startActivity(Intent(this@AddEventActivity, HomeActivity::class.java))
                    finish()
                  }, 1600
              )
              // 220 ms is length of R.drawable.avd_upload_complete
            }

          }
          .addOnFailureListener {
            Log.e("ADD_EVENT", it.message)
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                .show()
            toggleFields(true)
            done_fab.isActivated = true
            val failed =
              getDrawable(R.drawable.avd_upload_error) as AnimatedVectorDrawable?
            if (failed != null) {
              done_fab.setImageDrawable(failed)
              failed.start()
            }
          }

    }

    //If event is a new event
    else {

      done_fab.isActivated = false
      val uploadingAVD =
        getDrawable(R.drawable.avd_uploading) as AnimatedVectorDrawable
      if (uploadingAVD != null) {
        done_fab.setImageDrawable(uploadingAVD)
        uploadingAVD.start()
      }

      val eventColor = getEventColor(category_spinner.selectedItem.toString())

      val event = Event(
          eventId = documentReference.id,
          eventName = event_name.editText?.text.toString().trim(),
          eventDescription = event_description.editText?.text.toString().trim(),
          eventCategory = category_spinner.selectedItem.toString(),
          eventColor = eventColor,
          eventVenue = venue_spinner.selectedItem.toString(),
          eventDate = event_day_spinner.selectedItem.toString(),
          eventTime = select_time.text.toString().trim()
      )




      documentReference.set(event)
          .addOnCompleteListener {
            val complete =
              getDrawable(R.drawable.avd_upload_complete) as AnimatedVectorDrawable?
            if (complete != null) {
              done_fab.setImageDrawable(complete)
              complete.start()
              done_fab.postDelayed(
                  {
                    startActivity(Intent(this@AddEventActivity, HomeActivity::class.java))
                    finish()
                  }, 1600
              )
              // 220 ms is length of R.drawable.avd_upload_complete
            }
          }
          .addOnFailureListener {
            Log.e("ADD_EVENT", it.message)
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                .show()
            toggleFields(true)
            done_fab.isActivated = true
            val failed =
              getDrawable(R.drawable.avd_upload_error) as AnimatedVectorDrawable?
            if (failed != null) {
              done_fab.setImageDrawable(failed)
              failed.start()
            }
          }

    }

  }

  private fun getEventColor(category: String): String {

    var eventColor = ""
    when {
      category.equals("MAIN", false) -> {
        eventColor = ContextCompat.getColor(this, R.color.dance)
            .toString()
      }
      category.equals("TEACHER", false) -> {
        eventColor = ContextCompat.getColor(this, R.color.music)
            .toString()
      }
      category.equals("FLAGSHIP", false) -> {
        eventColor = ContextCompat.getColor(this, R.color.gaming)
            .toString()
      }
      category.equals("GENERAL", false) -> {
        eventColor = ContextCompat.getColor(this, R.color.tech)
            .toString()
      }
    }

    return eventColor
  }

  override fun onDestroy() {
    super.onDestroy()
    fieldObserver.dispose()
  }

  private fun toggleButton(value: Boolean?) {
    if (value == true) {

      done_fab.visibility = View.VISIBLE
      showFab()

    } else
      done_fab.visibility = View.INVISIBLE

  }

  private fun hideFab() {

    done_fab.alpha = 1f
    done_fab.scaleX = 1f
    done_fab.scaleY = 1f
    done_fab.translationY = done_fab.getHeight() / 2f;
    done_fab.animate()
        .alpha(0f)
        .scaleX(0f)
        .scaleY(0f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(this))
        .start()
  }

  private fun showFab() {

    done_fab.alpha = 0f
    done_fab.scaleX = 0f
    done_fab.scaleY = 0f
    done_fab.translationY = done_fab.getHeight() / 2f;
    done_fab.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(this))
        .start()

  }
}
