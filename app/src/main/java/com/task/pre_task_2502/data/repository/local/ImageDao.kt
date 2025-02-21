package com.task.pre_task_2502.data.repository.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.task.pre_task_2502.data.repository.remote.ImageModel

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageModel)

    //@Query("SELECT * FROM images WHERE bookmarked = 1")
    //fun getAllBookmarkedImagesPaged(): PagingSource<Int, ImageModel>
//
   // @Query("UPDATE images SET bookmarked = :isBookmarked WHERE id = :imageId")
    //suspend fun updateBookmark(imageId: String, isBookmarked: Boolean)
}
