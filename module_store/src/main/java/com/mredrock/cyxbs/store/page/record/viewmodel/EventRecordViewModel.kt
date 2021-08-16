package com.mredrock.cyxbs.store.page.record.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.bean.StampGetRecord
import com.mredrock.cyxbs.store.utils.TestRetrofit

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:47
 */
class EventRecordViewModel : BaseViewModel() {
    val mExchangeRecord by lazy {
        MutableLiveData<MutableList<ExchangeRecord.Data>>()
    }
    val mStampGetRecord by lazy {
        MutableLiveData<MutableList<StampGetRecord.Data>>()
    }

    fun getExchangeRecord() {
//        ApiGenerator.getApiService(ApiService::class.java)
        TestRetrofit.testRetrofit
                .getExchangeRecord()
                .setSchedulers()
//                .mapOrThrowApiException()
                .safeSubscribeBy(
                        onError = { toastEvent.value = R.string.store_exchange_detail_failure },
                        onNext = {
                            if (it.info == "success") {
                                mExchangeRecord.postValue(it.data as MutableList<ExchangeRecord.Data>?)
                            }
                        })
    }

    fun getStampRecord() {
//        ApiGenerator.getApiService(ApiService::class.java)
        TestRetrofit.testRetrofit
                .getStampGetRecord()
                .setSchedulers()
//                .mapOrThrowApiException()
                .safeSubscribeBy(
                        onError = { toastEvent.value = R.string.store_stamp_record_failure },
                        onNext = {
                            if (it.info == "success") {
                                mStampGetRecord.postValue(it.data as MutableList<StampGetRecord.Data>?)
                            }
                        })
    }

}