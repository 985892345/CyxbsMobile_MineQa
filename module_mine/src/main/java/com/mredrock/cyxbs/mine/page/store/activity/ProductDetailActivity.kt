package com.mredrock.cyxbs.mine.page.store.activity

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.MINE_STORE_EXCHANGE
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.BaseVPAdapter
import com.mredrock.cyxbs.mine.page.store.fragment.ProductImageFragment
import com.mredrock.cyxbs.mine.page.store.viewmodel.ProductDetailViewModel
import kotlinx.android.synthetic.main.mine_activity_product_detail.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:55
 */

@Route(path = MINE_STORE_EXCHANGE)
class ProductDetailActivity : BaseViewModelActivity<ProductDetailViewModel>() {
    private var mImageViewPagerAdapter: BaseVPAdapter<ProductImageFragment>?=null
    private var mImageFragmentList = arrayListOf<ProductImageFragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_product_detail)
        initAdapter()
        initView()
        initData()




    }

    private fun initView() {
        //设置进度条的最大值为图片总数
        mine_store_image_indicator.setImageCount(mImageFragmentList.size)
        //添加VP的滑动监听 来控制进度条重绘
        mine_store_vp_product_image.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mine_store_image_indicator.updateIndicator(position,positionOffset)
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }

    private fun initAdapter() {
        mImageFragmentList.add(ProductImageFragment())
        mImageFragmentList.add(ProductImageFragment())
        mImageFragmentList.add(ProductImageFragment())
        mImageViewPagerAdapter = BaseVPAdapter(this, mImageFragmentList)
        mine_store_vp_product_image.adapter = mImageViewPagerAdapter
    }

    private fun initData() {

    }
}