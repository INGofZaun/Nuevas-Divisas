package com.example.nuevasdivisas.data

import android.database.Cursor
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: ExchangeRate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ExchangeRate>): List<Long>

    @Query("SELECT * FROM exchange_rates ORDER BY date DESC")
    fun getAllRates(): Flow<List<ExchangeRate>>

    // ✅ Obtener todas las tasas de cambio en un `Cursor`
    @Query("SELECT * FROM exchange_rates ORDER BY date DESC")
    fun getAllRatesCursor(): Cursor

    // ✅ Obtener tasas de cambio filtradas por moneda en un `Cursor`
    @Query("SELECT * FROM exchange_rates WHERE currency = :currency ORDER BY date DESC")
    fun getRatesCursor(currency: String): Cursor

    // ✅ Nuevo método para que el `ContentProvider` recupere todas las tasas
    @Query("SELECT * FROM exchange_rates ORDER BY date DESC")
    fun getAllRatesForProvider(): List<ExchangeRate>

    // ✅ Nuevo método para obtener tasas filtradas
    @Query("SELECT * FROM exchange_rates WHERE currency = :currency ORDER BY date DESC")
    fun getRatesForProvider(currency: String): List<ExchangeRate>
}
