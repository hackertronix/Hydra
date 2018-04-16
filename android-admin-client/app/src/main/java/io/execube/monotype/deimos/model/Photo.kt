package io.execube.monotype.deimos.model

import java.io.Serializable

data class Photo(

  var photoId:String = "",
  var uploaderName: String = "",
  var uploaderEmail:String="",
  var uploaderAvatarUrl: String = "",
  var caption: String = "",
  var uploadedPhotoUrl: String = ""
):Serializable