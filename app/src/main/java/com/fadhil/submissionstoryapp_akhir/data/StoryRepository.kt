package com.fadhil.submissionstoryapp_akhir.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.fadhil.submissionstoryapp_akhir.local.StoryDao
import com.fadhil.submissionstoryapp_akhir.local.StoryDatabase
import com.fadhil.submissionstoryapp_akhir.local.StoryEntity
import com.fadhil.submissionstoryapp_akhir.networking.ApiService
import com.fadhil.submissionstoryapp_akhir.networking.UploadResponse
import com.fadhil.submissionstoryapp_akhir.pref.PreferenceDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.fadhil.submissionstoryapp_akhir.remote.Result


class StoryRepository(
    private val apiService: ApiService,
    private val storyDao: StoryDao,
    private val storyDatabase: StoryDatabase,
    private val pref: PreferenceDataSource
) {
    fun loginpost(email: String,password: String): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginstories(email, password)
            val loginResult = response.loginResult
            if (loginResult != null){
                loginResult.token?.let { pref.saveLoginInfo(it) }
            }
            val loginstatus = UploadResponse(
                response.error,
                response.message
            )
            emit(Result.Success(loginstatus))
        } catch (exception: Exception){
            Log.d("Login",exception.message.toString())
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun registerpost(name: String,email:String,password: String): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerstories(name, email, password)
            emit(Result.Success(response))
        }catch (exception: Exception){
            Log.d("Signup",exception.message.toString())
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun checkToken(): String? {
        return pref.readLoginInfo()
    }

    fun clearToken(){
        pref.clearLoginInfo()
    }

    fun clearLocalStory(){
        CoroutineScope(Dispatchers.IO).launch{
            storyDao.clearStory()
        }
    }

    fun getStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator =
            StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.StoryDao().getPagingStory()
            }
        ).liveData
    }

    fun getStoryFromDb(): LiveData<List<StoryEntity>> {
        return storyDao.getStoryMap()
    }

    fun addStory(file: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?) = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.addStories(file, description, lat, lon)
            val uploadStatus = UploadResponse(
                response.error,
                response.message
            )
            emit(Result.Success(uploadStatus))
        } catch (e: Exception) {
            Log.d("StoryRepository", "addStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDao: StoryDao,
            storyDatabase: StoryDatabase,
            sharedPref: PreferenceDataSource
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDao, storyDatabase, sharedPref)
            }.also { instance = it }
    }
}