package io.execube.monotype.deimos.notifications

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.R.anim
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.model.NotificationData
import kotlinx.android.synthetic.main.fragment_notifications.add_notification
import kotlinx.android.synthetic.main.fragment_notifications.notifications_feed

class NotificationsFragment : Fragment() {

  lateinit var adapter:NotificationsAdapter
  lateinit var notifications: ArrayList<NotificationData>

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    notifications = ArrayList()
    val view = inflater.inflate(R.layout.fragment_notifications, container, false)
    val feed = view.findViewById(R.id.notifications_feed) as RecyclerView
    feed.layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
    feed.itemAnimator = DefaultItemAnimator()
    adapter = NotificationsAdapter(notifications)
    feed.adapter =adapter
    return view
  }

  override fun onResume() {
    super.onResume()
    ViewModelProviders.of(this)
        .get(NotificationsViewModel::class.java)
        .getEvents()
        .observe(this, Observer { data ->
          if (data != null) {
            adapter.setData(data as ArrayList<NotificationData>)

          }
        })
    animateFab()
    add_notification.setOnClickListener {

      (this.context as HomeActivity).doRevealForNotifications(add_notification.width/2.toFloat(),add_notification.x,add_notification.y)
    }


  }

  private fun animateFab() {

    add_notification.visibility = View.VISIBLE
    add_notification.alpha = 0f
    add_notification.scaleX = 0f
    add_notification.scaleY = 0f
    add_notification.translationY = add_notification.getHeight() / 2f;
    add_notification.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(context!!))
        .start()
  }
}


