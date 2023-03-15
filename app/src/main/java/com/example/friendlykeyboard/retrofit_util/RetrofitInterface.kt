package com.example.friendlykeyboard.retrofit_util

import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @FormUrlEncoded
    @POST("get_account")
    fun getAccount(@FieldMap hashMap: HashMap<String, String>): Call<DataModel>

    @FormUrlEncoded
    @POST("sign-up")
    fun signUp(@FieldMap hashMap: HashMap<String, String>): Call<DataModel>
}
