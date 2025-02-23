package com.task.pre_task_2502.presentation.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.pre_task_2502.R
import com.task.pre_task_2502.data.repository.local.LocalImageModel

class BookmarkAdapter(
    private val context: Context,
    private val bookmarkedImages: List<LocalImageModel>,
    private val onClick: (String) -> Unit // 클릭 시 ID를 전달하는 람다
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.bookmark_image_view) // 이미지 뷰 ID 설정

        init {
            // 클릭 리스너 설정
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val image = bookmarkedImages[position]
                    onClick(image.id) // 클릭된 이미지의 ID를 전달
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.bookmark_item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = bookmarkedImages[position]

        // Glide를 사용하여 이미지 로딩
        Glide.with(context)
            .load(image.urls.regular)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return bookmarkedImages.size
    }
}