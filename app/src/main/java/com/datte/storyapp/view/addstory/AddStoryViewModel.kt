package com.datte.storyapp.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datte.storyapp.data.api.ApiConfig
import com.datte.storyapp.data.response.UploadStoriesResponse
import com.datte.storyapp.data.utility.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel : ViewModel() {
    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    private val _hasUploaded = MutableLiveData<File>()
    val hasUploaded: LiveData<File> = _hasUploaded

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setFile(value: File) {
        _hasUploaded.value = value
    }

    fun postStory(file: File, description: String) {
        val compressedFile = file.reduceFileImage()
        val desc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            compressedFile.name,
            requestImageFile
        )

        val service = ApiConfig.getApiService().uploadStories(imageMultipart, desc)
        _isLoading.value = true
        service.enqueue(object : Callback<UploadStoriesResponse> {
            override fun onResponse(
                call: Call<UploadStoriesResponse>,
                response: Response<UploadStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isPosted.value = true
                    }
                } else {
                    _isPosted.value = false
                }
            }

            override fun onFailure(call: Call<UploadStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _isPosted.value = false
            }
        })
    }
}