package com.example.friendlykeyboard.retrofit_util

import com.google.gson.annotations.SerializedName

// JSON, DTO 클래스
data class HateSpeech(
    @SerializedName("id") private val id: String,
    @SerializedName("text") private val text: String,
    @SerializedName("masking_option") private val masking_option: Boolean
)