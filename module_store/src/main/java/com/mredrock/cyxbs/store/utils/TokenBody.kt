package com.mredrock.cyxbs.store.utils

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TokenBody(
    @SerializedName("stuNum")
    val stuNum: String,
    @SerializedName("idNum")
    val idNum: String
) : Serializable