package com.task.pre_task_2502.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.task.pre_task_2502.R
import com.task.pre_task_2502.data.repository.remote.LatestImageModel

class LatestImageAdapter(private val onClick: (LatestImageModel) -> Unit) :
    PagingDataAdapter<LatestImageModel, LatestImageAdapter.ImageViewHolder>(DIFF_CALLBACK) {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val shimmerFrameLayout: ShimmerFrameLayout = view.findViewById(R.id.vertical_shimmer_frame_layout)
        val imageView: ImageView = view.findViewById(R.id.image_view)
        val titleTextView: TextView = view.findViewById(R.id.title_text_view)

        fun bind(image: LatestImageModel) {
            shimmerFrameLayout.stopShimmer() // 스켈레톤 애니메이션 중지
            shimmerFrameLayout.visibility = View.GONE // 스켈레톤 숨기기

            Glide.with(itemView.context).load(image.urls.regular).into(imageView) // 정규화된 이미지 URL 사용
            imageView.contentDescription = image.altDescription ?: "No description available"
            titleTextView.text = image.altDescription ?: "No title"

            itemView.setOnClickListener {
                onClick(image)
            }
        }

        fun showSkeleton() {
            shimmerFrameLayout.startShimmer() // 스켈레톤 애니메이션 시작
            shimmerFrameLayout.visibility = View.VISIBLE // 스켈레톤 보이기
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latest_item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        if (image != null) {
            holder.bind(image)
        } else {
            holder.showSkeleton() // 이미지가 없을 경우 스켈레톤 표시
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LatestImageModel>() {
            override fun areItemsTheSame(oldItem: LatestImageModel, newItem: LatestImageModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LatestImageModel, newItem: LatestImageModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
