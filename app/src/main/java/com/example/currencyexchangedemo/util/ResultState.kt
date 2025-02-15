package com.example.currencyexchangedemo.util

sealed interface ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>
    data class Error<T>(val error: Throwable, val data: T? = null) : ResultState<T>
    data object Loading : ResultState<Nothing>
}
