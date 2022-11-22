package com.fadhil.submissionstoryapp_akhir.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Query("SELECT * FROM Story_table")
    fun getPagingStory(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM Story_table")
    fun getStoryMap(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM Story_table")
    fun getStoryForWidget(): List<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStories(story: List<StoryEntity>)

    @Query("DELETE FROM STORY_TABLE")
    suspend fun clearStory()
}