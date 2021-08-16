package com.mredrock.cyxbs.store.page.center.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.utils.TestRetrofit

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/7
 */
class StoreCenterViewModel: BaseViewModel() {

    val stampCenterData = MutableLiveData<StampCenter>()
    val stampCenterRefreshData = MutableLiveData<Boolean>()

    fun refresh() {
        TestRetrofit
            .testRetrofit
            .getStampCenter()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    stampCenterRefreshData.value = false
                },
                onNext = {
                    stampCenterData.value = it
                    stampCenterRefreshData.value = true
                }
            )
    }
}