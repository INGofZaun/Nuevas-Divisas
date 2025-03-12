package com.example.nuevasdivisas.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.nuevasdivisas.data.AppDatabase
import com.example.nuevasdivisas.data.ExchangeRate
import com.example.nuevasdivisas.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class FetchExchangeRateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("WORKER_STATUS", "Ejecutando FetchExchangeRateWorker...")

        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.exchangeRateDao()

        return try {
            val response = RetrofitClient.api.getExchangeRates()
            Log.d("API_RESPONSE", "Código HTTP: ${response.code()}")

            if (response.isSuccessful) {
                val data = response.body()
                Log.d("API_RESPONSE", "Respuesta de la API: $data")

                if (data?.rates != null) { // ✅ Verifica que `data` y `rates` no sean nulos
                    withContext(Dispatchers.IO) {
                        val dateValue = data.date ?: "Fecha no disponible" // ✅ Evita valores nulos
                        data.rates.entries.forEach { (currency, rate) ->
                            dao.insert(ExchangeRate(currency = currency, rate = rate, date = dateValue))
                        }
                    }
                    Log.d("WORKER_STATUS", "Datos insertados en Room correctamente.")
                    Result.success()
                } else {
                    Log.e("WORKER_ERROR", "La respuesta de la API no contiene tasas de cambio.")
                    Result.retry()
                }
            } else {
                Log.e("WORKER_ERROR", "Error en la respuesta de la API: Código ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("WORKER_ERROR", "Excepción en FetchExchangeRateWorker", e)
            Result.retry()
        }
    }
}

// ✅ Función para programar el `Worker` cada hora
fun scheduleWork(context: Context) {
    val workManager = WorkManager.getInstance(context)
    val request = PeriodicWorkRequestBuilder<FetchExchangeRateWorker>(1, TimeUnit.HOURS)
        .setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        )
        .build()

    workManager.enqueueUniquePeriodicWork(
        "FetchExchangeRateWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}
