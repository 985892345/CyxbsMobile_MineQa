package com.mredrock.cyxbs.store.page.record.ui.activity

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.store.databinding.StoreActivityExchangeDetailBinding
import kotlinx.android.synthetic.main.store_common_toolbar_no_line.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 11:39
 */
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
        //判断类型 动态改变IV
//        store_iv_exchange_order_bg.setImageDrawable()
        //设置左上角返回点击事件
        store_iv_toolbar_no_line_arrow_left.setOnClickListener {
            finish()
        }
    }
}