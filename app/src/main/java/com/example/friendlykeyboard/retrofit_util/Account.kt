package com.example.friendlykeyboard.retrofit_util

import com.google.gson.annotations.SerializedName

// JSON, DTO 클래스
data class Account(
    @SerializedName("id") private val id: String,
    @SerializedName("password") private val password: String
)
