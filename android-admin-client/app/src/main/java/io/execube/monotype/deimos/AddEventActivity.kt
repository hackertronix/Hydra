package io.execube.monotype.deimos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.execube.monotype.deimos.model.Event
import kotlinx.android.synthetic.main.activity_add_event.*

class AddEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)


        setupDefaultState()
        validate()



    }

    private fun validate() {

       //TODO add validation code
    }

    private fun setupDefaultState() {
        //By default the fab will be invisible and will be spawned only on completing all fields
            done_fab.visibility = View.GONE

    }
}
