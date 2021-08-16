import android.util.Log
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.store.network.ApiService
import com.mredrock.cyxbs.store.utils.TokenApiService
import com.mredrock.cyxbs.store.utils.TokenBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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
                .addInterceptor(Retry(2))
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
    ): Interceptor {

        private var retryNum = 0

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var response = chain.proceed(request)
            while (!response.isSuccessful && retryNum < maxRetry) {
                retryNum++
                provideToken()
                    .getToken(TokenBody("2019211685", "096854"))
                    .safeSubscribeBy(
                        onError = {
                        },
                        onNext = {
                            response.close()
                            val build = request
                                .newBuilder()
                                .addHeader("Authorization",
                                    "Bearer ${it.data.token}")
                                .build()
                            response = chain.proceed(build)
                        }
                    )
            }
            return response
        }
    }
}