package com.example.nuevasdivisas.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.nuevasdivisas.data.AppDatabase
import com.example.nuevasdivisas.data.ExchangeRate
import com.example.nuevasdivisas.network.RetrofitClient
import kotlinx.coroutines.runBlocking

class FetchExchangeRateWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("FetchExchangeRateWorker", "🚀 Iniciando actualización de tasas de cambio")

        val database = AppDatabase.getInstance(applicationContext)
        val exchangeRateDao = database.exchangeRateDao()

        return try {
            // Usamos runBlocking para ejecutar la función suspendida dentro de WorkManager
            runBlocking {
                val response = RetrofitClient.api.getExchangeRates()

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse?.rates != null) {
                        Log.d("FetchExchangeRateWorker", "📥 Datos obtenidos de la API")

                        val exchangeRates = apiResponse.rates.map { (currency, rate) ->
                            ExchangeRate(currency = currency, rate = rate, date = apiResponse.date ?: "")
                        }

                        exchangeRateDao.insertAll(exchangeRates)
                        Log.d("FetchExchangeRateWorker", "✅ ${exchangeRates.size} tasas guardadas en la DB")
                    } else {
                        Log.w("FetchExchangeRateWorker", "⚠️ Respuesta de API vacía")
                    }
                } else {
                    Log.e("FetchExchangeRateWorker", "❌ Error en la API: ${response.code()}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("FetchExchangeRateWorker", "🚨 Error en la descarga de datos: ${e.message}")
            Result.failure()
        }
    }
}
