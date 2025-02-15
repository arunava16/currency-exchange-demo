package com.example.currencyexchangedemo.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.currencyexchangedemo.data.local.ExchangeRatesDb
import com.example.currencyexchangedemo.data.network.api.ExchangeRatesApiHandler
import com.example.currencyexchangedemo.data.network.api.OpenExchangeRatesApi
import com.example.currencyexchangedemo.data.repository.ExchangeRatesRepositoryImpl
import com.example.currencyexchangedemo.domain.repository.ExchangeRatesRepository
import com.example.currencyexchangedemo.domain.usecase.GetLatestRatesUseCase
import com.example.currencyexchangedemo.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAppContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideOpenExchangeRatesApi(retrofit: Retrofit): OpenExchangeRatesApi = retrofit.create()

    @Provides
    @Singleton
    fun provideExchangeRatesDb(context: Context): ExchangeRatesDb =
        Room.databaseBuilder(context, ExchangeRatesDb::class.java, Constants.DB_NAME)
            .build()

    @Provides
    @Singleton
    fun provideExchangeRatesApiHandler(openExchangeRatesApi: OpenExchangeRatesApi): ExchangeRatesApiHandler =
        ExchangeRatesApiHandler(openExchangeRatesApi)

    @Provides
    @Singleton
    fun provideExchangeRatesRepository(
        exchangeRatesApiHandler: ExchangeRatesApiHandler,
        exchangeRatesDb: ExchangeRatesDb
    ): ExchangeRatesRepository =
        ExchangeRatesRepositoryImpl(exchangeRatesApiHandler, exchangeRatesDb.exchangeRatesDao())

    @Provides
    @Singleton
    fun provideGetLatestRatesUseCase(exchangeRatesRepository: ExchangeRatesRepository): GetLatestRatesUseCase =
        GetLatestRatesUseCase(exchangeRatesRepository)
}
