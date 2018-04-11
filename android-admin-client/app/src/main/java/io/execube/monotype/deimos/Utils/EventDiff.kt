package io.execube.monotype.deimos.Utils

import android.os.Bundle
import android.support.v7.util.DiffUtil
import io.execube.monotype.deimos.model.Event

class EventDiff(
  private val oldList: ArrayList<Event>,
  private val newList: ArrayList<Event>
) : DiffUtil.Callback() {


  override fun areItemsTheSame(
    oldItemPosition: Int,
    newItemPosition: Int
  ): Boolean {

    return (oldList[oldItemPosition].eventId.equals(newList[newItemPosition].eventId))
  }

  override fun getOldListSize(): Int {
      return oldList.size
  }

  override fun getNewListSize(): Int {
      return newList.size
  }

  override fun areContentsTheSame(
    oldItemPosition: Int,
    newItemPosition: Int
  ): Boolean {
    return(oldList[oldItemPosition].eventDescription.equals(newList[newItemPosition].eventDescription,false)
        && oldList[oldItemPosition].eventName.equals(newList[newItemPosition].eventName,false)
        && oldList[oldItemPosition].eventCategory.equals(newList[newItemPosition].eventCategory,false))
  }

  override fun getChangePayload(
    oldItemPosition: Int,
    newItemPosition: Int
  ): Any? {
    val oldEvent = oldList[oldItemPosition]
    val newEvent = newList[newItemPosition]

    val diffBundle = Bundle()
    if(oldEvent.eventName!=newEvent.eventName)
        diffBundle.putString("EVENT_NAME",newEvent.eventName)
    if(oldEvent.eventDescription!=newEvent.eventDescription)
      diffBundle.putString("EVENT_DESCRIPTION",newEvent.eventDescription)
    if(oldEvent.eventCategory!=newEvent.eventCategory)
      diffBundle.putString("EVENT_CATEGORY",newEvent.eventCategory)


    if(diffBundle.isEmpty)
      return null
    else
      return diffBundle


  }
}