package com.mredrock.cyxbs.store.utils

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Token(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
) : Serializable {
    data class Data(
        @SerializedName("refreshToken")
        val refreshToken: String,
        @SerializedName("token")
        val token: String
    ) : Serializable
}