package com.task.pre_task_2502.data.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_images")
data class LocalImageModel(
    @PrimaryKey val id: String,          // 이미지 고유 ID
    val url: String,                     // 이미지 URL
    val username: String,                // 사용자 이름
    val altDescription: String?,          // 이미지 설명
    val bookmarked: Boolean = false      // 북마크 여부 (기본값 false)
)

