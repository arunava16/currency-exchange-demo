package com.example.currencyexchangedemo.data.repository

import com.example.currencyexchangedemo.data.local.dao.ExchangeRatesDao
import com.example.currencyexchangedemo.data.network.api.ExchangeRatesApiHandler
import com.example.currencyexchangedemo.domain.model.ExchangeRate
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
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
class ExchangeRatesRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var repository: ExchangeRatesRepositoryImpl

    @RelaxedMockK
    private lateinit var exchangeRatesApiHandler: ExchangeRatesApiHandler

    @RelaxedMockK
    private lateinit var exchangeRatesDao: ExchangeRatesDao

    @Before
    fun setUp() {
        repository = ExchangeRatesRepositoryImpl(exchangeRatesApiHandler, exchangeRatesDao)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `test getLatestRates - first time launch`() = runBlocking {
        coEvery { exchangeRatesDao.getFirstExchangeRate() } returns null
        coEvery { exchangeRatesApiHandler.getLatestRates(any()) } returns listOf(
            ExchangeRate(
                "INR",
                "",
                86.521596,
                "USD",
                1738047600
            )
        )
        coEvery { exchangeRatesApiHandler.getCurrencies() } returns mapOf("INR" to "Indian Rupee")

        val list = listOf(
            ExchangeRate(
                "INR",
                "Indian Rupee",
                86.521596,
                "USD",
                1738047600
            )
        )
        coEvery { exchangeRatesDao.getExchangeRates() } returns list

        val answer = repository.getLatestRates()

        assertThat(answer).isEqualTo(list)
        coVerify(exactly = 1) { exchangeRatesDao.getFirstExchangeRate() }
        coVerify(exactly = 1) { exchangeRatesApiHandler.getLatestRates(any()) }
        coVerify(exactly = 1) { exchangeRatesApiHandler.getCurrencies() }
        coVerify(exactly = 1) { exchangeRatesDao.insertExchangeRates(list) }
        coVerify(exactly = 1) { exchangeRatesDao.getExchangeRates() }
    }

    @Test
    fun `test getLatestRates - refresh data after 30 min period`() = runBlocking {
        coEvery { exchangeRatesDao.getFirstExchangeRate() } returns ExchangeRate(
            "USD",
            "United States Dollar",
            1.0,
            "USD",
            1738047600
        )
        coEvery { exchangeRatesApiHandler.getLatestRates(any()) } returns listOf(
            ExchangeRate(
                "INR",
                "",
                86.521596,
                "USD",
                1738047600
            )
        )
        coEvery { exchangeRatesApiHandler.getCurrencies() } returns mapOf("INR" to "Indian Rupee")

        val list = listOf(
            ExchangeRate(
                "INR",
                "Indian Rupee",
                86.521596,
                "USD",
                1738047600
            )
        )
        coEvery { exchangeRatesDao.getExchangeRates() } returns list

        val answer = repository.getLatestRates()

        assertThat(answer).isEqualTo(list)
        coVerify(exactly = 1) { exchangeRatesDao.getFirstExchangeRate() }
        coVerify(exactly = 1) { exchangeRatesApiHandler.getLatestRates(any()) }
        coVerify(exactly = 1) { exchangeRatesApiHandler.getCurrencies() }
        coVerify(exactly = 1) { exchangeRatesDao.insertExchangeRates(list) }
        coVerify(exactly = 1) { exchangeRatesDao.getExchangeRates() }
    }
}