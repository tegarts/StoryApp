package com.datte.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.datte.storyapp.model.UserModel
import com.datte.storyapp.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }
}