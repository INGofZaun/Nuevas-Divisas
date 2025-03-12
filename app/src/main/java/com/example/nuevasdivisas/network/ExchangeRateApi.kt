package com.example.nuevasdivisas.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

interface ExchangeRateApi {
    @GET("v6/f26e2020534c352ce224942f/latest/USD")
    suspend fun getExchangeRates(): Response<ApiResponse>
}

data class ApiResponse(
    @SerializedName("conversion_rates") val rates: Map<String, Double>?, // ✅ Corregido
    @SerializedName("time_last_update_utc") val date: String? // ✅ Corregido
)
