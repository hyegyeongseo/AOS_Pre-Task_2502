package com.task.pre_task_2502.presentation.view.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.task.pre_task_2502.R
import com.task.pre_task_2502.data.repository.remote.ApiService
import com.task.pre_task_2502.data.repository.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var tvUsername: TextView
    private lateinit var tvAltDescription: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail) // DetailActivity의 레이아웃

        tvUsername = findViewById(R.id.tvUsername)
        tvAltDescription = findViewById(R.id.tvAltDescription)

        imageView = findViewById(R.id.image_view)
        loadingSpinner = findViewById(R.id.loading_spinner)

        val imageId = intent.getStringExtra("imageId") // 이미지 ID 가져오기
        imageId?.let {
            loadImage(it)
        }
    }

    private fun loadImage(imageId: String) {
        loadingSpinner.visibility = View.VISIBLE

        // Retrofit API 호출
        CoroutineScope(Dispatchers.IO).launch {
            val apiService = getApiService()
            val response = apiService.getPhotoDetails(imageId, "COIhacekqhvFycs3iAEzezzDYYbTlJhNPKSNyB9dP18") // API 호출

            withContext(Dispatchers.Main) {
                loadingSpinner.visibility = View.GONE
                if (response.isSuccessful) {
                    val imageDetail = response.body()
                    imageDetail?.let {
                        // 데이터 바인딩
                        Glide.with(this@DetailActivity).load(it.urls.full).into(imageView)
                        tvUsername.text = it.user.username
                        tvAltDescription.text = it.alt_description ?: "No description available"
                    }
                } else {
                    // 오류 처리
                }
            }
        }
    }

    private fun getApiService(): ApiService {
        return RetrofitClient.apiService
    }
}
