package io.execube.monotype.deimos.notifications

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.model.NotificationData
import io.execube.monotype.deimos.notifications.NotificationsAdapter.NotificationHolder
import kotlinx.android.synthetic.main.notification_card_item.view.notification_description
import kotlinx.android.synthetic.main.notification_card_item.view.notification_title
import java.util.Collections

class NotificationsAdapter(private var notifications: ArrayList<NotificationData>): RecyclerView.Adapter<NotificationHolder>() {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): NotificationHolder {

    val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_card_item,parent,false)
    return NotificationHolder(view)

  }

  override fun getItemCount(): Int {
    return notifications.size
  }

  override fun onBindViewHolder(
    holder: NotificationHolder,
    position: Int
  ) {
    holder.bind(notifications[position])
  }

  fun setData(newList: ArrayList<NotificationData>) {
    notifications.clear()
    Collections.sort(newList,NotificationData())
    newList.reverse()
    notifications.addAll(newList)
    notifyDataSetChanged()
  }

  inner class NotificationHolder(itemView:View):RecyclerView.ViewHolder(itemView){
    fun bind(notificationData: NotificationData) {


      itemView.notification_title.text = notificationData.notificationTitle
      itemView.notification_description.text = notificationData.notificationDescription

    }

  }

}