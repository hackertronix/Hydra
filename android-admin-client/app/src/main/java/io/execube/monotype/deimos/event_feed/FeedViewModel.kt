package io.execube.monotype.deimos.event_feed

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.execube.monotype.deimos.model.FirestoreLiveData

class FeedViewModel(app: Application) : AndroidViewModel(app) {


    var firestoreLiveData =  FirestoreLiveData()

    public fun getEvents(): FirestoreLiveData {

        return firestoreLiveData


    }

}