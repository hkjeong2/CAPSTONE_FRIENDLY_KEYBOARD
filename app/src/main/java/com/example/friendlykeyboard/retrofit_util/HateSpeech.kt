package com.example.friendlykeyboard.retrofit_util

import com.google.gson.annotations.SerializedName

// JSON, DTO 클래스
data class HateSpeech(@SerializedName("text") private val text: String)