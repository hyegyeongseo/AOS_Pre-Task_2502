package com.task.pre_task_2502.data.repository.local

import androidx.paging.PagingSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import com.task.pre_task_2502.data.repository.remote.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class LocalImageRepository(private val apiService: ApiService) {

    fun getImages(): Flow<PagingData<LocalImageModel>> {
        return Pager(
            config = PagingConfig(pageSize = 10), // 페이지 크기 설정
            pagingSourceFactory = { getImagesPagingSource() }
        ).flow
    }

    private fun getImagesPagingSource(): PagingSource<Int, LocalImageModel> {
        return object : PagingSource<Int, LocalImageModel>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LocalImageModel> {
                val page = params.key ?: 1
                return try {
                    // API 호출 (페이지 번호를 포함)
                    val response = apiService.getImages(page)
                    val images = response.results

                    LoadResult.Page(
                        data = images,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (images.isEmpty()) null else page + 1
                    )
                } catch (exception: IOException) {
                    LoadResult.Error(exception)
                } catch (exception: HttpException) {
                    LoadResult.Error(exception)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, LocalImageModel>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    val page = state.closestPageToPosition(anchorPosition)
                    page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
                }
            }
        }
    }
}
