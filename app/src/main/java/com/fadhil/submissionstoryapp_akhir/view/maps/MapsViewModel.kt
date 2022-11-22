package com.fadhil.submissionstoryapp_akhir.view.maps

import androidx.lifecycle.ViewModel
import com.fadhil.submissionstoryapp_akhir.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel(){
    fun getStoryList() = storyRepository.getStoryFromDb()
}