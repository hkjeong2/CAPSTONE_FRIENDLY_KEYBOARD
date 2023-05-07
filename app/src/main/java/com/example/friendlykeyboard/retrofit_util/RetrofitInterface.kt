package com.example.friendlykeyboard.retrofit_util

import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @POST("get_account")
    fun getAccount(@Body account: Account): Call<AccountDataModel>

    @POST("sign-up")
    fun signUp(@Body account: Account): Call<AccountDataModel>

    @POST("sign-in")
    fun signIn(@Body account: Account): Call<AccountDataModel>

    @POST("inference_hate_speech")
    fun inferenceHateSpeech(@Body hateSpeech: HateSpeech): Call<HateSpeechDataModel>

    @POST("get_chat_list")
    fun getChatList(@Body chat: Chat) : Call<ChatDataModel>
}
