package com.gems.toplan.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHolder {
    private val tokenInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer Dono Hujanazarova")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(tokenInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://hive.mrdekk.ru")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val TodoApi = retrofit.create(TodoApi::class.java)
}
