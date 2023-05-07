package com.example.friendlykeyboard.retrofit_util

import com.google.gson.annotations.SerializedName

data class Chat (
    //id 매개변수로 넘기기
    @SerializedName("id") private val id : String,
)