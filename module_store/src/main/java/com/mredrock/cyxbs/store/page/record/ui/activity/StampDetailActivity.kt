package com.mredrock.cyxbs.store.page.record.ui.activity

import android.animation.ValueAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.animation.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.BaseFragmentVPAdapter
import com.mredrock.cyxbs.store.page.record.ui.fragment.RecordFragment
import com.mredrock.cyxbs.store.page.record.viewmodel.RecordViewModel
import com.mredrock.cyxbs.store.utils.dp2pxF

/**
 *    author : zz (后期优化: 985892345)
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:46
 */
class StampDetailActivity : BaseViewModelActivity<RecordViewModel>() {

    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_stamp_detail)
        initView()
        initViewPager2()
        initTabLayout()
        initJump() // 一些简单的跳转
        initData()
    }

    private fun initView() {
        mTabLayout = findViewById(R.id.store_tl_stamp_record)
        mViewPager2 = findViewById(R.id.store_vp_stamp_detail)
    }

    private fun initViewPager2() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.store_slide_from_bottom_to_top_in)
        animation.interpolator = DecelerateInterpolator()
        mViewPager2.startAnimation(animation) // 入场动画
//        mViewPager2.setPageTransformer(ScaleInTransformer())
        mViewPager2.adapter = BaseFragmentVPAdapter(
            this,
            listOf(
                RecordFragment.getFragment(RecordFragment.Page.EXCHANGE),
                RecordFragment.getFragment(RecordFragment.Page.GET)
            )
        )
    }

    private fun initTabLayout() {
        val tabs = arrayOf("兑换记录", "获取记录")
        //添加title
        TabLayoutMediator(mTabLayout, mViewPager2) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        val tab1 = mTabLayout.getTabAt(0)
        val textView1 = TextView(this).apply { gravity = Gravity.CENTER }
        tab1?.customView = textView1 // 替换 tab 中的 View, 用于产品要文字大小改变的需求
        textView1.text = tab1?.text

        val tab2 = mTabLayout.getTabAt(1)
        val textView2 = TextView(this).apply { gravity = Gravity.CENTER }
        tab2?.customView = textView2 // 替换 tab 中的 View, 用于产品要文字大小改变的需求
        textView2.text = tab2?.text

        // 设置 Tab 的选择监听, 用于加载动画
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tabUnselected(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        // 设置刚进入页面时 Tab 的选择状态
        val nowTab = mTabLayout.getTabAt(mViewPager2.currentItem)
        if (nowTab != null) {
            tabSelected(nowTab)
        }
    }

    private fun tabSelected(tab: TabLayout.Tab) {
        val textView = tab.customView
        if (textView is TextView) {
            textView.setTypeface(null, Typeface.BOLD) // 加粗
            textView.setTextColor(ContextCompat
                .getColor(
                    this@StampDetailActivity,
                    R.color.store_stamp_selected_title
                ))
            ValueAnimator.ofFloat(5.dp2pxF(), 5.6F.dp2pxF()).run {
                duration = 260L
                addUpdateListener { textView.textSize = animatedValue as Float }
                start()
            }
        }
    }

    private fun tabUnselected(tab: TabLayout.Tab) {
        val textView = tab.customView
        if (textView is TextView) {
            textView.setTypeface(null, Typeface.NORMAL) // 取消加粗
            textView.setTextColor(ContextCompat
                .getColor(
                    this@StampDetailActivity,
                    R.color.store_stamp_unselected_title
                ))
            ValueAnimator.ofFloat(5.6F.dp2pxF(), 5.dp2pxF()).run {
                duration = 260L
                addUpdateListener { textView.textSize = animatedValue as Float }
                start()
            }
        }
    }

    private fun initJump() {
        // 设置左上角返回点击事件
        val button: ImageButton = findViewById(R.id.store_iv_toolbar_arrow_left)
        button.setOnSingleClickListener {
            finish()
        }
    }

    private fun initData() {
        // 请求网络数据
        viewModel.getExchangeRecord()
        viewModel.getStampRecord()
    }
}