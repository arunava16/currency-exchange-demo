package com.example.currencyexchangedemo.presentation.state

import com.example.currencyexchangedemo.presentation.model.CurrencyModel
import com.example.currencyexchangedemo.util.Constants

data class ViewState(
    var isLoading: Boolean = false,
    var rates: List<CurrencyModel> = emptyList(),
    var countryCodes: List<String> = emptyList(),
    var base: String = Constants.DEFAULT_BASE,
    var amount: String? = null,
    var errorMsg: String = ""
)
