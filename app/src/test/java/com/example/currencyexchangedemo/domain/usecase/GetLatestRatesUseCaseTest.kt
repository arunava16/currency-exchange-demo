package com.example.currencyexchangedemo.domain.usecase

import app.cash.turbine.test
import com.example.currencyexchangedemo.domain.model.ExchangeRate
import com.example.currencyexchangedemo.domain.repository.ExchangeRatesRepository
import com.example.currencyexchangedemo.util.ResultState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetLatestRatesUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var useCase: GetLatestRatesUseCase

    @RelaxedMockK
    private lateinit var repository: ExchangeRatesRepository

    @Before
    fun setUp() {
        useCase = GetLatestRatesUseCase(repository)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `test Success state`() = runBlocking {
        coEvery { repository.getLatestRates() } returns dummyList
        useCase.invoke().test {
            val first = awaitItem()
            assertThat(first).isEqualTo(ResultState.Loading)
            val second = awaitItem()
            assertThat(second).isEqualTo(ResultState.Success(dummyList))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test Error state when empty data returned`() = runBlocking {
        coEvery { repository.getLatestRates() } returns emptyList()
        useCase.invoke().test {
            val first = awaitItem()
            assertThat(first).isEqualTo(ResultState.Loading)
            val second = awaitItem()
            assertThat(second).isInstanceOf(ResultState.Error::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test Success state when any exception occurs during operation and cached data is fetched`() =
        runBlocking {
            coEvery { repository.getLatestRates() } throws Exception()
            coEvery { repository.getCachedRates() } returns dummyList
            useCase.invoke().test {
                val first = awaitItem()
                assertThat(first).isEqualTo(ResultState.Loading)
                val second = awaitItem()
                assertThat(second).isEqualTo(ResultState.Success(dummyList))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `test Error state when any exception occurs during operation and no cached data is present`() =
        runBlocking {
            coEvery { repository.getLatestRates() } throws Exception()
            coEvery { repository.getCachedRates() } returns emptyList()
            useCase.invoke().test {
                val first = awaitItem()
                assertThat(first).isEqualTo(ResultState.Loading)
                val second = awaitItem()
                assertThat(second).isInstanceOf(ResultState.Error::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private val dummyList = listOf(
        ExchangeRate("INR", "Indian Rupee", 86.521596, "USD", 1738047600),
        ExchangeRate("USD", "United States Dollar", 1.0, "USD", 1738047600),
    )
}