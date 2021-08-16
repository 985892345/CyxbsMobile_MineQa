package com.mredrock.cyxbs.store.utils

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

    private var mToken = ""
    val testRetrofit = provideRetrofit()

    private fun provideRetrofit(): ApiService {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(Retry(1))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://be-dev.redrock.cqupt.edu.cn")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(ApiService::class.java)
    }

    class Retry(
        private val maxRetry: Int
    ) : Interceptor {

        private var retryNum = 0

        override fun intercept(chain: Interceptor.Chain): Response {
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

