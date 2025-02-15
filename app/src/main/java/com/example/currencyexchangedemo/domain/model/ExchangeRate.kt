package com.example.currencyexchangedemo.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rate")
data class ExchangeRate(
    @PrimaryKey val code: String,
    var name: String,
    val value: Double,
    val base: String,
    val timestamp: Long
)
