package com.mredrock.cyxbs.store.page.center.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageButton
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.STORE_CENTER
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.BaseFragmentVPAdapter
import com.mredrock.cyxbs.store.page.center.ui.fragment.StampShopFragment
import com.mredrock.cyxbs.store.page.center.ui.fragment.StampTaskFragment
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
    private lateinit var mTabLayout: TabLayout
    private lateinit var mRefreshLayout: SwipeRefreshLayout
    private lateinit var mSlideUpLayout: SlideUpLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_store_center)
        initView()
        initViewPager2()
        initTabLayout()
        initRefreshLayout()
        initSlideUpLayoutWithLeftTopStamp()
        initJump()
        initData()
        viewModel.refresh()
    }

    private fun initView() {
        mTvShopHint = findViewById(R.id.store_tv_stamp_center_hint)
        mTvStampNumber = findViewById(R.id.store_tv_stamp_number)
        mTvStampNumber2 = findViewById(R.id.store_tv_stamp_number_left_top)
        mViewPager2 = findViewById(R.id.store_vp_stamp_center)
        mRefreshLayout = findViewById(R.id.store_refreshLayout_stamp_center)
        mSlideUpLayout = findViewById(R.id.store_slideUpLayout_stamp_center)
    }

    private fun initViewPager2() {
        mViewPager2.adapter = BaseFragmentVPAdapter(
            this,
            listOf(
                StampShopFragment(),
                StampTaskFragment()
            )
        )
    }

    // 设置 TabLayout
    private fun initTabLayout() {
        mTabLayout = findViewById(R.id.store_tl_stamp_center)
        val tabs = listOf(
            getString(R.string.store_stamp_center_small_shop),
            getString(R.string.store_stamp_center_stamp_task)
        )
        TabLayoutMediator(
            mTabLayout, mViewPager2
        ) { tab, position -> tab.text = tabs[position] }.attach()
        // 以下代码是设置邮票任务的小圆点
        val tab = mTabLayout.getTabAt(1)
        if (tab != null) {
            val badge = tab.orCreateBadge
            badge.backgroundColor = 0xFF6D68FF.toInt()
            try {
                /*
                * 视觉说这个小圆点大了, 艹, 妈的官方也不提供方法修改, 只好靠反射拿了 :)
                * 官方中 badgeRadius 是 final 常量, 但反射却能修改, 原因在于它在构造器中被初始化, 不会被内联优化, 所以是可以改的
                * */
                val field = badge.javaClass.getDeclaredField("badgeRadius")
                field.isAccessible = true
                field.set(badge, dp2px(3.5F))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // 滑到邮票任务页面时就取消小圆点
            mViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) { if (position == 1) tab.removeBadge() }
            })
        }
    }

    private fun initRefreshLayout() {
        try { // 垃圾官方刷新控件, 不能修改偏移的误差值, 在左右滑动时容易出问题
            val field = mRefreshLayout.javaClass.getDeclaredField("mTouchSlop")
            field.isAccessible = true
            field.set(mRefreshLayout, 300)
        }catch (e: Exception) {
            e.printStackTrace()
        }
        mRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    // 用于设置向上滑时与右上角邮票小图标的联合效果
    private fun initSlideUpLayoutWithLeftTopStamp() {
        val appearLayout: AppearLayout = findViewById(R.id.store_gradualColorLayout_stamp_center)
        mSlideUpLayout.setMoveListener {
            appearLayout.setMultiple(1 - it)
        }
    }

    // 一些简单不传参的跳转写这里
    private fun initJump() {
        val btnBack: ImageButton = findViewById(R.id.store_iv_toolbar_no_line_arrow_left)
        btnBack.setOnClickListener {
            finish()
        }

        val tvDetail: TextView = findViewById(R.id.store_tv_stamp_center_detail)
        tvDetail.setOnClickListener {
            startActivity<StampDetailActivity>()
        }
    }

    private var refreshTimes = 0
    // 对于 ViewModel 数据的观察
    private fun initData() {
        viewModel.stampCenterData.observeNotNull{
            mRefreshLayout.isRefreshing = false
            if (refreshTimes != 0) {
                toast("刷新成功")
            }
            refreshTimes++
            val text = it.data.userAmount.toString()
            mTvStampNumber.text = text // 正上方的大的显示
            mTvStampNumber2.text = text // 右上方小的显示
            if (it.data.unGotGood) {
                // 显示"你还有待领取的商品，请尽快领取"
                mTvShopHint.visibility = View.VISIBLE
            }else {
                mTvShopHint.visibility = View.INVISIBLE
            }
        }
    }

    override fun onRestart() {
        viewModel.refresh()
        super.onRestart()
    }
}