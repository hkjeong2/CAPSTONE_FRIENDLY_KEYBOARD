package com.example.friendlykeyboard.retrofit_util

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    /**
     * IP 주소: http://52.78.26.88:5000/
     */
    private const val BASE_URL = "http://52.78.26.88:5000/"

    private fun getInstance(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getApiService(): RetrofitInterface = getInstance().create(RetrofitInterface::class.java)
}