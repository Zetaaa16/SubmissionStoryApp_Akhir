package com.fadhil.submissionstoryapp_akhir.networking

import android.content.Context
import android.os.Parcelable
import androidx.viewbinding.BuildConfig
import com.fadhil.submissionstoryapp_akhir.local.StoryEntity
import com.fadhil.submissionstoryapp_akhir.pref.ConstVal.MAIN_URL
import com.fadhil.submissionstoryapp_akhir.pref.PreferenceDataSource
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiConfig(context: Context) {

    private val interceptor = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    private val httpClient = Interceptor { chain ->
        val original = chain.request()
        val sharedPref = PreferenceDataSource(context)
        val httpBuilder = original.url.newBuilder()

        val request = original.newBuilder()
            .method(original.method, original.body)
        if (sharedPref.readLoginInfo() != null) {
            val token = sharedPref.readLoginInfo()
            request.header("Authorization", "Bearer $token")
        }
        val endRequest = request.url(httpBuilder.build()).build()


        chain.proceed(endRequest)
    }


    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor(httpClient)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(MAIN_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun instanceApi(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}


@Parcelize
data class GetStory(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Float,
    val lon: Float
) : Parcelable


data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null
)


data class LoginResult(
    val userId: String? = null,
    val name: String? = null,
    val token: String? = null
)


data class StoryGetResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryEntity>
)


data class UploadResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)
