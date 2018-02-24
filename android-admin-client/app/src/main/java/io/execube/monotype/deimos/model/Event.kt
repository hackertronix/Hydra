package io.execube.monotype.deimos.model

data class Event(val eventName: String,
                 val eventDescription: String,
                 val eventCategory: String,
                 val eventVenue: String,
                 val eventDate: Long)