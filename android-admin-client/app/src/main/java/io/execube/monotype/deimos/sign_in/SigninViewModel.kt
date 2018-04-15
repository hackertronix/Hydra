package io.execube.monotype.deimos.sign_in

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.execube.monotype.deimos.model.FirestoreLiveData

class SigninViewModel(app:Application):AndroidViewModel(app){

  var firestoreLiveData =  AdminListFireStore()

  public fun getEvents(): AdminListFireStore {

    return firestoreLiveData


  }


}