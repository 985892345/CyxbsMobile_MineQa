package com.mredrock.cyxbs.store.page.center.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.STORE_CENTER
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
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

    private lateinit var mTvStampNumber: TextView
    private lateinit var mTvStampNumber2: TextView
    private lateinit var mTvShopHint: TextView
    private lateinit var mViewPager2: ViewPager2
    private lateinit var mSmallShopItem: SmallShopItem
    private lateinit var mStampTaskItem: StampTaskItem
    private lateinit var mTabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_store_center)
        initView()
        initViewPager2()
        initTabLayout()
        initSlideUpLayoutWithLeftTopStamp()
        initJumpActivity()
        initData()
        viewModel.refresh()
    }

    private fun initView() {
        mTvShopHint = findViewById(R.id.store_tv_stamp_center_hint)
        mTvStampNumber = findViewById(R.id.store_tv_stamp_number)
        mTvStampNumber2 = findViewById(R.id.store_tv_stamp_number_left_top)
        mViewPager2 = findViewById(R.id.store_vp_stamp_center)
    }

    private fun initViewPager2() {
        mSmallShopItem = SmallShopItem()
        mStampTaskItem = StampTaskItem()
        mViewPager2.adapter = SimpleRVAdapter()
            .addItem(mSmallShopItem)
            .addItem(mStampTaskItem)
            .show()
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
            try {
                // 视觉说这个小圆点大了, 艹, 妈的官方也不提供方法修改, 只好靠反射拿了 :)
                // 官方中 badgeRadius 是 final 常量, 但反射却能修改, 原因在于它在构造器中
                // 被初始化, 不会被内联优化, 所以是可以改的
                val field = badge.javaClass.getDeclaredField("badgeRadius")
                field.isAccessible = true
                field.set(badge, dp2px(3.5F))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    private fun initData() {
        viewModel.stampCenterData.observeNotNull{
            val text = it.data.userAmount.toString()
            mTvStampNumber.text = text // 正上方的大的显示
            mTvStampNumber2.text = text // 右上方小的显示
            if (it.data.unGotGood) {
                // 显示"你还有待领取的商品，请尽快领取"
                mTvShopHint.visibility = View.VISIBLE
            }else {
                mTvShopHint.visibility = View.INVISIBLE
            }
            mSmallShopItem.refreshData(it.data.shop)
            mStampTaskItem.refreshData(it.data.task)
        }
    }
}