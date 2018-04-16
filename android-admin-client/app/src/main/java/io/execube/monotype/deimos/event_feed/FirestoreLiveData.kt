package io.execube.monotype.deimos.event_feed

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.execube.monotype.deimos.model.Event

class FirestoreLiveData : MutableLiveData<List<Event>>() {

  override fun onActive() {
    super.onActive()


    FirebaseFirestore.getInstance()
        .collection("Events")
        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

          if (firebaseFirestoreException == null) {
            var events: ArrayList<Event> = ArrayList()
            if (querySnapshot != null) {
              for (doc: DocumentSnapshot in querySnapshot) {
                var event = doc.toObject(Event::class.java)
                events.add(event!!)
              }
            }
            value = events
          }
        }
  }
}