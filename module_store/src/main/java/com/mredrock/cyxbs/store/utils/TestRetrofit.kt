
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
            .addInterceptor{
                    val build = it.request()
                        .newBuilder()
                        .addHeader("Authorization",
                            "Bearer eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDE5MjExNjg1In0sIkRvbWFpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiI1NGUxNzEzZTQ5MjUyMTcyNmMzZGQ3ZTg4Mzk1NDcxNzJhZTk1ZTlhIiwiZXhwIjoiNzM5ODcyMTA4NyIsImlhdCI6IjE2Mjg5MDk4MzQiLCJzdWIiOiJ3ZWIifQ==.UhLD0HnnB8M6Zl8K65MrlMVfqDNwXMSOihApTTF4EK46lR/HTuZ1WhlLQfylFPPyqjCNnK+yuuNFJVEemb0e159ZE3iVbRVIAKaGCyWrNUe4lP8lb1EliSbFWW1cP5Kukagfxhs8HX1bqcnFkn4ko2ILEKxF1tFVhTqm1bZ5Hjmdk7aQCC18EyAmb4aryu9E/js/0ESj2UlVb9GBAhnRXV/7xMmgcYL3LSOSu3P08lcfc6HKO6q5wO3BxhR5D0wNAXHFBlBz/EHn0lsspJeUXiYj4h0z/DNyewdA4mDyKIzTkJfFPwj6jKigWLkJFgdBvI1gSXnipOPVGHdc2n0iCA==")
                        .build()
                return@addInterceptor it.proceed(build)
                }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://be-dev.redrock.cqupt.edu.cn/")
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }
}