package com.task.pre_task_2502

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlin.collections.List
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.task.pre_task_2502.data.repository.local.AppDatabase
import com.task.pre_task_2502.data.repository.local.LocalImageModel
import com.task.pre_task_2502.data.repository.remote.ApiService
import com.task.pre_task_2502.data.repository.remote.ImageRepository
import com.task.pre_task_2502.data.repository.remote.LatestImageModel
import com.task.pre_task_2502.data.repository.remote.RetrofitClient
import com.task.pre_task_2502.presentation.view.activities.DetailActivity
import com.task.pre_task_2502.presentation.view.adapters.LatestImageAdapter
import com.task.pre_task_2502.presentation.viewmodel.Bookmark.BookmarkImageViewModel
import com.task.pre_task_2502.presentation.viewmodel.Bookmark.BookmarkImageViewModelFactory
import com.task.pre_task_2502.presentation.viewmodel.Latest.ImageViewModel
import com.task.pre_task_2502.presentation.viewmodel.Latest.LatestImageViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var bookmarkContainer: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var latestImageAdapter: LatestImageAdapter
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var latestImagesHeader: TextView
    private lateinit var bookmarkHeader: TextView
    private lateinit var bookmarkShimmerLayout: ShimmerFrameLayout
    private lateinit var latestImagesShimmerLayout: ShimmerFrameLayout

    private val imageViewModel: ImageViewModel by viewModels {
        LatestImageViewModelFactory(ImageRepository(getApiService()))
    }

    private val bookmarkImageViewModel: BookmarkImageViewModel by viewModels {
        BookmarkImageViewModelFactory(getAppDatabase().bookmarkDao(), getApiService())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        recyclerView = findViewById(R.id.recycler_view)
        loadingSpinner = findViewById(R.id.loading_spinner)
        latestImagesHeader = findViewById(R.id.latest_images_header)
        bookmarkHeader = findViewById(R.id.bookmark_header)
        bookmarkContainer = findViewById(R.id.bookmark_images)
        bookmarkShimmerLayout = findViewById(R.id.bookmark_shimmer_layout)
        latestImagesShimmerLayout = findViewById(R.id.latest_images_shimmer_layout)

        // 북마크 이미지 로드
        loadBookmarkedImages()
    }

    private fun loadBookmarkedImages() {
        lifecycleScope.launch {
            val hasBookmarks = bookmarkImageViewModel.hasBookmarkedImages() // 북마크 존재 유무 확인

            if (!hasBookmarks) {
                bookmarkHeader.visibility = View.GONE
                bookmarkContainer.visibility = View.GONE
                loadLatestImages()
            } else {
                // 북마크가 있는 경우, 이미지를 로드하거나 표시하는 로직 추가
                 displayBookmarkedImages()
            }
        }
    }




    private fun displayBookmarkedImages() {
        // 북마크 헤더와 컨테이너 가시성 설정
        bookmarkHeader.visibility = View.VISIBLE
        bookmarkContainer.visibility = View.VISIBLE

        startBookmarkShimmerAnimation()

        // Room에서 이미지 URL 로드
        lifecycleScope.launch {
            val urls = withContext(Dispatchers.IO) {
                val database = AppDatabase.getDatabase(applicationContext)
                database.bookmarkDao().getAllBookmarkedImages()
            }.map { it.url }

            // 이미지 로딩
            displayImages(urls)

            // Shimmer 애니메이션 중지
            stopBookmarkShimmerAnimation()
        }

        loadLatestImages()
    }

    private fun displayImages(imageUrls: List<String>) {
        for (url in imageUrls) {
            // 새로운 ImageView 생성
            val imageView = ImageView(this)
            imageView.layoutParams = LinearLayout.LayoutParams(
                150, // 너비
                100  // 높이
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            // Glide를 사용하여 이미지 로딩
            Glide.with(this)
                .load(url)
                .into(imageView)

            // LinearLayout에 추가
            bookmarkContainer.addView(imageView)
        }
    }


    private fun loadLatestImages() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayoutManager
        latestImageAdapter = LatestImageAdapter { image -> detailActivity(image) }
        recyclerView.adapter = latestImageAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                if (layoutManager.findLastCompletelyVisibleItemPosition() == latestImageAdapter.itemCount - 1) {
                    latestImageAdapter.retry()
                }
            }
        })

        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE

            imageViewModel.photosFlow.collectLatest { pagingData ->
                latestImageAdapter.submitData(pagingData)
                loadingSpinner.visibility = View.GONE
            }
        }
    }

    private fun detailActivity(image: LatestImageModel) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("imageId", image.id)
            putExtra("imageUrl", image.urls.regular) // Urls 객체의 regular 속성
            putExtra("username", image.user.username) // User 객체의 username 속성
            putExtra("altDescription", image.altDescription)
        }
        startActivity(intent)
    }

    private fun startBookmarkShimmerAnimation() {
        bookmarkShimmerLayout.visibility = View.VISIBLE
        bookmarkShimmerLayout.startShimmer()
    }

    private fun stopBookmarkShimmerAnimation() {
        bookmarkShimmerLayout.stopShimmer()
        bookmarkShimmerLayout.visibility = View.GONE
    }

    private fun getApiService(): ApiService {
        return RetrofitClient.apiService
    }

    private fun getAppDatabase(): AppDatabase {
        return AppDatabase.getDatabase(applicationContext)
    }
}
