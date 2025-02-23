package com.task.pre_task_2502.data.repository.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: LocalImageModel) // 이미지 추가

    @Query("SELECT COUNT(*) FROM bookmarked_images") // 데이터 개수를 반환
    suspend fun getBookmarkedImageCount(): Int // 북마크된 이미지 수 반환

    // PagingSource 반환하는 메서드 추가
    @Query("SELECT * FROM bookmarked_images ORDER BY id") // 원하는 정렬 기준에 따라 수정
    suspend fun getAllBookmarkedImages(): List<LocalImageModel>

    @Query("DELETE FROM bookmarked_images WHERE id = :imageId") // 북마크 삭제
    suspend fun deleteBookmark(imageId: String)

    // 업데이트 메서드는 필요 없을 경우 삭제 가능
    // @Query("UPDATE bookmarked_images SET bookmarked = :isBookmarked WHERE id = :imageId")
    // suspend fun updateBookmark(imageId: String, isBookmarked: Boolean)
}
