package com.example.currencyexchangedemo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.currencyexchangedemo.domain.model.ExchangeRate

@Dao
interface ExchangeRatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRate>)

    @Query("SELECT * FROM exchange_rate")
    suspend fun getExchangeRates(): List<ExchangeRate>?

    @Query("SELECT * FROM exchange_rate LIMIT 1")
    suspend fun getFirstExchangeRate(): ExchangeRate?
}
