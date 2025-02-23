package com.task.pre_task_2502.presentation.viewmodel.Bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.task.pre_task_2502.data.repository.local.BookmarkDao
import com.task.pre_task_2502.data.repository.remote.ApiService

class BookmarkImageViewModelFactory(
    private val bookmarkDao: BookmarkDao,
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarkImageViewModel::class.java)) {
            return BookmarkImageViewModel(bookmarkDao, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
