package com.task.pre_task_2502.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.task.pre_task_2502.data.repository.remote.ImageModel
import com.task.pre_task_2502.data.repository.remote.ImageRepository
import kotlinx.coroutines.flow.Flow

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {
    val photosFlow: Flow<PagingData<ImageModel>> = Pager(PagingConfig(pageSize = 10)) {
        ImagePagingSource(repository, "YOUR_UNSPLASH_CLIENT_ID")
    }.flow
}

class ImagePagingSource(
    private val repository: ImageRepository,
    private val clientId: String
) : PagingSource<Int, ImageModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageModel> {
        val page = params.key ?: 1
        return try {
            val response = repository.getPhotos(clientId, page)
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageModel>): Int? {
        return null
    }
}
