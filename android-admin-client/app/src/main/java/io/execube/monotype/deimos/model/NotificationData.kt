package io.execube.monotype.deimos.model

data class NotificationData(

  var notificationTitle:String="",
  var notificationDescription:String="",
  var timeOfGeneration:String=""

):Comparator<NotificationData> {
  override fun compare(
    o1: NotificationData?,
    o2: NotificationData?
  ): Int {
    return o1?.timeOfGeneration?.toLong()!!.compareTo(o2?.timeOfGeneration?.toLong()!!)
  }
}