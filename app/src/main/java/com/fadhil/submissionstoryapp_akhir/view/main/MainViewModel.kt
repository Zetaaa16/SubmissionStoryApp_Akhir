package com.fadhil.submissionstoryapp_akhir.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fadhil.submissionstoryapp_akhir.data.StoryRepository
import com.fadhil.submissionstoryapp_akhir.local.StoryEntity

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoryList() : LiveData<PagingData<StoryEntity>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun resetLocalStory() {
        storyRepository.clearLocalStory()
    }

    fun clearPrefs() {
        storyRepository.clearToken()
    }
}