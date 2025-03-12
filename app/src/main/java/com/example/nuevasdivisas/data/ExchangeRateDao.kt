package com.example.nuevasdivisas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: ExchangeRate): Long  // ✅ Debe devolver Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ExchangeRate>): List<Long>  // ✅ List<Long> para múltiples inserciones

    @Query("SELECT * FROM exchange_rates ORDER BY date DESC")
    fun getAllRates(): Flow<List<ExchangeRate>>
}
