package com.mredrock.cyxbs.store.page.record.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.page.record.ui.adapter.StampDetailVPAdapter
import com.mredrock.cyxbs.store.page.record.ui.fragment.EventRecordFragment
import kotlinx.android.synthetic.main.store_activity_stamp_detail.*
import kotlinx.android.synthetic.main.store_common_toolbar.*


/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:46
 */
class StampDetailActivity : BaseActivity() {
    private var mEventViewPagerAdapter: StampDetailVPAdapter<EventRecordFragment>? = null
    private var mEventRecordFragmentList = arrayListOf<EventRecordFragment>()
    private var mTabText = arrayOf("兑换记录", "获取记录")
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
                if (tab != null) {
                    val textView: TextView = LayoutInflater.from(this@StampDetailActivity).inflate(R.layout.store_item_tab_text, null) as TextView
                    textView.text = tab.text
                    tab.customView = textView
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //当tab从选中状态到不被选中状态 还原
                tab?.customView = null
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        //添加title
        TabLayoutMediator(store_tab_stamp_record, store_vp_stamp_detail) { tab, position ->
            tab.text = mTabText[position]
        }.attach()
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

        mEventViewPagerAdapter = StampDetailVPAdapter(this, mEventRecordFragmentList)
        store_vp_stamp_detail.adapter = mEventViewPagerAdapter
    }

    private fun initData() {

    }
}