package com.datte.storyapp.data.payload

import com.google.gson.annotations.SerializedName

data class LoginPayload (
    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String
)

