package com.mredrock.cyxbs.store.page.record.ui.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.BaseFragmentVPAdapter
import com.mredrock.cyxbs.store.page.record.ui.fragment.EventRecordFragment
import kotlinx.android.synthetic.main.store_activity_product_exchange.*
import kotlinx.android.synthetic.main.store_activity_stamp_detail.*
import kotlinx.android.synthetic.main.store_common_toolbar.*


/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:46
 */
class StampDetailActivity : BaseActivity() {
    private var mEventViewPagerAdapter: BaseFragmentVPAdapter<EventRecordFragment>? = null
    private var mEventRecordFragmentList = arrayListOf<EventRecordFragment>()
    private var mTabText = arrayOf("兑换记录", "获取记录")
    private var animDuration: Long = 400 //TabLayout文字缩放动画时间
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_stamp_detail)
        initAdapter()
        initView()
        initData()
    }

    private fun initView() {
        //设置TabLayout相关参数和监听
        initTabLayout()
        //设置预加载 使两个Fragment都加载 避免滑动到下一页时还需等待网络请求加载
        store_vp_stamp_detail.offscreenPageLimit = 1
        store_vp_stamp_detail.startAnimation(AnimationUtils.loadAnimation(this,R.anim.store_slide_from_right_to_left_in))
        //设置左上角返回点击事件
        store_iv_toolbar_arrow_left.setOnClickListener {
            finish()
        }
    }

    private fun initTabLayout() {
        //设置tab选中监听 用于处理选中的title字体变大
        store_tab_stamp_record.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //当tab被选中时 用size更大的TextView来代替原TextView
                initSelectedText(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //当tab从选中状态到不被选中状态 还原大小与颜色
                initUnselectedText(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        //添加title
        TabLayoutMediator(store_tab_stamp_record, store_vp_stamp_detail) { tab, position ->
            tab.text = mTabText[position]
        }.attach()
    }

    //设置TabLayout中未被选择时的文字属性
    @SuppressLint("InflateParams")
    private fun initUnselectedText(tab: TabLayout.Tab?) {
        if (tab != null) {
            tab.customView = null
            val textView: TextView = LayoutInflater.from(this@StampDetailActivity).inflate(R.layout.store_item_tab_text, null) as TextView
            textView.text = tab.text
            textView.setTypeface(null, Typeface.NORMAL)
            textView.setTextColor(ContextCompat.getColor(this@StampDetailActivity, R.color.store_stamp_unselected_title))
            val anim = ValueAnimator.ofFloat(16f, 14f)
            anim.duration = animDuration
            anim.addUpdateListener {
                textView.textSize = (it.animatedValue) as Float
            }
            anim.start()
            tab.customView = textView
        }
    }

    //设置TabLayout中当前被选择时的文字属性
    @SuppressLint("InflateParams")
    private fun initSelectedText(tab: TabLayout.Tab?) {
        if (tab != null) {
            tab.customView = null
            val textView: TextView = LayoutInflater.from(this@StampDetailActivity).inflate(R.layout.store_item_tab_text, null) as TextView
            textView.text = tab.text
            textView.setTypeface(null, Typeface.BOLD)
            val anim = ValueAnimator.ofFloat(14f, 16f)
            anim.duration = animDuration
            anim.addUpdateListener {
                textView.textSize = (it.animatedValue) as Float
            }
            anim.start()
            tab.customView = textView
        }
    }

    /**
     * 因为兑换记录和获取记录界面中均只有一个RecyclerView
     * 所以这里将两个界面基于EventRecordFragment
     * 根据event的不同 为RecyclerView赋予不同的item
     */
    private fun initAdapter() {
        //创建两个EventRecordFragment 用Bundle携带不同的事件字符串来控制Fragment中RecyclerView的item类型
        val exchangeBundle = Bundle()
        exchangeBundle.putString("event", "exchange")
        val exchangeRecordFragment = EventRecordFragment()
        exchangeRecordFragment.arguments = exchangeBundle
        val stampBundle = Bundle()
        val stampGetRecordFragment = EventRecordFragment()
        stampBundle.putString("event", "getStamp")
        stampGetRecordFragment.arguments = stampBundle
        mEventRecordFragmentList.add(exchangeRecordFragment)
        mEventRecordFragmentList.add(stampGetRecordFragment)

        mEventViewPagerAdapter = BaseFragmentVPAdapter(this, mEventRecordFragmentList)
        store_vp_stamp_detail.adapter = mEventViewPagerAdapter
    }

    private fun initData() {

    }
}