package com.fadhil.submissionstoryapp_akhir.view.addstory

import androidx.lifecycle.ViewModel
import com.fadhil.submissionstoryapp_akhir.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {

    private lateinit var img : MultipartBody.Part
    private lateinit var desc: RequestBody
    private var lat: RequestBody? = null
    private var lon: RequestBody? = null

    fun sendStory(imgMultipart: MultipartBody.Part, desc: RequestBody, lat: RequestBody?, lon: RequestBody?) {
        this.desc = desc
        this.img = imgMultipart
        this.lat = lat
        this.lon = lon
    }

    fun addStory() = storyRepository.addStory(img, desc, lat, lon)

    fun resetLocalStory() {
        storyRepository.clearLocalStory()
    }

    fun clearPrefs() {
        storyRepository.clearToken()
    }
}