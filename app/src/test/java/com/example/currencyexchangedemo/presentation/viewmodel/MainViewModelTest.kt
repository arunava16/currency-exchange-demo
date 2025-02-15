package com.example.currencyexchangedemo.presentation.viewmodel

import app.cash.turbine.test
import com.example.currencyexchangedemo.domain.model.ExchangeRate
import com.example.currencyexchangedemo.domain.usecase.GetLatestRatesUseCase
import com.example.currencyexchangedemo.presentation.model.CurrencyModel
import com.example.currencyexchangedemo.presentation.state.ViewState
import com.example.currencyexchangedemo.util.ResultState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var mainViewModel: MainViewModel

    @RelaxedMockK
    private lateinit var getLatestRatesUseCase: GetLatestRatesUseCase

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(getLatestRatesUseCase)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `test getLatestRates for loading ViewState`() = runBlocking {
        coEvery { getLatestRatesUseCase() } returns flowOf(ResultState.Loading)

        val job = launch {
            mainViewModel.mainViewState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(ViewState(isLoading = true))
                cancelAndIgnoreRemainingEvents()
            }
        }

        mainViewModel = MainViewModel(getLatestRatesUseCase)
        job.join()
        job.cancel()
    }

    @Test
    fun `test getLatestRates for Success ViewState`() = runBlocking {
        val list = dummyList
        coEvery { getLatestRatesUseCase() } returns flowOf(ResultState.Success(list))

        val job = launch {
            mainViewModel.mainViewState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(
                    ViewState(
                        countryCodes = listOf(
                            "Indian Rupee (INR)",
                            "United States Dollar (USD)"
                        )
                    )
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

        mainViewModel = MainViewModel(getLatestRatesUseCase)
        job.join()
        job.cancel()
    }

    @Test
    fun `test getLatestRates for Error ViewState`() = runBlocking {
        coEvery { getLatestRatesUseCase() } returns flowOf(ResultState.Error(Throwable()))

        val job = launch {
            mainViewModel.mainViewState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(ViewState(errorMsg = "Something went wrong. Please try again later"))
                cancelAndIgnoreRemainingEvents()
            }
        }

        mainViewModel = MainViewModel(getLatestRatesUseCase)
        job.join()
        job.cancel()
    }

    @Test
    fun `test updateBaseCurrency`() = runBlocking {
        val list = dummyList
        coEvery { getLatestRatesUseCase() } returns flowOf(ResultState.Success(list))
        mainViewModel = MainViewModel(getLatestRatesUseCase)

        val job = launch {
            mainViewModel.mainViewState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(
                    ViewState(
                        base = "INR",
                        rates = emptyList(),
                        amount = null,
                        countryCodes = listOf("Indian Rupee (INR)", "United States Dollar (USD)")
                    )
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

        mainViewModel.updateBaseCurrency("Indian Rupee (INR)")
        job.join()
        job.cancel()
    }

    @Test
    fun `update updateAmountToConvert when entered amount is empty`() = runBlocking {
        val list = dummyList
        coEvery { getLatestRatesUseCase() } returns flowOf(ResultState.Success(list))
        mainViewModel = MainViewModel(getLatestRatesUseCase)

        val job = launch {
            mainViewModel.mainViewState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(
                    ViewState(
                        base = "USD",
                        rates = emptyList(),
                        amount = "",
                        countryCodes = listOf("Indian Rupee (INR)", "United States Dollar (USD)")
                    )
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

        mainViewModel.updateAmountToConvert("")
        job.join()
        job.cancel()
    }

    @Test
    fun `update updateAmountToConvert when entered amount is valid`() = runBlocking {
        val list = dummyList
        coEvery { getLatestRatesUseCase() } returns flowOf(ResultState.Success(list))
        mainViewModel = MainViewModel(getLatestRatesUseCase)

        val job = launch {
            mainViewModel.mainViewState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(
                    ViewState(
                        base = "USD",
                        rates = listOf(
                            CurrencyModel("INR", "Indian Rupee", 86.521596, 3.6),
                            CurrencyModel("USD", "United States Dollar", 1.0, 3.6)
                        ),
                        amount = "3.6",
                        countryCodes = listOf("Indian Rupee (INR)", "United States Dollar (USD)")
                    )
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

        mainViewModel.updateAmountToConvert("3.6")
        job.join()
        job.cancel()
    }

    private val dummyList = listOf(
        ExchangeRate("INR", "Indian Rupee", 86.521596, "USD", 1738047600),
        ExchangeRate("USD", "United States Dollar", 1.0, "USD", 1738047600),
    )
}
