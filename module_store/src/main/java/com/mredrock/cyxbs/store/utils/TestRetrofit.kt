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
                                    "Bearer eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDE5MjExNjg1In0sIkRvbWFpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiI1NGUxNzEzZTQ5MjUyMTcyNmMzZGQ3ZTg4Mzk1NDcxNzJhZTk1ZTlhIiwiZXhwIjoiNzM5ODg0MTA3NCIsImlhdCI6IjE2MjkwMjk4MjEiLCJzdWIiOiJ3ZWIifQ==.AVYn1JBPM1GITKZHBhttkdL1OaO4BA5tm3/xOKhHhHCgYlWWp604jfrhnq0PC+1q1W7rNhnhRbgIALETMZSrWRDbO3AzP6UzfuYFidh5prMnt+qMJii0GsNj8REOL926ZWflE8YIzDD/OteG+z5c9vsoDNMDQZKwtjp3tdMl6FGViu8lSAU7YdJjzIaruNdGYmnoBrh7S8trPw1GFPcNukl3MHHezxdcbCDainPjAU0pin4VS+yPcuOOE/PHWs45DmBIHDrXNJPJkZge9tQ85K1pxg5UllzWF2y8qoZx0+6xX+ErCvwiCX3QR1oISM0h/PMDeJ66pjgAn8YYNbH1ew==")
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