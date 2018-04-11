package io.execube.monotype.deimos.event_feed

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.EventDiff
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
            holder.setCardColor(events[position].eventCategory)
          }
          else -> super.onBindViewHolder(holder, position, payloads)
         }
      }

    }
  }

  inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Divided {

    lateinit var event: Event
    fun bind(event: Event) {

      this.event = event

      setCardColor(event.eventCategory)
      itemView.event_name.text = event.eventName
      itemView.event_description.text = event.eventDescription
      itemView.event_category.text = event.eventCategory?.toUpperCase()
    }

    fun setCardColor(eventCategory: String?) {
      when {
        eventCategory.equals("MUSIC") -> {
          itemView.event_card.setCardBackgroundColor(
              ContextCompat.getColor(itemView.context, R.color.music)
          )
        }
        eventCategory.equals("DANCE") -> {
          itemView.event_card.setCardBackgroundColor(
              ContextCompat.getColor(itemView.context, R.color.dance)
          )
        }
        eventCategory.equals("TECH") -> {
          itemView.event_card.setCardBackgroundColor(
              ContextCompat.getColor(itemView.context, R.color.tech)
          )
        }
        eventCategory.equals("RAMP") -> {
          itemView.event_card.setCardBackgroundColor(
              ContextCompat.getColor(itemView.context, R.color.gaming)
          )
        }
        eventCategory.equals("GAMING") -> {
          itemView.event_card.setCardBackgroundColor(
              ContextCompat.getColor(itemView.context, R.color.card_color_bluish)
          )
        }
      }
    }

  }
}