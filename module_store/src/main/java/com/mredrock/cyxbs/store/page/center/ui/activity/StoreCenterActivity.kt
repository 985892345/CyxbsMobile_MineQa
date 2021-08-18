package com.mredrock.cyxbs.store.page.center.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
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
import com.mredrock.cyxbs.store.utils.widget.SlideUpLayout
import com.mredrock.cyxbs.store.utils.widget.TextRollView
import com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.transformer.ScaleInTransformer

/**
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/7
 */
@Route(path = STORE_CENTER)
class StoreCenterActivity : BaseViewModelActivity<StoreCenterViewModel>() {

    private lateinit var mTvStampNumber: TextRollView
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
        mViewPager2.setPageTransformer(ScaleInTransformer())
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
        if (IS_SHOW_BADGE) {
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
                    override fun onPageSelected(position: Int) {
                        if (position == 1) {
                            tab.removeBadge()
                            IS_SHOW_BADGE = false
                            mViewPager2.unregisterOnPageChangeCallback(this)
                        }
                    }
                })
            }
        }
    }

    private fun initRefreshLayout() {
        /*
        * 垃圾官方刷新控件, 不能修改偏移的误差值, 在左右滑动时与 ViewPager2 出现滑动冲突问题
        * 修改 mTouchSlop 可以修改允许的滑动偏移值, 位置在 SwipeRefreshLayout 的 1081 行
        * */
        try {
            val field = mRefreshLayout.javaClass.getDeclaredField("mTouchSlop")
            field.isAccessible = true
            field.set(mRefreshLayout, 220)
        }catch (e: Exception) {
            e.printStackTrace()
        }
        mRefreshLayout.setOnChildScrollUpCallback { _, _ ->
            !mSlideUpLayout.isUnfold()
        }
        mRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    // 用于设置向上滑时与右上角邮票小图标的联合效果
    private fun initSlideUpLayoutWithLeftTopStamp() {
        mTvStampNumber2.alpha = 0F // 初始时隐藏, 后面会还原
        val displayMetrics = resources.displayMetrics
        val windowWidth = displayMetrics.widthPixels // 获取屏幕总宽度
        mSlideUpLayout.setMoveListener {
            mTvStampNumber2.translationX = it * (windowWidth - mTvStampNumber2.left)
            if (mTvStampNumber2.alpha == 0F) {
                mTvStampNumber2.alpha = 1F
            }
        }
    }

    // 一些简单不传参的跳转写这里
    private fun initJump() {
        val btnBack: ImageButton = findViewById(R.id.store_iv_toolbar_no_line_arrow_left)
        btnBack.setOnClickListener {
            finish() // 左上角返回键
        }

        val ivDetail: ImageView = findViewById(R.id.store_iv_stamp_center_stamp_bg)
        ivDetail.setOnClickListener {
            startActivity<StampDetailActivity>() // 跳到邮票明细界面
        }
    }

    private var refreshTimes = 0 // 请求的次数, 用于判断刷新控件和 toast 显示
    private var isFirstLoad = true // 是否是第一次进入界面, 用于判断邮票数字显示动画
    // 对于 ViewModel 数据的观察
    @SuppressLint("SetTextI18n")
    private fun initData() {
        viewModel.stampCenterData.observeNotNull{
            val text = it.data.userAmount.toString()
            if (isFirstLoad) {
                mTvStampNumber.setTextOnlyAlpha(text) // 第一次进入界面就只使用隐现的动画
                isFirstLoad = false // over
            }else {
                mSlideUpLayout.setUnfoldCallBack {
                    mTvStampNumber.setText(text, true) // 正上方的大的邮票显示
                    mSlideUpLayout.removeUnfoldCallBack()
                }
            }
            mTvStampNumber2.text = " $text" // 右上方小的邮票显示
            if (it.data.unGotGood) {
                // 显示"你还有待领取的商品，请尽快领取"
                mTvShopHint.visibility = View.VISIBLE
            }else {
                mTvShopHint.visibility = View.INVISIBLE
            }
        }

        viewModel.stampCenterRefreshData.observeNotNull {
            mRefreshLayout.isRefreshing = false
            if (it) {
                if (refreshTimes != 0) {
                    toast("刷新成功")
                }
                refreshTimes++
            }else {
                toast("获取邮票数据失败")
            }
        }
    }

    // 从邮货详细界面返回
    override fun onRestart() {
        refreshTimes = 0
        viewModel.refresh()
        super.onRestart()
    }

    companion object {
        /**
         * 是否显示 TabLayout 的邮票任务的小圆点,
         * 产品说只有每次打开应用时才显示, 为减少处理逻辑, 故把它设置成私有的静态全局变量更合适
         * 只有在你清理了后台的每次打开才会重新显示小圆点
         */
        private var IS_SHOW_BADGE = true
    }
}