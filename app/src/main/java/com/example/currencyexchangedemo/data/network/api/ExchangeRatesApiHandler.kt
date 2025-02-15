package com.example.currencyexchangedemo.data.network.api

import com.example.currencyexchangedemo.domain.model.ExchangeRate
import javax.inject.Inject

class ExchangeRatesApiHandler @Inject constructor(
    private val exchangeRatesApi: OpenExchangeRatesApi
) {

    suspend fun getLatestRates(timestamp: Long): List<ExchangeRate> {
        val exchangeRatesResponse = exchangeRatesApi.getLatestRates()
        val exchangeRates = mutableListOf<ExchangeRate>()
        if (exchangeRatesResponse.isSuccessful) {
            val response = exchangeRatesResponse.body()
            response?.rates?.onEach {
                exchangeRates.add(
                    ExchangeRate(
                        code = it.key,
                        name = "",
                        value = it.value,
                        base = response.base.orEmpty(),
                        timestamp = response.timestamp ?: timestamp
                    )
                )
            }
        }
        return exchangeRates
    }

    suspend fun getCurrencies(): Map<String, String> {
        val currenciesResponse = exchangeRatesApi.getCurrencies()
        return if (currenciesResponse.isSuccessful) {
            currenciesResponse.body().orEmpty()
        } else {
            emptyMap()
        }
    }
}