package com.fadhil.submissionstoryapp_akhir.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fadhil.submissionstoryapp_akhir.data.StoryRepository
import com.fadhil.submissionstoryapp_akhir.networking.UploadResponse
import com.fadhil.submissionstoryapp_akhir.remote.Result

class SignupViewModel(private val storyRepository: StoryRepository) : ViewModel() {


    fun postRegister(name: String, email: String, pass: String) =
        storyRepository.registerpost(name, email, pass)



    fun getLoginInfo(): String? {
        return storyRepository.checkToken()
    }
}