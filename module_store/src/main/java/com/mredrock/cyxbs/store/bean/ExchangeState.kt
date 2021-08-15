package com.mredrock.cyxbs.store.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExchangeState(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
) : Serializable {
    data class Data(
        @SerializedName("amount")
        val amount: Int,
        @SerializedName("msg")
        val msg: String
    ) : Serializable
}