package com.example.currencyexchangedemo.domain.repository

import com.example.currencyexchangedemo.domain.model.ExchangeRate

interface ExchangeRatesRepository {

    suspend fun getLatestRates(): List<ExchangeRate>

    suspend fun getCachedRates(): List<ExchangeRate>
}
