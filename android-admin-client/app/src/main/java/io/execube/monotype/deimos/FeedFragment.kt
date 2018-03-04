package io.execube.monotype.deimos

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_feed, container, false)


    }


    override fun onResume() {
        super.onResume()

        animateFab()
        add_event.setOnClickListener {

            startActivity(Intent(context, AddEventActivity::class.java))
        }


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