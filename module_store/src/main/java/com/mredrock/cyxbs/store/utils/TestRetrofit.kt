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
                                    "Bearer eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDIwMjE0OTc0In0sIkRvbWFpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiI0OGU3MTdhNGI3N2JjMjU0YTI4YmIyNmZmOWYzMjYxMDY5YzIzMjBjIiwiZXhwIjoiNzM5ODg5NzA5NiIsImlhdCI6IjE2MjkwODU4NDMiLCJzdWIiOiJ3ZWIifQ==.WEtfAEiDFAuMMw2DpEphFW8MeRxehbk1eaKz8u5JcsGh8zST5nyAJBKYvfPLABBz5uNnk1rfOErk5sPdHSmK2VaFrx9UAwnu6rEKSdjEIxykGHYhhF+RQ8tkBbGiKWDk5Fsz4H39NYeAG5NBsClK+NDfLuoYMxbKGj0LS/IhEnvUpiAR01mgtjBwRlR9I6529p+2JPVMFP+qujgBBigOz8TFuXabnB+es6KWzScaWn/0XRjO1gSJrgXapB54/e3FbvW7ltk1MlfeNVsyDWoijYYZdhHhqh5BsnyHw2Tt6IEpNDgpAqfO1StwblU7IPtKMSQ2zsEFMNH7k7qVSYlSzA==")
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