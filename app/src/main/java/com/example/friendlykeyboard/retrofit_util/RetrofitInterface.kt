package com.example.friendlykeyboard.retrofit_util

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitInterface {
    // 회원가입 시 특정 아이디가 이미 존재하는지 확인
    @POST("get_account")
    fun getAccount(@Body account: Account): Call<AccountDataModel>

    // 회원가입
    @POST("sign-up")
    fun signUp(@Body account: Account): Call<AccountDataModel>

    // 로그인
    @POST("sign-in")
    fun signIn(@Body account: Account): Call<AccountDataModel>

    // 입력한 텍스트에 혐오표현이 존재하는지 확인
    @POST("inference_hate_speech")
    suspend fun inferenceHateSpeech(@Body hateSpeech: HateSpeech): Response<HateSpeechDataModel>

    // 입력한 텍스트에 혐오표현이 존재하는지 확인
    @POST("inference_hate_speech")
    fun inferenceHateSpeechCall(@Body hateSpeech: HateSpeech): Call<HateSpeechDataModel>

    // 입력한 채팅 내용을 저장
    @POST("save_chat")
    suspend fun saveChat(@Body chat: Chat): Response<String>

    // 특정 계정의 전체 채팅 내용 가져오기
    @POST("get_chat_list")
    suspend fun getChatList(@Body account: Account) : Response<ChatDataModel>

    // 특정 계정의 혐오표현 사용 횟수 가져오기
    @POST("get_hate_speech_counts")
    suspend fun getHateSpeechCounts(@Body account: Account): Response<HateSpeechCountDataModel>

    // 특정 계정의 혐오표현 사용 횟수 가져오기
    @POST("get_hate_speech_counts")
    fun getHateSpeechCountsCall(@Body account: Account): Call<HateSpeechCountDataModel>
}
