package com.mredrock.cyxbs.store.network

import com.mredrock.cyxbs.store.bean.*
import com.mredrock.cyxbs.store.utils.Token
import io.reactivex.Observable
import retrofit2.http.*

/**
 *    author : zz、985892345
 *    e-mail : 1140143252@qq.com、2767465918@qq.com
 *    date   : 2021/8/12 8:59
 */
interface ApiService {

    // 获取邮票中心界面
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
    ): Observable<StampGetRecord>
}