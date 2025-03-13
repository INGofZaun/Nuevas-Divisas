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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nuevasdivisas.data.AppDatabase
import com.example.nuevasdivisas.data.ExchangeRate
import com.example.nuevasdivisas.worker.FetchExchangeRateWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var rates by remember { mutableStateOf(emptyList<ExchangeRate>()) }

            LaunchedEffect(Unit) {
                cargarDatos { rates = it }
            }

            MostrarTasasDeCambio(rates)
        }

        // 🔥 **Ejecutar el Worker manualmente al iniciar la app**
        Log.d("MainActivity", "🚀 Ejecutando Worker para actualizar tasas")
        val workRequest = OneTimeWorkRequestBuilder<FetchExchangeRateWorker>().build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun cargarDatos(callback: (List<ExchangeRate>) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val exchangeRateDao = AppDatabase.getInstance(applicationContext).exchangeRateDao()
            val rates = exchangeRateDao.getAllRatesForProvider()

            Log.d("MainActivity", "📊 Datos obtenidos de la DB: ${rates.size} registros")

            withContext(Dispatchers.Main) {
                callback(rates)
            }
        }
    }
}

@Composable
fun MostrarTasasDeCambio(rates: List<ExchangeRate>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Tasas de Cambio", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(rates) { rate ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Moneda: ${rate.currency}")
                        Text(text = "Tasa: ${rate.rate}")
                        Text(text = "Fecha: ${rate.date}")
                    }
                }
            }
        }
    }
}
