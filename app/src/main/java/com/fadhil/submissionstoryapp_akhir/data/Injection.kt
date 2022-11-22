package com.fadhil.submissionstoryapp_akhir.data

import android.content.Context
import com.fadhil.submissionstoryapp_akhir.local.StoryDatabase
import com.fadhil.submissionstoryapp_akhir.networking.ApiConfig
import com.fadhil.submissionstoryapp_akhir.pref.PreferenceDataSource

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val client = ApiConfig(context)
        val apiService = client.instanceApi()
        val database = StoryDatabase.getInstance(context)
        val dao = database.StoryDao()
        val sharedPref = PreferenceDataSource(context)
        return StoryRepository.getInstance(apiService, dao, database, sharedPref)
    }
}