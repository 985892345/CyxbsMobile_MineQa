package com.mredrock.cyxbs.store.page.center.ui.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.STORE_CENTER
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.page.center.ui.item.SmallShopItem
import com.mredrock.cyxbs.store.page.center.ui.item.StampTaskItem
import com.mredrock.cyxbs.store.page.center.viewmodel.StoreCenterViewModel
import com.mredrock.cyxbs.store.page.record.ui.activity.StampDetailActivity
import com.mredrock.cyxbs.store.utils.widget.AppearLayout
import com.mredrock.cyxbs.store.utils.widget.SlideUpLayout

/**
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/7
 */
@Route(path = STORE_CENTER)
class StoreCenterActivity : BaseViewModelActivity<StoreCenterViewModel>() {

    private lateinit var mViewPager2: ViewPager2
    private lateinit var mTabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_store_center)
        initView()
        initViewPager2()
        initTabLayout()
        initSlideUpLayoutWithLeftTopStamp()
        initJumpActivity()
    }

    private fun initView() {
        mViewPager2 = findViewById(R.id.store_vp_stamp_center)
    }

    private fun initViewPager2() {
        mViewPager2.adapter = SimpleRVAdapter(2)
            .addItem(SmallShopItem(this))
            .addItem(StampTaskItem())
    }
    
    private fun initTabLayout() {
        mTabLayout = findViewById(R.id.store_tl_stamp_center)
        val tabs = listOf(
            getString(R.string.store_stamp_center_small_shop),
            getString(R.string.store_stamp_center_stamp_task)
        )
        TabLayoutMediator(
            mTabLayout, mViewPager2
        ) { tab, position -> tab.text = tabs[position] }.attach()

        val tab = mTabLayout.getTabAt(1)
        if (tab != null) {
            val badge = tab.orCreateBadge
            badge.backgroundColor = 0xFF6D68FF.toInt()
        }
    }

    private fun initSlideUpLayoutWithLeftTopStamp() {
        val slideUpLayout: SlideUpLayout = findViewById(R.id.store_slideUpLayout_stamp_center)
        val appearLayout: AppearLayout = findViewById(R.id.store_gradualColorLayout_stamp_center)
        slideUpLayout.setMoveListener {
            appearLayout.setMultiple(1 - it)
        }
    }

    private fun initJumpActivity() {

        val btnBack: ImageButton = findViewById(R.id.store_iv_toolbar_no_line_arrow_left)
        btnBack.setOnClickListener {
            finish()
        }

        val tvDetail: TextView = findViewById(R.id.store_tv_stamp_center_detail)
        tvDetail.setOnClickListener {
            startActivity<StampDetailActivity>()
        }
    }
}