package com.example.nuevasdivisas.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import androidx.room.Room
import com.example.nuevasdivisas.data.AppDatabase
import com.example.nuevasdivisas.data.ExchangeRate
import com.example.nuevasdivisas.data.ExchangeRateDao

class ExchangeRateProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.example.nuevasdivisas.provider"
        private const val PATH_EXCHANGE_RATES = "exchange_rates"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_EXCHANGE_RATES")

        private const val CODE_ALL_EXCHANGE_RATES = 1
        private const val CODE_SINGLE_EXCHANGE_RATE = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_EXCHANGE_RATES, CODE_ALL_EXCHANGE_RATES)
            addURI(AUTHORITY, "$PATH_EXCHANGE_RATES/*", CODE_SINGLE_EXCHANGE_RATE)
        }
    }

    private lateinit var exchangeRateDao: ExchangeRateDao

    override fun onCreate(): Boolean {
        Log.d("ExchangeRateProvider", "🔥 ContentProvider inicializado")
        val context = context ?: return false
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "exchange_rate_db"
        ).allowMainThreadQueries() // ⚠️ Solo para debugging
            .build()
        exchangeRateDao = db.exchangeRateDao()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        Log.d("ExchangeRateProvider", "📥 Recibida consulta con URI: $uri desde ${callingPackage ?: "desconocido"}")

        return try {
            when (uriMatcher.match(uri)) {
                CODE_ALL_EXCHANGE_RATES -> {
                    Log.d("ExchangeRateProvider", "📡 Consultando todas las tasas de cambio")
                    val rates = exchangeRateDao.getAllRatesForProvider()
                    Log.d("ExchangeRateProvider", "📄 Datos obtenidos del ContentProvider: $rates")
                    convertToCursor(rates)
                }
                CODE_SINGLE_EXCHANGE_RATE -> {
                    val currency = uri.lastPathSegment ?: return null
                    Log.d("ExchangeRateProvider", "📡 Consultando tasas para: $currency")
                    val rates = exchangeRateDao.getRatesForProvider(currency)
                    Log.d("ExchangeRateProvider", "📄 Datos obtenidos del ContentProvider para $currency: $rates")
                    convertToCursor(rates)
                }
                else -> {
                    Log.e("ExchangeRateProvider", "🚨 URI desconocida: $uri")
                    null
                }
            }
        } catch (e: SQLiteException) {
            Log.e("ExchangeRateProvider", "🚨 Error en query: ${e.message}")
            null
        }
    }




    override fun getType(uri: Uri): String? = "vnd.android.cursor.dir/$AUTHORITY.$PATH_EXCHANGE_RATES"

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

    private fun convertToCursor(rates: List<ExchangeRate>): Cursor {
        val cursor = MatrixCursor(arrayOf("id", "currency", "rate", "date"))
        rates.forEach { rate ->
            cursor.addRow(arrayOf(rate.id, rate.currency, rate.rate, rate.date))
        }
        Log.d("ExchangeRateProvider", "📄 Cursor generado con ${rates.size} registros")
        return cursor
    }
}
