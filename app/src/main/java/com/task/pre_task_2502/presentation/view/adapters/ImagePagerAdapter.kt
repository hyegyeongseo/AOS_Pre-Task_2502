package com.task.pre_task_2502.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.pre_task_2502.R
import com.task.pre_task_2502.data.repository.remote.LatestImageModel

class ImagePagerAdapter(private val images: List<LatestImageModel>) : RecyclerView.Adapter<ImagePagerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.random_item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView.context)
            .load(image.urls.regular)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = images.size
}
