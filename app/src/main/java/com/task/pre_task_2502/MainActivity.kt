package com.task.pre_task_2502

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.task.pre_task_2502.data.repository.remote.ApiService
import com.task.pre_task_2502.data.repository.remote.ImageRepository
import com.task.pre_task_2502.presentation.view.adapters.ImageAdapter
import com.task.pre_task_2502.presentation.viewmodel.ImageViewModel
import com.task.pre_task_2502.presentation.viewmodel.ImageViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.task.pre_task_2502.data.repository.remote.RetrofitClient

class MainActivity : AppCompatActivity() {

    // 화면에 보여지는 순서
    // 1. 최상단 앱이름('Test')을 알리는 텍스트 바(아래 위로 스크롤 해도 고정)
    // 2. 보여줄 이미지 목록 바로 위에 '최신 이미지' 섹션임을 알리는 하얀 배경의 타이틀 바(고정 아님)
    // 3. 타이틀 바 바로 밑에서부터 unsplash api를 사용하여 이미지를 10개 호출하여 앨범처럼 이미지 목록으로 출력. 이미지 로딩중임을 스켈레톤뷰로 표시
    // 4. 이미지를 선택하면 팝업창이 뜨면서 선택 이미지에 대한 상세 정보 확인(이미지, 사용자 이름(name), alt_description) 가능
    // 5. 스크롤을 아래로 내리다가 이미지가 없는 경우, 무한 스크롤 기능을 사용하여 이미지 10개 추가로 출력
    // 6. 화면 최하단 고정 바의 버튼 두 개로 화면 전환. 첫 화면과 다른 화면 간 전환 역할

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private val imageViewModel: ImageViewModel by viewModels {
        ImageViewModelFactory(ImageRepository(getApiService()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        recyclerView = findViewById(R.id.recycler_view)
        loadingSpinner = findViewById(R.id.loading_spinner) // 로딩 스피너 초기화
        shimmerFrameLayout = findViewById(R.id.sfl_sample) // 스켈레톤 UI 초기화

        // RecyclerView 설정
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 두 개의 열로 설정
        imageAdapter = ImageAdapter { image ->
            // 상세 정보를 보기 위한 Activity로 이동
            //val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("imageId", image.id) // 이미지 ID 전달
            startActivity(intent)
        }
        recyclerView.adapter = imageAdapter

        // 데이터 수집..
        lifecycleScope.launch {
            imageViewModel.photosFlow.collectLatest { pagingData ->
                imageAdapter.submitData(pagingData)
            }
        }

        // 스크롤 리스너
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // 마지막 아이템에 도달했을 때 추가 데이터 로드
                //if (lastVisibleItemPosition == totalItemCount - 1 && !imageAdapter.loadState.append is LoadState.Loading) {
                //    loadingSpinner.visibility = View.VISIBLE // 로딩 스피너 보이기
                //}
            }
        })

        // 이미지 처리
        imageAdapter.addLoadStateListener { loadState ->
            // 스켈레톤 UI 표시
            if (loadState.refresh is LoadState.Loading) {
                shimmerFrameLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            // 추가 데이터 로딩 상태 처리
            if (loadState.append is LoadState.Loading) {
                loadingSpinner.visibility = View.VISIBLE // 추가 로딩 UI 표시
            } else {
                loadingSpinner.visibility = View.GONE // 추가 로딩 완료 시 스피너 숨기기
            }

            // 오류 처리
            if (loadState.append is LoadState.Error) {
                val errorState = loadState.append as LoadState.Error
                Toast.makeText(this@MainActivity, "Error: ${errorState.error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Unsplash API 서비스 호출
    private fun getApiService(): ApiService {
        return RetrofitClient.apiService
    }
}
