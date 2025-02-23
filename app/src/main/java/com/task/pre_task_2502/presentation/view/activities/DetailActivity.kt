package com.task.pre_task_2502.presentation.view.activities
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.task.pre_task_2502.R
import com.task.pre_task_2502.data.repository.local.AppDatabase
import com.task.pre_task_2502.data.repository.local.LocalImageModel
import com.task.pre_task_2502.data.repository.remote.ApiService
import com.task.pre_task_2502.data.repository.remote.LatestImageModel
import com.task.pre_task_2502.data.repository.remote.RetrofitClient
import com.task.pre_task_2502.data.repository.remote.Urls
import com.task.pre_task_2502.data.repository.remote.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var imageView: com.github.chrisbanes.photoview.PhotoView
    private lateinit var tvUsername: TextView
    private lateinit var tvAltDescription: TextView
    private lateinit var btnClose: ImageButton
    private lateinit var btnBookmark: ImageButton
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var apiService: ApiService

    private var imageId: String? = null
    private var imageUrl: String? = null
    private var username: String? = null
    private var altDescription: String? = null
    private var isBookmarked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 뷰 초기화
        imageView = findViewById(R.id.image_view)
        tvUsername = findViewById(R.id.tvUsername)
        tvAltDescription = findViewById(R.id.tvAltDescription)
        btnClose = findViewById(R.id.btnClose)
        btnBookmark = findViewById(R.id.btnBookmark)
        loadingSpinner = findViewById(R.id.loading_spinner)

        // Retrofit API 서비스 초기화
        apiService = RetrofitClient.apiService

        // Intent로부터 데이터 받기
        imageId = intent.getStringExtra("imageId")
        imageId?.let { loadImage(it) }

        // 북마크 버튼 클릭 리스너
        btnBookmark.setOnClickListener {
            toggleBookmark()
        }

        // 닫기 버튼 클릭 리스너
        btnClose.setOnClickListener {
            finish() // Activity 종료
        }
    }

    private fun loadImage(imageId: String) {
        loadingSpinner.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val response: Response<LatestImageModel> = apiService.getImageById(imageId)

            withContext(Dispatchers.Main) {
                loadingSpinner.visibility = View.GONE
                // 북마크 상태 확인
                checkIfBookmarked()
                if (response.isSuccessful && response.body() != null) {
                    val imageResponse = response.body()!!
                    imageUrl = imageResponse.urls.regular // URL 저장
                    username = imageResponse.user.username // 사용자 이름 저장
                    altDescription = imageResponse.altDescription // 대체 설명 저장

                    // 이미지 URL을 Glide로 로드
                    Glide.with(this@DetailActivity)
                        .load(imageUrl)
                        .into(imageView)

                    // 사용자 이름과 대체 설명 설정
                    tvUsername.text = username
                    tvAltDescription.text = altDescription ?: "No description available"
                } else {
                    // 에러 처리
                    tvAltDescription.text = "Error loading image"
                }
            }
        }
    }

    private fun checkIfBookmarked() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val bookmarkedImage = withContext(Dispatchers.IO) {
                database.bookmarkDao().getImageById(imageId ?: "")
            }

            isBookmarked = bookmarkedImage != null
            updateBookmarkButton()
        }
    }

    private fun toggleBookmark() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(applicationContext)
            if (isBookmarked) {
                // 북마크 삭제
                withContext(Dispatchers.IO) {
                    database.bookmarkDao().deleteImageById(imageId ?: "")
                }
                isBookmarked = false
            } else {
                // 북마크 저장
                val newImage = LocalImageModel(
                    id = imageId ?: "", // 이미지 ID
                    altDescription = altDescription, // 이미지 설명
                    urls = Urls( // Urls 객체 생성
                        full = imageUrl ?: "", // 전체 이미지 URL
                        regular = imageUrl ?: "" // 정규 이미지 URL
                    ),
                    user = User( // User 객체 생성
                        username = username ?: "" // 사용자 이름
                    ),
                    bookmarked = true // 북마크 여부, 저장 시 true로 설정
                )

                withContext(Dispatchers.IO) {
                    database.bookmarkDao().insertImage(newImage) // 데이터베이스에 저장
                }
                isBookmarked = true
            }
            updateBookmarkButton()
        }
    }

    private fun updateBookmarkButton() {
        // 불투명도 설정
        btnBookmark.alpha = if (isBookmarked) 0.3f else 1.0f // 30% 또는 100%
    }
}
