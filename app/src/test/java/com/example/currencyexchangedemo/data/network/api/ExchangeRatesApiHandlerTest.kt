package com.example.currencyexchangedemo.data.network.api

import com.example.currencyexchangedemo.data.network.dto.LatestRatesResponse
import com.example.currencyexchangedemo.domain.model.ExchangeRate
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeRatesApiHandlerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var apiHandler: ExchangeRatesApiHandler

    @RelaxedMockK
    private lateinit var exchangeRatesApi: OpenExchangeRatesApi

    @Before
    fun setUp() {
        apiHandler = ExchangeRatesApiHandler(exchangeRatesApi)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `test getLatestRates success`() = runBlocking {
        val mockResponse = mockk<Response<LatestRatesResponse>>()
        coEvery { exchangeRatesApi.getLatestRates() } returns mockResponse
        coEvery { mockResponse.isSuccessful } returns true
        coEvery { mockResponse.body() } returns LatestRatesResponse(
            "",
            "",
            1,
            "USD",
            mapOf("INR" to 86.521596)
        )

        val answer = apiHandler.getLatestRates(1)
        assertThat(answer).isEqualTo(listOf(ExchangeRate("INR", "", 86.521596, "USD", 1)))
        coVerify(exactly = 1) { exchangeRatesApi.getLatestRates() }
    }

    @Test
    fun `test getLatestRates failure`() = runBlocking {
        val mockResponse = mockk<Response<LatestRatesResponse>>()
        coEvery { exchangeRatesApi.getLatestRates() } returns mockResponse
        coEvery { mockResponse.isSuccessful } returns false

        val answer = apiHandler.getLatestRates(1)
        assertThat(answer).isEqualTo(emptyList<ExchangeRate>())
        coVerify(exactly = 1) { exchangeRatesApi.getLatestRates() }
    }

    @Test
    fun `test getCurrencies success`() = runBlocking {
        val mockResponse = mockk<Response<Map<String, String>>>()
        coEvery { exchangeRatesApi.getCurrencies() } returns mockResponse
        coEvery { mockResponse.isSuccessful } returns true
        coEvery { mockResponse.body() } returns mapOf("INR" to "Indian Rupee")

        val answer = apiHandler.getCurrencies()
        assertThat(answer).isEqualTo(mapOf("INR" to "Indian Rupee"))
        coVerify(exactly = 1) { exchangeRatesApi.getCurrencies() }
    }

    @Test
    fun `test getCurrencies failure`() = runBlocking {
        val mockResponse = mockk<Response<Map<String, String>>>()
        coEvery { exchangeRatesApi.getCurrencies() } returns mockResponse
        coEvery { mockResponse.isSuccessful } returns false

        val answer = apiHandler.getCurrencies()
        assertThat(answer).isEqualTo(emptyMap<String, String>())
        coVerify(exactly = 1) { exchangeRatesApi.getCurrencies() }
    }
}