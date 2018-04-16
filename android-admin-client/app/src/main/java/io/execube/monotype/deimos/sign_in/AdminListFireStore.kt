package io.execube.monotype.deimos.sign_in

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.execube.monotype.deimos.model.Admin

class AdminListFireStore : MutableLiveData<List<Admin>>() {

  override fun onActive() {
    super.onActive()


    FirebaseFirestore.getInstance()
        .collection("Admins")
        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

          if (firebaseFirestoreException == null) {
            var admins: ArrayList<Admin> = ArrayList()
            if (querySnapshot != null) {
              for (doc: DocumentSnapshot in querySnapshot) {
                var admin = doc.toObject(Admin::class.java)
                admins.add(admin!!)
              }
            }
            value = admins
          }
        }
  }
}