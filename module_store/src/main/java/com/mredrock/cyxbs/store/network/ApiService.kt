package com.mredrock.cyxbs.store.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.store.bean.*
import io.reactivex.Observable
import retrofit2.http.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/12 8:59
 */
interface ApiService {
    @GET("/magipoke-intergral/User/info")
    fun getStampCenter(): Observable<StampCenter>

    //获取兑换详细界面内容
    @GET("/magipoke-intergral/Integral/getItemInfo")
    fun getProductDetail(
            @Query("id")
            id: String
    ): Observable<ProductDetail>

    //购买商品
    @POST("/magipoke-intergral/Integral/purchase")
    @FormUrlEncoded
    fun buyProduct(
            @Field("id")
            id: String
    ): Observable<ExchangeState>

    //得到兑换记录
    @GET("/magipoke-intergral/User/exchange")
    fun getExchangeRecord(
    ): Observable<ExchangeRecord>

    //得到邮票获取记录
    @GET("/magipoke-intergral/User/getRecord")
    fun getStampGetRecord(
            @Query("page") page: Int,
            @Query("size") size: Int
    ): Observable<StampGetRecord>
}