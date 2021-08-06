package com.mredrock.cyxbs.store.page.record.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.module_store.databinding.StoreActivityExchangeDetailBinding
import com.mredrock.cyxbs.common.config.STORE_EXCHANGE_DETAIL
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.store_activity_exchange_detail.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 11:39
 */

@Route(path = STORE_EXCHANGE_DETAIL)
class ExchangeDetailActivity : BaseActivity() {
    private lateinit var dataBinding: StoreActivityExchangeDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = StoreActivityExchangeDetailBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        initView()
    }

    private fun initView() {
        //绑定数据
        //...
        //判断类型 动态改变IV的src
//        store_iv_exchange_order_bg.setImageDrawable()
    }
}