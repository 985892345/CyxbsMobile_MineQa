package com.mredrock.cyxbs.store.page.stamp.activity

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.module_store.R
import com.mredrock.cyxbs.common.config.STORE_EXCHANGE
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.store.page.stamp.fragment.ProductImageFragment
import com.mredrock.cyxbs.store.page.stamp.viewmodel.ProductDetailViewModel
import com.mredrock.cyxbs.store.until.ui.BaseVPAdapter
import kotlinx.android.synthetic.main.store_activity_product_detail.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:55
 */

@Route(path = STORE_EXCHANGE)
class ProductDetailActivity : BaseViewModelActivity<ProductDetailViewModel>() {
    private var mImageViewPagerAdapter: BaseVPAdapter<ProductImageFragment>?=null
    private var mImageFragmentList = arrayListOf<ProductImageFragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_product_detail)
        initAdapter()
        initView()
        initData()




    }

    private fun initView() {
        //设置进度条的最大值为图片总数
        store_image_indicator.setImageCount(mImageFragmentList.size)
        //添加VP的滑动监听 来控制进度条重绘
        store_vp_product_image.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                store_image_indicator.updateIndicator(position,positionOffset)
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }

    private fun initAdapter() {
        mImageFragmentList.add(ProductImageFragment())
        mImageFragmentList.add(ProductImageFragment())
        mImageFragmentList.add(ProductImageFragment())
        mImageViewPagerAdapter = BaseVPAdapter(this, mImageFragmentList)
        store_vp_product_image.adapter = mImageViewPagerAdapter
    }

    private fun initData() {

    }
}