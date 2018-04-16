package io.execube.monotype.deimos.notifications

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.execube.monotype.deimos.model.NotificationData
import java.util.Collections

class NotificationsFireStore: MutableLiveData<List<NotificationData>>() {

  override fun onActive() {
    super.onActive()


    FirebaseFirestore.getInstance()
        .collection("Notifications")
        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

          if (firebaseFirestoreException == null) {
            var events: ArrayList<NotificationData> = ArrayList()
            if (querySnapshot != null) {
              for (doc: DocumentSnapshot in querySnapshot) {
                var event = doc.toObject(NotificationData::class.java)
                events.add(event!!)
              }
            }
            Collections.sort(events,NotificationData())
            value = events
          }
        }
  }
}