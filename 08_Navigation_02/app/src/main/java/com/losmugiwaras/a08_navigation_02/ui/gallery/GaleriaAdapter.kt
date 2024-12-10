package com.losmugiwaras.a08_navigation_02.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.losmugiwaras.a08_navigation_02.R

class GalleryAdapter(
    private val photoList: MutableList<GalleryItem>
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_foto_evento, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val eventPhoto = photoList[position]
        holder.imageView.setImageResource(EventPhoto.imageResId)
    }

    override fun getItemCount(): Int = photoList.size
}
