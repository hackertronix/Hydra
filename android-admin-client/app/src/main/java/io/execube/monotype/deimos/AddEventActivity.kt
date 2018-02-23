package io.execube.monotype.deimos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_add_event.*

class AddEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

//        category_spinner.prompt = "Event Category"
//        val categorySpinnerAdapter = ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,arrayListOf("DANCE","MUSIC","GAMING","RAMP"))
//        category_spinner.adapter = categorySpinnerAdapter
    }
}
