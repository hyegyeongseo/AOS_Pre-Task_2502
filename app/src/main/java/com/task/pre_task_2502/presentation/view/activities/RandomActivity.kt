package com.task.pre_task_2502.presentation.view.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.task.pre_task_2502.R
import com.task.pre_task_2502.data.repository.remote.ApiService
import com.task.pre_task_2502.data.repository.remote.RetrofitClient
import com.task.pre_task_2502.presentation.view.adapters.ImagePagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RandomActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var buttonBookmark: Button
    private lateinit var adapter: ImagePagerAdapter
    private var isAutoSlideEnabled = false
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (isAutoSlideEnabled) {
                val nextItem = (viewPager.currentItem + 1) % adapter.itemCount
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // 3초 간격
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random)

        viewPager = findViewById(R.id.viewPager)
        buttonBookmark = findViewById(R.id.button_bookmark)

        // Retrofit API 서비스 초기화
        val apiService = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // 랜덤 이미지 로드
        loadRandomImages(apiService)

        // 북마크 버튼 클릭 리스너
        buttonBookmark.setOnClickListener {
            isAutoSlideEnabled = !isAutoSlideEnabled
            if (isAutoSlideEnabled) {
                handler.post(runnable) // 자동 슬라이드 시작
            } else {
                handler.removeCallbacks(runnable) // 자동 슬라이드 중지
            }
        }
    }

    private fun loadRandomImages(apiService: ApiService) {
        lifecycleScope.launch {
            val randomPhotos = withContext(Dispatchers.IO) {
                apiService.getRandomPhotos(5) // 5개의 랜덤 사진 요청
            }
            adapter = ImagePagerAdapter(randomPhotos)
            viewPager.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Activity 종료 시 핸들러 정리
    }
}