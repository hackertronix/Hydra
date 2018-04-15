package io.execube.monotype.deimos.event_feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnItemTouchListener
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.GridItemDividerDecoration
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.event_details.EventDetailsActivity
import io.execube.monotype.deimos.model.Event
import kotlinx.android.synthetic.main.activity_main.reveal_view
import kotlinx.android.synthetic.main.fragment_feed.add_event
import kotlinx.android.synthetic.main.fragment_feed.events_feed
import java.io.Serializable

class FeedFragment : Fragment() {

  lateinit var events: ArrayList<Event>
  lateinit var adapter: FeedAdapter
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val view = inflater.inflate(R.layout.fragment_feed, container, false)
    events = ArrayList()
    val feed = view.findViewById(R.id.events_feed) as RecyclerView
    feed.layoutManager = GridLayoutManager(this.context, 2)
    feed.itemAnimator = DefaultItemAnimator()
    feed.addItemDecoration(GridItemDividerDecoration(this@FeedFragment.requireContext(),R.dimen.divider_height,R.color.divider))
    adapter = FeedAdapter(events)
    feed.adapter = adapter
    return view
  }

  override fun onPause() {
    super.onPause()
    add_event.visibility = View.INVISIBLE
  }

  override fun onResume() {
    super.onResume()
    reveal_view.visibility = View.INVISIBLE
    ViewModelProviders.of(this)
        .get(FeedViewModel::class.java)
        .getEvents()
        .observe(this, Observer { data ->
          if (data != null) {
            adapter.swapData(data as ArrayList<Event>)
            events_feed.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.context,R.anim.recyclerview_animation)
            events_feed.scheduleLayoutAnimation()
          }
        })
    animateFab()
    add_event.setOnClickListener {

      (this.context as HomeActivity).doReveal(add_event.width/2.toFloat(),add_event.x,add_event.y)
    }



  }


  private fun animateFab() {

    add_event.visibility = View.VISIBLE
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