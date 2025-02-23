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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.task.pre_task_2502.data.repository.local.AppDatabase
import com.task.pre_task_2502.data.repository.remote.ApiService
import com.task.pre_task_2502.data.repository.remote.ImageRepository
import com.task.pre_task_2502.data.repository.remote.LatestImageModel
import com.task.pre_task_2502.data.repository.remote.RetrofitClient
import com.task.pre_task_2502.presentation.view.activities.DetailActivity
import com.task.pre_task_2502.presentation.view.adapters.BookmarkAdapter
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var bookmarkHeader: TextView
    private lateinit var bookmarkContainer: LinearLayout
    private lateinit var bookmarkShimmerLayout: ShimmerFrameLayout

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
        bookmarkHeader = findViewById(R.id.bookmark_header)
        bookmarkContainer = findViewById(R.id.bookmark_images)
        bookmarkShimmerLayout = findViewById(R.id.bookmark_shimmer_layout)

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
                displayBookmarkedImages()
            }
        }
    }

    private fun displayBookmarkedImages() {
        bookmarkHeader.visibility = View.VISIBLE
        bookmarkContainer.visibility = View.VISIBLE

        startBookmarkShimmerAnimation()

        lifecycleScope.launch {
            val bookmarkedImages = withContext(Dispatchers.IO) {
                val database = AppDatabase.getDatabase(applicationContext)
                database.bookmarkDao().getAllBookmarkedImages()
            }

            // RecyclerView 설정
            val bookmarkAdapter = BookmarkAdapter(this@MainActivity, bookmarkedImages) { imageId ->
                detailActivity(imageId) // 클릭한 이미지의 ID를 전달
            }

            // RecyclerView에 어댑터 설정
            recyclerView.adapter = bookmarkAdapter

            stopBookmarkShimmerAnimation()
        }

        loadLatestImages()
    }

    private fun loadLatestImages() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayoutManager

        val latestImageAdapter = LatestImageAdapter { image ->
            detailActivity(image.id) // 최신 이미지 클릭 시 ID 전달
        }
        recyclerView.adapter = latestImageAdapter

        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE

            imageViewModel.photosFlow.collectLatest { pagingData ->
                latestImageAdapter.submitData(pagingData)
                loadingSpinner.visibility = View.GONE
            }
        }
    }

    private fun detailActivity(imageId: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("imageId", imageId) // 이미지 ID를 전달
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
