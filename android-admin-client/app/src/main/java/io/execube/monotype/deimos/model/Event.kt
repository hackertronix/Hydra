package io.execube.monotype.deimos.model

import java.io.Serializable

data class Event(var eventName: String="",
                 var eventId: String="",
                 var eventDescription:  String="",
                 var eventCategory:  String="",
                 var eventVenue:  String="",
                 var eventDate:  String="",
                 var eventColor: String="",
                 var eventTime:  String=""): Serializable
