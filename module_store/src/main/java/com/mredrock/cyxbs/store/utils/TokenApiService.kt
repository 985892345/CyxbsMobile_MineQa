package com.mredrock.cyxbs.store.utils

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/16
 */
interface TokenApiService {
    @POST("/magipoke/token")
    fun getToken(
        @Body
        tokenBody: TokenBody
    ): Call<Token>
}