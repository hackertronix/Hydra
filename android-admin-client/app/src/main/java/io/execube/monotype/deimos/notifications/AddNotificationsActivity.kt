package io.execube.monotype.deimos.notifications

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.widget.RxTextView
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.model.NotificationData
import kotlinx.android.synthetic.main.activity_add_notifications.notification_description_field
import kotlinx.android.synthetic.main.activity_add_notifications.notification_title_field
import kotlinx.android.synthetic.main.fragment_notifications.add_notification
import timber.log.Timber

class AddNotificationsActivity : AppCompatActivity() {

  var titleCount = 0
  var descCount = 0

  internal val NOTIFICATIONS = "Notifications"
  var documentReference = FirebaseFirestore.getInstance()
      .collection(NOTIFICATIONS)
      .document()

  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_notifications)

    RxTextView.textChanges(notification_title_field.editText as TextView)
        .skipInitialValue()
        .subscribe { notificationTitle ->
          when {
            notificationTitle!!.trim().length in 1..3 || notificationTitle.isEmpty() -> {
              notification_title_field.error =
                  "Notification title must be at least 4 characters long. You are ${5 - notificationTitle.length} characters short"
              titleCount = 0
              checkIfValid()

            }
            else -> {
              notification_title_field.isErrorEnabled = false
              titleCount = 1
              checkIfValid()
            }
          }

        }

    RxTextView.textChanges(notification_description_field.editText as TextView)
        .skipInitialValue()
        .subscribe { notificationDescription ->
          when {

            notificationDescription.trim().length in 1..20 || notificationDescription.isEmpty() -> {
              notification_description_field.error =
                  "Notification description must be at least 20 characters long. You are ${22 - notificationDescription.length} characters short"
              descCount = 0
              checkIfValid()
            }
            else -> {
              notification_description_field.isErrorEnabled = false
              descCount = 1
              checkIfValid()
            }
          }
        }

    add_notification.setOnClickListener {
      if (add_notification.visibility == View.VISIBLE) {

        saveToFireBase()
      }
    }
  }

  private fun saveToFireBase() {

    add_notification.isActivated = false
    val uploadingAVD =
      getDrawable(R.drawable.avd_uploading) as AnimatedVectorDrawable
    if (uploadingAVD != null) {
      add_notification.setImageDrawable(uploadingAVD)
      uploadingAVD.start()
    }
    val title = notification_title_field.editText?.text.toString()
    val description = notification_description_field.editText?.text.toString()

    val notification = NotificationData(title, description)
    notification.timeOfGeneration = System.currentTimeMillis()
        .toString()
    documentReference.set(notification)
        .addOnSuccessListener {
          val complete = getDrawable(R.drawable.avd_upload_complete) as AnimatedVectorDrawable?
          if (complete != null) {
            add_notification.setImageDrawable(complete)
            complete.start()
            add_notification.postDelayed(
                {
                  val intent = Intent(this@AddNotificationsActivity, HomeActivity::class.java)
                  intent.putExtra("SWAP", "NOTIFICATION")
                  startActivity(intent)
                  finish()
                }, 1600
            )
          }

        }
        .addOnFailureListener {
          Timber.e(it.message)
          Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
              .show()
          add_notification.isActivated = true
          val failed =
            getDrawable(R.drawable.avd_upload_error) as AnimatedVectorDrawable?
          if (failed != null) {
            add_notification.setImageDrawable(failed)
            failed.start()
          }
        }

  }

  private fun checkIfValid() {
    if (titleCount + descCount == 2) {
      add_notification.visibility = View.VISIBLE
      add_notification.alpha = 0f
      add_notification.scaleX = 0f
      add_notification.scaleY = 0f
      add_notification.translationY = add_notification.getHeight() / 2f;
      add_notification.animate()
          .alpha(1f)
          .scaleX(1f)
          .scaleY(1f)
          .translationY(0f)
          .setDuration(500L)
          .setInterpolator(getLinearOutSlowInInterpolator(this))
          .start()
    } else {
      add_notification.visibility = View.INVISIBLE
    }

  }
}