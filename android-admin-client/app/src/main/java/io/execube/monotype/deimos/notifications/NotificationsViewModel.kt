package io.execube.monotype.deimos.notifications

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class NotificationsViewModel(app: Application) : AndroidViewModel(app) {

  var firestoreLiveData = NotificationsFireStore()

  public fun getEvents(): NotificationsFireStore {

    return firestoreLiveData

  }

}