package com.mredrock.cyxbs.store.page.center.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.*
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

    val stampCenterData = MutableLiveData<StampCenter>() // 邮票中心界面数据
    val stampCenterRefreshData = MutableLiveData<Boolean>() // 网络请求是否刷新成功

    fun refresh() {
        TestRetrofit
            .testRetrofit
            .getStampCenter()
            .mapOrThrowApiException()
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