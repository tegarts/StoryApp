package com.datte.storyapp.view.main

import androidx.lifecycle.*
import com.datte.storyapp.data.api.ApiConfig
import com.datte.storyapp.data.response.ListStoryItem
import com.datte.storyapp.data.response.StoriesResponse
import com.datte.storyapp.model.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _loadingScreen = MutableLiveData<Boolean>()
    val loadingScreen: LiveData<Boolean> = _loadingScreen


    fun getAllStories() {
        _loadingScreen.value = true
        val client = ApiConfig.getApiService().getAllStories()
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _loadingScreen.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _listStory.value = responseBody.listStory ?: emptyList()
                    }
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _loadingScreen.value = false
            }
        })
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}