package io.execube.monotype.deimos.photo_details

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.model.Photo
import kotlinx.android.synthetic.main.activity_photo_details.fab
import kotlinx.android.synthetic.main.activity_photo_details.photo_caption
import kotlinx.android.synthetic.main.activity_photo_details.photo_details_toolbar
import kotlinx.android.synthetic.main.activity_photo_details.photo_preview
import kotlin.properties.Delegates

class PhotoDetailsActivity : AppCompatActivity() {

  lateinit var photo: Photo
  var width by Delegates.notNull<Int>()
  var height by Delegates.notNull<Int>()
  private val storage = FirebaseStorage.getInstance()
  private val collectionRef = FirebaseFirestore.getInstance()
      .collection("Photos")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_photo_details)
    initToolbar()
    getDisplaySize()
    checkIfPayloadExists()

    val backArrowDrawable = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)
    backArrowDrawable.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
    supportActionBar?.setHomeAsUpIndicator(backArrowDrawable)
    fab.setColorFilter(R.color.black)

    fab.setOnClickListener {

      if (fab.visibility == View.VISIBLE) {

        askForConfirmation()

      }
    }

  }

  private fun deletePhoto() {
    fab.isActivated = false
    val uploadingAVD =
      getDrawable(R.drawable.avd_uploading) as AnimatedVectorDrawable
    if (uploadingAVD != null) {
      fab.setImageDrawable(uploadingAVD)
      uploadingAVD.start()
    }
    val photoReference = storage.getReferenceFromUrl(photo.uploadedPhotoUrl)
    photoReference.delete()
        .addOnCompleteListener {
          deleteRecordFromFirebase()

        }
        .addOnFailureListener {
          val failed =
            getDrawable(R.drawable.avd_upload_error) as AnimatedVectorDrawable?
          if (failed != null) {
            fab.setImageDrawable(failed)
            failed.start()
          }
          Toast.makeText(this, "Failed to delete photo", Toast.LENGTH_SHORT)
              .show()
        }
  }

  private fun askForConfirmation() {
    AlertDialog.Builder(this)
        .setTitle("Delete")
        .setCancelable(false)
        .setMessage("Deleting this photo will permanently remove it. Are you sure?")
        .setPositiveButton("YES") { dialog, _ ->
          deletePhoto()
          dialog.dismiss()
        }
        .setNegativeButton("NO") { dialog, _ ->
          dialog.dismiss()
        }
        .show()
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when {

      item?.itemId == android.R.id.home -> {

        finish()
        return true
      }
      else -> return super.onOptionsItemSelected(item)

    }

  }

  private fun deleteRecordFromFirebase() {

    val docRef = collectionRef.document(photo.photoId)
    docRef.delete()
        .addOnSuccessListener {
          val complete =
            getDrawable(R.drawable.avd_upload_complete) as AnimatedVectorDrawable?
          if (complete != null) {
            fab.setImageDrawable(complete)
            complete.start()
            fab.postDelayed(
                {
                  finish()
                }, 1600
            )
            // 220 ms is length of R.drawable.avd_upload_complete
          }
        }
        .addOnFailureListener {
          val failed =
            getDrawable(R.drawable.avd_upload_error) as AnimatedVectorDrawable?
          if (failed != null) {
            fab.setImageDrawable(failed)
            failed.start()
          }
          Toast.makeText(this, "Failed to delete record from server", Toast.LENGTH_SHORT)
              .show()
        }

  }

  private fun checkIfPayloadExists() {
    if (intent.hasExtra("PHOTO")) {
      photo = intent.extras.get("PHOTO") as Photo
      populateDetails()
    } else {
      finish()
    }
  }

  private fun initToolbar() {
    photo_details_toolbar.title = " "
    setSupportActionBar(photo_details_toolbar as Toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  private fun getDisplaySize() {
    val displayMetrics = DisplayMetrics()
    this.getWindowManager()
        .getDefaultDisplay()
        .getMetrics(displayMetrics)
    height = displayMetrics.heightPixels
    height /= 1.2.toInt()
    width = displayMetrics.widthPixels
  }

  private fun populateDetails() {
    photo_caption.text =
        Html.fromHtml(String.format("<strong>%s</strong>: %s", photo.uploaderName, photo.caption))

    Picasso.with(this)
        .load(photo.uploadedPhotoUrl)
        .placeholder(R.color.darkGray)
        .resize(width, height)
        .centerInside()
        .error(R.color.error)
        .into(photo_preview)
    //TODO change this
    animateFab()
//    if(photo.uploaderEmail.equals(FirebaseAuth.getInstance().currentUser?.email))
//    {
//      animateFab()
//    }
  }

  private fun animateFab() {

    fab.visibility = View.VISIBLE
    fab.alpha = 0f
    fab.scaleX = 0f
    fab.scaleY = 0f
    fab.translationY = fab.getHeight() / 2f;
    fab.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(this))
        .start()
  }
}
