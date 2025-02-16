package com.task.pre_task_2502.data.repository.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String, // API 키
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 10 // 한 번에 10개 이미지 요청
    ): List<ImageModel>
}
