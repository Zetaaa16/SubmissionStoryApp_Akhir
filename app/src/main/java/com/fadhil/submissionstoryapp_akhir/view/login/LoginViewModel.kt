package com.fadhil.submissionstoryapp_akhir.view.login

import androidx.lifecycle.ViewModel
import com.fadhil.submissionstoryapp_akhir.data.StoryRepository


class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {


    fun postLogin(email: String, password: String) = storyRepository.loginpost(email, password)


}