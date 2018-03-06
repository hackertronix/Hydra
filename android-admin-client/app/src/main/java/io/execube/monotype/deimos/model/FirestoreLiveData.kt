package io.execube.monotype.deimos.model

import android.arch.lifecycle.LiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreLiveData : LiveData<List<Event>>() {


    override fun onActive() {
        super.onActive()


        FirebaseFirestore.getInstance()
                .collection("Events")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    if (firebaseFirestoreException == null) {
                        var events: ArrayList<Event> = ArrayList()
                        for (doc: DocumentSnapshot in querySnapshot) {
                            var event = doc.toObject(Event::class.java)
                            events.add(event)
                            value = events
                        }
                    }
                }
    }
}