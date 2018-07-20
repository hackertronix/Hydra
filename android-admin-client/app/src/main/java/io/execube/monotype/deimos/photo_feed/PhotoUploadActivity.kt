package io.execube.monotype.deimos.photo_feed

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jakewharton.rxbinding2.widget.RxTextView
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.model.Photo
import kotlinx.android.synthetic.main.activity_photo_upload.photo_caption_field
import kotlinx.android.synthetic.main.activity_photo_upload.photo_preview
import kotlinx.android.synthetic.main.activity_photo_upload.upload_photo
import java.io.ByteArrayOutputStream
import java.util.UUID

class PhotoUploadActivity : AppCompatActivity() {

  var documentReference = FirebaseFirestore.getInstance()
      .collection("Photos")
      .document()
  val storageReference = FirebaseStorage.getInstance()
  lateinit var fileUri: Uri

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_photo_upload)

    initUri()
    showImagePreview()

    RxTextView.textChanges(photo_caption_field.editText as TextView)
        .subscribe { caption ->

          when {
            caption.isEmpty() || !imageIsPresent() -> {
              upload_photo.visibility = View.INVISIBLE
              photo_caption_field.error = "Photo caption cannot be empty"
            }
            else -> {
              toggleFab()
              photo_caption_field.isErrorEnabled = false
            }
          }
        }

    upload_photo.setOnClickListener {

      uploadPhoto()
    }

  }

  private fun uploadPhoto() {

    val photo = Photo(photoId = documentReference.id)
    photo.caption = photo_caption_field.editText?.text.toString()
        .trim()
    photo.uploaderName = FirebaseAuth.getInstance()
        .currentUser?.displayName.toString()
    photo.uploaderAvatarUrl = FirebaseAuth.getInstance()
        .currentUser?.photoUrl.toString()
    photo.uploaderEmail = FirebaseAuth.getInstance()
        .currentUser?.email.toString()

    upload_photo.isActivated = false
    val uploadingAVD =
      getDrawable(R.drawable.avd_uploading) as AnimatedVectorDrawable
    if (uploadingAVD != null) {
      upload_photo.setImageDrawable(uploadingAVD)
      uploadingAVD.start()
    }

    val exifInterface = ExifInterface(fileUri.path)
    val bitmap = BitmapFactory.decodeFile(fileUri.path)
    val fixedUploadBitmap = fixRotation(exifInterface, bitmap)
    val baos = ByteArrayOutputStream()
    fixedUploadBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)
    val data = baos.toByteArray()
    val path = "images " + UUID.randomUUID() + ".jpg"
    val storageReference = storageReference.getReference(path)
    val uploadTask = storageReference.putBytes(data)
    var uri: Uri?

    uploadTask.addOnSuccessListener {

      uri = it.uploadSessionUri
      photo.uploadedPhotoUrl = uri.toString()
      savePhotoDataToFirebase(photo)
      /*Picasso.with(this)
          .load(uri)
          .placeholder(R.color.lightGray)
          .error(R.drawable.ic_broken_image_black_24dp)
          .into(photo)*/

    }
        .addOnFailureListener {
          upload_photo.isActivated = true
          val failed =
            getDrawable(R.drawable.avd_upload_error) as AnimatedVectorDrawable?
          if (failed != null) {
            upload_photo.setImageDrawable(failed)
            failed.start()
          }
        }

  }

  private fun savePhotoDataToFirebase(photoToSave: Photo) {

    documentReference.set(photoToSave)
        .addOnSuccessListener {
          val complete = getDrawable(R.drawable.avd_upload_complete) as AnimatedVectorDrawable?
          if (complete != null) {
            upload_photo.setImageDrawable(complete)
            complete.start()
            upload_photo.postDelayed(
                {
                  val intent = Intent(this@PhotoUploadActivity, HomeActivity::class.java)
                  intent.putExtra("SWAP", "PHOTOS")
                  startActivity(intent)
                  finish()
                }, 1600
            )
          }
        }
        .addOnFailureListener {
          upload_photo.isActivated = true
          val failed =
            getDrawable(R.drawable.avd_upload_error) as AnimatedVectorDrawable?
          if (failed != null) {
            upload_photo.setImageDrawable(failed)
            failed.start()
          }
        }
  }

  private fun toggleFab() {
    upload_photo.visibility = View.VISIBLE
    upload_photo.alpha = 0f
    upload_photo.scaleX = 0f
    upload_photo.scaleY = 0f
    upload_photo.translationY = upload_photo.getHeight() / 2f;
    upload_photo.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(getLinearOutSlowInInterpolator(this))
        .start()
  }

  private fun imageIsPresent(): Boolean {
    val drawable = photo_preview.getDrawable()
    var hasImage = (drawable != null)

    if (hasImage && (drawable is BitmapDrawable)) {
      hasImage = (drawable).getBitmap() != null
    }

    return hasImage
  }

  private fun initUri() {
    if (intent.hasExtra("IMAGE_PATH"))
    {

      fileUri = Uri.parse(intent.extras.getString("IMAGE_PATH"))
    }
  }

  private fun showImagePreview() {
    val options = BitmapFactory.Options()
    options.inSampleSize = 2
    val fetchedBitmap = BitmapFactory.decodeFile(fileUri.path, options)
    val exifInterface = ExifInterface(fileUri.path)
    val fixedBitmap = fixRotation(exifInterface, fetchedBitmap)

    photo_preview.setImageBitmap(fixedBitmap)

  }

  private fun fixRotation(
    exifInterface: ExifInterface,
    fetchedBitmap: Bitmap
  ): Bitmap {

    val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )

    var rotatedBitmap: Bitmap? = null
    when {

      orientation == ExifInterface.ORIENTATION_ROTATE_90 -> {
        rotatedBitmap = rotateImage(fetchedBitmap, 90f)
      }
      orientation == ExifInterface.ORIENTATION_ROTATE_180 -> {
        rotatedBitmap = rotateImage(fetchedBitmap, 180f)

      }
      orientation == ExifInterface.ORIENTATION_ROTATE_270 -> {
        rotatedBitmap = rotateImage(fetchedBitmap, 270f)

      }
      else -> {
        rotatedBitmap = fetchedBitmap
      }
    }

    return rotatedBitmap

  }

  private fun rotateImage(
    source: Bitmap,
    angle: Float
  ): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle);
    return Bitmap.createBitmap(
        source, 0, 0, source.getWidth(), source.getHeight(),
        matrix, true
    )
  }

}
