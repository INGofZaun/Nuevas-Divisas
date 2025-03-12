package com.example.nuevasdivisas.network

import com.google.gson.annotations.SerializedName

data class ExchangeRateResponse(
    @SerializedName("base") val base: String,
    @SerializedName("date") val date: String,
    @SerializedName("rates") val rates: Map<String, Double>?
)
