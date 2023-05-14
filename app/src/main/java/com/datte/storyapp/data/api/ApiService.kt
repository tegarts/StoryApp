package com.datte.storyapp.data.api

import com.datte.storyapp.data.payload.LoginPayload
import com.datte.storyapp.data.payload.RegisterPayload
import com.datte.storyapp.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("No-Authentication: true")
    @POST("v1/register")
    fun createUser(
        @Body payload: RegisterPayload
    ): Call<RegisterResponse>

    @Headers("No-Authentication: true")
    @POST("v1/login")
    fun loginUser(
        @Body payload: LoginPayload
    ): Call<LoginResponse>

    @GET("v1/stories")
    fun getAllStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Call<StoriesResponse>

    @GET("v1/stories/{userId}")
    fun getDetailStory(
        @Path("userId") userId: String
    ): Call<DetailStory>

    @Multipart
    @POST("v1/stories")
    fun uploadStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<UploadStoriesResponse>

}