package io.execube.monotype.deimos.sign_in

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class SigninViewModel(app:Application):AndroidViewModel(app){

  var firestoreLiveData =  AdminListFireStore()

  public fun getEvents(): AdminListFireStore {

    return firestoreLiveData


  }


}