package com.task.pre_task_2502.presentation.view.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.task.pre_task_2502.R

class DetailActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var subscriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail) // 팝업을 위한 레이아웃 파일을 설정

        // UI 요소 초기화
        imageView = findViewById(R.id.image_view)
        titleTextView = findViewById(R.id.title_text_view)
        usernameTextView = findViewById(R.id.username_text_view)
        subscriptionTextView = findViewById(R.id.subscription_text_view)

        // Intent로부터 데이터 받기
        val imageId = intent.getStringExtra("imageId") // 이미지 ID
        val imageUrl = intent.getStringExtra("imageUrl") // 이미지 URL
        val title = intent.getStringExtra("title") // 이미지 제목
        val username = intent.getStringExtra("username") // 사용자 이름
        val subscription = intent.getStringExtra("subscription") // 구독 정보

        // UI에 데이터 설정
        Glide.with(this).load(imageUrl).into(imageView)
        titleTextView.text = title
        usernameTextView.text = username
        subscriptionTextView.text = subscription
    }
}