package com.gems.toplan.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitHolder {
    private const val BASEURL = "https://beta.mrdekk.ru/todo"
    private const val TOKEN = "Isilme"

    private val json = Json { ignoreUnknownKeys = true }

    private val tokenInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $TOKEN")
            .build()
        chain.proceed(request)
    }

    private val certificatePiner = CertificatePinner.Builder().add(
        "hive.mrdekk.ru",
        "sha256/8200cd21cca0502764cc00b530f936961a141cc0a43c95b9c960bec192aa8454"
    ).build()

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(
            certificatePiner
        )
        .addInterceptor(tokenInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()


    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    val api = retrofit.create(TodoApi::class.java)
}
