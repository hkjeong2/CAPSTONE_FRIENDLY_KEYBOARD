package com.example.friendlykeyboard.retrofit_util

import com.google.gson.annotations.SerializedName

// 입력한 채팅 내용을 저장하는 클래스
data class Chat (
    @SerializedName("account_id") private val accountID: String,
    @SerializedName("id") private val id: Int,
    @SerializedName("text") private val text: String,
    @SerializedName("date") private val date: String
)