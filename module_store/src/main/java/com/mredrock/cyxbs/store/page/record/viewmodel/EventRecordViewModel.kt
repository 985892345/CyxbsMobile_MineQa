package com.mredrock.cyxbs.store.page.record.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
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
    // 兑换记录
    val mExchangeRecord by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<List<ExchangeRecord>>() }

    // 获取记录
    val mStampGetRecord by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<List<StampGetRecord>>() }

    fun getExchangeRecord() {
//        ApiGenerator.getApiService(ApiService::class.java)
        TestRetrofit.testRetrofit
            .getExchangeRecord()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = { toastEvent.value = R.string.store_exchange_detail_failure },
                onNext = {
                    mExchangeRecord.value = it
                }
            )
    }

    fun getStampRecord() {
//        ApiGenerator.getApiService(ApiService::class.java)
        TestRetrofit.testRetrofit
            .getStampGetRecord(1, 100)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = { toastEvent.value = R.string.store_stamp_record_failure },
                onNext = {
                    mStampGetRecord.value = it
                }
            )
    }
}