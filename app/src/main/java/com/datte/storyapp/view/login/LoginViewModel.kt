package com.datte.storyapp.view.login

import androidx.lifecycle.*
import com.datte.storyapp.data.api.ApiConfig
import com.datte.storyapp.data.payload.LoginPayload
import com.datte.storyapp.data.response.ErrorResponse
import com.datte.storyapp.data.response.LoginResponse
import com.datte.storyapp.model.UserModel
import com.datte.storyapp.model.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {
    private val _login = MutableLiveData<Boolean>()
    val login: LiveData<Boolean> = _login

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private fun saveUser(user: UserModel) {
        viewModelScope.launch {
            ApiConfig.setToken(user.tokenAuth)
            pref.saveUser(user)
        }
    }

    fun loginUser(email: String, password: String) {
        val payload = LoginPayload(email, password)
        val client = ApiConfig.getApiService().loginUser(payload)
        _isLoading.value = true
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val token = responseBody.loginResult?.token as String
                        _login.value = true
                        saveUser(UserModel(token, true))
                        _snackbarText.value = responseBody.message
                    }
                } else {
                    val responseBody = response.errorBody()
                    _login.value = false
                    if (responseBody != null) {
                        val mapper =
                            Gson().fromJson(responseBody.string(), ErrorResponse::class.java)

                        _snackbarText.value = mapper.message
                    } else {
                        _snackbarText.value = response.message()
                    }

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _login.value = false
                _snackbarText.value = t.message ?: "Error !"
            }

        })
    }
}