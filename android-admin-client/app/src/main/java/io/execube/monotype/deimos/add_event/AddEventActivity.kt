package io.execube.monotype.deimos.add_event

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.model.Event
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function6
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_add_event.*
import java.util.*


class AddEventActivity : AppCompatActivity() {

    internal val EVENTS = "Events"

    lateinit var combinedObservable: Observable<Boolean>
    lateinit var fieldObserver: Disposable
    var documentReference = FirebaseFirestore.getInstance().collection(EVENTS).document()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)



        combinedObservable = combineLatest(
                RxAdapterView.itemSelections(category_spinner).skipInitialValue().distinctUntilChanged(),
                RxAdapterView.itemSelections(venue_spinner).skipInitialValue().distinctUntilChanged(),
                RxAdapterView.itemSelections(event_day_spinner).skipInitialValue().distinctUntilChanged(),
                RxTextView.textChanges(select_time),
                RxTextView.textChanges(event_name.editText as TextView),
                RxTextView.textChanges(event_description.editText as TextView),


                Function6 { eventCategory, eventVenue, eventDay, eventTime, eventName, eventDescription ->


                    when {

                        eventName.length in 1..3 -> {
                            event_name.error = "Event Name must be at least 4 characters long. You are ${4 - eventName.length} characters short"

                        }

                        else -> event_name.isErrorEnabled = false
                    }
                    when {

                        eventDescription.length in 1..19 -> {
                            event_description.error = "Event Description must be at least 20 characters long. You are ${20 - eventDescription.length} characters short"

                        }

                        else -> event_description.isErrorEnabled = false
                    }

                    if (eventTime.contains("TAP TO SELECT TIME", true)) {

                        select_time.setTextColor(Color.parseColor("#FF0000"))
                        select_time.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.abc_popup_enter))
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
                    val isValid = !eventName.isEmpty() &&
                            eventName.length > 4 &&
                            !eventDescription.isEmpty() &&
                            eventDescription.length > 20 &&
                            eventCategory != 0 &&
                            eventVenue != 0 &&
                            eventDay != 0 &&
                            !eventTime.contains("TAP TO SELECT TIME", true)

                    return@Function6 isValid

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
            val timePickerDialog = TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> select_time.text = """${hourOfDay.toString()}:$minute""" }, mHour, mMinute, true)
            timePickerDialog.show()
        }


        done_fab.setOnClickListener {
            saveEventToFirebase()
        }

    }

    private fun saveEventToFirebase() {
        val event = Event(
                eventName = event_name.editText?.text.toString().trim(),
                eventDescription = event_description.editText?.text.toString().trim(),
                eventCategory = category_spinner.selectedItem.toString(),
                eventVenue = venue_spinner.selectedItem.toString(),
                eventDate = event_day_spinner.selectedItem.toString(),
                eventTime = select_time.text.toString().trim()
        )

        documentReference.set(event)
                .addOnCompleteListener { Toast.makeText(this, "Event Saved Successfully!", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(this, "Failed to Save Event", Toast.LENGTH_SHORT).show() }
    }

    override fun onStop() {
        super.onStop()
        fieldObserver.dispose()
    }


    private fun toggleButton(value: Boolean?) {
        if (value == true) {


            done_fab.show()

        } else
            done_fab.hide()
    }
}
