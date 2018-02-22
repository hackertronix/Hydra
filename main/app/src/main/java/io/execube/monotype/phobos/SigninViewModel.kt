package io.execube.monotype.phobos

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

class SigninViewModel(context: Application) : AndroidViewModel(context),
        GoogleApiClient.OnConnectionFailedListener {


    private var toastMessage = "DEFAULT MESSAGE"




    fun authenticateUser(){



    }


    fun getToastMessage: LiveData<String>{
        return toastMessage
    }




    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}