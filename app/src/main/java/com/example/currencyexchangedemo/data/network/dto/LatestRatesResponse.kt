package com.example.currencyexchangedemo.data.network.dto

data class LatestRatesResponse(
    val disclaimer: String?,
    val license: String?,
    val timestamp: Long?,
    val base: String?,
    val rates: Map<String, Double>?
)
