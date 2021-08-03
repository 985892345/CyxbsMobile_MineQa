package com.mredrock.cyxbs.store.page.stamp.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.module_store.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.STORE_STAMP_DETAIL
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.store.page.stamp.fragment.EventRecordFragment
import com.mredrock.cyxbs.store.until.ui.BaseVPAdapter
import kotlinx.android.synthetic.main.store_activity_stamp_detail.*


/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:46
 */

@Route(path = STORE_STAMP_DETAIL)
class StampDetailActivity : BaseActivity() {
    private var mEventViewPagerAdapter: BaseVPAdapter<EventRecordFragment>? = null
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


        store_tab_stamp_record.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if (tab!=null){
//                    val textView:TextView= LayoutInflater.from(this@StampDetailActivity).inflate(R.layout.mine_item_tab_text,null) as TextView
//                    textView.text = tab.text
//                    tab.customView = textView
//                }


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        //设置TabLayout
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
        val bundle = Bundle()
        bundle.putString("event", "exchange")
        val exchangeRecordFragment = EventRecordFragment()
        exchangeRecordFragment.arguments = bundle
        bundle.clear()
        bundle.putString("event", "getStamp")
        val stampGetRecordFragment = EventRecordFragment()
        mEventRecordFragmentList.add(exchangeRecordFragment)
        mEventRecordFragmentList.add(stampGetRecordFragment)

        mEventViewPagerAdapter = BaseVPAdapter(this, mEventRecordFragmentList)
        store_vp_stamp_detail.adapter = mEventViewPagerAdapter
    }

    private fun initData() {

    }
}