package com.jaredmuralt.stockmarkettrainer.api

import com.jaredmuralt.stockmarkettrainer.model.StockData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockAPIEndpoints {

    @GET("stock/candle")
    fun getStockHistory(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("token") key: String
    ): Call<StockData>
}