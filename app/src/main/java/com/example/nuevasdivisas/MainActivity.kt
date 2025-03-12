package com.example.nuevasdivisas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.nuevasdivisas.data.AppDatabase
import com.example.nuevasdivisas.data.ExchangeRate
import com.example.nuevasdivisas.worker.FetchExchangeRateWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MAIN_ACTIVITY", "Iniciando MainActivity...")

        // ✅ Programar WorkManager al iniciar la aplicación
        scheduleWorkManager()

        // ✅ Ejecutar inmediatamente el worker para pruebas
        val workManager = WorkManager.getInstance(this)
        val request = OneTimeWorkRequestBuilder<FetchExchangeRateWorker>().build()
        workManager.enqueue(request)

        Log.d("MAIN_ACTIVITY", "Worker programado y ejecutado una vez manualmente.")

        setContent {
            ExchangeRateListScreen()
        }
    }

    // ✅ Función para programar WorkManager cada hora
    private fun scheduleWorkManager() {
        val workManager = WorkManager.getInstance(this)
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
        Log.d("MAIN_ACTIVITY", "WorkManager programado para cada hora.")
    }
}

@Composable
fun ExchangeRateListScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val rates = remember { mutableStateListOf<ExchangeRate>() }

    LaunchedEffect(Unit) {
        db.exchangeRateDao().getAllRates().collect { newRates ->
            rates.clear()
            rates.addAll(newRates)
            Log.d("ROOM_DB", "Datos recibidos de Room: $newRates")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (rates.isEmpty()) {
            // ✅ Mostrar mensaje si la lista está vacía
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Cargando datos...", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(rates) { rate ->
                    Text(text = "${rate.currency}: ${rate.rate}", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
