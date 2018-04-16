package io.execube.monotype.deimos.photo_feed

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.DisplayMetrics
import android.view.View
import com.squareup.picasso.Picasso
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.CircleTransform
import io.execube.monotype.deimos.model.Photo
import kotlinx.android.synthetic.main.photo_entry_item.view.photo
import kotlinx.android.synthetic.main.photo_entry_item.view.photo_caption
import kotlinx.android.synthetic.main.photo_entry_item.view.user_image
import kotlinx.android.synthetic.main.photo_entry_item.view.user_name
import io.execube.monotype.deimos.photo_feed.PhotosHolder.ClickListener



public class PhotosHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

  fun bind(
    model: Photo?,
    width: Int,
    height: Int
  ) {

    if (model != null) {
      itemView.photo_caption.text =
          Html.fromHtml(String.format("<strong>%s</strong>: %s", model.uploaderName, model.caption))
      itemView.user_name.text = model.uploaderName.toUpperCase()

      Picasso.with(itemView.context)
          .load(model.uploaderAvatarUrl)
          .transform(CircleTransform())
          .fit()
          .placeholder(R.drawable.avatar_placeholder)
          .error(R.drawable.avatar_placeholder)
          .into(itemView.user_image)

      Picasso.with(itemView.context)
          .load(model.uploadedPhotoUrl)
          .placeholder(R.drawable.rectange)
          .resize(width, height/2)
          .centerInside()
          .error(R.color.error)
          .into(itemView.photo)

      itemView.setOnClickListener {
        mClickListener?.onItemClick(itemView,adapterPosition)
      }
    }
  }

  private var mClickListener: PhotosHolder.ClickListener? = null

  //Interface to send callbacks...
  interface ClickListener {
    fun onItemClick(
      view: View,
      position: Int
    )
  }

  fun setOnClickListener(clickListener: PhotosHolder.ClickListener) {
    mClickListener = clickListener
  }

}