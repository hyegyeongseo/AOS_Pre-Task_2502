package com.task.pre_task_2502.data.repository.remote

data class LatestImageModel(
    val id: String,
    val altDescription: String?,
    val urls: Urls,
    val user: User
)

data class Urls(
    val full: String, // 전체 이미지 URL
    val regular: String
)

data class User(
    val username: String
)

