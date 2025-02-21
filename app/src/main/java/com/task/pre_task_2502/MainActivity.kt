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
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.task.pre_task_2502.data.repository.local.AppDatabase
import com.task.pre_task_2502.data.repository.remote.ApiService
import com.task.pre_task_2502.data.repository.remote.ImageModel
import com.task.pre_task_2502.data.repository.remote.ImageRepository
import com.task.pre_task_2502.data.repository.remote.RetrofitClient
import com.task.pre_task_2502.presentation.view.activities.DetailActivity
import com.task.pre_task_2502.presentation.view.adapters.ImageAdapter
import com.task.pre_task_2502.presentation.viewmodel.ImageViewModel
import com.task.pre_task_2502.presentation.viewmodel.ImageViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bookmarkContainer: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var latestImagesHeader: TextView
    private lateinit var bookmarkHeader: TextView
    private lateinit var bookmarkShimmerLayout: ShimmerFrameLayout
    private lateinit var latestImagesShimmerLayout: ShimmerFrameLayout // 외부 이미지 스켈레톤 뷰

    private val imageViewModel: ImageViewModel by viewModels {
        ImageViewModelFactory(ImageRepository(getApiService()))
    }

    private var isFirstLoad = true // 첫 로딩 여부

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        bookmarkContainer = findViewById(R.id.bookmark_images)
        recyclerView = findViewById(R.id.recycler_view)
        loadingSpinner = findViewById(R.id.loading_spinner)
        latestImagesHeader = findViewById(R.id.latest_images_header)
        bookmarkHeader = findViewById(R.id.bookmark_header)
        bookmarkShimmerLayout = findViewById(R.id.bookmark_shimmer_layout) // 북마크 스켈레톤 뷰
        latestImagesShimmerLayout = findViewById(R.id.latest_images_shimmer_layout) // 외부 스켈레톤 뷰

        // RecyclerView 설정 (수직 스크롤)
        recyclerView = findViewById(R.id.recycler_view)
        val gridLayoutManager = GridLayoutManager(this, 2) // 2열 설정
        recyclerView.layoutManager = gridLayoutManager
        imageAdapter = ImageAdapter { image ->
            detailActivity(image)
        }
        recyclerView.adapter = imageAdapter

        // 무한 스크롤 구현
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                if (layoutManager.findLastCompletelyVisibleItemPosition() == imageAdapter.itemCount - 1) {
                    imageAdapter.retry() // 추가 데이터 요청
                }
            }
        })

        // 북마크된 이미지를 로컬 DB에서 불러오기
        loadBookmarkedImages()
    }

    private fun loadBookmarkedImages() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val bookmarkedImagesFlow = db.imageDao().getAllBookmarkedImagesPaged().cachedIn(lifecycleScope)

            bookmarkedImagesFlow.collect { pagingData: PagingData<ImageModel> ->
                // 새로운 코루틴에서 submitData를 호출
                launch {
                    imageAdapter.submitData(pagingData)
                }

                // 데이터가 있는지 확인하기 위해 어댑터의 itemCount를 확인
                if (imageAdapter.itemCount > 0) {
                    // 북마크된 이미지가 있을 경우
                    bookmarkHeader.visibility = View.VISIBLE
                    bookmarkContainer.visibility = View.VISIBLE
                    startBookmarkShimmerAnimation() // 북마크 스켈레톤 애니메이션 시작

                    // 북마크된 이미지 표시
                    val pagingItems = viewModel.pagingData.collectAsLazyPagingItems()

                    for (image in pagingItems) {
                        image?.let {
                            val imageView = ImageView(this@MainActivity).apply {
                                val layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT, // 가로는 원본 비율에 맞춤
                                    150 // 세로는 고정
                                )
                                this.layoutParams = layoutParams
                                Glide.with(this).load(it.url).into(this)
                                setPadding(8, 8, 8, 8) // 패딩 추가
                                setOnClickListener {
                                    DetailActivity(it)
                                }
                            }
                            bookmarkContainer.addView(imageView)
                        }
                    }

                    stopBookmarkShimmerAnimation() // 북마크 스켈레톤 뷰 중지
                    latestImagesHeader.visibility = View.VISIBLE // 외부 이미지 헤더 보이기

                    // 외부 이미지를 로드
                    loadLatestImages()
                } else {
                    // 북마크된 이미지가 없을 경우
                    bookmarkHeader.visibility = View.GONE
                    bookmarkContainer.visibility = View.GONE
                    latestImagesHeader.visibility = View.VISIBLE // 외부 이미지 헤더 보이기
                    startLatestImagesShimmerAnimation() // 외부 이미지 스켈레톤 뷰 시작

                    // 최신 이미지를 로드
                    loadLatestImages()
                }
            }
        }
    }

    private fun loadLatestImages() {
        lifecycleScope.launch {
            loadingSpinner.visibility = View.VISIBLE // 로딩 시작 시 스피너 보이기

            imageViewModel.photosFlow.collect { pagingData ->
                imageAdapter.submitData(pagingData)
                loadingSpinner.visibility = View.GONE // 데이터 로딩 후 스피너 숨기기
            }

            // 첫 로딩 후 스켈레톤 애니메이션 중지
            if (isFirstLoad) {
                stopLatestImagesShimmerAnimation() // 외부 스켈레톤 뷰 중지
                isFirstLoad = false // 첫 로딩 완료 상태로 변경
            }
        }
    }


    private fun detailActivity(image: ImageModel) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("imageId", image.id) // 이미지 ID를 전달
        }
        startActivity(intent)
    }


    private fun startBookmarkShimmerAnimation() {
        bookmarkShimmerLayout.visibility = View.VISIBLE
        bookmarkShimmerLayout.startShimmer() // 북마크 스켈레톤 애니메이션 시작
    }

    private fun stopBookmarkShimmerAnimation() {
        bookmarkShimmerLayout.stopShimmer() // 북마크 스켈레톤 애니메이션 중지
        bookmarkShimmerLayout.visibility = View.GONE // 북마크 스켈레톤 숨기기
    }

    private fun startLatestImagesShimmerAnimation() {
        latestImagesShimmerLayout.visibility = View.VISIBLE
        latestImagesShimmerLayout.startShimmer() // 최신 이미지 스켈레톤 애니메이션 시작
    }

    private fun stopLatestImagesShimmerAnimation() {
        latestImagesShimmerLayout.stopShimmer() // 최신 이미지 스켈레톤 애니메이션 중지
        latestImagesShimmerLayout.visibility = View.GONE // 스켈레톤 숨기기
    }

    private fun getApiService(): ApiService {
        return RetrofitClient.apiService
    }
}
