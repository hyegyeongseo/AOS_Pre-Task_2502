package com.task.pre_task_2502.presentation.view.adapters

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.task.pre_task_2502.R
import com.task.pre_task_2502.data.repository.local.LocalImageModel
import com.bumptech.glide.request.target.Target
import com.task.pre_task_2502.presentation.view.activities.DetailActivity

class LocalPagingDataAdapter : PagingDataAdapter<LocalImageModel, LocalViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latest_item_image, parent, false)
        return LocalViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocalImageModel>() {
            override fun areItemsTheSame(oldItem: LocalImageModel, newItem: LocalImageModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LocalImageModel, newItem: LocalImageModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class LocalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val shimmerFrame: ShimmerFrameLayout = itemView.findViewById(R.id.bookmark_shimmer_frame_layout)
    private val imageView: ImageView = itemView.findViewById(R.id.bookmark_image_view)

    fun bind(image: LocalImageModel) {
        // Shimmer 애니메이션 시작
        shimmerFrame.startShimmer()

        // Glide를 사용하여 이미지를 로드
        Glide.with(itemView.context)
            .load(image.url) // 이미지 URL을 사용하여 로드
            .into(imageView) // 이미지 뷰에 적용
            .onLoadStarted(null) // 로딩 시작 시 Shimmer 애니메이션을 시작

        // Glide 로딩이 완료되면 Shimmer 애니메이션 중지
        Glide.with(itemView.context)
            .load(image.url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>, // 제네릭 제거
                    isFirstResource: Boolean
                ): Boolean {
                    shimmerFrame.stopShimmer() // 로딩 실패 시 Shimmer 중지
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    shimmerFrame.stopShimmer() // 로딩 성공 시 Shimmer 중지
                    shimmerFrame.visibility = View.GONE // Shimmer 뷰 숨기기
                    return false
                }
            })
            .into(imageView)



        // 클릭 리스너 설정
        itemView.setOnClickListener {
            // 상세 화면으로 이동하는 로직 구현
            val context = itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("IMAGE_ID", image.id) // 이미지 ID를 인텐트에 추가
            }
            context.startActivity(intent) // 상세 화면으로 이동
        }
    }
}
