package com.example.currencyexchangedemo.domain.usecase

import com.example.currencyexchangedemo.domain.model.ExchangeRate
import com.example.currencyexchangedemo.domain.repository.ExchangeRatesRepository
import com.example.currencyexchangedemo.util.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetLatestRatesUseCase @Inject constructor(
    private val exchangeRatesRepositoryImpl: ExchangeRatesRepository
) {

    operator fun invoke(): Flow<ResultState<List<ExchangeRate>>> = flow {
        emit(ResultState.Loading)
        val exchangeRates = exchangeRatesRepositoryImpl.getLatestRates()
        if (exchangeRates.isNotEmpty()) {
            emit(ResultState.Success(exchangeRates))
        } else {
            emit(ResultState.Error(Throwable("No data found.")))
        }
    }.catch { ex ->
        val cachedRates = exchangeRatesRepositoryImpl.getCachedRates()
        if (cachedRates.isNotEmpty()) {
            emit(ResultState.Success(cachedRates))
        } else {
            emit(ResultState.Error(ex))
        }
    }.flowOn(Dispatchers.IO)
}
