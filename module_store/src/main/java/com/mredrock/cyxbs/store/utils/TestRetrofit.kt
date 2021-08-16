package com.mredrock.cyxbs.store.utils

import android.util.Log
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.store.network.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object TestRetrofit {

    private var mToken = ""
    val testRetrofit = provideRetrofit()

    private fun provideRetrofit(): ApiService {
        if (mToken.isEmpty()) {
            Log.println(Log.ASSERT,"123","(TestRetrofit.kt:21)-->> " +
                    "1")
            getNewToken()
            Log.println(Log.ASSERT,"123","(TestRetrofit.kt:24)-->> " +
                    "2")
        }
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

    private fun provideToken(): TokenApiService {
        return Retrofit.Builder()
            .baseUrl("https://be-dev.redrock.cqupt.edu.cn")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TokenApiService::class.java)
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
                    Log.println(Log.ASSERT,"123","(TestRetrofit.kt:96)-->> " +
                            "===")
                }
            }
        }catch (e: Exception) {
            Log.println(Log.ASSERT,"123","(TestRetrofit.kt:102)-->> " +
                    "${e.message}")
        }
    }
}