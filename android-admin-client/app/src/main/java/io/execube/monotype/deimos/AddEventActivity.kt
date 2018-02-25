package io.execube.monotype.deimos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.execube.monotype.deimos.model.Event
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.fragment_feed.*
import android.widget.DatePicker
import android.app.DatePickerDialog
import java.util.*
import android.widget.TimePicker
import android.app.TimePickerDialog




class AddEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)


        setupDefaultState()

        add_event_fab.setOnClickListener {
            validate()
        }


        select_date.setOnClickListener {

            val calendar = Calendar.getInstance()
            val mYear = calendar.get(Calendar.YEAR)
            val mMonth = calendar.get(Calendar.MONTH)
            val mDay = calendar.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth -> select_date.text = "${dayOfMonth.toString()}-${monthOfYear + 1}-$year" }, mYear, mMonth, mDay)
            datePickerDialog.show()
        }


        select_time.setOnClickListener {
            // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> select_time.text = """${hourOfDay.toString()}:$minute""" }, mHour, mMinute, false)
            timePickerDialog.show()
        }


    }

    private fun validate() {

        //TODO add validation code

        //event name should be atleast 8 characters
        //event description should be atleast 140 characters
        //event venue should not be the label venue
        //event category should not be the label category

        var eventName = event_name.editText?.text.toString().trim()
        var eventDescription = event_description.editText.toString().trim()
        var eventVenue = venue_spinner.selectedItemPosition
        var eventCategory = category_spinner.selectedItemPosition




    }

    private fun setupDefaultState() {
        //By default the fab will be invisible and will be spawned only on completing all fields

    }
}
