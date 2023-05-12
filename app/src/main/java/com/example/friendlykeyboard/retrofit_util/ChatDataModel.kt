package com.example.friendlykeyboard.retrofit_util

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// <id, text, date> 로 이루어진 ArrayList 받아오기
// id 는 내가 보낸 것인지 system이 보낸 것인지 기록하기 위함 ex 1 = me , 2 = system
data class ChatDataModel(
    @SerializedName("id_list") @Expose val idList: Array<Int>,
    @SerializedName("text_list") @Expose val textList: Array<String>,
    @SerializedName("date_list") @Expose val dateList: Array<String>
)
