package io.execube.monotype.deimos.model

data class Event(val eventName: String?=null,
                 val eventDescription: String?=null,
                 val eventCategory: String?=null,
                 val eventVenue: String?=null,
                 val eventDate: String?=null,
                 val eventTime: String?=null   )