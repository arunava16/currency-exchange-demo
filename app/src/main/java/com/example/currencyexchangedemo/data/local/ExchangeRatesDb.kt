package com.example.currencyexchangedemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyexchangedemo.data.local.dao.ExchangeRatesDao
import com.example.currencyexchangedemo.domain.model.ExchangeRate

@Database(entities = [ExchangeRate::class], version = 1)
abstract class ExchangeRatesDb : RoomDatabase() {

    abstract fun exchangeRatesDao(): ExchangeRatesDao
}
