package com.task.pre_task_2502.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.task.pre_task_2502.data.repository.remote.ImageModel
import com.task.pre_task_2502.data.repository.remote.ImageRepository
import kotlinx.coroutines.flow.Flow

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {
    val photosFlow: Flow<PagingData<ImageModel>> = Pager(PagingConfig(pageSize = 10)) {
        ImagePagingSource(repository, "COIhacekqhvFycs3iAEzezzDYYbTlJhNPKSNyB9dP18")
    }.flow.cachedIn(viewModelScope) // 캐시된 Flow
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
            // 예외 처리 로직 추가
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageModel>): Int? {
        // 현재 페이지 키 반환
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
