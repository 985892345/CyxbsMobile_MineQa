package com.mredrock.cyxbs.store.page.exchange.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.module_store.R
import com.example.module_store.databinding.StoreActivityProductExchengeBinding
import com.mredrock.cyxbs.common.config.STORE_PRODUCT_EXCHANGE
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.store.page.exchange.ui.adapter.ProductImageVPAdapter
import com.mredrock.cyxbs.store.page.exchange.viewmodel.ProductExchangeViewModel
import com.mredrock.cyxbs.store.utils.ui.ProductExchangeDialogFragment
import kotlinx.android.synthetic.main.store_activity_product_exchenge.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:55
 */

@Route(path = STORE_PRODUCT_EXCHANGE)
class ProductExchangeActivity : BaseViewModelActivity<ProductExchangeViewModel>() {
    private var mImageViewPagerAdapter: ProductImageVPAdapter? = null
    private lateinit var dataBinding: StoreActivityProductExchengeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //创建binding
        dataBinding = StoreActivityProductExchengeBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        initAdapter()
        initView()
        initData()

    }

    private fun initView() {
        //添加VP的页面选中监听 来控制圆点重绘
        store_vp_product_image.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                store_progress_dot.updatePosition(position)
                super.onPageSelected(position)
            }
        })
        dataBinding.eventHandle = EventHandle()
    }

    private fun initAdapter() {
        mImageViewPagerAdapter = ProductImageVPAdapter(arrayListOf("ss", "dd"))
        store_vp_product_image.adapter = mImageViewPagerAdapter
    }

    private fun initData() {

    }

    /**
     * 事件处理内部类
     * 通过binding绑定到xml中
     */
    inner class EventHandle() {
        //处理单击事件
        fun onItemSingleClick(view: View) {

            when (view.id) {
                R.id.store_btn_exchange -> {
                    //进行判断
                    if (true) {
                        ProductExchangeDialogFragment().apply {
                            initView(
                                    dialogRes = R.layout.store_dialog_exchange_product,
                                    onPositiveClick = { dismiss() },
                                    onNegativeClick = { dismiss() }
                            )
                        }.show(supportFragmentManager, "zz")
                    }
                }
            }
        }
    }
}