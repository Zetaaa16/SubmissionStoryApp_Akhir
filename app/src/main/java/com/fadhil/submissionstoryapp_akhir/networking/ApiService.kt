package com.fadhil.submissionstoryapp_akhir.networking

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: String? = null,
        @Query("size") size: String? = null,
        @Query("location") location: String? = null
    ): StoryGetResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun registerstories(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): UploadResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginstories(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): UploadResponse


}