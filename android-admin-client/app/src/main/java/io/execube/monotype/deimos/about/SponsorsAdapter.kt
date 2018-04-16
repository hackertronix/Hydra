package io.execube.monotype.deimos.about

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.about.SponsorsAdapter.SponsorsHolder
import kotlinx.android.synthetic.main.sponsor_item.view.image

class SponsorsAdapter(private var listOfImages: Array<Int>): RecyclerView.Adapter<SponsorsHolder>() {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): SponsorsHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.sponsor_item,parent,false)
    return SponsorsHolder(view)
  }

  override fun getItemCount(): Int {
   return listOfImages.size
  }

  override fun onBindViewHolder(
    holder: SponsorsHolder,
    position: Int
  ) {
    holder.bind(listOfImages[position])
  }

  inner class SponsorsHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    fun bind(id: Int) {
      itemView.image.setImageDrawable(itemView.context.getDrawable(id))
    }

  }
}