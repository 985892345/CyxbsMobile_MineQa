package com.mredrock.cyxbs.store.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.bean.StampGetRecord
import io.reactivex.Observable
import retrofit2.http.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/12 8:59
 */
interface ApiService {

























































































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
    ): Observable<RedrockApiStatus>

    //得到兑换记录
    @GET("/magipoke-intergral/User/exchange")
    fun getExchangeRecord(
    ): Observable<ExchangeRecord>

    //得到邮票获取记录
    @GET("/magipoke-intergral/User/getRecord")
    fun getStampGetRecord(
    ): Observable<StampGetRecord>
}