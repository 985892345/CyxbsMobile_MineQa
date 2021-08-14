package com.mredrock.cyxbs.store.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductDetail(
        @SerializedName("data")
        val `data`: Data?,
        @SerializedName("info")
        val info: String,
        @SerializedName("status")
        val status: Int
) : Serializable {
    data class Data(
            @SerializedName("amount")
            val amount: Int,
            @SerializedName("description")
            val description: String,
            @SerializedName("life")
            val life: Int,
            @SerializedName("price")
            val price: Int,
            @SerializedName("title")
            val title: String,
            @SerializedName("type")
            val type: Int,
            @SerializedName("urls")
            val urls: List<String>
    ) : Serializable
}