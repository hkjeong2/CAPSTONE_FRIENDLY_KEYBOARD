package com.example.friendlykeyboard.retrofit_util

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitInterface {
    @POST("get_account")
    fun getAccount(@Body account: Account): Call<AccountDataModel>

    @POST("sign-up")
    fun signUp(@Body account: Account): Call<AccountDataModel>

    @POST("sign-in")
    fun signIn(@Body account: Account): Call<AccountDataModel>

    @POST("inference_hate_speech")
    suspend fun inferenceHateSpeech(@Body hateSpeech: HateSpeech): Response<HateSpeechDataModel>

    @POST("inference_hate_speech")
    fun inferenceHateSpeechCall(@Body hateSpeech: HateSpeech): Call<HateSpeechDataModel>

    @POST("get_chat_list")
    suspend fun getChatList(@Body chat: Chat) : Response<ChatDataModel>

    @POST("get_hate_speech_counts")
    suspend fun getHateSpeechCounts(@Body account: Account): Response<HateSpeechCountDataModel>
}
