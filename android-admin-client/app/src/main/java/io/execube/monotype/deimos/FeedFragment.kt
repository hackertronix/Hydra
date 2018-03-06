package io.execube.monotype.deimos

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.model.Event
import io.execube.monotype.deimos.model.FirestoreLiveData
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment() {

     lateinit var events: List<Event>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_feed, container, false)


    }


    override fun onResume() {
        super.onResume()

        animateFab()
        add_event.setOnClickListener {

            startActivity(Intent(context, AddEventActivity::class.java))
        }

        ViewModelProviders.of(this).get(FeedViewModel::class.java)
                .getEvents()
                .observe(this, Observer {
                    Log.d("YOYO",it.toString())
                    events = it!!
                })

    }

    private fun animateFab() {

        add_event.alpha = 0f
        add_event.scaleX = 0f
        add_event.scaleY = 0f
        add_event.translationY = add_event.getHeight() / 2f;
        add_event.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(500L)
                .setInterpolator(getLinearOutSlowInInterpolator(context!!))
                .start()
    }


}