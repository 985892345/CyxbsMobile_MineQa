package com.mredrock.cyxbs.store.page.exchange.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ExchangeState
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.utils.TestRetrofit
import okhttp3.ResponseBody
import retrofit2.HttpException

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:56
 */
class ProductExchangeViewModel : BaseViewModel() {
    val productDetail by lazy {
        MutableLiveData<ProductDetail>()
    }
    val exchangeResult by lazy {
        MutableLiveData<ExchangeState>()
    }
    val exchangeError by lazy {
        MutableLiveData<String>()
    }

    fun getProductDetail(id: String) {
        TestRetrofit
            .testRetrofit
            .getProductDetail(id)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    toastEvent.value = R.string.store_product_detail_failure
                },
                onNext = {
                    productDetail.value = it
                }
            )
    }

    fun exchangeProduct(id: String) {
        TestRetrofit.testRetrofit
            .buyProduct(id)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    //因为只要不是兑换成功 接口都返回错误 500 导致回调到onError方法 所以这里手动拿到返回的bean类
                    val responseBody: ResponseBody? = (it as HttpException).response()?.errorBody()
                    val code = it.response()?.code()//返回的code 如果为500即为余额不足/库存不足
                    if (code == 500) {
                        val gson = Gson()
                        val exchangeState = gson.fromJson(responseBody?.string(), RedrockApiStatus::class.java)
                        exchangeError.value = exchangeState.info
                    } else {
                        toastEvent.value = R.string.store_exchange_product_failure
                    }
                },
                onNext = {
                    exchangeResult.value = it
                }
            )
    }
}