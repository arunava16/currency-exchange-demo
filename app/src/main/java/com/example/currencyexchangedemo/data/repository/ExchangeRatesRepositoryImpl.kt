package com.example.currencyexchangedemo.data.repository

import com.example.currencyexchangedemo.data.local.dao.ExchangeRatesDao
import com.example.currencyexchangedemo.data.network.api.ExchangeRatesApiHandler
import com.example.currencyexchangedemo.domain.model.ExchangeRate
import com.example.currencyexchangedemo.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val exchangeRatesApiHandler: ExchangeRatesApiHandler,
    private val exchangeRatesDao: ExchangeRatesDao,
) : ExchangeRatesRepository {

    override suspend fun getLatestRates(): List<ExchangeRate> {
        val lastCallTimestamp = exchangeRatesDao.getFirstExchangeRate()?.timestamp
        val currentTimestamp = System.currentTimeMillis() / 1000
        if (lastCallTimestamp != null) {
            if (currentTimestamp - lastCallTimestamp > 1800) {
                fetchAndStore(currentTimestamp)
            }
        } else {
            fetchAndStore(currentTimestamp)
        }
        return getCachedRates()
    }

    override suspend fun getCachedRates(): List<ExchangeRate> {
        return exchangeRatesDao.getExchangeRates().orEmpty()
    }

    private suspend fun fetchAndStore(currentTimestamp: Long) {
        coroutineScope {
            val ratesListDeferred = async {
                exchangeRatesApiHandler.getLatestRates(currentTimestamp)
            }
            val currenciesDeferred = async { exchangeRatesApiHandler.getCurrencies() }
            awaitAll(ratesListDeferred, currenciesDeferred)
            val ratesList = ratesListDeferred.await()
            val currencies = currenciesDeferred.await()
            ratesList.onEach {
                it.name = currencies[it.code].orEmpty()
            }
            exchangeRatesDao.insertExchangeRates(ratesList)
        }
    }
}
