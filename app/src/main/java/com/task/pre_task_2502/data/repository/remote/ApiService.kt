package com.task.pre_task_2502.data.repository.remote

import com.task.pre_task_2502.data.repository.local.ImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String, // API 키
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 10 // 한 번에 10개 이미지 요청
    ): List<LatestImageModel>

    @GET("photos/{id}") // :id 대신 {id}로 작성
    suspend fun getPhotoDetails(
        @Path("id") id: String, // 이미지 ID
        @Query("client_id") clientId: String // API 키
    ): Response<LatestImageModel>

    suspend fun getImages(@Query("page") page: Int): ImageResponse

}

