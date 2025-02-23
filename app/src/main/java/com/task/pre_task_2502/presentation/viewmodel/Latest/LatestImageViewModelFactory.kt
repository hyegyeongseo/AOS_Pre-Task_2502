package com.task.pre_task_2502.presentation.viewmodel.Latest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.task.pre_task_2502.data.repository.remote.ImageRepository

class LatestImageViewModelFactory(private val repository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            return ImageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}