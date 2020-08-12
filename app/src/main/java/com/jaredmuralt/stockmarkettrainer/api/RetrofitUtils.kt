package com.jaredmuralt.stockmarkettrainer.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitUtils {
    private val client = OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://finnhub.io/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}