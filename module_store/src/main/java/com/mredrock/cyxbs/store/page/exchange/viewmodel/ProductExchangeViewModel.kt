package com.mredrock.cyxbs.store.page.exchange.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.TestRetrofit
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.network.ApiService

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
        MutableLiveData<String>()
    }

    fun getProductDetail(id: String) {
//        ApiGenerator.getApiService(ApiService::class.java)
//        .getProductDetail(id)
        TestRetrofit.testRetrofit.getProductDetail(id)
                .setSchedulers()
                .mapOrThrowApiException()
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
        ApiGenerator.getApiService(ApiService::class.java)
                .buyProduct(id)
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.store_exchange_product_failure

                }
                .safeSubscribeBy {
                    exchangeResult.postValue(it.info)
                }

    }

}