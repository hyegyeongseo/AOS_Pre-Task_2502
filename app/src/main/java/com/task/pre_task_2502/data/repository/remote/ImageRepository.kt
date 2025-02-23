package com.task.pre_task_2502.data.repository.remote

class ImageRepository(private val apiService: ApiService) {
    suspend fun getPhotos(clientId: String, page: Int): List<LatestImageModel> {
        return apiService.getPhotos(clientId, page)
    }
}
