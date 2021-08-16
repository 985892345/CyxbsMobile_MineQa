package com.mredrock.cyxbs.store.utils

import io.reactivex.Observable
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
    ): Observable<Token>
}