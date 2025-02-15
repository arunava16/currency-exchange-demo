package com.example.currencyexchangedemo.presentation.model

data class CurrencyModel(
    val code: String,
    val name: String,
    val rate: Double,
    val amount: Double
)
