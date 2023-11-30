package com.blueduck.crud

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://shubham-mogarkar-demo2.onrender.com/" // Replace with your base URL

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Replace with your desired converter factory
            .build()
    }

    val studentApiService: StudentApiService by lazy {
        retrofit.create(StudentApiService::class.java)
    }
}
