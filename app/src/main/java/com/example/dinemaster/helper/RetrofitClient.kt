package com.example.dinemaster.helper

import com.example.dinemaster.MyApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL_MAIN =
        "https://dinemaster.codecrafterinfotech.online/WS/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor()) // no context
            .addInterceptor(AuthInterceptor(MyApplication.instance))
            .build()

    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MAIN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}


