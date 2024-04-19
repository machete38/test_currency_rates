package com.machete3845.test_currency_rates

import com.google.gson.annotations.SerializedName



data class InitDataModel(
    @SerializedName("Valute")
    var valute : LinkedHashMap<String, Currency>
)

data class Currency (
    @SerializedName("ID")
    var id: String,
    @SerializedName("NumCode")
    var numCode: String,
    @SerializedName("CharCode")
    var charCode: String,
    @SerializedName("Nominal")
    var nominal: Int,
    @SerializedName("Name")
    var name: String,
    @SerializedName("Value")
    var valute: Double,
    )