package io.execube.monotype.deimos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_feed, container, false)


    }




    override fun onResume() {
        super.onResume()
        add_event_fab.show()
        add_event_fab.setOnClickListener {

            startActivity(Intent(context, AddEventActivity::class.java))
        }
    }


}