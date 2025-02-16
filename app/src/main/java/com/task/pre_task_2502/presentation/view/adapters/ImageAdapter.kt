package com.task.pre_task_2502.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.task.pre_task_2502.data.repository.remote.ImageModel
import androidx.recyclerview.widget.RecyclerView
import com.task.pre_task_2502.R

class ImageAdapter(private val onItemClick: (ImageModel) -> Unit) :
    PagingDataAdapter<ImageModel, ImageAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val shimmerFrameLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmer_frame_layout)
        val imageView: ImageView = view.findViewById(R.id.image_view)
        val titleTextView: TextView = view.findViewById(R.id.title_text_view)
        val usernameTextView: TextView = view.findViewById(R.id.username_text_view)

        fun bind(image: ImageModel) {
            shimmerFrameLayout.stopShimmer() // 스켈레톤 애니메이션 중지
            shimmerFrameLayout.visibility = View.GONE // 스켈레톤 숨기기

            Glide.with(itemView.context).load(image.urls.regular).into(imageView)
            imageView.contentDescription = image.alt_description ?: "No description available"
            titleTextView.text = image.alt_description ?: "No title"
            usernameTextView.text = image.user.name

            itemView.setOnClickListener {
                onItemClick(image)
            }
        }

        fun showSkeleton() {
            shimmerFrameLayout.startShimmer() // 스켈레톤 애니메이션 시작
            shimmerFrameLayout.visibility = View.VISIBLE // 스켈레톤 보이기
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = getItem(position)
        if (image != null) {
            holder.bind(image)
        } else {
            holder.showSkeleton() // 이미지가 없을 경우 스켈레톤 표시
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageModel>() {
            override fun areItemsTheSame(oldItem: ImageModel, newItem: ImageModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ImageModel, newItem: ImageModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
