package io.execube.monotype.deimos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.fragment_feed.*

class AddEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)


        setupDefaultState()

        add_event_fab.setOnClickListener {
            validate()
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
        var eventVenue = venue_spinner.selectedItem.toString()
        var eventCategory = category_spinner.selectedItem.toString()


        





    }

    private fun setupDefaultState() {
        //By default the fab will be invisible and will be spawned only on completing all fields

    }
}
