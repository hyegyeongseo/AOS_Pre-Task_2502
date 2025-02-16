package com.task.pre_task_2502.data.repository.remote

data class ImageModel(
    val id: String,
    val alt_description: String?,
    val urls: Urls,
    val user: User
)

data class Urls(
    val regular: String
)

data class User(
    val name: String
)

