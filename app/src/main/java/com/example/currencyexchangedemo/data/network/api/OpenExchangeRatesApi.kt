package com.example.currencyexchangedemo.data.network.api

import com.example.currencyexchangedemo.data.network.dto.LatestRatesResponse
import com.example.currencyexchangedemo.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeRatesApi {

    @GET("latest.json")
    suspend fun getLatestRates(@Query("app_id") appId: String = Constants.APP_ID): Response<LatestRatesResponse>

    @GET("currencies.json")
    suspend fun getCurrencies(@Query("app_id") appId: String = Constants.APP_ID): Response<Map<String, String>>
}
