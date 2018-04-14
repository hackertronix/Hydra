package io.execube.monotype.deimos.event_feed

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.color
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.EventDiff
import io.execube.monotype.deimos.Utils.generateTextColor
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.event_feed.FeedAdapter.FeedViewHolder
import io.execube.monotype.deimos.model.Event
import kotlinx.android.synthetic.main.event_card_item.view.event_card
import kotlinx.android.synthetic.main.event_card_item.view.event_category
import kotlinx.android.synthetic.main.event_card_item.view.event_description
import kotlinx.android.synthetic.main.event_card_item.view.event_name

class FeedAdapter(private var events: ArrayList<Event>) : RecyclerView.Adapter<FeedViewHolder>() {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): FeedViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.event_card_item, parent, false)
    return FeedViewHolder(view)
  }

  override fun getItemCount(): Int {

    return events.size
  }

  fun swapData(newList: ArrayList<Event>) {
    val eventDiff = EventDiff(events, newList)
    val diffResult = DiffUtil.calculateDiff(eventDiff)
    events.clear()
    events.addAll(newList)
    diffResult.dispatchUpdatesTo(this)
  }

  override fun onBindViewHolder(
    holder: FeedViewHolder,
    position: Int
  ) {

    holder.bind(events[position])
  }

  override fun onBindViewHolder(
    holder: FeedViewHolder,
    position: Int,
    payloads: MutableList<Any>
  ) {
    if (payloads.isEmpty()) {
      super.onBindViewHolder(holder, position, payloads)

    }
    else{

      val bundle = payloads.get(0) as Bundle
      val keySet = bundle.keySet()
      keySet.forEach {
        when(it){

          "EVENT_NAME" -> {
            holder.itemView.event_name.text = events[position].eventName
          }
          "EVENT_DESCRIPTION" ->{
            holder.itemView.event_description.text = events[position].eventDescription
          }
          "EVENT_CATEGORY" ->{
            holder.itemView.event_category.text = events[position].eventCategory
            holder.setCardColor(events[position].eventColor)
          }
          else -> super.onBindViewHolder(holder, position, payloads)
         }
      }

    }
  }

  inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Divided,OnClickListener {


    override fun onClick(v: View?) {
      (itemView.context as HomeActivity).showDetails(events[adapterPosition])
    }

    lateinit var event: Event
    fun bind(event: Event) {

      this.event = event

      setCardColor(event.eventColor)
      itemView.setOnClickListener(this)
      itemView.event_name.text = event.eventName
      itemView.event_description.text = event.eventDescription
      itemView.event_category.text = event.eventCategory.toUpperCase()
    }

    fun setCardColor(eventColor: String) {

          val colorIntValue = Color.argb(Color.alpha(eventColor.toInt()),
              Color.red(eventColor.toInt()),
              Color.green(eventColor.toInt()),
              Color.blue(eventColor.toInt())
          )

          itemView.event_card.setCardBackgroundColor(colorIntValue)
          val textColor = generateTextColor(colorIntValue)
          itemView.event_name.setTextColor(textColor)
          itemView.event_description.setTextColor(textColor)
        }
      }
    }
