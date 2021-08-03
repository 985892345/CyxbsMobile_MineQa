package com.mredrock.cyxbs.mine.page.store.activity

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.store.fragment.EventRecordFragment
import com.mredrock.cyxbs.mine.util.ui.BaseVPAdapter
import kotlinx.android.synthetic.main.mine_activity_stamp_detail.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:46
 */

class StampDetailActivity : BaseActivity() {
    private var mEventViewPagerAdapter: BaseVPAdapter<EventRecordFragment>? = null
    private var mEventRecordFragmentList = arrayListOf<EventRecordFragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_stamp_detail)
        initView()
        initAdapter()
        initData()
    }

    private fun initView() {

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

        mEventViewPagerAdapter=BaseVPAdapter(this,mEventRecordFragmentList)
        mine_store_vp_stamp_detail.adapter=mEventViewPagerAdapter
    }

    private fun initData() {

    }
}