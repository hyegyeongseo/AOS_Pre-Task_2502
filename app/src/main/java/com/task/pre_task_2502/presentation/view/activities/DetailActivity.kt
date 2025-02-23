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
import com.task.pre_task_2502.data.repository.remote.Urls
import com.task.pre_task_2502.data.repository.remote.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    private lateinit var imageView: com.github.chrisbanes.photoview.PhotoView
    private lateinit var tvUsername: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvAltDescription: TextView
    private lateinit var btnClose: ImageButton
    private lateinit var btnBookmark: ImageButton
    private lateinit var loadingSpinner: ProgressBar

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
        tvTitle = findViewById(R.id.tvTitle)
        tvAltDescription = findViewById(R.id.tvAltDescription)
        btnClose = findViewById(R.id.btnClose)
        btnBookmark = findViewById(R.id.btnBookmark)
        loadingSpinner = findViewById(R.id.loading_spinner)

        // Intent로부터 데이터 받기
        imageId = intent.getStringExtra("imageId")
        imageUrl = intent.getStringExtra("imageUrl")
        username = intent.getStringExtra("username")
        altDescription = intent.getStringExtra("altDescription")

        // 뷰에 데이터 설정
        setupViews()

        // 북마크 버튼 클릭 리스너
        btnBookmark.setOnClickListener {
            toggleBookmark()
        }

        // 닫기 버튼 클릭 리스너
        btnClose.setOnClickListener {
            finish() // Activity 종료
        }
    }

    private fun setupViews() {
        loadingSpinner.visibility = View.VISIBLE
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        tvUsername.text = username
        tvAltDescription.text = altDescription
        loadingSpinner.visibility = View.GONE

        // 북마크 상태 확인
        checkIfBookmarked()
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
