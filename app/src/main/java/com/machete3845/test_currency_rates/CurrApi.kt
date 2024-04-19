package com.machete3845.test_currency_rates

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET


interface CurrApi {
    @GET("/daily_json.js")
    suspend fun readJson(): InitDataModel
}