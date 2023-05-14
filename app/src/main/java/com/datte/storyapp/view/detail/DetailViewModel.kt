package com.datte.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datte.storyapp.data.api.ApiConfig
import com.datte.storyapp.data.response.DetailStory
import com.datte.storyapp.data.response.ListStoryItem
import com.datte.storyapp.data.utility.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _detailStory = MutableLiveData<ListStoryItem>()
    val detailStory: LiveData<ListStoryItem> = _detailStory

    private val _loadingScreen = MutableLiveData<Boolean>()
    val loadingScreen: LiveData<Boolean> = _loadingScreen

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText


    fun getDetailStory(userId: String) {
        _loadingScreen.value = true

        val cilent = ApiConfig.getApiService().getDetailStory(userId)
        cilent.enqueue(object : Callback<DetailStory> {
            override fun onResponse(
                call: Call<DetailStory>,
                response: Response<DetailStory>
            ) {
                _loadingScreen.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _detailStory.value = responseBody.story ?: ListStoryItem()
                    }
                } else {
                    _snackBarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<DetailStory>, t: Throwable) {
                _loadingScreen.value = false
                _snackBarText.value = Event("onFailure: Gagal, ${t.message ?: ""}")
            }
        })

    }
}