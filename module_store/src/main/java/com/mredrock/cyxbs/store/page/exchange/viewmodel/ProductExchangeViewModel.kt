package com.mredrock.cyxbs.store.page.exchange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
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
        MutableLiveData<ProductDetail.Data>()
    }
    val exchangeResult by lazy {
        MutableLiveData<ExchangeState>()
    }

    fun getProductDetail(id: String) {
//        ApiGenerator.getApiService(ApiService::class.java)
//        .getProductDetail(id)
        TestRetrofit
                .testRetrofit
                .getProductDetail(id)
//                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy(
                        onError = {
                            toastEvent.value = R.string.store_product_detail_failure
                        },
                        onNext = {
                            if (it.info == "success") {
                                productDetail.postValue(it.data)
                            }
                        })
    }

    fun exchangeProduct(id: String) {
//        ApiGenerator.getApiService(ApiService::class.java)
        TestRetrofit.testRetrofit
                .buyProduct(id)
                .setSchedulers()
                .safeSubscribeBy(
                        onError = {
                            //因为只要不是兑换成功 接口都返回错误 500 导致回调到onError方法 所以这里手动拿到返回的bean类
                            val responseBody: ResponseBody? = (it as HttpException).response()?.errorBody()
                            if (responseBody != null) {
                                val gson = Gson()
                                val exchangeState = gson.fromJson(responseBody.string(), ExchangeState::class.java)
                                exchangeResult.postValue(exchangeState)
                            } else {
                                toastEvent.value = R.string.store_exchange_product_failure
                            }
                        },
                        onNext = {
                            exchangeResult.postValue(it)
                        }
                )

    }

}