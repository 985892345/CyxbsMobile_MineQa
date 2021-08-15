import com.mredrock.cyxbs.store.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object TestRetrofit {
    val testRetrofit = provideRetrofit()


    private fun provideRetrofit(): ApiService {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor {
                    val build = it.request()
                            .newBuilder()
                            .addHeader("Authorization",
                                    "Bearer eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDE5MjExNjg1In0sIkRvbWFpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiI1NGUxNzEzZTQ5MjUyMTcyNmMzZGQ3ZTg4Mzk1NDcxNzJhZTk1ZTlhIiwiZXhwIjoiNzM5ODgzMDM3NCIsImlhdCI6IjE2MjkwMTkxMjAiLCJzdWIiOiJ3ZWIifQ==.K7vVK0t7o1fn60FXMbBLIsX3YLhRIFKG+ZWhCT7cytfjSe4NrhrOk/wjWiEXJ/+F2FtxmgUDvT79l3/EH0PnbkSPotTHo//o45IjSF+BeQ4cpKaNL9lTZfGL6sndgjklUpsgnBHticzuW/Yb8/NSwDBK/xAnYiWkUokEmzVP4h+3TLg6Sxl+YWy8dn1P1Pxl2O7QwzGSy/g6lpQIVAJihxoiLSWNyl9a4fFKCfvzp2rJOjNy5tKNpii4QhcYqAB6LrKmHdIYb2NJHt/Kb3aMGxnFoRK60ULF2M/Inhh9L78jqj9/QCUx/7ITzBE6lMPVKNATlFPPkFwY1punxQ9nRg==")
                            .build()
                    return@addInterceptor it.proceed(build)
                }
                .build()

        return Retrofit.Builder()
                .baseUrl("https://be-dev.redrock.cqupt.edu.cn")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(ApiService::class.java)
    }
}