package com.fadhil.submissionstoryapp_akhir.pref

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fadhil.submissionstoryapp_akhir.data.Injection
import com.fadhil.submissionstoryapp_akhir.data.StoryRepository
import com.fadhil.submissionstoryapp_akhir.view.addstory.AddStoryViewModel
import com.fadhil.submissionstoryapp_akhir.view.login.LoginViewModel
import com.fadhil.submissionstoryapp_akhir.view.main.MainViewModel
import com.fadhil.submissionstoryapp_akhir.view.maps.MapsViewModel
import com.fadhil.submissionstoryapp_akhir.view.signup.SignupViewModel


class ViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            SignupViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            AddStoryViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            MapsViewModel(storyRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}