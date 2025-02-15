package com.example.currencyexchangedemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchangedemo.domain.model.ExchangeRate
import com.example.currencyexchangedemo.domain.usecase.GetLatestRatesUseCase
import com.example.currencyexchangedemo.presentation.model.CurrencyModel
import com.example.currencyexchangedemo.presentation.state.ViewState
import com.example.currencyexchangedemo.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLatestRatesUseCase: GetLatestRatesUseCase
) : ViewModel() {

    private val _mainViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState())
    val mainViewState: StateFlow<ViewState> = _mainViewState.asStateFlow()

    private var exchangeRates = listOf<ExchangeRate>()

    init {
        viewModelScope.launch {
            getLatestRatesUseCase().collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _mainViewState.emit(ViewState(isLoading = true))
                    }

                    is ResultState.Success -> {
                        exchangeRates = result.data
                        val countryCodes = exchangeRates.map { "${it.name} (${it.code})" }
                        _mainViewState.emit(
                            ViewState(
                                isLoading = false,
                                countryCodes = countryCodes
                            )
                        )
                    }

                    is ResultState.Error -> {
                        result.error.printStackTrace()
                        _mainViewState.emit(ViewState(errorMsg = "Something went wrong. Please try again later"))
                    }
                }
            }
        }
    }

    fun updateBaseCurrency(selectedCurrency: String) {
        _mainViewState.update { viewState ->
            viewState.copy(
                base = selectedCurrency.substring(
                    selectedCurrency.indexOf("(") + 1,
                    selectedCurrency.indexOf(")")
                ),
                rates = emptyList(),
                amount = null
            )
        }
    }

    fun updateAmountToConvert(input: String?) {
        _mainViewState.update { viewState ->
            val amount = input?.toDoubleOrNull()
            viewState.copy(
                rates = if (amount != null) {
                    val newRate = exchangeRates.first { it.code == viewState.base }.value
                    exchangeRates.map {
                        CurrencyModel(
                            code = it.code,
                            name = it.name,
                            rate = it.value / newRate,
                            amount = amount
                        )
                    }
                } else {
                    emptyList()
                },
                amount = input
            )
        }
    }
}
