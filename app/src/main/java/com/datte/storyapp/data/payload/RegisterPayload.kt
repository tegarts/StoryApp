package com.datte.storyapp.data.payload

import com.google.gson.annotations.SerializedName

data class RegisterPayload(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String
)