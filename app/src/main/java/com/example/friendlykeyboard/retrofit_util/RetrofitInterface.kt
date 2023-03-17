package com.example.friendlykeyboard.retrofit_util

import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @POST("get_account")
    fun getAccount(@Body account: Account): Call<DataModel>

    @POST("sign-up")
    fun signUp(@Body account: Account): Call<DataModel>
}
