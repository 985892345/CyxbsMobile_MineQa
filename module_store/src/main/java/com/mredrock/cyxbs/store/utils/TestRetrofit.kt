package com.mredrock.cyxbs.store.utils

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.store.network.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.Serializable

object TestRetrofit {

    private var mToken = "eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDIwMjE0OTg4In0sIkRvbW" +
            "FpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiJmYWQ1ODAwMmMwMWY3MjE3ZjQxM2ZkYWI1MjdjNzM5MmQxMDZmM" +
            "WI3IiwiZXhwIjoiNzM5OTg0Njc2NCIsImlhdCI6IjE2MzAwMzU1MTEiLCJzdWIiOiJ3ZWIifQ==.haMwF9e" +
            "oUjMh/jN0EMmuLgh6xK/IWIkwV8THji3HvbJdFuXho9+QxcieymUXL0gjzummGmQwEFakzr/S42gzlFkSyO" +
            "PZ1h2IM9CZhgH7l5kkkqf538dIMlErYjv99QepfgGGV6jBBi8v5sC8oUcupscYZTFHzhLYNf6G9XIeBbj8J" +
            "PzaBYYYugcN/xrPzN6wUbtFAfsnDHxFml3nQGsw0V8zvBH9fgFE+UgZ7hfffwsNdgSCt2GAk90AKT3oFeUs" +
            "Z+h5J5HWxzHhwKNipnVbgzM7Nou7B8qRXYZ8g0bFJB/uTRaWm7Cl3OGkHMluzd5cEX5H1JAApRVBxP0bWx0" +
            "fDA=="

    private val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor(Retry(1))
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://be-dev.redrock.cqupt.edu.cn")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    val testRetrofit = provideRetrofit()

    private fun provideRetrofit(): ApiService {
        return retrofit.build().create(ApiService::class.java)
    }

    class Retry(
        private val maxRetry: Int
    ) : Interceptor {

        private var retryNum = 0

        override fun intercept(chain: Interceptor.Chain): Response {
            if (mToken.isEmpty()) {
                getNewToken()
            }
            val request = chain.request()
            val build = request
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $mToken"
                )
                .build()
            var response = chain.proceed(build)
            while (!response.isSuccessful && retryNum < maxRetry) {
                retryNum++
                getNewToken()
                val build2 = request
                    .newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer $mToken"
                    )
                    .build()
                response.close()
                response = chain.proceed(build2)
            }
            retryNum = 0
            return response
        }
    }

    private fun getNewToken() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://be-dev.redrock.cqupt.edu.cn")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(TokenApiService::class.java)
        val call = api.getToken(TokenBody("2020214988", "671597"))
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val token = response.body()?.data?.token
                if (token != null) {
                    mToken = token
                }
            }
        }catch (e: Exception) {
        }
    }

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

    data class TokenBody(
        @SerializedName("stuNum")
        val stuNum: String,
        @SerializedName("idNum")
        val idNum: String
    ) : Serializable

    interface TokenApiService {
        @POST("/magipoke/token")
        fun getToken(
            @Body
            tokenBody: TokenBody
        ): Call<Token>
    }
}

