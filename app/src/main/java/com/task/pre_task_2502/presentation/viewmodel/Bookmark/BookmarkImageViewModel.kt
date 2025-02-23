package com.task.pre_task_2502.presentation.viewmodel.Bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.task.pre_task_2502.data.repository.local.BookmarkDao
import com.task.pre_task_2502.data.repository.local.LocalImageModel
import com.task.pre_task_2502.data.repository.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BookmarkImageViewModel(
    private val bookmarkDao: BookmarkDao,
    private val apiService: ApiService
) : ViewModel() {

    // 북마크 데이터의 존재 유무를 확인
    suspend fun hasBookmarkedImages(): Boolean {
        return bookmarkDao.getBookmarkedImageCount() > 0
    }

    // 특정 이미지를 북마크에 추가
    fun addBookmark(image: LocalImageModel) {
        viewModelScope.launch {
            bookmarkDao.insert(image)
        }
    }

    // 특정 이미지를 북마크에서 제거
    fun removeBookmark(imageId: String) {
        viewModelScope.launch {
            bookmarkDao.deleteBookmark(imageId)
        }
    }

    // 북마크된 이미지를 가져오는 메서드 추가
    fun getBookmarkedImages(): Flow<PagingData<LocalImageModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20), // 원하는 페이지 크기 설정
            pagingSourceFactory = { bookmarkDao.getAllBookmarkedImages() } // PagingSource 반환
        ).flow.cachedIn(viewModelScope) // Flow를 cachedIn으로 래핑
    }
}
