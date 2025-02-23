package com.task.pre_task_2502.data.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.task.pre_task_2502.data.repository.remote.Urls
import com.task.pre_task_2502.data.repository.remote.User

@Entity(tableName = "bookmarked_images")
data class LocalImageModel(
    @PrimaryKey val id: String,            // 이미지 고유 ID
    val altDescription: String?,            // 이미지 설명
    val urls: Urls,                        // 이미지 URL 정보
    val user: User,                        // 사용자 정보
    val bookmarked: Boolean = false         // 북마크 여부 (기본값 false)
)

//data class Urls(
//    val full: String,                      // 전체 이미지 URL
//    val regular: String                    // 정규 이미지 URL
//)

//data class User(
//    val username: String                   // 사용자 이름
//)

