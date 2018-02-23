package io.execube.monotype.phobos.signin

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel(context: Application) : AndroidViewModel(context) {


    private var toastMessage: MutableLiveData<String> = MutableLiveData()
    private var user: MutableLiveData<String> = MutableLiveData()


    fun isLoggedIn(): LiveData<String> {

        if (user.value == null) {
            user.value = FirebaseAuth.getInstance().currentUser.toString()
        }

        return user
    }

    fun authenticateUser() {

        //toastMessage.value = "Hey this is working"
    }


    fun getToastMessage(): LiveData<String> {
        return toastMessage
    }


}